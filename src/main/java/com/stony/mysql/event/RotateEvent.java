package com.stony.mysql.event;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 下午6:01
 * @since 2018/10/17
 */
public class RotateEvent implements BinlogEvent.Event {

    long position;
    String name;  //name of the next binlog

    public RotateEvent(long position, String name) {
        this.position = position;
        this.name = name;
    }

    public long getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "RotateEvent{" +
                "position=" + position +
                ", name='" + name + '\'' +
                '}';
    }
}