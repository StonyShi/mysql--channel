package com.stony.mysql.protocol;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 *
 * @author stony
 * @version 下午4:06
 * @since 2018/10/17
 */
public class ColumnDefinition {
    int index;
    String schema;
    String table;
    String name;
    ColumnType type;

    public ColumnDefinition(String schema, String table, String name, ColumnType type) {
        this.schema = schema;
        this.table = table;
        this.name = name;
        this.type = type;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }



    public ColumnDefinition copy() {
        return new ColumnDefinition(this.schema, this.table, this.name, this.type);
    }
    @Override
    public String toString() {
        return "ColumnDefinition{" +
                "index=" + index +
                ", schema='" + schema + '\'' +
                ", table='" + table + '\'' +
                ", name='" + name + '\'' +
                ", type=" + type +
                '}';
    }
}
