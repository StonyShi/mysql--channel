package com.stony.mysql.io;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.net
 *
 * @author stony
 * @version 上午11:29
 * @since 2018/10/23
 */
public class RingByteBuffer {
    int maxSize = Integer.MAX_VALUE;

    final byte[] data;

    int writeIndex = 0;
    int readIndex = 0;

    final ReentrantLock lock;
    final Condition isFull;
    final Condition isEmpty;

    public RingByteBuffer(int maxSize) {
        this.maxSize = maxSize;
        this.data = new byte[this.maxSize];

        lock = new ReentrantLock();
        isFull = lock.newCondition();
        isEmpty = lock.newCondition();
    }

    public void put(byte[] value) {
        int len = value.length;
        if(len > 0) {
            try {
                lock.lock();

                if(capacity()-len <= 0) {
                    System.out.println("满了等待。。。");
                    isFull.await();
                }
                System.arraycopy(value, 0,  data,  this.writeIndex, len);
                this.writeIndex += len;
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                isEmpty.signal();
                lock.unlock();
            }
        }
    }

    public byte[] take(int len) {
        if (len > 0) {
            try {
                lock.lock();
                if (remaining()-len <= 0) {
                    System.out.println("空着等待。。。");
                    isEmpty.await();
                }
                byte[] result = new byte[len];

                System.arraycopy(this.data, this.readIndex, result, 0, len);

                this.readIndex += len;
                return result;
            } catch (InterruptedException e) {

            }finally {
                isFull.signal();
                lock.unlock();
            }
        }
        return new byte[0];
    }

    public final int remaining() {
        return this.writeIndex - this.readIndex;
    }

    public final int capacity(){
        return this.maxSize - this.writeIndex;
    }

    public static void main(String[] args){
        RingByteBuffer buffer = new RingByteBuffer(10);
        byte[] val = {1,2,3};
        buffer.put(val);
        buffer.put(val);
        buffer.put(val);
        buffer.put(val);
    }

}
