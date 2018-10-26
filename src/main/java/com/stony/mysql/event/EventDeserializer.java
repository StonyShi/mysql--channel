package com.stony.mysql.event;

import com.stony.mysql.io.LittleByteBuffer;
import com.stony.mysql.io.LruCache;
import com.stony.mysql.protocol.ChecksumType;
import com.stony.mysql.protocol.ColumnType;
import com.stony.mysql.protocol.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import static com.stony.mysql.event.ColumnValue.*;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 上午10:33
 * @since 2018/10/18
 */
public class EventDeserializer {
    private static final Logger logger = LoggerFactory.getLogger(EventDeserializer.class);

    ChecksumType checksumType;
    String binlogFile;
    long binlogPosition;
    int binlogVersion;
    int cacheSize = 1024 * 1024; // 1MiB
    //id, name
    LruCache<Long, TableInfo> tableMapCache = new LruCache<Long, TableInfo>(cacheSize);


    public EventDeserializer(ChecksumType checksumType) {
        this.checksumType = checksumType;
    }

    public long getBinlogPosition() {
        return binlogPosition;
    }

    public BinlogEvent deserializerEvent(LittleByteBuffer byteBuffer, int head, byte[] checksum) {
        EventHeader header = new EventHeader();
        header.timestamp = byteBuffer.readLong(4);
        header.eventType = EventType.byCode(byteBuffer.readInt(1));
        header.serverId = byteBuffer.readLong(4);
        header.eventSize = byteBuffer.readLong(4);
        header.logPos = byteBuffer.readLong(4);
        header.flags = byteBuffer.readInt(2);
        updateBinlogPosition(header.getLogPos());
        BinlogEvent binlogEvent = null;
        if(header.eventType == null) {
            binlogEvent = new BinlogEvent(header, null);
            binlogEvent.code = head;
            binlogEvent.checksum = checksum;
            return binlogEvent;
        }
        switch (header.eventType) {
            case ROTATE_EVENT:
                binlogEvent = new BinlogEvent(header, processRotateEvent(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case FORMAT_DESCRIPTION_EVENT:
                binlogEvent = new BinlogEvent(header, processFormatDescriptionEvent(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case QUERY_EVENT:
                binlogEvent = new BinlogEvent(header, processQueryEvent(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case TABLE_MAP_EVENT:
                binlogEvent = new BinlogEvent(header, processTableMapEvent(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
//                System.out.println("data:" + Arrays.toString(byteBuffer.remainingData()));
                return binlogEvent;
            case DELETE_ROWS_EVENT_V0:
            case DELETE_ROWS_EVENT_V1:
            case DELETE_ROWS_EVENT_V2:
                binlogEvent = new BinlogEvent(header, processDeleteRowsEvent(byteBuffer, header.eventType));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case UPDATE_ROWS_EVENT_V0:
            case UPDATE_ROWS_EVENT_V1:
            case UPDATE_ROWS_EVENT_V2:
                binlogEvent = new BinlogEvent(header, processUpdateRowsEvent(byteBuffer, header.eventType));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case WRITE_ROWS_EVENT_V0:
            case WRITE_ROWS_EVENT_V1:
            case WRITE_ROWS_EVENT_V2:
                binlogEvent = new BinlogEvent(header, processWriteRowsEvent(byteBuffer, header.eventType));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case XID_EVENT:
                binlogEvent = new BinlogEvent(header, processXidEvent(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case HEARTBEAT_EVENT:
                binlogEvent = new BinlogEvent(header, processHeartbeatEvent(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case INCIDENT_EVENT:
                binlogEvent = new BinlogEvent(header, processIncidentEvent(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case CREATE_FILE_EVENT:
                binlogEvent = new BinlogEvent(header, processCreateFileEvent(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case DELETE_FILE_EVENT:
                binlogEvent = new BinlogEvent(header, processDeleteFileEvent(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case RAND_EVENT:
                binlogEvent = new BinlogEvent(header, processRandEvent(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case INTVAR_EVENT:
                binlogEvent = new BinlogEvent(header, processIntvarEvent(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case START_EVENT_V3:
                binlogEvent = new BinlogEvent(header, processStartEventV3(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            case STOP_EVENT:
                binlogEvent = new BinlogEvent(header, processStopEvent(byteBuffer));
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
            default:
                //System.out.println("data:" + Arrays.toString(byteBuffer.remainingData()));
                binlogEvent = new BinlogEvent(header, null);
                binlogEvent.code = head;
                binlogEvent.checksum = checksum;
                return binlogEvent;
        }

    }
    public BinlogEvent deserializer(byte[] data) {
        if(logger.isTraceEnabled()) {
            logger.trace("Event(len={}, data={})", data.length, Arrays.toString(data));
        }

        byte[] checksum = new byte[0];

        LittleByteBuffer byteBuffer = LittleByteBuffer.warp(data);
        int head = byteBuffer.readInt(1);

        if(checksumType == ChecksumType.CRC32) {
            int end = data.length - checksumType.getLength();
            checksum = Arrays.copyOfRange(data, end, data.length);
        }

        if(head == 0) {
            return deserializerEvent(byteBuffer, head, checksum);
        } else if (head == 0XFF) { //ERR
            int c = byteBuffer.readInt(2); // err code
            String s = null;
            if('#' == byteBuffer.peekByte()) {
                byteBuffer.readString(1);     //SqlStateMarker
                s= byteBuffer.readString(5);  //SQLState
            }
            String m = byteBuffer.readString(byteBuffer.remaining());

            BinlogEvent binlogEvent = new BinlogEvent(null, new ErrorEvent(c, m, s));
            binlogEvent.code = head;
            binlogEvent.checksum = checksum;
            return binlogEvent;
        }
        else if (head == 0xFE) { //EOF
            int w = 0;
            int f = 0;
            if(byteBuffer.remaining() >= 4) {
                w = byteBuffer.readInt(2);
                f = byteBuffer.readInt(2);
            }
            BinlogEvent binlogEvent = new BinlogEvent(null, new EOFEvent(head, w, f));
            binlogEvent.code = head;
            binlogEvent.checksum = checksum;
            return binlogEvent;
        }
        return null;
    }

    void updateBinlogPosition(long position) {
        if(position > this.binlogPosition) {
            this.binlogPosition = position;
        }
    }


    private RotateEvent processRotateEvent(LittleByteBuffer byteBuffer) {
        //position, name
        RotateEvent event = new RotateEvent(byteBuffer.readLong(8), getLastRemainingString(byteBuffer));
        this.binlogPosition = event.getPosition();
        this.binlogFile = event.getName();
        return event;
    }

    private FormatDescriptionEvent processFormatDescriptionEvent(LittleByteBuffer byteBuffer) {
        FormatDescriptionEvent event = new FormatDescriptionEvent(
                byteBuffer.readInt(2),
                byteBuffer.readString(50).trim(),
                byteBuffer.readLong(4),
                byteBuffer.readInt(1),
                getLastRemainingString(byteBuffer));

        updateBinlogPosition(event.getBinlogVersion());
        return event;
    }
    private int getLastRemaining(LittleByteBuffer byteBuffer) {
        return byteBuffer.remaining() - checksumType.getLength();
    }
    private String getLastRemainingString(LittleByteBuffer byteBuffer) {
//        System.out.println("last:" + Arrays.toString(byteBuffer.remainingData()));
        return byteBuffer.readString(byteBuffer.remaining() - checksumType.getLength());
    }

    private QueryEvent processQueryEvent(LittleByteBuffer byteBuffer) {
        long slaveProxyId = byteBuffer.readLong(4);
        long executionTime = byteBuffer.readLong(4);
        int schemaLen = byteBuffer.readInt(1);
        int err = byteBuffer.readInt(2);


        byte[] status = byteBuffer.readBytes(byteBuffer.readInt(2)); //status-vars length

        String schema = byteBuffer.readStringEndZero(); //readString(schemaLen).replaceAll("\u0000", ""); //
        String query = getLastRemainingString(byteBuffer);

        QueryEvent event = new QueryEvent(slaveProxyId, executionTime);
        event.setErrorCode(err);
//        event.setStatus(status);
//        event.setStatusVars(parseStatusVariables(status));
        event.setSchema(schema);
        event.setQuery(query);
        return event;
    }

    /**
     *
     * @param data
     * @return
     * @see <a href="https://dev.mysql.com/doc/internals/en/query-event.html#q-flags2-code">code</a>
     */
    private QueryEvent.QueryEventStatusPair[] parseStatusVariables(byte[] data){
        // [len=$status_vars_length] a sequence of status key-value pairs.
        // //The key is 1-byte, while its value is dependent on the key.
        if(data.length < 1) {
            return null;
        }
        QueryEvent.QueryEventStatusPair[] statusVars = new QueryEvent.QueryEventStatusPair[0];
        LittleByteBuffer buffer = LittleByteBuffer.warp(data);
        boolean abort = false;
        while (!abort && buffer.hasRemaining()) {
            final int code = buffer.readByte() & 0xFF;
            QueryEvent.QueryEventStatus queryEventStatus = QueryEvent.QueryEventStatus.byCode(code);
            if(logger.isTraceEnabled()){
                logger.trace("code: {}, status: {}", code, queryEventStatus);
            }
            switch (queryEventStatus) {
                case Q_AUTO_INCREMENT:
                    //2-byte autoincrement-increment and 2-byte autoincrement-offset
                    final int increment = buffer.readInt(2);
                    final int offset = buffer.readInt(2);
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus,
                                    new Object[]{increment,offset})
                    );
                    break;
                case Q_FLAGS2_CODE:
                    final int flag = buffer.readInt(4);
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus, new Object[]{flag})
                    );
                    break;
                case Q_SQL_MODE_CODE:
                    //Bitmask of flags that are usually set with SET sql_mode:
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus, new Object[]{buffer.readLong(8)})
                    );
                    break;
                case Q_CATALOG:
                    //1-byte length + <length> chars of the catalog + '0'-char
                    buffer.readInt(1);
                    String catalog = buffer.readStringEndZero();
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus, new Object[]{catalog})
                    );
                    break;
                case Q_CHARSET_CODE:
                    //2-byte character_set_client + 2-byte collation_connection + 2-byte collation_server
                    final int character_set_client = buffer.readInt(2);
                    final int collation_connection = buffer.readInt(2);
                    final int collation_server = buffer.readInt(2);
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus,
                                    new Object[]{character_set_client,collation_connection,collation_server})
                    );
                    break;
                case Q_TIME_ZONE_CODE:
                case Q_CATALOG_NZ_CODE:
                    //1-byte length + <length> chars of the timezone
                    //1-byte length + <length> chars of the catalog
                    final int length = buffer.readInt(1);
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus,
                                    new Object[]{buffer.readString(length)})
                    );
                    break;
                case Q_LC_TIME_NAMES_CODE:
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus,
                                    new Object[]{buffer.readInt(2)})
                    );
                    break;
                case Q_CHARSET_DATABASE_CODE:
                    //characterset and collation of the schema
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus,
                                    new Object[]{buffer.readInt(2)})
                    );
                    break;
                case Q_TABLE_MAP_FOR_UPDATE_CODE:
                    //a 64bit-field ... should only be used in Row Based Replication and multi-table updates
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus,
                                    new Object[]{buffer.readLong(8)})
                    );
                    break;
                case Q_MASTER_DATA_WRITTEN_CODE:
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus,
                                    new Object[]{buffer.readInt(4)})
                    );
                    break;
                case Q_INVOKERS:
                    //1-byte length + <length> bytes username and 1-byte length + <length> bytes hostname
                    final int userLength = buffer.readInt(1);
                    final String username = buffer.readString(userLength);
                    final int hostLength = buffer.readInt(1);
                    final String hostname = buffer.readString(hostLength);
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus,
                                    new Object[]{username, hostname})
                    );
                    break;
                case Q_UPDATED_DB_NAMES:
                    //1-byte count + <count> \0 terminated string
                    int accessedDbCount= buffer.readInt(1);
                    String[] accessedDbs = new String[accessedDbCount];
                    if(accessedDbCount > 0) {
                        for (int i = 0; i < accessedDbCount; i++) {
                            accessedDbs[i] = buffer.readStringEndZero();
                        }
                    }
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus,
                                    new Object[]{accessedDbCount, accessedDbs})
                    );
                    break;
                case Q_MICROSECONDS:
                    //3-byte microseconds
                    statusVars = addPair(statusVars,
                            new QueryEvent.QueryEventStatusPair(queryEventStatus,
                                    new Object[]{buffer.readInt(3)})
                    );
                    break;
                default:
                    abort = true;
                    break;
            }
        }
        return statusVars;
    }
    private QueryEvent.QueryEventStatusPair[] addPair(QueryEvent.QueryEventStatusPair[] pairs, QueryEvent.QueryEventStatusPair pair) {
        if (pairs.length == 0) {
            pairs = new QueryEvent.QueryEventStatusPair[1];
            pairs[0] = pair;
        }
        if (pairs.length > 0) {
            QueryEvent.QueryEventStatusPair[] temp = new QueryEvent.QueryEventStatusPair[pairs.length + 1];
            System.arraycopy(pairs, 0, temp, 0, pairs.length);
            temp[pairs.length] = pair;
            pairs = temp;
        }
        return pairs;
    }
    private TableMapEvent processTableMapEvent(LittleByteBuffer byteBuffer) {
        TableMapEvent event = new TableMapEvent();
        event.setTableId(byteBuffer.readLong(6));
        event.setFlags(byteBuffer.readInt(2));
       //schema name length
        event.setSchema(byteBuffer.readString(byteBuffer.readInt(1)));
        byteBuffer.skip(1);
         //table name length
        event.setTable(byteBuffer.readString(byteBuffer.readInt(1)));
        byteBuffer.skip(1);

        int columnCount = (int) byteBuffer.readEncodedInteger(); //lenenc-int     column-count
        event.setColumnCount(columnCount);
        event.setColumnDef(byteBuffer.readBytes(columnCount));
        event.setColumnTypes(resolveColumnDef(event.getColumnDef()));

        int lm = (int) byteBuffer.readEncodedInteger();  //lenenc-str     column-meta-def
        event.setColumnMetaDef(byteBuffer.readBytes(lm));
        event.setColumnMeta(resolveColumnMeta(event.getColumnMetaDef(), event.getColumnTypes()));

        event.setColumnNullBitmask(byteBuffer.readBitSet(columnCount, true));

        tableMapCache.put(event.getTableId(),
                new TableInfo(
                        event.getSchema(),
                        event.getTable(),
                        event.getColumnTypes(),
                        event.getColumnMeta(),
                        event.getColumnNullBitmask()));
        return event;
    }
    private ColumnType[] resolveColumnDef(byte[] data){
        ColumnType[] types = new ColumnType[data.length];
        for (int i = 0; i < data.length; i++) {
            types[i] = ColumnType.byCode((data[i] & 0xFF));
        }
        return types;
    }
    public static int[] resolveColumnMetaDef(byte[] data, ColumnType[] types){
        LittleByteBuffer byteBuffer = LittleByteBuffer.warp(data);
        int[] metadata = new int[types.length];
        for (int i = 0; i < types.length; i++) {
            switch(types[i]) {
                case BLOB:
                case DOUBLE:
                case FLOAT:
                    metadata[i] = byteBuffer.readInt(1);
                    break;
                case STRING:
                case VAR_STRING:
                case VARCHAR:
                case DECIMAL:
                case NEWDECIMAL:
                case ENUM:
                case SET:
                    metadata[i] = byteBuffer.readInt(2);
                    break;
                default:
                    metadata[i] = 0;
            }
        }
        return metadata;
    }
    public static int[] resolveColumnMeta(byte[] data, ColumnType[] types) {
        LittleByteBuffer byteBuffer = LittleByteBuffer.warp(data);
        int[] metadata = new int[types.length];
        for (int i = 0; i < types.length; i++) {
            switch(types[i]) {
                case FLOAT:
                case DOUBLE:
                case BLOB:
                case GEOMETRY:
                    metadata[i] = byteBuffer.readInt(1);
                    break;
                case BIT:
                case VARCHAR:
                case NEWDECIMAL:
                    metadata[i] = byteBuffer.readInt(2);
                    break;
                case SET:
                case ENUM:
                case STRING:
                    metadata[i] = bigEndianInteger(byteBuffer.readBytes(2), 0, 2);
                    break;
                case TIME2:
                case DATETIME2:
                case TIMESTAMP2:
                    metadata[i] = byteBuffer.readInt(1); // fsp (@see {@link ColumnType})
                    break;
                default:
                    metadata[i] = 0;
            }
        }
        return metadata;
    }
    static int bigEndianInteger(byte[] bytes, int offset, int length) {
        int result = 0;
        for (int i = offset; i < (offset + length); i++) {
            byte b = bytes[i];
            result = (result << 8) | (b >= 0 ? (int) b : (b + 256));
        }
        return result;
    }

    private DeleteRowsEvent processDeleteRowsEvent(LittleByteBuffer byteBuffer, EventType eventType) {
        DeleteRowsEvent event = new DeleteRowsEvent(getRowsEventVersion(eventType));
        event.setTableId(byteBuffer.readLong(6));
        TableInfo info = tableMapCache.get(event.getTableId());
        if(info == null) {
            throw new RuntimeException(String.format("Not found TABLE_MAP_EVENT by tableId=%d", event.getTableId()));
        }
        event.setTableName(info.getName());
        event.setSchema(info.getSchema());
        event.setNullColumns(info.getNullColumns());
        event.setFlags(byteBuffer.readInt(2));

        if(event.getVersion() == RowsEvent.V2) {
            int extraInfoLength = byteBuffer.readInt(2); //extra_data [length=extra_data_len - 2], zero or more
            byteBuffer.skip(extraInfoLength - 2); //Binlog::RowsEventExtraData
        }

        int columnCount = (int) byteBuffer.readEncodedInteger();
        event.setColumns(byteBuffer.readBitSet(columnCount, true));

        event.setRows(deserializeRows(info, event.getColumns(), byteBuffer));

        return event;
    }
    private UpdateRowsEvent processUpdateRowsEvent(LittleByteBuffer byteBuffer, EventType eventType) {
        UpdateRowsEvent event = new UpdateRowsEvent(getRowsEventVersion(eventType));
        event.setTableId(byteBuffer.readLong(6));
        TableInfo info = tableMapCache.get(event.getTableId());
        if(info == null) {
            throw new RuntimeException(String.format("Not found TABLE_MAP_EVENT by tableId=%d", event.getTableId()));
        }

        event.setTableName(info.getName());
        event.setSchema(info.getSchema());
        event.setNullColumns(info.getNullColumns());
        event.setFlags(byteBuffer.readInt(2));

        if(event.getVersion() == RowsEvent.V2) {
            int extraInfoLength = byteBuffer.readInt(2); //extra_data [length=extra_data_len - 2], zero or more
            byteBuffer.skip(extraInfoLength - 2); //Binlog::RowsEventExtraData
        }

        int columnCount = (int) byteBuffer.readEncodedInteger();
        event.setColumns(byteBuffer.readBitSet(columnCount, true));
        event.setUpdateColumns(byteBuffer.readBitSet(columnCount, true));

        event.setUpdateRows(deserializeUpdateRows(info, event.getColumns(), event.getUpdateColumns(), byteBuffer));

        return event;
    }

    private WriteRowsEvent processWriteRowsEvent(LittleByteBuffer byteBuffer, EventType eventType) {
        WriteRowsEvent event = new WriteRowsEvent(getRowsEventVersion(eventType));
        event.setTableId(byteBuffer.readLong(6));
        TableInfo info = tableMapCache.get(event.getTableId());
        if(info == null) {
            throw new RuntimeException(String.format("Not found TABLE_MAP_EVENT by tableId=%d", event.getTableId()));
        }

        event.setTableName(info.getName());
        event.setSchema(info.getSchema());
        event.setNullColumns(info.getNullColumns());
        event.setFlags(byteBuffer.readInt(2));

        if(event.getVersion() == RowsEvent.V2) {
            int extraInfoLength = byteBuffer.readInt(2); //extra_data [length=extra_data_len - 2], zero or more
            byteBuffer.skip(extraInfoLength - 2); //Binlog::RowsEventExtraData
        }

        int columnCount = (int) byteBuffer.readEncodedInteger();
        event.setColumns(byteBuffer.readBitSet(columnCount, true));

        event.setRows(deserializeRows(info, event.getColumns(), byteBuffer));
//        System.out.println("data:" + Arrays.toString(byteBuffer.remainingData()));
//        System.out.println("data:" + new String(byteBuffer.remainingData()));
        return event;
    }
    public int getRowsEventVersion(EventType eventType) {
        switch (eventType) {
            case DELETE_ROWS_EVENT_V0:
            case UPDATE_ROWS_EVENT_V0:
            case WRITE_ROWS_EVENT_V0:
                return RowsEvent.V0;
            case DELETE_ROWS_EVENT_V2:
            case UPDATE_ROWS_EVENT_V2:
            case WRITE_ROWS_EVENT_V2:
                return RowsEvent.V2;
            case DELETE_ROWS_EVENT_V1:
            case UPDATE_ROWS_EVENT_V1:
            case WRITE_ROWS_EVENT_V1:
            default:
                return RowsEvent.V1;
        }
    }

    private XidEvent processXidEvent(LittleByteBuffer byteBuffer) {
        return new XidEvent(byteBuffer.readLong(8));
    }
    private StartEventV3 processStartEventV3(LittleByteBuffer byteBuffer) {
        StartEventV3 event = new StartEventV3(
                byteBuffer.readInt(2),
                byteBuffer.readString(50).trim(),
                byteBuffer.readLong(4));
        updateBinlogPosition(event.getBinlogVersion());
        return event;
    }
    private CreateFileEvent processCreateFileEvent(LittleByteBuffer byteBuffer) {
        CreateFileEvent event = new CreateFileEvent();
        return event;
    }
    private DeleteFileEvent processDeleteFileEvent(LittleByteBuffer byteBuffer) {
        DeleteFileEvent event = new DeleteFileEvent();
        return event;
    }

    private StopEvent processStopEvent(LittleByteBuffer byteBuffer) {
        StopEvent event = new StopEvent();
        return event;
    }

    private IntvarEvent processIntvarEvent(LittleByteBuffer byteBuffer) {
        IntvarEvent event = IntvarEvent.of(byteBuffer.readInt(1), byteBuffer.readLong(8));
        return event;
    }
    private LoadEvent processLoadEvent(LittleByteBuffer byteBuffer) {
        LoadEvent event = new LoadEvent();
        return event;
    }
    private SlaveEvent processSlaveEvent(LittleByteBuffer byteBuffer) {
        SlaveEvent event = new SlaveEvent();
        return event;
    }

    private AppendBlockEvent processAppendBlockEvent(LittleByteBuffer byteBuffer) {
        AppendBlockEvent event = new AppendBlockEvent();
        return event;
    }
    private ExecLoadEvent processExecLoadEvent(LittleByteBuffer byteBuffer) {
        ExecLoadEvent event = new ExecLoadEvent();
        return event;
    }

    private NewLoadEvent processNewLoadEvent(LittleByteBuffer byteBuffer) {
        NewLoadEvent event = new NewLoadEvent();
        return event;
    }
    private RandEvent processRandEvent(LittleByteBuffer byteBuffer) {
        RandEvent event = new RandEvent(byteBuffer.readLong(8),byteBuffer.readLong(8));
        return event;
    }
    private UserVarEvent processUserVarEvent(LittleByteBuffer byteBuffer) {
        UserVarEvent event = new UserVarEvent();
        return event;
    }

    private BeginLoadQueryEvent processBeginLoadQueryEvent(LittleByteBuffer byteBuffer) {
        BeginLoadQueryEvent event = new BeginLoadQueryEvent();
        return event;
    }
    private ExecuteLoadQueryEvent processExecuteLoadQueryEvent(LittleByteBuffer byteBuffer) {
        ExecuteLoadQueryEvent event = new ExecuteLoadQueryEvent();
        return event;
    }


    private IncidentEvent processIncidentEvent(LittleByteBuffer byteBuffer) {
        IncidentEvent event = new IncidentEvent();
        return event;
    }
    private HeartbeatEvent processHeartbeatEvent(LittleByteBuffer byteBuffer) {
        HeartbeatEvent event = new HeartbeatEvent();
        return event;
    }


    private List<UpdateRowsEvent.UpdateRow> deserializeUpdateRows(TableInfo info,
                                                                  BitSet sourceColumns,
                                                                  BitSet updateColumns,
                                                                  LittleByteBuffer byteBuffer) {
        List<UpdateRowsEvent.UpdateRow> result = new LinkedList<UpdateRowsEvent.UpdateRow>();
        while (getLastRemaining(byteBuffer) > 0) {
            result.add(new UpdateRowsEvent.UpdateRow(
                    deserializeRow(info, sourceColumns, byteBuffer),
                    deserializeRow(info, updateColumns, byteBuffer)));
        }
        return result;
    }
    private List<Object[]> deserializeRows(TableInfo info, BitSet columns, LittleByteBuffer byteBuffer) {
        List<Object[]> result = new LinkedList<Object[]>();
        while (getLastRemaining(byteBuffer) > 0) {
            result.add(deserializeRow(info, columns, byteBuffer));
        }
        return result;
    }

    private Object[] deserializeRow(TableInfo info, BitSet columns, LittleByteBuffer byteBuffer) {

        ColumnType[] types = info.getColumnTypes();
        int[] metadata = info.getColumnMeta();

        Object[] result = new Object[types.length];

        BitSet nullColumns = byteBuffer.readBitSet(types.length, true);
//        System.out.println("remainingData:" + Arrays.toString(byteBuffer.remainingData()));
        int unusedColumnCount = 0;
        for(int i = 0; i < types.length; i++) {
            int length = 0;
            int meta = metadata[i];
            ColumnType type = types[i];
            int typeCode = type.getCode();
            if(type == ColumnType.STRING && meta > 256) {
                final int meta0 = meta >> 8;
                final int meta1 = meta & 0xFF;
                if ((meta0 & 0x30) != 0x30) { // a long CHAR() field: see #37426
                    type = ColumnType.byCode(meta0);
                    length = meta1;
                } else {
                    switch (ColumnType.byCode(meta0)) {
                        case SET:
                        case ENUM:
                        case STRING:
                            type = ColumnType.byCode(meta0);
                            length = meta1;
                            break;
                        default:
                            throw new RuntimeException("assertion failed, unknown column type: " + type);
                    }
                }
            }
            if (!columns.get(i)) {
                unusedColumnCount++;
                continue;
            } else if (nullColumns.get(i - unusedColumnCount)) {
                result[i] = null;
                continue;
            }

            switch(type) {
                case TINY:
                    result[i] = byteBuffer.readInt(1);
                    break;
                case SHORT:
                    result[i] = byteBuffer.readInt(2);
                    break;
                case INT24:
                    result[i] = byteBuffer.readInt(3);
                    break;
                case LONG:
                    result[i] = byteBuffer.readLong(4);
                    break;
                case LONGLONG:
//                    System.out.println("LONGLONG:" + Arrays.toString( byteBuffer.peekBytes(8)));
                    result[i] = byteBuffer.readLong(8);
                    break;
                case FLOAT:
                    result[i] = Float.intBitsToFloat(byteBuffer.readInt(4));
                    break;
                case DOUBLE:
                    result[i] = Double.longBitsToDouble(byteBuffer.readLong(8));
                    break;
                case NULL:
                    result[i] = null;
                    break;
                case YEAR:
                    result[i] = toYear(byteBuffer.readInt(1));
                    break;
                case DATE:
                    result[i] = toDate(byteBuffer.readInt(3));
                    break;
                case TIME:
                    result[i] = toTime(byteBuffer.readInt(3));
                    break;
                case DATETIME:
                    result[i] = toDatetime(byteBuffer.readLong(8));
                    break;
                case TIMESTAMP:
                    result[i] = toTimestamp(byteBuffer.readLong(4));
                    break;

                case ENUM:
                    result[i] = byteBuffer.readInt(length);
                    break;
                case SET:
                    result[i] = byteBuffer.readLong(length);
                    break;

                case BIT:
                    final int bitLength = (meta >> 8) * 8 + (meta & 0xFF);
                    result[i] = byteBuffer.readBitSet(bitLength, true);
                    break;
                case BLOB:
                    final int blobLength = byteBuffer.readInt(meta);
                    result[i] = byteBuffer.readBytes(blobLength);
                    break;
                case NEWDECIMAL:
                    final int precision = meta & 0xFF;
                    final int scale = meta >> 8;
                    final int decimalLength = getDecimalBinarySize(precision, scale);
                    result[i] = toDecimal(precision, scale, byteBuffer.readBytes(decimalLength));
                    break;
                case STRING:
                    final int stringLength = length < 256 ? byteBuffer.readInt(1) : byteBuffer.readInt(2);
                    result[i] =  byteBuffer.readString(stringLength);
                    break;
                case VARCHAR:
                case VAR_STRING:
                    final int len = meta < 256 ? byteBuffer.readInt(1) : byteBuffer.readInt(2);
                    result[i] = byteBuffer.readString(len);
                    break;
                case TIME2:
                    final int value1 = byteBuffer.readIntBig(3);
                    final int nanos1 = byteBuffer.readIntBig((meta + 1) / 2);
                    result[i] = toTime2(value1, nanos1);
                    break;
                case DATETIME2:
                    final long value2 = byteBuffer.readLongBig(5);
                    final int nanos2 = byteBuffer.readIntBig((meta + 1) / 2);
                    result[i] = toDatetime2(value2, nanos2);
                    break;
                case TIMESTAMP2:
                    final long value3 = byteBuffer.readLongBig(4);
                    final int nanos3 = byteBuffer.readIntBig((meta + 1) / 2);
                    result[i] = toTimestamp2(value3, nanos3);
                    break;
                default:
                    throw new RuntimeException("assertion failed, unknown column type: " + type);
            }
        }
        return result;
    }


    private static int bitsSet2Int(BitSet bitSet) {
        int result = 0;
        for (int i = bitSet.nextSetBit(0); i >= 0; i = bitSet.nextSetBit(i + 1)) {
            result++;
        }
        return result;
    }
}