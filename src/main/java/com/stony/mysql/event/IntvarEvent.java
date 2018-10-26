package com.stony.mysql.event;

/**
* @author stony
* @since 2018/10/18
*/
public class IntvarEvent implements BinlogEvent.Event{
    IntvarEventType type;
    long value;

    public IntvarEvent(IntvarEventType type, long value) {
        this.type = type;
        this.value = value;
    }

    public IntvarEventType getType() {
        return type;
    }


    public long getValue() {
        return value;
    }

    public static IntvarEvent of(int code, long value) {
        return new IntvarEvent(
                IntvarEvent.IntvarEventType.byCode(code),
                value);
    }

    @Override
    public String toString() {
        return "IntvarEvent{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }

    public static enum IntvarEventType {
        INVALID_INT_EVENT(0),LAST_INSERT_ID_EVENT(1), INSERT_ID_EVENT(2);
        int code;
        IntvarEventType(int code) {
            this.code = code;
        }
        public static IntvarEventType byCode(int code) {
            return IntvarEventType.values()[code];
        }
    }
}