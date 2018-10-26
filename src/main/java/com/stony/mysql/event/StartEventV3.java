package com.stony.mysql.event;

/**
* @author stony
* @since 2018/10/18
*/
public class StartEventV3 implements BinlogEvent.Event{
    int binlogVersion;
    String mysqlServerVersion;
    long createTimestamp;

    public StartEventV3(int binlogVersion, String mysqlServerVersion, long createTimestamp) {
        this.binlogVersion = binlogVersion;
        this.mysqlServerVersion = mysqlServerVersion;
        this.createTimestamp = createTimestamp;
    }

    public int getBinlogVersion() {
        return binlogVersion;
    }

    public String getMysqlServerVersion() {
        return mysqlServerVersion;
    }

    public long getCreateTimestamp() {
        return createTimestamp;
    }

    @Override
    public String toString() {
        return "StartEventV3{" +
                "binlogVersion=" + binlogVersion +
                ", mysqlServerVersion='" + mysqlServerVersion + '\'' +
                ", createTimestamp=" + createTimestamp +
                '}';
    }
}