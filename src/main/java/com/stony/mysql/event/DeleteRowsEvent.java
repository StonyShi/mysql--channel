package com.stony.mysql.event;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 上午9:37
 * @since 2018/10/19
 */
public class DeleteRowsEvent extends RowsEvent{

    public DeleteRowsEvent(int version) {
        super(version);
    }

    @Override
    public String toString() {
        return "DeleteRowsEvent {" +
                super.toString() +
                "}";
    }
}
