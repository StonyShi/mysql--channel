package com.stony.mysql;

import com.stony.mysql.io.LittleByteBuffer;
import com.stony.mysql.protocol.CapabilityFlags;
import com.stony.mysql.protocol.ColumnType;
import com.stony.mysql.protocol.EventType;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

/**
 * <p>mysql-x
 * <p>com.mysql.test
 *
 * @author stony
 * @version 下午4:27
 * @since 2018/10/11
 */
public class HandshakeTest extends AbstractMainTest{



    public static void main(String[] args) throws IOException {
        Socket socket = new Socket(host, port);

        OutputStream out = socket.getOutputStream();


        InputStream in = socket.getInputStream();
        byte[] buff = new byte[1024];
        int size = -1;
        size = in.read(buff);
        if(size == -1) {
            throw new IOException(String.format("连接服务器[%s:%d]错误.", host, port));
        }

        for (int i = 0; i < size; i++) {
            if(i > 0) System.out.print(",");
            System.out.print(buff[i]);
        }
        System.out.println(String.format("连接服务器[%s:%d]成功.", host, port));
        System.out.println();
        System.out.println("----------------------------------");
        if(size > 4){

//                int writeIndex = 0;
//
//                byte packetId= buff[0];
//                writeIndex++;
//                //skip 4
//                int pos = (writeIndex + 3);
//                int protocolVersion = buff[pos--] & 0xFF;
//                writeIndex += 4;
//
//
//                int s = 0;
//                for (int i = writeIndex; i < buff.length; i++) {
//                    if(buff[i] == 0){
//                        break;
//                    }
//                    s++;
//                }
//                byte[] temp = new byte[s];
//                System.arraycopy(buff, writeIndex, temp, 0, s);
//                writeIndex += s;
//
//                String serverVersion = new String(temp);
//
//                pos = (writeIndex + 3);

            LittleByteBuffer byteBuffer = new LittleByteBuffer(1024);
            byteBuffer.writerBytes(buff, 0, size);
            int packetLen = byteBuffer.readInt(3);
            int seq = byteBuffer.readInt(1); //skip


            int protocolVersion = byteBuffer.readInt(1); //1
            String serverVersion = byteBuffer.readStringEndZero(); //2

            int connectionId = byteBuffer.readInt(); //3
            String authPluginDataPart1 = byteBuffer.readString(8);///readStringEndZero(); //4

            int filler = byteBuffer.readByte() & 0xFF; //5
            byte[] capabilityFlagsLower = byteBuffer.readBytes(2); //6


            int characterSet = byteBuffer.readInt(1);
            int statusFlags = byteBuffer.readInt(2);

            byte[] capabilityFlagsUpper = byteBuffer.readBytes(2);

            byte[] temp = new byte[4];
            System.arraycopy(capabilityFlagsUpper, 0, temp, 0, 2);
            System.arraycopy(capabilityFlagsLower, 0, temp, 2, 2);
            int capabilityFlags = temp[0] & 0xFF |
                    (temp[1] & 0xFF) << 8 |
                    (temp[2] & 0xFF) << 16 |
                    (temp[3] & 0xFF) << 24;

            System.out.println(String.format("temp=(%s)", Arrays.toString(temp)));
            System.out.println(String.format("capabilityFlagsUpper=(%s), capabilityFlagsLower=(%s), capabilityFlags=%d",
                    Arrays.toString(capabilityFlagsUpper),
                    Arrays.toString(capabilityFlagsLower),
                    capabilityFlags));

            int authPluginDataLen = 0;
            if((capabilityFlags & CapabilityFlags.CLIENT_PLUGIN_AUTH) != 0){
                authPluginDataLen = byteBuffer.readInt(1);
                System.out.println("-length of auth-plugin-data = " + authPluginDataLen);
            }

            byteBuffer.skip(10); //reserved

            int part2len = 0;
            if((capabilityFlags & CapabilityFlags.CLIENT_SECURE_CONNECTION) != 0){
                part2len = Math.max(13, authPluginDataLen - 8);
                System.out.println("-length of auth-plugin-data-part-2 = " + part2len);
            }
            String authPluginDataPart2 = byteBuffer.readStringEndZero();

            String authPluginDataPart = authPluginDataPart1 + authPluginDataPart2;
            System.out.println(
                    String.format(">>>packetId=%s,seq=%s, protocolVersion=%d, serverVersion=%s, \n" +
                                    ">>>connectionId=%d, authPluginDataPart=%s, filler=%d, capabilityFlags=%s, \n" +
                                    ">>>characterSet=%s, statusFlags=%s, authPluginDataPart2=%s",
                            packetLen, seq, protocolVersion, serverVersion, connectionId,
                            authPluginDataPart1, 0, capabilityFlags, characterSet,
                            statusFlags, authPluginDataPart2)
            );
            System.out.println("----------------------------------");
            System.out.println("authPluginDataPart="+authPluginDataPart);
            System.out.println(String.format("remaining=%d, capacity=%d, size=%d",
                    byteBuffer.remaining(), byteBuffer.capacity(), size));
            System.out.println("----------------------------------");

            //rest offset, begin write
            byteBuffer.restOffset();
            ///
            int clientCapabilities = CapabilityFlags.CLIENT_LONG_FLAG |
                    CapabilityFlags.CLIENT_PROTOCOL_41 | CapabilityFlags.CLIENT_SECURE_CONNECTION;
            if(isNotEmpty(schema)){
                clientCapabilities |= CapabilityFlags.CLIENT_CONNECT_WITH_DB;
            }
            byteBuffer.writerInt(clientCapabilities);
            byteBuffer.writerInt(0); //max-packet size
            byteBuffer.writerInt(characterSet, 1);
            byteBuffer.writerZero(23); //reserved (all [0])
            byteBuffer.writerStringEndZero(username); //username

            byte[] passwordSHA1 = passwordCalculate(password, authPluginDataPart);
            //capabilities & CLIENT_SECURE_CONNECTION
            byteBuffer.writerInt(passwordSHA1.length, 1);
            byteBuffer.writerBytes(passwordSHA1);  //string[n]  auth-response
            if(isNotEmpty(schema)){
                //capabilities & CLIENT_CONNECT_WITH_DB
                byteBuffer.writerStringEndZero(schema);  //string[NUL]    database
            }

            int responseSeq = 1;
            int responseLen = byteBuffer.getLength();
            //write 3 bit
            for (int i = 0; i < 3; i++) {
                out.write((byte) (responseLen >>> (i << 3)));
            }
            //write 1 bit
            for (int i = 0; i < 1; i++) {
                out.write((byte) (responseSeq >>> (i << 3)));
            }
            responseSeq++;
            out.write(byteBuffer.getData(), 0 , responseLen);
            out.flush();

//            System.out.println("data: " + Arrays.toString(byteBuffer.getData()));
            System.out.println("resBody: " + Arrays.toString(Arrays.copyOf(byteBuffer.getData(), responseLen)));
            System.out.println("passwordSHA1: " + Arrays.toString(passwordSHA1));


            System.out.println("----------------------------------");
            buff = new byte[1024];
            size = -1;
            size = in.read(buff);
            for (int i = 0; i < size; i++) {
                if(i > 0) System.out.print(",");
                System.out.print(buff[i]);
            }
            System.out.println();
            System.out.println("----------------------------------");


            byteBuffer.restOffset();
            byteBuffer.writerBytes(buff, 0, size);

            packetLen = byteBuffer.readInt(3);
            seq = byteBuffer.readInt(1); //skip

            int status = byteBuffer.readInt(1);

            boolean isAuth = false;
            System.out.println(">> status: " + status);
            if(status == 0XFF) {
                String msg = byteBuffer.readString(packetLen - 1);
                System.out.println(">> msg: " + msg);
            } else {
                isAuth = true;
            }
            if(isAuth){
                schema = "config";
                byteBuffer.restOffset();
                byteBuffer.writerInt(2, 1); //0x02 COM_INIT_DB
                byteBuffer.writerString(schema);


                responseLen = byteBuffer.getLength();

                responseSeq = 0;
                //write 3 bit
                for (int i = 0; i < 3; i++) {
                    out.write((byte) (responseLen >>> (i << 3)));
                }
                //write 1 bit
                for (int i = 0; i < 1; i++) {
                    out.write((byte) (responseSeq >>> (i << 3)));
                }


                out.write(byteBuffer.getData(), 0 , responseLen);
                out.flush();

                System.out.println("responseLen: " + responseLen);
//                System.out.println("data: " + Arrays.toString(byteBuffer.getData()));
                System.out.println("resBody: " + Arrays.toString(Arrays.copyOf(byteBuffer.getData(), responseLen)));

                System.out.println("----------------------------------");
                buff = new byte[1024];
                size = -1;
                size = in.read(buff);
                for (int i = 0; i < size; i++) {
                    if(i > 0) System.out.print(",");
                    System.out.print(buff[i]);
                }
                System.out.println();
                System.out.println("----------------------------------");


                byteBuffer.restOffset();
                byteBuffer.writerBytes(buff, 0, size);

                packetLen = byteBuffer.readInt(3);
                seq = byteBuffer.readInt(1); //skip

                boolean db = false;
                status = byteBuffer.readInt(1);
                System.out.println(">> status: " + status);
                if(status == 0XFF) {
                    String msg = byteBuffer.readString(packetLen - 1);
                    System.out.println(">> msg: " + msg);
                } else {
                    System.out.println("Change to DB: " + schema);
                    db = true;
                }

                boolean ping = false;
                if(db) {
                    //[0e] COM_PING 14
                    byteBuffer.restOffset();
                    byteBuffer.writerInt(14, 1); //0x0E
                    responseLen = byteBuffer.getLength();
                    responseSeq = 0;
                    //write 3 bit
                    for (int i = 0; i < 3; i++) {
                        out.write((byte) (responseLen >>> (i << 3)));
                    }
                    //write 1 bit
                    for (int i = 0; i < 1; i++) {
                        out.write((byte) (responseSeq >>> (i << 3)));
                    }

                    out.write(byteBuffer.getData(), 0 , responseLen);
                    out.flush();

                    System.out.println("responseLen: " + responseLen);
                    System.out.println("resBody: " + Arrays.toString(Arrays.copyOf(byteBuffer.getData(), responseLen)));


                    System.out.println("---------- Ping ------------------------");
                    buff = new byte[1024];
                    size = -1;
                    size = in.read(buff);
                    for (int i = 0; i < size; i++) {
                        if(i > 0) System.out.print(",");
                        System.out.print(buff[i]);
                    }
                    System.out.println();
                    System.out.println("---------- Ping  ------------------------");

                    byteBuffer.restOffset();
                    byteBuffer.writerBytes(buff, 0, size);

                    packetLen = byteBuffer.readInt(3);
                    seq = byteBuffer.readInt(1); //skip
                    System.out.println("packetLen: " + packetLen);


                    status = byteBuffer.readInt(1);
                    System.out.println(">> status: " + status);
                    if(status == 0XFF) {
                        String msg = byteBuffer.readString(packetLen - 1);
                        System.out.println(">> msg: " + msg);
                    } else {
                        ping = true;
                    }
                }
                boolean query = false;
                String fileName = null;
                int position = -1;
                long lastDataTime = System.currentTimeMillis();
                if(ping) {
                    String sql = "select * from user limit 5";
                    sql = "select * from app limit 5";
                    sql = "select * from user order by id desc";
                    sql = "SHOW MASTER LOGS";
                    sql = "SHOW TABLES";
                    sql = "show processlist";
                    sql = "show columns from config.app";
                    sql = "SHOW MASTER status";
                    sql = "select * from config.app limit 0";
                    byteBuffer.restOffset();
                    byteBuffer.writerInt(3, 1); //0x03 COM_QUERY
                    byteBuffer.writerString(sql);

                    responseLen = byteBuffer.getLength();
                    responseSeq = 0;
                    //write 3 bit
                    for (int i = 0; i < 3; i++) {
                        out.write((byte) (responseLen >>> (i << 3)));
                    }
                    //write 1 bit
                    for (int i = 0; i < 1; i++) {
                        out.write((byte) (responseSeq >>> (i << 3)));
                    }

                    out.write(byteBuffer.getData(), 0 , responseLen);
                    out.flush();

                    System.out.println("responseLen: " + responseLen);
                    System.out.println("resBody: " + Arrays.toString(Arrays.copyOf(byteBuffer.getData(), responseLen)));


                    System.out.println("----------- Query -----------------------");

                    buff = new byte[1024*8];
                    size = -1;
                    size = in.read(buff);
                    for (int i = 0; i < size; i++) {
                        if(i > 0) System.out.print(",");
                        System.out.print(buff[i]);
                    }
                    System.out.println();
                    System.out.println("----------- Query -----------------------  " + size);

//                    byteBuffer.clear();
                    byteBuffer.restOffset();
                    byteBuffer.writerBytes(buff, 0, size);

                    packetLen = byteBuffer.readInt(3);
                    seq = byteBuffer.readInt(1); //skip
                    System.out.println("packetLen: " + packetLen);

                    int head = byteBuffer.readInt(1);

                    if(head == 0) {
                        System.out.println("OK");
                    } else if (head == 0XFF) { //ERR
                        String msg = byteBuffer.readString(packetLen - 1);
                        System.out.println(">> ERR msg: " + msg);
                    } else if (head == 0XFB) { //GET_MORE_CLIENT_DATA
                        //pass
                        System.out.println("GET_MORE_CLIENT_DATA");
                    } else if (head >= 1) {
                        lastDataTime = System.currentTimeMillis();
                        int fieldCount = head;
                        System.out.println(">>>>>>>>>>>>>>>");

                        byte[] dd = byteBuffer.remainingData();
                        System.out.println(Arrays.toString(dd));
//                        System.out.println(new String(dd));

                        Map<Integer, String> colMap = new HashMap<>(128);
                        for (int i = 0; i < fieldCount; i++) {

                            byte[] vv = byteBuffer.readBytes(3); //skip
                            //System.out.println(Arrays.toString(vv));
                            seq = byteBuffer.readInt(1);

                            int lv = byteBuffer.readInt(1);
                            String catalog = byteBuffer.readString(lv);

                            lv = byteBuffer.readInt(1);
                            String v_schema = byteBuffer.readString(lv);

                            lv = byteBuffer.readInt(1);
                            String table = byteBuffer.readString(lv);


                            lv = byteBuffer.readInt(1);
                            String org_table = byteBuffer.readString(lv);

//                            lv = byteBuffer.readInt(1);
//                            String name = byteBuffer.readString(lv);
                            String name = byteBuffer.readLengthEncodedString();


//                            lv = byteBuffer.readInt(1);
//                            String org_name = byteBuffer.readString(lv);
                            String org_name = byteBuffer.readLengthEncodedString();

//                            byteBuffer.skip(13);

                            int columnInfoLen = byteBuffer.readInt(1);
                            int v_character_set = byteBuffer.readInt(2);
                            int v_column_length = byteBuffer.readInt(4);
                            int v_column_type = byteBuffer.readInt(1);
                            int v_column_flags = byteBuffer.readInt(2);
                            int v_column_decimals = byteBuffer.readInt(1);
                            int v_column_filler = byteBuffer.readInt(2);

                            System.out.printf("%d >>> catalog=%s,schema=%s,table=%s,org_table=%s,name=%s,org_name=%s\n",
                                    seq, catalog,v_schema, table, org_table, name, org_name);

                            System.out.printf("       columnInfoLen=%s,character_set=%s," +
                                    "column_length=%s, column_type=%s,column_flags=%s,column_decimals=%s,column_filler=%s\n",
                                    columnInfoLen,v_character_set,
                                    v_column_length, ColumnType.byCode(v_column_type),
                                    v_column_flags,  v_column_decimals,
                                    v_column_filler);

                            colMap.put(i, name);

                        }
                        System.out.println(">>>>>>>>>>>>>>> fieldCount : " + fieldCount);
                        System.out.println("remainingData: " + Arrays.toString(byteBuffer.remainingData()));

                        System.out.println();
                        System.out.println(String.format("remaining=%d, capacity=%d, size=%d",
                                byteBuffer.remaining(), byteBuffer.capacity(), size));

                        //
                        List<Entry[]> entries = new LinkedList<>();
                        int vvl = 0xFE;  //254 EOF  [-2, 0, 0, 34, 0]
                        String val = null;
                        //NULL is sent as 0xfb   251
                        while (byteBuffer.hasRemaining()){

                            vvl = byteBuffer.readInt(3); //skip
                            seq = byteBuffer.readInt(1);
                            System.out.printf("seq=%s, len=%s", seq, vvl);

                            //0xFE 254 EOF
                            if(0xFE == (byteBuffer.peekByte() & 0xFF)){
                                byteBuffer.skip(vvl);
                                System.out.printf(", val=%s\n", "EOF");
                                continue;
                            }
                            Entry[] entry = new Entry[fieldCount];
                            for (int i = 0; i < fieldCount; i++) {
                                int ll = (int) byteBuffer.readEncodedInteger();
                                if(ll == 0xFB) {
                                    val = null; //"null";
                                } else {
                                    val = byteBuffer.readString(ll);
                                }
                                entry[i] = new Entry(colMap.get(i), val);
                                System.out.printf("| %s=%s |", colMap.get(i), val);
                            }
                            System.out.println();
                            entries.add(entry);
                        }
                        System.out.println("remainingDataStr: " + new String(byteBuffer.remainingData()));

                        if(entries.size() > 0) {
                            query = true;
//                            entries.stream().forEach(e -> System.out.println(Arrays.toString(e)));

                            Entry[] entry = entries.get(0);
                            fileName = entry[0].value;
                            position = isNotEmpty(entry[1].value) ? Integer.valueOf(entry[1].value) : -1;
                        }
                    }
                } //query
                //HY000Slave can not handle replication events with the checksum that master is configured to log;
                if(query && position > 0) {
                    System.out.printf("BinLog: fileName=%s, position=%d\n", fileName, position);

                    int serverId = 4;
                    byteBuffer.restOffset();
                    byteBuffer.writerInt(18, 1); //18 COM_BINLOG_DUMP
                    byteBuffer.writerInt(position, 4);
                    byteBuffer.writerInt(0, 2); //0x01 BINLOG_DUMP_NON_BLOCK if there is no more event to send send a EOF_Packet instead of blocking the connection
                    byteBuffer.writerInt(serverId, 4);
                    byteBuffer.writerString(fileName);


                    responseLen = byteBuffer.getLength();
                    responseSeq = 0;
                    //write 3 bit
                    for (int i = 0; i < 3; i++) {
                        out.write((byte) (responseLen >>> (i << 3)));
                    }
                    //write 1 bit
                    for (int i = 0; i < 1; i++) {
                        out.write((byte) (responseSeq >>> (i << 3)));
                    }

                    out.write(byteBuffer.getData(), 0 , responseLen);
                    out.flush();

                    System.out.println("responseLen: " + responseLen);
                    System.out.println("resBody: " + Arrays.toString(Arrays.copyOf(byteBuffer.getData(), responseLen)));


                    System.out.println("----------- Query -----------------------");

                    buff = new byte[1024*8];
                    size = -1;

                    size = in.read(buff);
                    for (int i = 0; i < size; i++) {
                        if(i > 0) System.out.print(",");
                        System.out.print(buff[i]);
                    }
                    System.out.println();
                    System.out.println("----------- BinLog ----------------------  " + size);


                    byteBuffer.restOffset();
                    byteBuffer.writerBytes(buff, 0, size);

                    packetLen = byteBuffer.readInt(3);
                    seq = byteBuffer.readInt(1); //skip
                    System.out.println("packetLen: " + packetLen);

                    int head = byteBuffer.readInt(1);

                    if(head == 0) {
                        System.out.println("OK");
                        long timestamp = byteBuffer.readLong(4);
                        int eventType = byteBuffer.readInt(1);
                        long serverId2 = byteBuffer.readLong(4);
                        long eventSize = byteBuffer.readLong(4);
                        long logPos = byteBuffer.readLong(4);
                        int flags = byteBuffer.readInt(2);
                        System.out.printf("Head: timestamp=%d, eventType=%s, serverId=%d, eventSize=%d, logPos=%d, flags=%d\n",
                                timestamp, EventType.byCode(eventType), serverId2, eventSize, logPos, flags);

                        if(EventType.ROTATE_EVENT == EventType.byCode(eventType)) {
                            long binlogPos = byteBuffer.readLong(8);
                            int s = packetLen-1-8-4-1-4-4-4-2; //byteBuffer.remaining();
                            String binlogFile = new String(byteBuffer.readBytes(s));
                            System.out.printf("RotateEvent: binlogPos=%d, binlogFile=%s\n", binlogPos, binlogFile);
                        }

                        if(byteBuffer.hasRemaining()) {
                            System.out.println("packetLen:  " + byteBuffer.readInt(3));
                            byteBuffer.skip(1);
                            if(byteBuffer.peekFirst() == 0XFF) {
                                byteBuffer.readInt(1); //head
                                byteBuffer.readInt(2); // err code
                                if((clientCapabilities & CapabilityFlags.CLIENT_PROTOCOL_41) == CapabilityFlags.CLIENT_PROTOCOL_41) {
                                    byteBuffer.readString(1); //SqlStateMarker
                                    byteBuffer.readString(5); //SQLState
                                }
                                System.out.println(byteBuffer.readString(byteBuffer.remaining()));
                            }
                        }

                    } else if (head == 0XFF) { //ERR
                        String msg = byteBuffer.readString(packetLen-1);
                        System.out.println(">> ERR msg: " + msg);
                    }

                    System.out.println("remainingData: " + Arrays.toString(byteBuffer.remainingData()));


                }

            }
        }
        socket.close();
    }

    static class Entry {
        String key;
        String value;
        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }
        @Override
        public String toString() {
            return "Entry{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}