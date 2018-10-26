package com.stony.mysql.event;

import com.stony.mysql.protocol.EventType;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 下午6:02
 * @since 2018/10/17
 */
public class EventHeader {

    protected long timestamp;
    protected EventType eventType;
    protected long serverId;
    protected long eventSize;
    protected long logPos;
    protected int flags;

    public long getTimestamp() {
        return timestamp;
    }

    public EventType getEventType() {
        return eventType;
    }

    public long getServerId() {
        return serverId;
    }

    public long getEventSize() {
        return eventSize;
    }

    public long getLogPos() {
        return logPos;
    }

    public int getFlags() {
        return flags;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public void setServerId(long serverId) {
        this.serverId = serverId;
    }

    public void setEventSize(long eventSize) {
        this.eventSize = eventSize;
    }

    public void setLogPos(long logPos) {
        this.logPos = logPos;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }


    public long getHeaderSize() {
        return 19;
    }


    public long getDataSize() {
        return getEventSize() - getHeaderSize();
    }


    @Override
    public String toString() {
        return "EventHeader{" +
                "timestamp=" + timestamp +
                ", eventType=" + eventType +
                ", serverId=" + serverId +
                ", eventSize=" + eventSize +
                ", dataSize=" + getDataSize() +
                ", logPos=" + logPos +
                ", flags=" + flags +
                '}';
    }
}
