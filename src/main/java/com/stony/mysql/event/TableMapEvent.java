package com.stony.mysql.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stony.mysql.json.BitSetDeserializer;
import com.stony.mysql.json.BitSetSerializer;
import com.stony.mysql.protocol.ColumnType;

import java.util.Arrays;
import java.util.BitSet;

/**
* @author stony
* @since 2018/10/18
*/
public class TableMapEvent implements BinlogEvent.Event{
    long tableId;
    int flags;
    String schema;
    String table;
    int columnCount;
    byte[] columnDef;
    @JsonIgnore
    byte[] columnMetaDef;
    private ColumnType[] columnTypes;
    private int[] columnMeta;
    //a bitmask contained a bit set for each column that can be NULL. The column-length is taken from the column-def
    @JsonDeserialize(using = BitSetDeserializer.class)
    @JsonSerialize(using = BitSetSerializer.class)
    private BitSet columnNullBitmask; // NULL-bitmask, length: (column-count + 8) / 7

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

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public byte[] getColumnDef() {
        return columnDef;
    }

    public void setColumnDef(byte[] columnDef) {
        this.columnDef = columnDef;
    }

    public byte[] getColumnMetaDef() {
        return columnMetaDef;
    }

    public void setColumnMetaDef(byte[] columnMetaDef) {
        this.columnMetaDef = columnMetaDef;
    }

    public ColumnType[] getColumnTypes() {
        return columnTypes;
    }

    public void setColumnTypes(ColumnType[] columnTypes) {
        this.columnTypes = columnTypes;
    }

    public int[] getColumnMeta() {
        return columnMeta;
    }

    public void setColumnMeta(int[] columnMeta) {
        this.columnMeta = columnMeta;
    }

    public BitSet getColumnNullBitmask() {
        return columnNullBitmask;
    }

    public void setColumnNullBitmask(BitSet columnNullBitmask) {
        this.columnNullBitmask = columnNullBitmask;
    }


    @Override
    public String toString() {
        return "TableMapEvent{" +
                "tableId=" + tableId +
                ", flags=" + flags +
                ", schema='" + schema + '\'' +
                ", table='" + table + '\'' +
                ", columnCount=" + columnCount +
                ", columnDef=" + Arrays.toString(columnDef) +
                ", columnMetaDef=" + Arrays.toString(columnMetaDef) +
                ", columnTypes=" + Arrays.toString(columnTypes) +
                ", columnMeta=" + Arrays.toString(columnMeta) +
                ", columnNullBitmask=" + columnNullBitmask +
                '}';
    }
}