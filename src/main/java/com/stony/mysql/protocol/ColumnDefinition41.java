package com.stony.mysql.protocol;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 * <h4>ColumnDefinition41</h4>
 *<pre>
 lenenc_str     catalog
 lenenc_str     schema
 lenenc_str     table
 lenenc_str     org_table
 lenenc_str     name
 lenenc_str     org_name
 lenenc_int     length of fixed-length fields [0c]
 2              character set
 4              column length
 1              type
 2              flags
 1              decimals
 2              filler [00] [00]
 if command was COM_FIELD_LIST {
 lenenc_int     length of default-values
 string[$len]   default values
 }
 *</pre>
 * @author stony
 * @version 下午1:59
 * @since 2018/10/15
 * @see <a href="https://dev.mysql.com/doc/internals/en/com-query-response.html#packet-Protocol::ColumnDefinition41">
 *     ColumnDefinition41</a>
 */
public class ColumnDefinition41 extends ColumnDefinition{
    String catalog; //lenenc_str 类型: 一位int长度，后面为str, 例如：【3, 100, 101, 102 】3为长度，100, 101, 102为字符串 def
//    String schema; //lenenc_str
//    String table; //lenenc_str
    String orgTable; //lenenc_str
//    String name; //lenenc_str
    String orgName; //lenenc_str
    //lenenc_int     length of fixed-length fields [0c]
    int characterSet; //2
    int columnLength; //4
//    ColumnType type;  //1
    int flags; //2
    int decimals; //1
    int filler ; //2 [00] [00]
    //if command was COM_FIELD_LIST { lenenc_int     length of default-values string[$len]   default values }
    int defaultValuesLen;
    String defaultValues;

    public ColumnDefinition41(String schema, String table, String name, ColumnType type) {
        super(schema, table, name, type);
    }

    public String getCatalog() {
        return catalog;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
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

    public String getOrgTable() {
        return orgTable;
    }

    public void setOrgTable(String orgTable) {
        this.orgTable = orgTable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public int getCharacterSet() {
        return characterSet;
    }

    public void setCharacterSet(int characterSet) {
        this.characterSet = characterSet;
    }

    public int getColumnLength() {
        return columnLength;
    }

    public void setColumnLength(int columnLength) {
        this.columnLength = columnLength;
    }

    public ColumnType getType() {
        return type;
    }

    public void setType(ColumnType type) {
        this.type = type;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public int getDecimals() {
        return decimals;
    }

    public void setDecimals(int decimals) {
        this.decimals = decimals;
    }

    public int getFiller() {
        return filler;
    }

    public void setFiller(int filler) {
        this.filler = filler;
    }

    public int getDefaultValuesLen() {
        return defaultValuesLen;
    }

    public void setDefaultValuesLen(int defaultValuesLen) {
        this.defaultValuesLen = defaultValuesLen;
    }

    public String getDefaultValues() {
        return defaultValues;
    }

    public void setDefaultValues(String defaultValues) {
        this.defaultValues = defaultValues;
    }

    @Override
    public String toString() {
        return "ColumnDefinition41{" +
                "catalog='" + catalog + '\'' +
                ", schema='" + schema + '\'' +
                ", table='" + table + '\'' +
                ", orgTable='" + orgTable + '\'' +
                ", name='" + name + '\'' +
                ", orgName='" + orgName + '\'' +
                ", characterSet=" + characterSet +
                ", columnLength=" + columnLength +
                ", type=" + type +
                ", flags=" + flags +
                ", decimals=" + decimals +
                ", filler=" + filler +
                ", defaultValuesLen=" + defaultValuesLen +
                ", defaultValues='" + defaultValues + '\'' +
                '}';
    }
}
