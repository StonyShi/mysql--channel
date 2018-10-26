package com.stony.mysql.event;

/**
* @author stony
* @since 2018/10/18
*/
public class XidEvent implements BinlogEvent.Event{
    long xid;

    public XidEvent(long xid) {
        this.xid = xid;
    }

    public long getXid() {
        return xid;
    }

    @Override
    public String toString() {
        return "XidEvent{" +
                "xid=" + xid +
                '}';
    }
}