package com.stony.mysql.event;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *<pre>
 2                binlog-version
 string[50]       mysql-server version
 4                create timestamp
 1                event header length
 string[p]        event type header lengths
 *</pre>
 * @author stony
 * @version 下午1:49
 * @since 2018/10/18
 * @see <a href="https://dev.mysql.com/doc/internals/en/format-description-event.html">
 *     FORMAT_DESCRIPTION_EVENT</a>
 */
public class FormatDescriptionEvent implements BinlogEvent.Event{
    int binlogVersion; //2
    String serverVersion; //50
    long createTimestamp; //4
    int eventHeaderLength; //1
    @JsonIgnore
    String eventTypeHeaderLengths; //p

    public FormatDescriptionEvent(int binlogVersion, String serverVersion, long createTimestamp, int eventHeaderLength, String eventTypeHeaderLengths) {
        this.binlogVersion = binlogVersion;
        this.serverVersion = serverVersion;
        this.createTimestamp = createTimestamp;
        this.eventHeaderLength = eventHeaderLength;
        this.eventTypeHeaderLengths = eventTypeHeaderLengths;
    }

    public int getBinlogVersion() {
        return binlogVersion;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    public int getEventHeaderLength() {
        return eventHeaderLength;
    }

    public String getEventTypeHeaderLengths() {
        return eventTypeHeaderLengths;
    }

    public void setBinlogVersion(int binlogVersion) {
        this.binlogVersion = binlogVersion;
    }

    public void setServerVersion(String serverVersion) {
        this.serverVersion = serverVersion;
    }

    public void setCreateTimestamp(long createTimestamp) {
        this.createTimestamp = createTimestamp;
    }

    public void setEventHeaderLength(int eventHeaderLength) {
        this.eventHeaderLength = eventHeaderLength;
    }

    public void setEventTypeHeaderLengths(String eventTypeHeaderLengths) {
        this.eventTypeHeaderLengths = eventTypeHeaderLengths;
    }

    @Override
    public String toString() {
        return "FormatDescriptionEvent{" +
                "binlogVersion=" + binlogVersion +
                ", serverVersion='" + serverVersion + '\'' +
                ", createTimestamp=" + createTimestamp +
                ", eventHeaderLength=" + eventHeaderLength +
//                ", eventTypeHeaderLengths='" + eventTypeHeaderLengths + '\'' +
                '}';
    }
}
