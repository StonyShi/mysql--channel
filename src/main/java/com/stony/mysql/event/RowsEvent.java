package com.stony.mysql.event;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stony.mysql.json.BitSetDeserializer;
import com.stony.mysql.json.BitSetSerializer;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 下午6:31
 * @since 2018/10/18
 * @see <a href="https://dev.mysql.com/doc/internals/en/rows-event.html">ROWS_EVENT</a>
 */
public abstract class RowsEvent implements BinlogEvent.Event{

    public static final int V0 = 0; //written from MySQL 5.1.0 to 5.1.15
    public static final int V1 = 1; //written from MySQL 5.1.15 to 5.6.x
    public static final int V2 = 2; //written from MySQL 5.6.x

    protected long tableId;
    protected int flags;
    protected String tableName;
    protected String schema;

    @JsonDeserialize(using = BitSetDeserializer.class)
    @JsonSerialize(using = BitSetSerializer.class)
    protected BitSet columns;

    @JsonDeserialize(using = BitSetDeserializer.class)
    @JsonSerialize(using = BitSetSerializer.class)
    private BitSet nullColumns;

    protected List<Object[]> rows;

    protected String columnNames;


    protected int version;

    public RowsEvent(int version) {
        this.version = version;
    }

    public long getTableId() {
        return tableId;
    }

    public void setTableId(long tableId) {
        this.tableId = tableId;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getSchema() {
        return schema;
    }

    public BitSet getColumns() {
        return columns;
    }

    public void setColumns(BitSet columns) {
        this.columns = columns;
    }

    public void setRows(List<Object[]> rows) {
        this.rows = rows;
    }

    public int getVersion() {
        return version;
    }

    public String getVersionName(){
        return String.format("V%d", this.version);
    }

    public BitSet getNullColumns() {
        return nullColumns;
    }

    public void setNullColumns(BitSet nullColumns) {
        this.nullColumns = nullColumns;
    }

    public List<Object[]> getRows() {
        return rows;
    }

    public String getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String columnNames) {
        this.columnNames = columnNames;
    }

    @Override
    public String toString() {
        String rowsStr = null;
        if(rows != null) {
            rowsStr = rows.stream().map(row -> Arrays.toString(row)).collect(Collectors.joining(",", "{", "}"));
        }
        return "" +
                "version=" + getVersionName() +
                ", tableId=" + tableId +
                ", flags=" + flags +
                ", tableName='" + tableName + '\'' +
                ", schema='" + schema + '\'' +
                ", columns=" + columns +
                ", columnNames=" + columnNames +
                ", nullColumns=" + nullColumns +
                ", rows=" + rowsStr;
    }
}
