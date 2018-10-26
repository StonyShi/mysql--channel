package com.stony.mysql.protocol;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 *
 * @author stony
 * @version 下午4:46
 * @since 2018/10/18
 * @see <a href="https://dev.mysql.com/doc/internals/en/com-query-response.html#packet-Protocol::MYSQL_TYPE_DECIMAL">Column Types</a>
 */
public enum ColumnType {
    DECIMAL(0),  //Implemented   by   ProtocolBinary::DECIMAL
    TINY(1),  //Implemented   by   ProtocolBinary::TINY
    SHORT(2),  //Implemented   by   ProtocolBinary::SHORT
    LONG(3),  //Implemented   by   ProtocolBinary::LONG
    FLOAT(4),  //Implemented   by   ProtocolBinary::FLOAT
    DOUBLE(5),  //Implemented   by   ProtocolBinary::DOUBLE
    NULL(6),  //Implemented   by   ProtocolBinary::NULL
    TIMESTAMP(7),  //Implemented   by   ProtocolBinary::TIMESTAMP
    LONGLONG(8),  //Implemented   by   ProtocolBinary::LONGLONG
    INT24(9),  //Implemented   by   ProtocolBinary::INT24
    DATE(10),  //Implemented   by   ProtocolBinary::DATE
    TIME(11),  //Implemented   by   ProtocolBinary::TIME
    DATETIME(12),  //Implemented   by   ProtocolBinary::DATETIME
    YEAR(13),  //Implemented   by   ProtocolBinary::YEAR
    NEWDATE(14),//see                      DATE
    VARCHAR(15),  //Implemented   by   ProtocolBinary::VARCHAR
    BIT(16),  //Implemented   by   ProtocolBinary::BIT
    TIMESTAMP2(17),  //see                      TIMESTAMP
    DATETIME2(18),  //see                      DATETIME
    TIME2(19),  //see                      TIME
    NEWDECIMAL(246),  //Implemented   by   ProtocolBinary::NEWDECIMAL
    ENUM(247),  //Implemented   by   ProtocolBinary::ENUM
    SET(248),  //Implemented   by   ProtocolBinary::SET
    TINY_BLOB(249),  //Implemented   by   ProtocolBinary::TINY_BLOB
    MEDIUM_BLOB(250),  //Implemented   by   ProtocolBinary::MEDIUM_BLOB
    LONG_BLOB(251),  //Implemented   by   ProtocolBinary::LONG_BLOB
    BLOB(252),  //Implemented   by   ProtocolBinary::BLOB
    VAR_STRING(253),  //Implemented   by   ProtocolBinary::VAR_STRING
    STRING(254),  //Implemented   by   ProtocolBinary::STRING
    GEOMETRY(255);

    int code;

    ColumnType(int code) {
        this.code = code;
    }
    public int getCode() {
        return code;
    }

    private static final Map<Integer, ColumnType> INDEX_BY_CODE;

    static {
        INDEX_BY_CODE = new HashMap<Integer, ColumnType>(64);
        for (ColumnType type : values()) {
            INDEX_BY_CODE.put(type.code, type);
        }
    }

    public static ColumnType byCode(int code) {
        return INDEX_BY_CODE.get(code);
    }
}
