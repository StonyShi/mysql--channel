package com.stony.mysql.event;

/**
* @author stony
* @since 2018/10/18
*/
public class RandEvent implements BinlogEvent.Event{
    long seed1;
    long seed2;
    public RandEvent(long seed1, long seed2) {
        this.seed1 = seed1;
        this.seed2 = seed2;
    }

    public long getSeed1() {
        return seed1;
    }

    public long getSeed2() {
        return seed2;
    }

    @Override
    public String toString() {
        return "RandEvent{" +
                "seed1=" + seed1 +
                ", seed2=" + seed2 +
                '}';
    }
}