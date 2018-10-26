package com.stony.mysql.event;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Arrays;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 上午10:28
 * @since 2018/10/18
 */
public class BinlogEvent {

    @JsonIgnore
    int code;
    @JsonIgnore
    byte[] checksum;

    EventHeader header;
    Event event;

    public BinlogEvent(EventHeader header, Event event) {
        this.header = header;
        this.event = event;
    }

    public boolean hasErr() {
        return code == 0XFF;
    }
    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public byte[] getChecksum() {
        return checksum;
    }

    public void setChecksum(byte[] checksum) {
        this.checksum = checksum;
    }

    public EventHeader getHeader() {
        return header;
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public String toString() {
        return "BinlogEvent{" +
                "code=" + code +
                ", checksum=" + Arrays.toString(checksum) +
                ", header=" + header +
                ", event=" + event +
                "}";
    }

    public static interface Event {
    }
}