package com.stony.mysql.event;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 下午6:13
 * @since 2018/10/23
 */
public class EOFEvent implements BinlogEvent.Event {

    int code;

    int warnings;
    int statusFlags;

    public EOFEvent(int code, int warnings, int statusFlags) {
        this.code = code;
        this.warnings = warnings;
        this.statusFlags = statusFlags;
    }

    public int getCode() {
        return code;
    }

    public int getWarnings() {
        return warnings;
    }

    public int getStatusFlags() {
        return statusFlags;
    }

    @Override
    public String toString() {
        return "EOFEvent{" +
                "code=" + code +
                ", warnings=" + warnings +
                ", statusFlags=" + statusFlags +
                '}';
    }
}