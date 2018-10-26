package com.stony.mysql.event;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 下午1:39
 * @since 2018/10/18
 */
public class ErrorEvent implements BinlogEvent.Event{

    int code;
    String msg;
    String state;

    public ErrorEvent(int code, String msg, String state) {
        this.code = code;
        this.msg = msg;
        this.state = state;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public String getState() {
        return state;
    }


    @Override
    public String toString() {
        return "ErrorEvent{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
