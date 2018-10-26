package com.stony.mysql.event;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 上午9:38
 * @since 2018/10/19
 */
public class WriteRowsEvent extends RowsEvent {

    public WriteRowsEvent(int version) {
        super(version);
    }

    @Override
    public String toString() {
        return "WriteRowsEvent{" +
                super.toString() +
                "}";
    }
}
