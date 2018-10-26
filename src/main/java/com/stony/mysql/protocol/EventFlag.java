package com.stony.mysql.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 *
 * @author stony
 * @version 上午9:58
 * @since 2018/10/17
 */
public enum EventFlag {

    LOG_EVENT_BINLOG_IN_USE_F(0x0001),
    LOG_EVENT_FORCED_ROTATE_F(0x0002),
    LOG_EVENT_THREAD_SPECIFIC_F(0x0004),
    LOG_EVENT_SUPPRESS_USE_F(0x0008),
    LOG_EVENT_UPDATE_TABLE_MAP_VERSION_F(0x0010),
    LOG_EVENT_ARTIFICIAL_F(0x0020),
    LOG_EVENT_RELAY_LOG_F(0x0040),
    LOG_EVENT_IGNORABLE_F(0x0080),
    LOG_EVENT_NO_FILTER_F(0x0100),
    LOG_EVENT_MTS_ISOLATE_F(0x0200);
    int code;

    EventFlag(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    private static final Map<Integer, EventFlag> INDEX_BY_CODE;

    static {
        INDEX_BY_CODE = new HashMap<Integer, EventFlag>(16);
        for (EventFlag type : values()) {
            INDEX_BY_CODE.put(type.code, type);
        }
    }

    public static EventFlag byCode(int code) {
        return INDEX_BY_CODE.get(code);
    }

}
