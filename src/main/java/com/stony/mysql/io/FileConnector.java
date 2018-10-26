package com.stony.mysql.io;

import com.stony.mysql.event.EventDeserializer;
import com.stony.mysql.protocol.ChecksumType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.io
 *
 * @author stony
 * @version 上午9:45
 * @since 2018/10/25
 */
public class FileConnector extends BaseConnector {
    private static final Logger logger = LoggerFactory.getLogger(FileConnector.class);
    public static final byte[] MAGIC_HEADER = new byte[]{(byte) 0xfe, (byte) 0x62, (byte) 0x69, (byte) 0x6e};

    InputStream in;

    ChecksumType checksumType = ChecksumType.NONE;
    EventDeserializer deserializer;
    final String binlogFilePath;


    public FileConnector(String binlogFilePath) {
        this.binlogFilePath = binlogFilePath;
    }

    @Override
    public void connect() throws XException {

    }

    public void setChecksumType(ChecksumType checksumType) {
        this.checksumType = checksumType;
    }

    @Override
    public void start() throws XException {
        try {
            File file = new File(binlogFilePath);
            if(!file.exists()) {
                throw new XException(String.format("This binlog[%s] not exists", binlogFilePath));
            }
            this.in = new BufferedInputStream(new FileInputStream(file));

            LittleByteBuffer byteBuffer = new LittleByteBuffer(1024*8);

            int size = byteBuffer.fromInput(in, true);

            byte[] magicHeader = byteBuffer.readBytes(MAGIC_HEADER.length);

            if(size == 0 || size == -1) {
                throw new XException(String.format("This binlog[%s] is null", binlogFilePath));
            }
            if (!Arrays.equals(magicHeader, MAGIC_HEADER)) {
                throw new XException(String.format("This[%s] a valid binary log", binlogFilePath));
            }
            logger.info(String.format("This[%s] a good binary log", binlogFilePath));

            this.deserializer = new EventDeserializer(checksumType);



            this.listenEvent = true;

            int checkFinish = 0;
            size = -1;
            int packetLen = 0;
            long seq = 0;
            int enlargeSize = 0;

            loop:
            while (listenEvent) {
                size = byteBuffer.fromInput(in, false, enlargeSize);
                logger.debug("-------------- BinLog ----------------------  {}", size);
                enlargeSize = 0;
                if(size == -1) {
                    logger.info(String.format("This[%s] binary log process finish.", binlogFilePath));
                    shutdown();
                    break;
                }

                while (byteBuffer.hasRemaining()) {
                    if (byteBuffer.remaining() < 19) {
                        continue loop;
                    }
                    int beginIndex = byteBuffer.readIndex();

                    byteBuffer.readLong(4); //timestamp
                    byteBuffer.readInt(1); //eventType
                    byteBuffer.readLong(4); //serverId

                    packetLen = (int) byteBuffer.readLong(4);
                    if (byteBuffer.remaining() < packetLen) {
                        byteBuffer.readIndex(beginIndex);  //rest read index to beginIndex
                        if(packetLen > byteBuffer.remaining() || packetLen > byteBuffer.capacity()) {
                            enlargeSize = (int) packetLen;
                        }
                        continue loop;
                    }
                    byteBuffer.readIndex(beginIndex);

                    if(seq >= Long.MAX_VALUE) {
                        seq = 0;
                    }
                    logger.debug(String.format("Event seq: %3d, Event packetLen: %5d, Event remaining: %5d\n",
                            seq, packetLen, byteBuffer.remaining()));
                    onEvent(deserializer.deserializerEvent(
                            LittleByteBuffer.warp(byteBuffer.readBytes((int) packetLen)), 0, new byte[0]));
                    seq++;
                }
            }

        } catch (IOException e) {
            throw new XException("read binlog file error:", e);
        }
    }


    @Override
    public void shutdown() throws XException {
        this.listenEvent = false;
        signal();
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}