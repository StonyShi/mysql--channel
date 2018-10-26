package com.stony.mysql.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 *
 * @author stony
 * @version 上午9:46
 * @since 2018/10/17
 * @see <a href="https://dev.mysql.com/doc/internals/en/binlog-event-type.html">
 *     Binlog Event Type</a>
 * @see <a href="https://dev.mysql.com/doc/internals/en/event-meanings.html">
 *     Event Meanings</a>
 * @see <a href="https://dev.mysql.com/doc/internals/en/event-data-for-specific-event-types.html">
 *     Event Data</a>
 */
public enum EventType {

    UNKNOWN_EVENT(0x00) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    START_EVENT_V3(0x01) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    QUERY_EVENT(0x02) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    STOP_EVENT(0x03) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    ROTATE_EVENT(0x04) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    INTVAR_EVENT(0x05) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    LOAD_EVENT(0x06) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    SLAVE_EVENT(0x07) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    CREATE_FILE_EVENT(0x08) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    APPEND_BLOCK_EVENT(0x09) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    EXEC_LOAD_EVENT(0x0a) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    DELETE_FILE_EVENT(0x0b) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    NEW_LOAD_EVENT(0x0c) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    RAND_EVENT(0x0d) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    USER_VAR_EVENT(0x0e) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    FORMAT_DESCRIPTION_EVENT(0x0f) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    XID_EVENT(0x10) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    BEGIN_LOAD_QUERY_EVENT(0x11) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    EXECUTE_LOAD_QUERY_EVENT(0x12) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    TABLE_MAP_EVENT(0x13) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    WRITE_ROWS_EVENT_V0(0x14) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    UPDATE_ROWS_EVENT_V0(0x15) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    DELETE_ROWS_EVENT_V0(0x16) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    WRITE_ROWS_EVENT_V1(0x17) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    UPDATE_ROWS_EVENT_V1(0x18) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    DELETE_ROWS_EVENT_V1(0x19) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    INCIDENT_EVENT(0x1a) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    HEARTBEAT_EVENT(0x1b) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    IGNORABLE_EVENT(0x1c) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    ROWS_QUERY_EVENT(0x1d) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    WRITE_ROWS_EVENT_V2(0x1e) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    UPDATE_ROWS_EVENT_V2(0x1f) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    DELETE_ROWS_EVENT_V2(0x20) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    GTID_EVENT(0x21) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    ANONYMOUS_GTID_EVENT(0x22) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },
    PREVIOUS_GTIDS_EVENT(0x23) {
        @Override
        public boolean is(EventType other) {
            return this.code == other.code;
        }
    },

    UPDATE_ROWS_EVENT(UPDATE_ROWS_EVENT_V0.code + UPDATE_ROWS_EVENT_V1.code + UPDATE_ROWS_EVENT_V2.code) {
        @Override
        public boolean is(EventType other) {
            return other.code == UPDATE_ROWS_EVENT_V0.code
                    || other.code == UPDATE_ROWS_EVENT_V1.code
                    || other.code == UPDATE_ROWS_EVENT_V2.code;
        }
    },
    WRITE_ROWS_EVENT(WRITE_ROWS_EVENT_V0.code + WRITE_ROWS_EVENT_V1.code + WRITE_ROWS_EVENT_V2.code) {
        @Override
        public boolean is(EventType other) {
            return other.code == WRITE_ROWS_EVENT_V0.code
                    || other.code == WRITE_ROWS_EVENT_V1.code
                    || other.code == WRITE_ROWS_EVENT_V2.code;
        }
    },
    DELETE_ROWS_EVENT(DELETE_ROWS_EVENT_V0.code + DELETE_ROWS_EVENT_V1.code + DELETE_ROWS_EVENT_V2.code) {
        @Override
        public boolean is(EventType other) {
            return other.code == DELETE_ROWS_EVENT_V0.code
                    || other.code == DELETE_ROWS_EVENT_V1.code
                    || other.code == DELETE_ROWS_EVENT_V2.code;
        }
    };

    int code;

    EventType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }


    public abstract boolean is(EventType other);

    private static final Map<Integer, EventType> INDEX_BY_CODE;

    static {
        INDEX_BY_CODE = new HashMap<Integer, EventType>(64);
        for (EventType type : values()) {
            INDEX_BY_CODE.put(type.code, type);
        }
    }



    public static EventType byCode(int code) {
        return INDEX_BY_CODE.get(code);
    }
}
