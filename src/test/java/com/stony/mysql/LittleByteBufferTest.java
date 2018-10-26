package com.stony.mysql;

import com.stony.mysql.io.LittleByteBuffer;
import org.junit.Test;
import org.junit.Assert;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.BitSet;

/**
 * <p>mysql-x
 * <p>com.mysql.test
 *
 * @author stony
 * @version 上午11:47
 * @since 2018/10/12
 */
public class LittleByteBufferTest {


    @Test
    public void test_bufffer() {
        LittleByteBuffer buffer = new LittleByteBuffer();

        buffer.writerInt(999, 4);
        buffer.writerInt(1299, 4);
        buffer.writerInt(100, 3);
        buffer.writerInt(20, 2);
        buffer.writerInt(300);

        Assert.assertEquals(999, buffer.readInt(4));
        Assert.assertEquals(1299, buffer.readInt());
        Assert.assertEquals(100, buffer.readInt(3));
        Assert.assertEquals(20, buffer.readInt(2));
        Assert.assertEquals(300, buffer.readInt());



        buffer.writerLong(9999, 4);
        buffer.writerLong(99999, 5);
        buffer.writerLong(99911);
        long x = System.currentTimeMillis();
        buffer.writerLong(x);

        Assert.assertEquals(9999,  buffer.readLong(4));
        Assert.assertEquals(99999, buffer.readLong(5));
        Assert.assertEquals(99911, buffer.readLong());
        Assert.assertEquals(x,     buffer.readLong());



    }

    @Test
    public void test_bit() {
        BitSet nullColumns = new BitSet();
        nullColumns.set(1);

        BitSet columns = new BitSet();
        columns.set(0);
        columns.set(1);
        columns.set(2);
        columns.set(3);


        System.out.println(columns.get(0));
        System.out.println(nullColumns.get(0));

        BitSet bitSet = new BitSet();
        bitSet.set(0);
        System.out.println(bitSet);

    }


    @Test
    public void test_big() {
        ByteBuffer bb = ByteBuffer.allocate(8);
        long x = 1540288505975L; //[0, 0, 1, 102, -96, 89, 104, 119]
        System.out.println("x: \n" + x);
        bb.order(ByteOrder.BIG_ENDIAN).putLong(x);
        System.out.println(Arrays.toString(bb.array()));


        Assert.assertArrayEquals(new byte[]{0, 0, 1, 102, -96, 89, 104, 119},     bb.array());
        Assert.assertEquals(x,     LittleByteBuffer.warp(bb.array()).readLongBig(8));


        byte[] dst = Arrays.copyOfRange(bb.array(), 2, 8);
        System.out.println(Arrays.toString(dst));

        Assert.assertArrayEquals(new byte[]{1, 102, -96, 89, 104, 119},     dst);

        Assert.assertEquals(x,     LittleByteBuffer.warp(dst).readLongBig(6));


        System.out.println();
        bb = ByteBuffer.allocate(4);
        int z = 999; //[0, 0, 3, -25]
        System.out.println("z: \n" + z);
        bb.order(ByteOrder.BIG_ENDIAN).putInt(z);
        System.out.println(Arrays.toString(bb.array()));


        Assert.assertArrayEquals(new byte[]{0, 0, 3, -25},     bb.array());
        Assert.assertEquals(z,     LittleByteBuffer.warp(bb.array()).readIntBig(4));


        dst = Arrays.copyOfRange(bb.array(), 2, 4);
        System.out.println(Arrays.toString(dst));


        Assert.assertArrayEquals(new byte[]{3, -25},     dst);
        Assert.assertEquals(z,     LittleByteBuffer.warp(dst).readIntBig(2));

    }

}