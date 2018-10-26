package com.stony.mysql.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.BitSet;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.net
 *
 * @author stony
 * @version 下午6:21
 * @since 2018/10/10
 */
public class LittleByteBuffer {
    private static final Logger logger = LoggerFactory.getLogger(LittleByteBuffer.class);

    /**
     * Default initial capacity.
     */
    static final int DEFAULT_CAPACITY = 64;
    static final byte ZERO = 0;
    byte[] data;
    int writeIndex = 0;
    int readIndex = 0;
    int capacity;

    public LittleByteBuffer() {
        this(DEFAULT_CAPACITY);
    }

    public LittleByteBuffer(int initialCapacity) {
        capacity = initialCapacity;
        this.data = new byte[initialCapacity];
    }

    public LittleByteBuffer(byte[] data) {
        this.capacity = data.length;
        this.writeIndex = data.length;
        this.data = data;
    }

    public LittleByteBuffer writerZero(int len) {
        for (int i = 0; i < len; i++) {
            writerByte(ZERO);
        }
        return this;
    }
    public void writerStringEndZero(String str) {
        writerBytes(str.getBytes());
//        for(byte b : str.getBytes()){
//            writerByte(b);
//        }
        writerByte(ZERO);
    }

    public int fromInput(InputStream in) throws IOException {
        return fromInput(in, true);
    }

    public int fromInput(InputStream in, boolean restOffset, int enlargeSize) throws IOException {
        if (enlargeSize > 0 && (enlargeSize > remaining() || enlargeSize > capacity())) {
            enlarge(enlargeSize);
        }
        return fromInput(in, restOffset);
    }
    public int fromInput(InputStream in, boolean restOffset) throws IOException {
        if(restOffset) {
            restOffset();
        }
        return doFromInput(in, restOffset);
    }

    int doFromInput(InputStream in,  boolean restOffset) throws IOException {
        int len = -1;
        if(restOffset) {
            len = in.read(this.data);
        } else {
            byte[] remainingData = remainingData();
            restOffset();
            writerBytes(remainingData);
            if(logger.isDebugEnabled()) {
                logger.debug("writeIndex={}, readIndex={}, capacity={}, remaining={}",
                        writeIndex,readIndex,(this.capacity - this.writeIndex), (remainingData.length));
            }
            long time = System.currentTimeMillis();
//            System.out.println("writeIndex=" + this.writeIndex +
//                    ",readIndex=" + this.readIndex +
//                    ",capacity=" + (this.capacity - this.writeIndex) +
//                    ",remaining:="+ remainingData.length);
            len = in.read(this.data, this.writeIndex, (this.capacity - this.writeIndex));
            if(logger.isDebugEnabled()) {
                logger.debug("From Read size: {}, cost: {}", len, (System.currentTimeMillis() - time));
            }
//            System.out.println("read len: " + len);
        }
        if (len != -1) {
            this.writeIndex += len;
        }
        return len;
    }



    /**
     * 一个字节
     *
     * @param value
     * @return
     */
    public LittleByteBuffer writerByte(byte value) {
        int pos = this.writeIndex;
        if (pos + 1 > data.length) {
            enlarge(1);
        }
        byte[] _data = this.data;
        _data[pos++] = value;
        this.writeIndex = pos;
        return this;
    }

    /**
     * 写入2个字节
     *
     * @param value
     * @return
     */
    public LittleByteBuffer writerShort(short value) {
        int pos = this.writeIndex;
        if (pos + 2 > data.length) {
            enlarge(2);
        }
        byte[] _data = this.data;
        _data[pos++] = (byte) (value >>> 0);
        _data[pos++] = (byte) (value >>> 8);
        this.writeIndex = pos;
        return this;
    }

    /**
     * 写入4个字节
     *
     * @param value
     * @return
     */
    public LittleByteBuffer writerInt(int value) {
        int pos = this.writeIndex;
        if (pos + 4 > data.length) {
            enlarge(4);
        }
        byte[] _data = this.data;
        _data[pos++] = (byte) (value >>> 0);
        _data[pos++] = (byte) (value >>> 8);
        _data[pos++] = (byte) (value >>> 16);
        _data[pos++] = (byte) (value >>> 24);
        this.writeIndex = pos;
        return this;
    }
    public LittleByteBuffer writerInt(int value, int len) {
        int pos = this.writeIndex;
        if (pos + len > data.length) {
            enlarge(len);
        }
        byte[] _data = this.data;
        for (int i = 0; i < len; i++) {
            _data[pos++] = (byte) (value >>> (i << 3));
        }
        this.writeIndex = pos;
        return this;
    }
    public LittleByteBuffer writerLong(long value, int len) {
        int pos = this.writeIndex;
        if (pos + len > data.length) {
            enlarge(len);
        }
        byte[] _data = this.data;
        for (int i = 0; i < len; i++) {
            _data[pos++] = (byte) (value >>> (i << 3));
        }
        this.writeIndex = pos;
        return this;
    }

    /**
     * 写入8个字节
     *
     * @param value
     * @return
     */
    public LittleByteBuffer writerLong(long value) {
        int pos = this.writeIndex;
        if (pos + 8 > data.length) {
            enlarge(8);
        }
        byte[] _data = this.data;
        _data[pos++] = (byte) (value >>> 0);
        _data[pos++] = (byte) (value >>> 8);
        _data[pos++] = (byte) (value >>> 16);
        _data[pos++] = (byte) (value >>> 24);
        _data[pos++] = (byte) (value >>> 32);
        _data[pos++] = (byte) (value >>> 40);
        _data[pos++] = (byte) (value >>> 48);
        _data[pos++] = (byte) (value >>> 56);
        this.writeIndex = pos;
        return this;
    }

    public LittleByteBuffer writerBytes(byte[] value, int off, int len) {
        int pos = this.writeIndex;
        if (pos + len > data.length) {
            enlarge(len);
        }
        byte[] _data = this.data;
        System.arraycopy(value, off, _data, pos, len);
        pos += len;
        this.writeIndex = pos;
        return this;
    }

    public LittleByteBuffer writerBytes(byte[] value) {
        if(value == null || value.length == 0) {
            return this;
        }
        int pos = this.writeIndex;
        int len = value.length;
        if (pos + len > data.length) {
            enlarge(len);
        }
        byte[] _data = this.data;
        System.arraycopy(value, 0, _data, pos, len);
        pos += len;
        this.writeIndex = pos;
        return this;
    }

    public LittleByteBuffer writerString(String value) {
        return writerBytes(value.getBytes());
    }
    public LittleByteBuffer writerUTF8(String value) {
        return writerBytes(value.getBytes(UTF_8));
    }

    public void skip(int len){
        this.readIndex += len;
    }

    public byte peekByte() {
        return this.data[this.readIndex];
    }
    public byte[] peekBytes(int len) {
        byte[] result = new byte[len];
        System.arraycopy(this.data, this.readIndex, result, 0, len);
        return result;
    }
    public int peekFirst() {
        return this.data[this.readIndex] & 0xFF;
    }
    public int readByte2Int() {
        return readByte() & 0xFF;
    }
    public byte readByte() {
        int pos = (this.readIndex);
        this.readIndex = pos + 1;
        byte[] _data = this.data;
        return _data[pos--];
    }
    public int byte2Int(byte a){
        return a & 0xFF;
    }

    /**
     * 读取2个字节
     * @return
     */
    public short readShort() {
        int pos = (this.readIndex + 1);
        this.readIndex = pos + 1;
        byte[] _data = this.data;
//        return (short) (_data[pos--] & 0xFF | (_data[pos--] & 0xFF) << 8);
        return (short) (_data[pos--] & 0xFF << 8 | (_data[pos--] & 0xFF));
    }

    static private int makeInt(byte b3, byte b2, byte b1, byte b0) {
        return (((b3       ) << 24) |
                ((b2 & 0xff) << 16) |
                ((b1 & 0xff) <<  8) |
                ((b0 & 0xff)      ));
    }
    static private long makeLong(byte b7, byte b6, byte b5, byte b4,
                                 byte b3, byte b2, byte b1, byte b0) {
        return ((((long) b7) << 56) |
                (((long) b6 & 0xff) << 48) |
                (((long) b5 & 0xff) << 40) |
                (((long) b4 & 0xff) << 32) |
                (((long) b3 & 0xff) << 24) |
                (((long) b2 & 0xff) << 16) |
                (((long) b1 & 0xff) << 8) |
                (((long) b0 & 0xff)));
    }


    public int readInt(int len) {
        int size = 4;
        byte[] src = getFixBytes(len, size);
        return makeInt(src[3], src[2], src[1], src[0]);
    }

    /**
     * 读取4个字节
     * @return
     */
    public int readInt() {
        int pos = (this.readIndex + 3);
        this.readIndex = pos + 1;
        byte[] _data = this.data;
        return   _data[pos--] & 0xFF  << 24|
                (_data[pos--] & 0xFF) << 16 |
                (_data[pos--] & 0xFF) << 8 |
                (_data[pos--] & 0xFF);
    }



    public long readLong(int len) {
        int size = 8;
        byte[] src = getFixBytes(len, size);
        return makeLong(src[7], src[6], src[5], src[4], src[3], src[2], src[1], src[0]);
    }
    /**
     * 读取8个字节
     *
     * @return
     */
    public long readLong() {
        int pos = (this.readIndex + 7);
        this.readIndex = pos + 1;
        byte[] _data = this.data;
        return  (_data[pos--] & 0xFFL) << 56 |
                (_data[pos--] & 0xFFL) << 48 |
                (_data[pos--] & 0xFFL) << 40 |
                (_data[pos--] & 0xFFL) << 32 |
                (_data[pos--] & 0xFFL) << 24 |
                (_data[pos--] & 0xFFL) << 16 |
                (_data[pos--] & 0xFFL) << 8 |
                (_data[pos--] & 0xFFL);
    }

    public long readLong2() {
        final byte[] b = this.data;
        long l = (long) (b[this.readIndex++] & 0xff);
        l |= (long) (b[this.readIndex++] & 0xff) << 8;
        l |= (long) (b[this.readIndex++] & 0xff) << 16;
        l |= (long) (b[this.readIndex++] & 0xff) << 24;
        l |= (long) (b[this.readIndex++] & 0xff) << 32;
        l |= (long) (b[this.readIndex++] & 0xff) << 40;
        l |= (long) (b[this.readIndex++] & 0xff) << 48;
        l |= (long) (b[this.readIndex++] & 0xff) << 56;
        return l;
    }
    byte[] getFixBytes(int len, int size) {
        byte[] src = readBytes(len);
        if (len < size) {
            byte[] temp = new byte[size];
            System.arraycopy(src, 0, temp, 0, len);
            //地位 fill ZERO
            for (int i = len; i < size; i++) {
                temp[i] = 0;
            }
            src = temp;
        }
        return src;
    }
    byte[] getFixBigBytes(int len, int size) {
        byte[] src = readBytes(len);
        if (len < size) {
            byte[] temp = new byte[size];
            System.arraycopy(src, 0, temp, (size-len), len);
            //高位 fill ZERO
            for (int i = 0; i < (size-len); i++) {
                temp[i] = 0;
            }
            src = temp;
        }
//        System.out.println(Arrays.toString(src));
        return src;
    }
    /** BigEndian **/
    public int readIntBig(int len) {
        int size = 4;
        byte[] src = getFixBigBytes(len, size);
        return makeInt(src[0], src[1], src[2], src[3]);
//        int result = 0;
//        for (int i = 0; i < len; ++i) {
//            result = (result << 8) | (readByte()& 0xFF);
//        }
//        return result;
    }
    /** BigEndian **/
    public long readLongBig(int len) {
        int size = 8;
        byte[] src = getFixBigBytes(len, size);
        return makeLong(src[0], src[1], src[2], src[3], src[4], src[5], src[6], src[7]);
//        long result = 0;
//        for (int i = 0; i < len; ++i) {
//            result = (result << 8) | (readByte()& 0xFF);
//        }
//        return result;
    }

    public String readString(int len) {
        return new String(readBytes(len));
    }
    public String readUTF8(int len) {
        return new String(readBytes(len), UTF_8);
    }

    public byte[] readBytes(int len) {
        int pos = this.readIndex;
        byte[] _data = this.data;
        byte[] result = new byte[len];
        System.arraycopy(_data, pos, result, 0, len);
        pos += len;
        this.readIndex = pos;
        return result;
    }
    public String readStringEndZero() {
        int pos = this.readIndex;
        int count = this.writeIndex;
        int len = 0;
        byte[] _data = this.data;
        for (int b; (b = _data[pos]) != 0; ) {
            len++;
            pos++;
        }
        String str = readString(len);
        //skip zero
        skip(1);
        return str;

    }

    public final int remaining() {
        return writeIndex - readIndex;
    }
    public final boolean hasRemaining() {
        return readIndex < writeIndex;
    }
    public byte[] remainingData() {
        if(hasRemaining()) {
            return Arrays.copyOfRange(this.data, this.readIndex, this.writeIndex);
        }
        return new byte[0];
    }
    public byte[] copyData(int begin, int end){
        if(hasRemaining()) {
            return Arrays.copyOfRange(this.data, begin, end);
        }
        return new byte[0];
    }
    public byte[] copyData(int begin){
        if(hasRemaining()) {
            return copyData(begin, this.writeIndex);
        }
        return new byte[0];
    }
    public byte[] realData() {
        return Arrays.copyOfRange(this.data, 0, this.writeIndex);
    }


    static final int MAXIMUM_CAPACITY = Integer.MAX_VALUE;
    static final int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= MAXIMUM_CAPACITY) ? MAXIMUM_CAPACITY : n + 1;
    }
    final synchronized void enlarge(final int size) {
        int newLen = this.data.length * 2;
        int newLen2 = this.writeIndex + (size < 1024 ? tableSizeFor(size) : size);
        byte[] newData = new byte[newLen > newLen2 ? newLen : newLen2];
        this.capacity = newData.length;
        System.arraycopy(this.data, 0, newData, 0, this.writeIndex);
        this.data = newData;
    }

    /**
     * 数据包长度
     **/
    public int getLength() {
        return this.writeIndex;
    }

    /**
     * 偏移量位置
     **/
    public int size() {
        return this.writeIndex;
    }

    public byte[] getData() {
        return this.data;
    }

    public void clear() {
        restOffset();
        this.data = new byte[DEFAULT_CAPACITY];
    }

    /**
     * 重置读写指针
     **/
    public void restOffset() {
        this.writeIndex = 0;
        this.readIndex = 0;
    }


    public static LittleByteBuffer warp(byte[] data){
        return new LittleByteBuffer(data);
    }

    public static LittleByteBuffer warp(InputStream in) throws IOException {
        return warp(in, 1024);
    }
    public static LittleByteBuffer warp(InputStream in, int size) throws IOException {
        LittleByteBuffer buffer = new LittleByteBuffer(size);
        int n;
        byte[] temp = new byte[size];
        while (-1 != (n = in.read(temp))) {
            buffer.writerBytes(temp, 0, n);
        }
        return buffer;
    }

    public int capacity() {
        return this.capacity;
    }


    public long readEncodedInteger() {
        int length = data[this.readIndex++] & 0xff;
        switch (length) {
            case 251:  //251 If it is 0xfc, it is 251
                return 0xFB;
            case 252: //252 If it is 0xfc, it is followed by a 2-byte integer.
                return readInt(2);
            case 253: //253 If it is 0xfd, it is followed by a 3-byte integer.
                return readInt(3);
            case 254: //254 If it is 0xfe, it is followed by a 8-byte integer.
                return readLong();
            default:  //251 If it is < 0xfb, treat it as a 1-byte integer.
                return length;
        }
    }

    private String readLenencStr(){
        return readString(readInt(1));
    }
    public String readLengthEncodedString(){
        int len = (int) readEncodedInteger();
        return readString(len);
    }

    public BitSet readBitSet(int length, boolean bigEndian) {
        // Variable-sized. Bit-field indicating whether each column is used, one bit per column. For this field,
        // the amount of storage required for N columns is INT((N+7)/8) bytes.
        byte[] bytes = readBytes(((length + 7) >> 3));
        bytes = bigEndian ? bytes : reverse(bytes);
        BitSet result = new BitSet();
        for (int i = 0; i < length; i++) {
            if ((bytes[i >> 3] & (1 << (i % 8))) != 0) {
                result.set(i);
            }
        }
        return result;
    }

    private byte[] reverse(byte[] bytes) {
        for (int i = 0, length = bytes.length >> 1; i < length; i++) {
            int j = bytes.length - 1 - i;
            byte t = bytes[i];
            bytes[i] = bytes[j];
            bytes[j] = t;
        }
        return bytes;
    }


    public int readIndex() {
        return this.readIndex;
    }
    public void readIndex(int readIndex) {
        this.readIndex = readIndex;
    }
}