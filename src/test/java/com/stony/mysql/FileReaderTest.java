package com.stony.mysql;

import com.stony.mysql.event.BinlogEvent;
import com.stony.mysql.event.EventDeserializer;
import com.stony.mysql.event.FormatDescriptionEvent;
import com.stony.mysql.io.FileConnector;
import com.stony.mysql.io.LittleByteBuffer;
import com.stony.mysql.protocol.ChecksumType;
import com.stony.mysql.protocol.ColumnType;
import com.stony.mysql.protocol.EventType;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;
import java.util.BitSet;

import static com.stony.mysql.protocol.EventType.*;
import static com.stony.mysql.event.ColumnValue.*;
import static com.stony.mysql.protocol.ColumnType.*;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.test
 *
 * @author stony
 * @version 下午3:11
 * @since 2018/10/25
 */
public class FileReaderTest {


    public static void main(String[] args) throws IOException {
        File file = new File("/Users/stony/Downloads/liangyanghe-bin.000032");


        InputStream in = new FileInputStream(file);


        int magic = FileConnector.MAGIC_HEADER.length;

        LittleByteBuffer byteBuffer = new LittleByteBuffer(1024*4);

        byteBuffer.fromInput(in, false);


        System.out.println("magic: " + Arrays.toString(FileConnector.MAGIC_HEADER));
        System.out.println("magic: " + Arrays.toString(byteBuffer.readBytes(magic)));

        System.out.println("OK");

        boolean listenEvent = true;

        EventDeserializer deserializer = new EventDeserializer(ChecksumType.CRC32);
        int enlargeSize = 0;
        BinlogEvent event = null;
        loop2:
        while (listenEvent) {
            int size = byteBuffer.fromInput(in, false, enlargeSize);
            enlargeSize = 0;
            if(size == -1) {
                System.out.println("The log finish done.");
                break loop2;
            }
            while (byteBuffer.hasRemaining()) {
                if (byteBuffer.remaining() < 19) {
                    continue loop2;
                }

                int beginIndex = byteBuffer.readIndex();

                long timestamp = byteBuffer.readLong(4);
                int eventType = byteBuffer.readInt(1);
                long serverId2 = byteBuffer.readLong(4);

                long packetLen = byteBuffer.readLong(4);

//                System.out.printf("Head: timestamp=%d, eventType=%s, serverId=%d, eventSize=%d\n",
//                                timestamp, EventType.byCode(eventType), serverId2, packetLen);
//

                if (byteBuffer.remaining() < packetLen) {
                    System.out.println("need len: " + packetLen);
                    if(packetLen > byteBuffer.remaining() || packetLen > byteBuffer.capacity()) {
                        enlargeSize = (int) packetLen;
                    }
                    byteBuffer.readIndex(beginIndex);  //rest read index to beginIndex
                    continue loop2;
                }
                byteBuffer.readIndex(beginIndex);
                byte[] xxv = null;
               try{
                   xxv = byteBuffer.readBytes((int) packetLen);
                   event = deserializer
                           .deserializerEvent(LittleByteBuffer.warp(xxv),
                                   0, new byte[0]);
//                   System.out.println(event);
               } catch (Exception e) {
//                   e.printStackTrace();
                   System.out.println("-------- error ------------------------------------------------");
                   System.out.println(event);
                   if(xxv != null) {
                        LittleByteBuffer bb = LittleByteBuffer.warp(xxv);
                        timestamp = bb.readLong(4);
                        eventType = bb.readInt(1);
                        serverId2 = bb.readLong(4);
                        long eventSize = bb.readLong(4);
                        long logPos = bb.readLong(4);
                        int flags = bb.readInt(2);
                        System.out.printf("Head: timestamp=%d, eventType=%s, serverId=%d, eventSize=%d, logPos=%d, flags=%d\n",
                                timestamp, EventType.byCode(eventType), serverId2, eventSize, logPos, flags);

                        System.out.println("xxv: " + Arrays.toString(bb.remainingData()));

                    }
               }
            }
        }
        System.out.println("------------------------");


        System.out.println("data: " + Arrays.toString(byteBuffer.remainingData()));



//        loop:
//        while (listenEvent) {
//            byteBuffer.fromInput(in, false);
//            while (byteBuffer.remaining() >= 19) {
//                long timestamp = byteBuffer.readLong(4);
//                int eventType = byteBuffer.readInt(1);
//                long serverId2 = byteBuffer.readLong(4);
//                long eventSize = byteBuffer.readLong(4);
//                long logPos = byteBuffer.readLong(4);
//                int flags = byteBuffer.readInt(2);
//                System.out.printf("Head: timestamp=%d, eventType=%s, serverId=%d, eventSize=%d, logPos=%d, flags=%d\n",
//                        timestamp, EventType.byCode(eventType), serverId2, eventSize, logPos, flags);
//
//                if(EventType.byCode(eventType) == FORMAT_DESCRIPTION_EVENT) {
//                    LittleByteBuffer bb = LittleByteBuffer.warp(byteBuffer.readBytes((int)eventSize-19));
//                    System.out.println(new FormatDescriptionEvent(
//                            bb.readInt(2),
//                            bb.readString(50).trim(),
//                            bb.readLong(4),
//                            bb.readInt(1),
//                            bb.readString(bb.remaining())));
//                    System.out.println("data: " + Arrays.toString(bb.remainingData()));
//                } else {
//                    int packetLen = (int) eventSize - 19;
//                    if (byteBuffer.remaining() < packetLen) {
//                        continue loop;
//                    }
//                    byte[] xx = byteBuffer.readBytes((int) eventSize - 19);
//                    System.out.println("data: " + Arrays.toString(xx));
//                }
//            }
//        }
//
//
//        System.out.println("data: " + Arrays.toString(byteBuffer.remainingData()));

    }

    @Test
    public void test_decimal() {

        byte[] xxv = new byte[]{-17, 55, 0, 0, 0, 0, 1, 0, 6, -1, -64, 80, -115, 0, 0, 68, -51, -5, 2, 0, 0, 0, 0, 19, 0, 54, 54, 48, 53, 56, 49, 53, 52, 53, 50, 49, 54, 54, 56, 55, 50, 50, 48, 50, 9, 0, 0, 0, -128, 0, 0, 0, 0, -113, -91, -2, -68, 90, 18, 0, 0, 39, -7, 85, -40};

        LittleByteBuffer byteBuffer = LittleByteBuffer.warp(xxv);

        long tid = byteBuffer.readLong(6);
        int flag = byteBuffer.readInt(2);

        int columnCount = (int) byteBuffer.readEncodedInteger();

        BitSet bi = byteBuffer.readBitSet(columnCount, true);

        System.out.println(columnCount);
        System.out.println(bi);

        byte[] columnMetaDef = {-128, 1, 10, 2};
        int[] clumnMeta= new int[]{0, 0, 384, 0, 522, 0};


        BitSet nullColumns = byteBuffer.readBitSet(columnCount, true);

        System.out.println(nullColumns);

        //columnDef=[3, 8, 15, 3, -10, 12],
        //[LONG, LONGLONG, VARCHAR, LONG, NEWDECIMAL, DATETIME]


        System.out.println(byteBuffer.readLong(4));

        System.out.println(byteBuffer.readLong(8));

        System.out.println(byteBuffer.readInt(2));
        System.out.println(byteBuffer.readString(19));

        System.out.println(byteBuffer.readLong(4));

        final int precision = 522 & 0xFF;
        final int scale = 522 >> 8;

        System.out.println("NEWDECIMAL");
        System.out.println(precision);
        System.out.println(scale);

        int decimalLength = getDecimalBinarySize(precision, scale);
//        System.out.println("decimal " + Arrays.toString(byteBuffer.readBytes(decimalLength)));
        System.out.println(toDecimal(precision, scale, byteBuffer.readBytes(decimalLength)));;


        System.out.println(toDatetime(byteBuffer.readLong(8)));

        System.out.println("data: " + Arrays.toString(byteBuffer.remainingData()));





    }

    @Test
    public void test_string(){

        byte[] xxv = new byte[]{30, 56, 0, 0, 0, 0, 1, 0, 2, -1, -4, 4, 100, 48, 48, 49, 9, 77, 97, 114, 107, 101, 116, 105, 110, 103, -4, 4, 100, 48, 48, 50, 7, 70, 105, 110, 97, 110, 99, 101, -4, 4, 100, 48, 48, 51, 15, 72, 117, 109, 97, 110, 32, 82, 101, 115, 111, 117, 114, 99, 101, 115, -4, 4, 100, 48, 48, 52, 10, 80, 114, 111, 100, 117, 99, 116, 105, 111, 110, -4, 4, 100, 48, 48, 53, 11, 68, 101, 118, 101, 108, 111, 112, 109, 101, 110, 116, -4, 4, 100, 48, 48, 54, 18, 81, 117, 97, 108, 105, 116, 121, 32, 77, 97, 110, 97, 103, 101, 109, 101, 110, 116, -4, 4, 100, 48, 48, 55, 5, 83, 97, 108, 101, 115, -4, 4, 100, 48, 48, 56, 8, 82, 101, 115, 101, 97, 114, 99, 104, -4, 4, 100, 48, 48, 57, 16, 67, 117, 115, 116, 111, 109, 101, 114, 32, 83, 101, 114, 118, 105, 99, 101, -21, -109, -97, -33};

        LittleByteBuffer byteBuffer = LittleByteBuffer.warp(xxv);

        long tid = byteBuffer.readLong(6);
        int flag = byteBuffer.readInt(2);

        int columnCount = (int) byteBuffer.readEncodedInteger();

        BitSet bi = byteBuffer.readBitSet(columnCount, true);

        System.out.println(columnCount);
        System.out.println(bi);

        byte[] olumnMetaDef = {-2, 12, 120, 0};
        int[] clumnMeta= new int[]{3326, 120};


        BitSet nullColumns = byteBuffer.readBitSet(columnCount, true);

        System.out.println(nullColumns);

        System.out.println("data : " + Arrays.toString(byteBuffer.remainingData()));
        System.out.println("data : " + new String(byteBuffer.remainingData()));

        while ((byteBuffer.remaining() - 4) > 0) {
            for (int i = 0; i < columnCount; i++) {
                int s = byteBuffer.readInt(1);
                System.out.print(s);
                System.out.print("=");
                System.out.print(byteBuffer.readString(s));

                System.out.print(",");
                s = byteBuffer.readInt(1);
                System.out.print(s);
                System.out.print("=");
                System.out.print(byteBuffer.readString(s));
                System.out.println();
                if((byteBuffer.remaining() - 4) == 0) {
                    break;
                }
                System.out.println("skip =  " + (byteBuffer.peekByte()));
                System.out.println("skip =  " + (byteBuffer.readByte() &0xFF));

            }
        }
        System.out.println("data: " + Arrays.toString(byteBuffer.remainingData()));



        //[STRING, VARCHAR]

        int meta = 3326;

        System.out.println("--------");
        if (meta >= 256) {
            int meta0 = meta >> 8, meta1 = meta & 0xFF;

            System.out.println(meta0);
            System.out.println(meta1);

            if ((meta0 & 0x30) != 0x30) {
                System.out.println("--------");

                int length = meta1 | (((meta0 & 0x30) ^ 0x30) << 4);

                System.out.println(length);
            }
        }
//
//        final int meta0 = meta >> 8;
//        final int meta1 = meta & 0xFF;
//
//
//        if ((meta0 & 0x30) != 0x30) {
//            System.out.println("--------");
//
//            int typeCode = meta0 | 0x30;
//            System.out.println(typeCode);
//
//            System.out.println(ColumnType.byCode(typeCode));
//
//            int length = meta1 | (((meta0 & 0x30) ^ 0x30) << 4);
//
//            System.out.println(length);
//
//            System.out.println(byteBuffer.readInt(2));
//        }

    }

    @Test
    public void test_enum(){

        byte[] xxv = new byte[]{32, 56, 0, 0, 0, 0, 0, 0, 6, -1, -64, 17, 39, 0, 0, 34, 67, 15, 6, 71, 101, 111, 114, 103, 105, 7, 70, 97, 99, 101, 108, 108, 111, 1, -38, -124, 15, -64, 18, 39, 0, 0, -62, 88, 15, 7, 66, 101, 122, 97, 108, 101, 108, 6, 83, 105, 109, 109, 101, 108, 2, 117, -125, 15, -64, 19, 39, 0, 0, -125, 79, 15, 5, 80, 97, 114, 116, 111, 7, 66, 97, 109, 102, 111, 114, 100, 1, 28, -123, 15, -64, 20, 39, 0, 0, -95, 68, 15, 9, 67, 104, 105, 114, 115, 116, 105, 97, 110, 7, 75, 111, 98, 108, 105, 99, 107, 1, -127, -123, 15, -64, 21, 39, 0, 0, 53, 70, 15, 7, 75, 121, 111, 105, 99, 104, 105, 8, 77, 97, 108, 105, 110, 105, 97, 107, 1, 44, -117, 15, -64, 22, 39, 0, 0, -108, 66, 15, 6, 65, 110, 110, 101, 107, 101, 7, 80, 114, 101, 117, 115, 105, 103, 2, -62, -118, 15, -64, 23, 39, 0, 0, -73, 74, 15, 7, 84, 122, 118, 101, 116, 97, 110, 9, 90, 105, 101, 108, 105, 110, 115, 107, 105, 2, 74, -118, 15, -64, 24, 39, 0, 0, 83, 76, 15, 6, 83, 97, 110, 105, 121, 97, 8, 75, 97, 108, 108, 111, 117, 102, 105, 1, 47, -107, 15, -64, 25, 39, 0, 0, -109, 64, 15, 6, 83, 117, 109, 97, 110, 116, 4, 80, 101, 97, 99, 2, 82, -126, 15, -64, 26, 39, 0, 0, -63, 86, 15, 9, 68, 117, 97, 110, 103, 107, 97, 101, 119, 8, 80, 105, 118, 101, 116, 101, 97, 117, 2, 24, -117, 15, -64, 27, 39, 0, 0, 103, 67, 15, 4, 77, 97, 114, 121, 5, 83, 108, 117, 105, 115, 2, 54, -116, 15, -64, 28, 39, 0, 0, 68, 81, 15, 8, 80, 97, 116, 114, 105, 99, 105, 111, 9, 66, 114, 105, 100, 103, 108, 97, 110, 100, 1, -110, -111, 15, -64, 29, 39, 0, 0, -57, 86, 15, 9, 69, 98, 101, 114, 104, 97, 114, 100, 116, 6, 84, 101, 114, 107, 107, 105, 1, 84, -125, 15, -64, 30, 39, 0, 0, 76, 72, 15, 5, 66, 101, 114, 110, 105, 5, 71, 101, 110, 105, 110, 1, 107, -122, 15, -64, 31, 39, 0, 0, 19, 79, 15, 8, 71, 117, 111, 120, 105, 97, 110, 103, 9, 78, 111, 111, 116, 101, 98, 111, 111, 109, 1, -30, -122, 15, -64, 32, 39, 0, 0, -94, 82, 15, 8, 75, 97, 122, 117, 104, 105, 116, 111, 11, 67, 97, 112, 112, 101, 108, 108, 101, 116, 116, 105, 1, 59, -106, 15, -64, 33, 39, 0, 0, -26, 76, 15, 9, 67, 114, 105, 115, 116, 105, 110, 101, 108, 9, 66, 111, 117, 108, 111, 117, 99, 111, 115, 2, 3, -109, 15, -64, 34, 39, 0, 0, -45, 68, 15, 8, 75, 97, 122, 117, 104, 105, 100, 101, 4, 80, 101, 104, 97, 2, -125, -122, 15, -64, 35, 39, 0, 0, 55, 66, 15, 7, 76, 105, 108, 108, 105, 97, 110, 7, 72, 97, 100, 100, 97, 100, 105, 1, -98, -98, 15, -64, 36, 39, 0, 0, -104, 65, 15, 6, 77, 97, 121, 117, 107, 111, 7, 87, 97, 114, 119, 105, 99, 107, 1, 58, -114, 15, -64, 37, 39, 0, 0, 84, 80, 15, 5, 82, 97, 109, 122, 105, 4, 69, 114, 100, 101, 1, 74, -120, 15, -64, 38, 39, 0, 0, -24, 64, 15, 6, 83, 104, 97, 104, 97, 102, 6, 70, 97, 109, 105, 108, 105, 1, 22, -105, 15, -64, 39, 39, 0, 0, 61, 67, 15, 5, 66, 111, 106, 97, 110, 10, 77, 111, 110, 116, 101, 109, 97, 121, 111, 114, 2, -111, -117, 15, -64, 40, 39, 0, 0, 37, 77, 15, 7, 83, 117, 122, 101, 116, 116, 101, 6, 80, 101, 116, 116, 101, 121, 2, -77, -102, 15, -64, 41, 39, 0, 0, 95, 77, 15, 9, 80, 114, 97, 115, 97, 100, 114, 97, 109, 6, 72, 101, 121, 101, 114, 115, 1, 17, -121, 15, -64, 42, 39, 0, 0, -125, 66, 15, 8, 89, 111, 110, 103, 113, 105, 97, 111, 8, 66, 101, 114, 122, 116, 105, 115, 115, 1, 116, -106, 15, -64, 43, 39, 0, 0, -22, 84, 15, 6, 68, 105, 118, 105, 101, 114, 7, 82, 101, 105, 115, 116, 97, 100, 2, -25, -118, 15, -64, 44, 39, 0, 0, 122, 87, 15, 8, 68, 111, 109, 101, 110, 105, 99, 107, 8, 84, 101, 109, 112, 101, 115, 116, 105, 1, 86, -113, 15, -64, 45, 39, 0, 0, -115, 73, 15, 5, 79, 116, 109, 97, 114, 6, 72, 101, 114, 98, 115, 116, 1, 116, -125, 15, -64, 46, 39, 0, 0, -18, 76, 15, 5, 69, 108, 118, 105, 115, 7, 68, 101, 109, 101, 121, 101, 114, 1, 81, -108, 15, -64, 47, 39, 0, 0, 59, 78, 15, 7, 75, 97, 114, 115, 116, 101, 110, 6, 74, 111, 115, 108, 105, 110, 1, 33, -113, 15, -64, 48, 39, 0, 0, 9, 81, 15, 5, 74, 101, 111, 110, 103, 7, 82, 101, 105, 115, 116, 97, 100, 2, -44, -116, 15, -64, 49, 39, 0, 0, 110, 73, 15, 4, 65, 114, 105, 102, 5, 77, 101, 114, 108, 111, 1, 114, -122, 15, -64, 50, 39, 0, 0, -99, 85, 15, 5, 66, 97, 100, 101, 114, 4, 83, 119, 97, 110, 1, 53, -119, 15, -64, 51, 39, 0, 0, 72, 66, 15, 5, 65, 108, 97, 105, 110, 9, 67, 104, 97, 112, 112, 101, 108, 101, 116, 1, 37, -119, 15, -64, 52, 39, 0, 0, 10, 79, 15, 10, 65, 100, 97, 109, 97, 110, 116, 105, 111, 115, 9, 80, 111, 114, 116, 117, 103, 97, 108, 105, 1, 35, -112, 15, 22, 1, 48, 54};

        LittleByteBuffer byteBuffer = LittleByteBuffer.warp(xxv);

        long tid = byteBuffer.readLong(6);
        int flag = byteBuffer.readInt(2);

        int columnCount = (int) byteBuffer.readEncodedInteger();

        BitSet bi = byteBuffer.readBitSet(columnCount, true);

        System.out.println(columnCount);
        System.out.println(bi);

        byte[] columnMetaDef = {42, 0, 48, 0, -9, 1};

        int[] clumnMeta= new int[]{0, 0, 42, 48, 503, 0};


        BitSet nullColumns = byteBuffer.readBitSet(columnCount, true);

        System.out.println(nullColumns);
        System.out.println("------");

        //[LONG, DATE, VARCHAR, VARCHAR, STRING, DATE]




        while (byteBuffer.remaining() - 4 > 0) {
            System.out.print(byteBuffer.readLong(4));
            System.out.print(",");

            System.out.print(toDate(byteBuffer.readInt(3)));
            System.out.print(",");


            int s = byteBuffer.readInt(1);
            System.out.print(byteBuffer.readString(s));
            System.out.print(",");


            s = byteBuffer.readInt(1);
            System.out.print(byteBuffer.readString(s));
            System.out.print(",");



            System.out.print(byteBuffer.readInt(1));
            System.out.print(",");

            System.out.print(toDate(byteBuffer.readInt(3)));
            System.out.println(",");
            byteBuffer.skip(1);

        }



        int meta = 503;

        System.out.println("-------");


        if (meta >= 256) {
            int meta0 = meta >> 8, meta1 = meta & 0xFF;
            System.out.println(meta0);
            System.out.println(meta1);

            System.out.println("-------");
            if ((meta0 & 0x30) != 0x30) {
                System.out.println(meta0 | 0x30);
                System.out.println("--------");


                int length = meta1 | (((meta0 & 0x30) ^ 0x30) << 4);

                System.out.println(length);


            }
        }
    }

    @Test
    public void test_398(){
        //0, 0, 4000, 0, 0, 1, 0, 1, 0
        byte[] cc =  {-96, 15, 1, 0, 1, 0, -96, 15, -96, 15};
        ColumnType[] types = {LONGLONG, LONGLONG, VARCHAR, BIT, BIT, VARCHAR, LONG, VARCHAR, LONGLONG};
        System.out.println(Arrays.toString(EventDeserializer.resolveColumnMetaDef(cc, types)));
        System.out.println(Arrays.toString(EventDeserializer.resolveColumnMeta(cc, types)));


        //3326, 120
        ColumnType[] tt = {STRING, VARCHAR};
        byte[] aa = {-2, 12, 120, 0};
        System.out.println(Arrays.toString(EventDeserializer.resolveColumnMetaDef(aa, tt)));
        System.out.println(Arrays.toString(EventDeserializer.resolveColumnMeta(aa, tt)));




        System.out.println("-------");
        //0, 0, 42, 48, 503, 0
        aa = new byte[]{42, 0, 48, 0, -9, 1};
        tt= new ColumnType[]{LONG, DATE, VARCHAR, VARCHAR, STRING, DATE};

        System.out.println(Arrays.toString(EventDeserializer.resolveColumnMetaDef(aa, tt)));
        System.out.println(Arrays.toString(EventDeserializer.resolveColumnMeta(aa, tt)));


        System.out.println("-------");
        int meta = 63233;
        if (meta >= 256) {
            int meta0 = meta >> 8, meta1 = meta & 0xFF;
            System.out.println(meta0);
            System.out.println(meta1);
        }

        System.out.println("-------");
        //0, 0, 384, 0, 522, 0
        tt= new ColumnType[]{LONG, LONGLONG, VARCHAR, LONG, NEWDECIMAL, DATETIME};
        System.out.println(Arrays.toString(EventDeserializer.resolveColumnMeta(new byte[]{-128, 1, 10, 2}, tt)));
        System.out.println(Arrays.toString(EventDeserializer.resolveColumnMetaDef(new byte[]{-128, 1, 10, 2}, tt)));
//        meta = 65036;
//        if (meta >= 256) {
//            int meta0 = meta >> 8, meta1 = meta & 0xFF;
//            System.out.println(meta0);
//            System.out.println(meta1);
//        }
//
//        meta = 503;
//
//        System.out.println("-------");
//
//
//        if (meta >= 256) {
//            int meta0 = meta >> 8, meta1 = meta & 0xFF;
//            System.out.println(meta0);
//            System.out.println(meta1);
//        }

    }
    @Test
    public void test_bit(){
        byte[] xxv = new byte[]{75, 49, 0, 0, 0, 0, 1, 0, 9, -1, -1, 0, -2, 70, 1, 0, 0, 0, 0, 0, 0, 31, 0, 0, 0, 0, 0, 0, 0, 61, 0, 111, 114, 103, 46, 97, 112, 97, 99, 104, 101, 46, 104, 97, 100, 111, 111, 112, 46, 104, 105, 118, 101, 46, 113, 108, 46, 105, 111, 46, 112, 97, 114, 113, 117, 101, 116, 46, 77, 97, 112, 114, 101, 100, 80, 97, 114, 113, 117, 101, 116, 73, 110, 112, 117, 116, 70, 111, 114, 109, 97, 116, 0, 0, 56, 0, 104, 100, 102, 115, 58, 47, 47, 110, 115, 49, 47, 117, 115, 101, 114, 47, 104, 105, 118, 101, 47, 119, 97, 114, 101, 104, 111, 117, 115, 101, 47, 103, 101, 111, 46, 100, 98, 47, 103, 101, 111, 95, 111, 114, 100, 101, 114, 95, 112, 111, 115, 105, 116, 105, 111, 110, -1, -1, -1, -1, 62, 0, 111, 114, 103, 46, 97, 112, 97, 99, 104, 101, 46, 104, 97, 100, 111, 111, 112, 46, 104, 105, 118, 101, 46, 113, 108, 46, 105, 111, 46, 112, 97, 114, 113, 117, 101, 116, 46, 77, 97, 112, 114, 101, 100, 80, 97, 114, 113, 117, 101, 116, 79, 117, 116, 112, 117, 116, 70, 111, 114, 109, 97, 116, 70, 1, 0, 0, 0, 0, 0, 0, 41, -19, -11, -26};



        LittleByteBuffer byteBuffer = LittleByteBuffer.warp(xxv);

        long tid = byteBuffer.readLong(6);
        int flag = byteBuffer.readInt(2);

        int columnCount = (int) byteBuffer.readEncodedInteger();

        BitSet bi = byteBuffer.readBitSet(columnCount, true);

        System.out.println(columnCount);
        System.out.println(bi);

        byte[] columnMetaDef = {-96, 15, 1, 0, 1, 0, -96, 15, -96, 15};
        int[] clumnMeta= new int[]{0, 0, 4000, 0, 0, 1,  0, 1, 0};


        int meta = 0;
        BitSet nullColumns = byteBuffer.readBitSet(columnCount, true);

        System.out.println(nullColumns);

        //LONGLONG, LONGLONG, VARCHAR, BIT, BIT, VARCHAR, LONG, VARCHAR, LONGLONG

        System.out.println("-------------");

        System.out.println(byteBuffer.readLong(8));
        System.out.println(byteBuffer.readLong(8));

        int s = byteBuffer.readInt(2);
        System.out.print(s);
        System.out.print("=");
        System.out.println(byteBuffer.readString(s));


        System.out.println(Arrays.toString(byteBuffer.remainingData()));
        meta = 0;
        int bitLength = (meta >> 8) * 8 + (meta & 0xFF);
        System.out.println("bitLength = " + bitLength);
        byteBuffer.readInt(1);
        byteBuffer.readInt(1);

        //hdfs://ns1/user/hive/warehouse/geo.db/geo_order_position
        s = byteBuffer.readInt(2);
        System.out.print(s);
        System.out.print("=");
        System.out.println(byteBuffer.readString(s));



        meta = 4000;
        if (meta >= 256) {
            int meta0 = meta >> 8, meta1 = meta & 0xFF;
            System.out.println(meta0);
            System.out.println(meta1);
        }
    }

}