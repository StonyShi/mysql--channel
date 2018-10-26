package com.stony.mysql.event;

import com.stony.mysql.protocol.ColumnDefinition;
import com.stony.mysql.protocol.ColumnType;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Map;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 下午6:05
 * @since 2018/10/18
 */
public class TableInfo {

    private String schema;
    private String name;
    private ColumnType[] columnTypes;
    private int[] columnMeta;
    private BitSet nullColumns;
    Map<Integer, ColumnDefinition> columnDefinitionMap;


    public TableInfo(String schema, String name, ColumnType[] columnTypes, int[] columnMeta, BitSet nullColumns) {
        this.schema = schema;
        this.name = name;
        this.columnTypes = columnTypes;
        this.columnMeta = columnMeta;
        this.nullColumns = nullColumns;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public BitSet getNullColumns() {
        return nullColumns;
    }

    public void setNullColumns(BitSet nullColumns) {
        this.nullColumns = nullColumns;
    }

    public Map<Integer, ColumnDefinition> getColumnDefinitionMap() {
        return columnDefinitionMap;
    }

    public void setColumnDefinitionMap(Map<Integer, ColumnDefinition> columnDefinitionMap) {
        this.columnDefinitionMap = columnDefinitionMap;
    }

    @Override
    public String toString() {
        return "TableInfo{" +
                "schema='" + schema + '\'' +
                ", name='" + name + '\'' +
                ", columnTypes=" + Arrays.toString(columnTypes) +
                ", columnMeta=" + Arrays.toString(columnMeta) +
                ", nullColumns=" + nullColumns +
                ", columnDefinitionMap=" + columnDefinitionMap +
                '}';
    }
}
