package com.stony.mysql.io;

import com.stony.mysql.command.BinlogDumpCommand;
import com.stony.mysql.command.QueryCommand;
import com.stony.mysql.command.responses.QueryResponse;
import com.stony.mysql.event.EventDeserializer;
import com.stony.mysql.event.EventListener;
import com.stony.mysql.protocol.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.io
 *
 * @author stony
 * @version 下午3:10
 * @since 2018/10/24
 */
public class SlaveConnector extends BaseConnector {
    private static final Logger logger = LoggerFactory.getLogger(SlaveConnector.class);

    int serverId = 13;
    final String hostname;
    final int port;

    String schema;
    String username;
    String password;

    int clientCapabilities;

    int connTimeout = 2000;
    int soTimeout = 1000 * 60 * 60; //milliseconds 1小时 SO_TIMEOUT
    int buffSize = 2 << 15; //12:8192, 15:65536
    static final int MAX_BUFF_SIZE = 2 << 23; //8192 //max payload of 16 777 215 (2^24−1) bytes

    LittleByteBuffer byteBuffer;
    Socket socket;
    OutputStream out;
    InputStream in;

    String binlogFileName;
    long binlogPosition;

    ChecksumType checksumType = ChecksumType.NONE;
    EventDeserializer deserializer;


    volatile boolean listenEvent = true;
    volatile boolean isAuth = false;
    AtomicBoolean startup = new AtomicBoolean(false);


    public SlaveConnector(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }


    @Override
    public void connect() throws XException {
        if(startup.compareAndSet(false, true)) {
            logger.info(String.format("连接服务器[%s:%d]开始.", hostname, port));
            socket = new Socket();
            try {
                socket.connect(new InetSocketAddress(hostname, port), connTimeout); //连接
                if(soTimeout > 0) {
                    socket.setSoTimeout(soTimeout);                                     //读操作
                }

                out = socket.getOutputStream();
                in = socket.getInputStream();

                byteBuffer = new LittleByteBuffer(this.buffSize);


                byteBuffer.fromInput(in);

                int size = byteBuffer.getLength();

                if(size == 0 || size == -1) {
                    throw new XException(String.format("连接服务器[%s:%d]错误.", hostname, port));
                }
                logger.info(String.format("连接服务器[%s:%d]成功.", hostname, port));


                int packetLen = byteBuffer.readInt(3);
                int seq = byteBuffer.readInt(1); //skip

                if(byteBuffer.peekFirst() == 0xFF) {
                    ERRPacket packet = new ResponsePacket(byteBuffer.remainingData(), 0).getErr();
                    throw new XException("握手失败，" + packet.toString());
                }

                if(!auth(byteBuffer.remainingData())) {
                    throw new XException(String.format("认证异常[%s, %s]异常", username, password));
                }
                logger.info(String.format("认证服务[%s, %s]通过.", username, password));
                this.isAuth = true;
            } catch (IOException e) {
                throw new XException(String.format("连接到[%s:%d]异常", hostname, port), e);
            }
        }
    }

    @Override
    public void start() throws XException {
        connect();
        if(startup.get()) {
            try {
                QueryResponse queryResponse;
                QueryResponse.ResultSet resultSet;
                if(this.isAuth) {
                    String sql = "show master status";
                    queryResponse = query(sql);

                    if (queryResponse.hasResult()) {
                        logger.debug("QueryCommand---------------------------------------------");

                        resultSet = queryResponse.getResultSet();
                        if (resultSet.next()) {
                            binlogFileName = resultSet.getString(0);
                            binlogPosition = resultSet.getLong(1);
                        } else {
                            throw new XException(String.format("服务[%s:%d]没有开启log-bin.", hostname, port));
                        }
                    }
                }

                String checksumName = null;
                String checksumValue = null;

                if(binlogPosition > 0) {
                    logger.info("BinLog: fileName={}, position={}", binlogFileName,  binlogPosition);

                    String sql = "show global variables like 'binlog_checksum'";
                    queryResponse = query(sql);
                    if (queryResponse.hasResult()) {
                        logger.debug("QueryCommand---------------------------------------------");
                        resultSet = queryResponse.getResultSet();

                        if (resultSet.next()) {
                            checksumName = resultSet.getString(0);
                            checksumValue = resultSet.getString(1).toUpperCase();
                            checksumType = ChecksumType.valueOf(checksumValue);
                        }
                    }

                    if(checksumType == ChecksumType.CRC32) {
                        logger.info("BinLog: checksumName={}, checksumValue={}", checksumName, checksumValue);
                        sql = "set @master_binlog_checksum= @@global.binlog_checksum";
                        query(sql);
                    }

                    binlogDump();
                }

            } catch (IOException e) {
                throw new XException(String.format("连接到[%s:%d]监听异常", hostname, port), e);
            }
        }
//        if(startup.compareAndSet(false, true)) {
//            logger.info(String.format("连接服务器[%s:%d]开始.", hostname, port));
//            socket = new Socket();
//            try {
//                socket.connect(new InetSocketAddress(hostname, port), connTimeout); //连接
//                if(soTimeout > 0) {
//                    socket.setSoTimeout(soTimeout);                                     //读操作
//                }
//
//                out = socket.getOutputStream();
//                in = socket.getInputStream();
//
//                byteBuffer = new LittleByteBuffer(this.buffSize);
//
//
//                byteBuffer.fromInput(in);
//
//                int size = byteBuffer.getLength();
//
//                if(size == 0 || size == -1) {
//                    throw new XException(String.format("连接服务器[%s:%d]错误.", hostname, port));
//                }
//                logger.info(String.format("连接服务器[%s:%d]成功.", hostname, port));
//
//
//                int packetLen = byteBuffer.readInt(3);
//                int seq = byteBuffer.readInt(1); //skip
//
//                if(byteBuffer.peekFirst() == 0xFF) {
//                    ERRPacket packet = new ResponsePacket(byteBuffer.remainingData(), 0).getErr();
//                    throw new XException("握手失败，" + packet.toString());
//                }
//
//                QueryResponse queryResponse;
//                QueryResponse.ResultSet resultSet;
//                if(auth(byteBuffer.remainingData())) {
//                    String sql = "show master status";
//                    queryResponse = query(sql);
//
//                    if (queryResponse.hasResult()) {
//                        logger.debug("QueryCommand---------------------------------------------");
//
//                        resultSet = queryResponse.getResultSet();
//                        if (resultSet.next()) {
//                            binlogFileName = resultSet.getString(0);
//                            binlogPosition = resultSet.getLong(1);
//                        }
//                    }
//                }
//
//                String checksumName = null;
//                String checksumValue = null;
//
//                if(binlogPosition > 0) {
//                    logger.info("BinLog: fileName={}, position={}", binlogFileName,  binlogPosition);
//
//                    String sql = "show global variables like 'binlog_checksum'";
//                    queryResponse = query(sql);
//                    if (queryResponse.hasResult()) {
//                        logger.debug("QueryCommand---------------------------------------------");
//                        resultSet = queryResponse.getResultSet();
//
//                        if (resultSet.next()) {
//                            checksumName = resultSet.getString(0);
//                            checksumValue = resultSet.getString(1).toUpperCase();
//                            checksumType = ChecksumType.valueOf(checksumValue);
//                        }
//                    }
//                }
//                if(checksumType == ChecksumType.CRC32) {
//                    logger.info("BinLog: checksumName={}, checksumValue={}", checksumName, checksumValue);
//                    String sql = "set @master_binlog_checksum= @@global.binlog_checksum";
//                    query(sql);
//                }
//
//                binlogDump();
//            } catch (IOException e) {
//                throw new XException(String.format("连接到[%s:%d]异常", hostname, port), e);
//            }
//        }
    }
    private boolean auth(byte[] data) throws IOException {
        HandshakeV10 handshake = new HandshakeV10(data);

        HandshakeResponse41 handshakeResponse = new HandshakeResponse41();

        clientCapabilities = CapabilityFlags.CLIENT_LONG_FLAG |
                CapabilityFlags.CLIENT_PROTOCOL_41 | CapabilityFlags.CLIENT_SECURE_CONNECTION;
        if(isNotEmpty(schema)){
            clientCapabilities |= CapabilityFlags.CLIENT_CONNECT_WITH_DB;
        }

        handshakeResponse.setCapabilityFlags(clientCapabilities);
        handshakeResponse.setUsername(username);
        handshakeResponse.setPassword(password);
        handshakeResponse.setMaxPacketSize(0);
        handshakeResponse.setCharacterSet(handshake.getCharacterSet());
        handshakeResponse.setAuthPluginDataPart(handshake.getAuthPluginDataPart());
        if(isNotEmpty(schema)){
            handshakeResponse.setDatabase(schema);
        }
        //rest offset, begin write
        handshakeResponse.writeTo(out, byteBuffer);


        //read input
        byteBuffer.fromInput(in, true);

        int packetLen = byteBuffer.readInt(3);
        int seq = byteBuffer.readInt(1); //skip

        ResponsePacket packet = new ResponsePacket(byteBuffer.remainingData(), clientCapabilities);

        if(packet.isERR()) {
            throw new XException("认证失败，" + packet.toString());
        }
        if(packet.isOk()) {
            return true;
        }
        return false;
    }
    private QueryResponse query(String sql) throws IOException {
        QueryCommand command = new QueryCommand(sql);
        command.writeTo(out, byteBuffer);
        byteBuffer.fromInput(in, true);
        int packetLen = byteBuffer.readInt(3);
        int seq = byteBuffer.readInt(1); //skip
        QueryResponse response = new QueryResponse(byteBuffer.remainingData(), clientCapabilities);

        if(response.hasError()) {
            throw new XException("Query["+sql+"] error," + response.getPacket().toString());
        }
        return response;
    }
    private void binlogDump() throws IOException {
        BinlogDumpCommand command = new BinlogDumpCommand(serverId, binlogPosition, binlogFileName);
        command.writeTo(out, byteBuffer);

        deserializer = new EventDeserializer(checksumType);

        logger.info("服务器[{}:{}]BINGLOG({}|{})开始监听...", hostname, port, binlogPosition, binlogFileName);

        byteBuffer.restOffset();
        int size = -1;
        int packetLen = 0;
        int seq = 0;

        loop:
        while (listenEvent) {
            size = byteBuffer.fromInput(in, false);
            logger.debug("-------------- BinLog ----------------------  {}", size);

            while (byteBuffer.hasRemaining()) {
                if (byteBuffer.remaining() < 4) {
                    logger.debug("buffer remaining小于4: {}", Arrays.toString(byteBuffer.remainingData()));
                    continue loop;
                }
                int beginIndex = byteBuffer.readIndex();
                packetLen = byteBuffer.readInt(3);
                seq = byteBuffer.readInt(1); //skip

                logger.debug(String.format("Event seq: %3d, Event packetLen: %5d, Event remaining: %5d\n",
                        seq, packetLen, byteBuffer.remaining()));

                if (byteBuffer.remaining() < packetLen) {
                    int need = packetLen - byteBuffer.remaining();
                    logger.debug("Event need: " + need);
                    byteBuffer.readIndex(beginIndex);  //rest read index to beginIndex
                    continue loop;
                }
                onEvent(deserializer.deserializer(byteBuffer.readBytes(packetLen)));
            }
        }
    }



    @Override
    public void shutdown() throws XException {
        if(startup.compareAndSet(true, false)) {
            logger.info("服务器[{}:{}]连接即将关闭...", hostname, port);
            this.listenEvent = false;
            signal();
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            logger.info("服务器[{}:{}]连接已经关闭...", hostname, port);
        }
    }



    public void setConnTimeout(int connTimeout) {
        this.connTimeout = Math.min(buffSize, 1000);;
    }

    public void setSoTimeout(int soTimeout) {
        this.soTimeout = soTimeout;
    }

    public void setBuffSize(int buffSize) {
        this.buffSize = Math.min(buffSize, MAX_BUFF_SIZE);
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public static class DefaultConnectorBuilder {

        int serverId = 13;
        String schema;

        String hostname;
        int port;
        String username;
        String password;


        int connTimeout;
        int soTimeout; //milliseconds 1小时 SO_TIMEOUT
        int buffSize; //12:8192, 15:65536

        EventListener[] listeners;

        public DefaultConnectorBuilder serverId(int serverId) {
            this.serverId = serverId;
            return this;
        }
        public DefaultConnectorBuilder schema(String schema) {
            this.schema = schema;
            return this;
        }
        public DefaultConnectorBuilder hostname(String hostname) {
            this.hostname = hostname;
            return this;
        }
        public DefaultConnectorBuilder port(int port) {
            this.port = port;
            return this;
        }
        public DefaultConnectorBuilder username(String username) {
            this.username = username;
            return this;
        }
        public DefaultConnectorBuilder password(String password) {
            this.password = password;
            return this;
        }

        public DefaultConnectorBuilder connTimeout(int connTimeout) {
            this.connTimeout = connTimeout;
            return this;
        }
        public DefaultConnectorBuilder soTimeout(int soTimeout) {
            this.soTimeout = soTimeout;
            return this;
        }
        public DefaultConnectorBuilder buffSize(int buffSize) {
            this.buffSize = buffSize;
            return this;
        }

        public SlaveConnector build() {
            SlaveConnector connector = new SlaveConnector(hostname, port, username, password);
            connector.serverId = serverId;
            connector.schema = schema;
            if(connTimeout > 0) {
                connector.setConnTimeout(connTimeout);
            }
            if(soTimeout > 0) {
                connector.setSoTimeout(soTimeout);
            }
            if(buffSize > 0) {
                connector.setBuffSize(buffSize);
            }
            if(connector.hasArray(listeners)) {
                connector.registerListeners(listeners);
            }
            return connector;
        }
    }
}