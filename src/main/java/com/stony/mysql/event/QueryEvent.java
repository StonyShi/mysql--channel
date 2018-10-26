package com.stony.mysql.event;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *<pre>
 4              slave_proxy_id
 4              execution time
 1              schema length
 2              error-code
 if binlog-version ≥ 4:
 2              status-vars length

 string[$len]   status-vars
 string[$len]   schema
 1              [00]
 string[EOF]    query
 *</pre>
 * @author stony
 * @version 下午2:34
 * @since 2018/10/18
 */
public class QueryEvent implements BinlogEvent.Event{
    long slaveProxyId;
    long executionTime;
    int schemaLength;
    int errorCode;
    int statusVarsLength;

    QueryEventStatusPair[] statusVars;
    String schema;
    String query;
    private byte[] status;

    public QueryEvent(long slaveProxyId, long executionTime) {
        this.slaveProxyId = slaveProxyId;
        this.executionTime = executionTime;
    }

    public long getSlaveProxyId() {
        return slaveProxyId;
    }

    public void setSlaveProxyId(long slaveProxyId) {
        this.slaveProxyId = slaveProxyId;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(long executionTime) {
        this.executionTime = executionTime;
    }

    public int getSchemaLength() {
        return schemaLength;
    }

    public void setSchemaLength(int schemaLength) {
        this.schemaLength = schemaLength;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getStatusVarsLength() {
        return statusVarsLength;
    }

    public void setStatusVarsLength(int statusVarsLength) {
        this.statusVarsLength = statusVarsLength;
    }

    public QueryEventStatusPair[] getStatusVars() {
        return statusVars;
    }

    public void setStatusVars(QueryEventStatusPair[] statusVars) {
        this.statusVars = statusVars;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setStatus(byte[] status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "QueryEvent{" +
                "slaveProxyId=" + slaveProxyId +
                ", executionTime=" + executionTime +
                ", schemaLength=" + schemaLength +
                ", errorCode=" + errorCode +
                ", statusVarsLength=" + statusVarsLength +
                ", statusVars='" + Arrays.toString(statusVars) + '\'' +
                ", schema='" + schema + '\'' +
                ", query='" + query + '\'' +
                '}';
    }

    public static class QueryEventStatusPair {
        QueryEventStatus code;
        Object[] values;
        public QueryEventStatusPair(QueryEventStatus code, Object[] values) {
            this.code = code;
            this.values = values;
        }

        @Override
        public String toString() {
            return "{" +
                    "code=" + code +
                    ", values='" + Arrays.toString(values) + '\'' +
                    '}';
        }
    }

    public enum QueryEventStatus {
        Q_FLAGS2_CODE(0, 4),          //4
        Q_SQL_MODE_CODE(1, 8),          //8
        Q_CATALOG(2, 2),                //1+n+1
        Q_AUTO_INCREMENT(3, 4),          //2+2
        Q_CHARSET_CODE(4, 6),             //2+2+2
        Q_TIME_ZONE_CODE(5, 1),             //1+n
        Q_CATALOG_NZ_CODE(6, 1),            //1+n
        Q_LC_TIME_NAMES_CODE(7, 2),          //2
        Q_CHARSET_DATABASE_CODE(8, 2),        //2
        Q_TABLE_MAP_FOR_UPDATE_CODE(9, 8),    //8
        Q_MASTER_DATA_WRITTEN_CODE(10, 4),    //4
        Q_INVOKERS(11, 2),                    //1+n+1+n
        Q_UPDATED_DB_NAMES(12, 1),            //1+n*nul-term-string
        Q_MICROSECONDS(13, 3),                 //3
        Q_NONE(-1, 1000);

        int code;
        int length;
        QueryEventStatus(int code, int length) {
            this.code = code;
        }

        public int getCode() {
            return code;
        }


        private static final Map<Integer, QueryEventStatus> INDEX_BY_CODE;

        static {
            INDEX_BY_CODE = new HashMap<>(32);
            for (QueryEventStatus type : QueryEventStatus.values()) {
                INDEX_BY_CODE.put(type.code, type);
            }
        }

        public static QueryEventStatus byCode(int code) {
            return INDEX_BY_CODE.get(code);
        }
    }
}