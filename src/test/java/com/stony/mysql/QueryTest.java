package com.stony.mysql;

import com.stony.mysql.command.*;
import com.stony.mysql.command.responses.QueryResponse;
import com.stony.mysql.event.BinlogEvent;
import com.stony.mysql.event.EventDeserializer;
import com.stony.mysql.io.LittleByteBuffer;
import com.stony.mysql.protocol.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;


import static com.stony.mysql.protocol.EventType.*;
import static com.stony.mysql.event.ColumnValue.*;

/**
 * <p>mysql-x
 * <p>com.mysql.test
 *
 * @author stony
 * @version 下午2:40
 * @since 2018/10/12
 */
public class QueryTest extends AbstractMainTest{


    public static void main(String[] args) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 1000); //连接
        socket.setSoTimeout(600000);          //读操作
        OutputStream out = socket.getOutputStream();
        InputStream in = socket.getInputStream();


        System.out.println("in: ");
        System.out.println(in);
        System.out.println("out: ");
        System.out.println(out);

//        LittleByteBuffer byteBuffer = new LittleByteBuffer(1024*4);
        LittleByteBuffer byteBuffer = new LittleByteBuffer(2 << 23);//max payload of 16 777 215 (2^24−1) bytes
        byteBuffer.fromInput(in);

        int size = byteBuffer.getLength();

        if(size == 0 || size == -1) {
            throw new IOException(String.format("连接服务器[%s:%d]错误.", host, port));
        }

        System.out.println(String.format("Input >>> %s", Arrays.toString(byteBuffer.remainingData())));
        System.out.println(String.format("连接服务器[%s:%d]成功.", host, port));
        System.out.println("---------------------------------------------");


        int packetLen = byteBuffer.readInt(3);
        int seq = byteBuffer.readInt(1); //skip

        if(byteBuffer.peekFirst() == 0xFF) {
            ERRPacket packet = new ResponsePacket(byteBuffer.remainingData(), 0).getErr();
            throw new IOException(packet.toString());
        }

        HandshakeV10 handshake = new HandshakeV10(byteBuffer.remainingData());

        System.out.println(handshake);
        System.out.println("---------------------------------------------");


        HandshakeResponse41 handshakeResponse = new HandshakeResponse41();

        int clientCapabilities = CapabilityFlags.CLIENT_LONG_FLAG |
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


        boolean isAuth = false;
        boolean isDB = false;
        boolean isPing = false;
        boolean isQuery = false;
        boolean isBinlog = false;

        packetLen = byteBuffer.readInt(3);
        seq = byteBuffer.readInt(1); //skip

        ResponsePacket packet = new ResponsePacket(byteBuffer.remainingData(), clientCapabilities);

        if(packet.isERR()) {
            throw new IOException(packet.toString());
        }
        if(packet.isOk()) {
            isAuth = true;
        }

        System.out.println(packet);
        System.out.println("---------------------------------------------");

        if(isAuth) {
            schema = "config";
            BaseCommand command = new InitDBCommand(schema);
            command.writeTo(out, byteBuffer);
            byteBuffer.fromInput(in, true);

            packetLen = byteBuffer.readInt(3);
            seq = byteBuffer.readInt(1); //skip

            packet = new ResponsePacket(byteBuffer.remainingData(), clientCapabilities);

            if(packet.isERR()) {
                throw new IOException(packet.toString());
            }
            if(packet.isOk()) {
                isDB = true;
            }

            System.out.println(packet);
            System.out.println("InitDBCommand---------------------------------------------");
        }
        if(isDB){
            BaseCommand command = new PingCommand();
            command.writeTo(out, byteBuffer);
            byteBuffer.fromInput(in, true);

            packetLen = byteBuffer.readInt(3);
            seq = byteBuffer.readInt(1); //skip

            packet = new ResponsePacket(byteBuffer.remainingData(), clientCapabilities);

            if(packet.isERR()) {
                throw new IOException(packet.toString());
            }
            if(packet.isOk()) {
                isPing = true;
            }

            System.out.println(packet);
            System.out.println("PingCommand---------------------------------------------");
        }
        if(isPing) {
            String sql = "select * from user limit 2";
            BaseCommand command = new QueryCommand(sql);
            command.writeTo(out, byteBuffer);
            byteBuffer.fromInput(in, true);

            packetLen = byteBuffer.readInt(3);
            seq = byteBuffer.readInt(1); //skip

            QueryResponse queryResponse = new QueryResponse(byteBuffer.remainingData(), clientCapabilities);
            int head = queryResponse.getFieldCount();

            if (head == 0) {
                System.out.println(new ResponsePacket(byteBuffer.remainingData(), clientCapabilities));
            } else if (head == 0XFF) { //ERR
                throw new IOException(new ResponsePacket(byteBuffer.remainingData(), clientCapabilities).toString());
            } else if (queryResponse.getFieldCount() == 0XFB) { //GET_MORE_CLIENT_DATA
                //pass
                System.out.println("GET_MORE_CLIENT_DATA");
            } else if (queryResponse.hasResult()) {
                System.out.println(queryResponse);

                System.out.println("QueryCommand---------------------------------------------");

                QueryResponse.ResultSet resultSet = queryResponse.getResultSet();
                while (resultSet.next()) {
                    System.out.print(resultSet.getLong(0));
                    System.out.print(", ");
                    System.out.print(resultSet.getString(1));
                    System.out.print(", ");
                    System.out.print(resultSet.getInt(2));
                    System.out.print(", ");
                    System.out.print(resultSet.getInt(3));
                    System.out.print(", ");
                    System.out.print(resultSet.getString(4));
                    System.out.print(", ");
                    System.out.print(resultSet.getString(5));
                    System.out.print(", ");
                    System.out.print(resultSet.getString(6));
                    System.out.println();
                }
                isQuery = true;

            }
            String fileName = null;
            long position = -1;

            if(isQuery) {
                sql = "show master status";
                command = new QueryCommand(sql);
                command.writeTo(out, byteBuffer);
                byteBuffer.fromInput(in, true);

                packetLen = byteBuffer.readInt(3);
                seq = byteBuffer.readInt(1); //skip


                queryResponse = new QueryResponse(byteBuffer.remainingData(), clientCapabilities);

                head = queryResponse.getFieldCount();
                if (head == 0) {
                    System.out.println(new ResponsePacket(byteBuffer.remainingData(), clientCapabilities));
                } else if (head == 0XFF) { //ERR
                    throw new IOException(new ResponsePacket(byteBuffer.remainingData(), clientCapabilities).toString());
                } else if (queryResponse.getFieldCount() == 0XFB) { //GET_MORE_CLIENT_DATA
                    //pass
                    System.out.println("GET_MORE_CLIENT_DATA");
                } else if (queryResponse.hasResult()) {
                    System.out.println(queryResponse);

                    System.out.println("QueryCommand---------------------------------------------");

                    QueryResponse.ResultSet resultSet = queryResponse.getResultSet();
                    if (resultSet.next()) {
                        fileName = resultSet.getString(0);
                        position = resultSet.getLong(1);
                    }
                }

            }
            boolean checksum = false;
            String checksumName = null;
            String checksumValue = null;
            ChecksumType checksumType = ChecksumType.NONE;
            if(position > 0) {
                System.out.printf("BinLog: fileName=%s, position=%d\n", fileName, position);
                sql = "show global variables like 'binlog_checksum'";

                command = new QueryCommand(sql);
                command.writeTo(out, byteBuffer);
                byteBuffer.fromInput(in, true);

                packetLen = byteBuffer.readInt(3);
                seq = byteBuffer.readInt(1); //skip


                queryResponse = new QueryResponse(byteBuffer.remainingData(), clientCapabilities);

                head = queryResponse.getFieldCount();
                if (head == 0) {
                    System.out.println(new ResponsePacket(byteBuffer.remainingData(), clientCapabilities));
                } else if (head == 0XFF) { //ERR
                    throw new IOException(new ResponsePacket(byteBuffer.remainingData(), clientCapabilities).toString());
                } else if (queryResponse.getFieldCount() == 0XFB) { //GET_MORE_CLIENT_DATA
                    //pass
                    System.out.println("GET_MORE_CLIENT_DATA");
                } else if (queryResponse.hasResult()) {
                    System.out.println(queryResponse);

                    System.out.println("QueryCommand---------------------------------------------");

                    QueryResponse.ResultSet resultSet = queryResponse.getResultSet();
                    if (resultSet.next()) {
                        checksumName = resultSet.getString(0);
                        checksumValue = resultSet.getString(1).toUpperCase();
                        checksumType = ChecksumType.valueOf(checksumValue);
                    }
                    checksum = true;
                }
            }
            if(checksum) {
                if(checksumType == ChecksumType.CRC32) {
                    System.out.printf("BinLog: checksumName=%s, checksumValue=%s\n", checksumName, checksumValue);
                    sql = "set @master_binlog_checksum= @@global.binlog_checksum";
                    command = new QueryCommand(sql);
                    command.writeTo(out, byteBuffer);
                    byteBuffer.fromInput(in, true);

                    packetLen = byteBuffer.readInt(3);
                    seq = byteBuffer.readInt(1); //skip

                    queryResponse = new QueryResponse(byteBuffer.remainingData(), clientCapabilities);

                    head = queryResponse.getFieldCount();
                    if (head == 0) {
                        System.out.println(new ResponsePacket(byteBuffer.remainingData(), clientCapabilities));
                        System.out.println("QueryCommand---------------------------------------------");
                    } else if (head == 0XFF) { //ERR
                        throw new IOException(new ResponsePacket(byteBuffer.remainingData(), clientCapabilities).toString());
                    } else if (queryResponse.getFieldCount() == 0XFB) { //GET_MORE_CLIENT_DATA
                        //pass
                        System.out.println("GET_MORE_CLIENT_DATA");
                    }
                }
            }
            if(checksum) {

                int serverId = 4;

                command = new BinlogDumpCommand(serverId, position, fileName);
                command.writeTo(out, byteBuffer);
                boolean listenEvent = true;

                EventDeserializer deserializer = new EventDeserializer(checksumType);

                byteBuffer.restOffset();

                loop:
                while (listenEvent) {
                    size = byteBuffer.fromInput(in, false);
                    System.out.println("----------- BinLog ----------------------  " + size);


                    while (byteBuffer.hasRemaining()) {
                        if(byteBuffer.remaining() < 4) {
                            System.out.println("remaining小于4: " + Arrays.toString(byteBuffer.remainingData()));
                            continue loop;
//                            break loop;
                        }
                        int beginIndex = byteBuffer.readIndex();
                        packetLen = byteBuffer.readInt(3);
                        seq = byteBuffer.readInt(1); //skip
//

                        System.out.printf("Event seq: %3d, Event packetLen: %5d, Event remaining: %5d\n",
                                seq, packetLen, byteBuffer.remaining());
                        if(byteBuffer.remaining() < packetLen) {
                            int need = packetLen - byteBuffer.remaining();
                            System.out.println("Event need: " + need);
                            byteBuffer.readIndex(beginIndex); //rest read index to beginIndex
                            continue loop;
//                            System.out.println("Event need: " + Arrays.toString(lastData));
//                            continue loop;
//                            if(need > 1024*10) {
//                                System.out.println("need大于10240: " + Arrays.toString(byteBuffer.realData()));
//                                break loop;
//                            }
//                            byte[] temp = new byte[need];
//                            int _read = in.read(temp);
//                            byteBuffer.writerBytes(temp);
//                            need -= _read;
//                            while (need > 0) {
//                                temp = new byte[need];
//                                _read = in.read(temp);
//                                byteBuffer.writerBytes(temp);
//                                need -= _read;
//                            }
//                            System.out.println("Event need: " + Arrays.toString(byteBuffer.realData()));
                        }
//                        byteBuffer.readBytes(packetLen);//test skip deserializer
                        BinlogEvent event = deserializer.deserializer(byteBuffer.readBytes(packetLen));
                        System.out.println(event);

                    }

//                System.out.println("Event checksum: " + LittleByteBuffer.warp(event.getChecksum()).readInt(1));
//                System.out.println("Event checksum: " + LittleByteBuffer.warp(event.getChecksum()).readInt(3));
//                System.out.println("Event checksum: " + LittleByteBuffer.warp(event.getChecksum()).readInt(4));
//                    packetLen = byteBuffer.readInt(3);
//                    seq = byteBuffer.readInt(1); //skip
//                    System.out.println("seq: " + seq);
//                    System.out.println("packetLen: " + packetLen);
//                    event = deserializer.deserializer(byteBuffer.readBytes(packetLen));
//                    System.out.println(event);

//                System.out.println("check: " + (byteBuffer.readInt(3) == byteBuffer.remaining()-1));
//                System.out.println("seq: " + byteBuffer.readInt(1));
//                    System.out.println("remainingData: " + Arrays.toString(byteBuffer.remainingData()));

                }
            }
        }
        socket.close();

    }
}
