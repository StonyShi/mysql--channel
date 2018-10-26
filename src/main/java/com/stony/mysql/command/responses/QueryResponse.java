package com.stony.mysql.command.responses;

import com.stony.mysql.io.LittleByteBuffer;
import com.stony.mysql.protocol.ColumnDefinition;
import com.stony.mysql.protocol.ColumnDefinition41;
import com.stony.mysql.protocol.ColumnType;
import com.stony.mysql.protocol.ResponsePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.command
 *
 * @author stony
 * @version 下午3:40
 * @since 2018/10/17
 */
public class QueryResponse {
    private static final Logger logger = LoggerFactory.getLogger(QueryResponse.class);

    int fieldCount;
    ResultSet resultSet;
    ResponsePacket packet;
    Map<Integer, ColumnDefinition> columns;

    public QueryResponse(byte[] data, int clientCapabilities) {
        this(data, false, clientCapabilities);
    }

    public QueryResponse(byte[] data, boolean COM_FIELD_LIST, int clientCapabilities) {
        LittleByteBuffer byteBuffer = LittleByteBuffer.warp(data);
        int head = byteBuffer.peekFirst();
        if (head == 0) {
            this.packet = new ResponsePacket(byteBuffer.remainingData(), clientCapabilities);
        } else if (head == 0XFF) { //ERR
            this.packet = new ResponsePacket(byteBuffer.remainingData(), clientCapabilities);
        } else if (head == 0XFB) { //GET_MORE_CLIENT_DATA
            //pass
            System.out.println("GET_MORE_CLIENT_DATA");
        } else if (head >= 1) {
            process(byteBuffer, COM_FIELD_LIST);
        }
    }
    public boolean hasError() {
        return packet != null && packet.getErr() != null;
    }
    public boolean hasResult() {
        return this.resultSet != null;
    }
    private void process(LittleByteBuffer byteBuffer, boolean COM_FIELD_LIST) {
        this.fieldCount = byteBuffer.readInt(1);

        Map<Integer, ColumnDefinition41> colMap = new HashMap<>(fieldCount == 0 ? 2 : fieldCount*2);

//        logger.trace(">>>> fieldCount: {}", fieldCount);
//        logger.trace(">>>> remainingData: {}", Arrays.toString(byteBuffer.remainingData()));
//
        for (int i = 0; i < fieldCount; i++) {

            byte[] vv = byteBuffer.readBytes(3); //skip
            int seq = byteBuffer.readInt(1);

            int lv = byteBuffer.readInt(1);
            String catalog = byteBuffer.readString(lv);

            lv = byteBuffer.readInt(1);
            String v_schema = byteBuffer.readString(lv);

            lv = byteBuffer.readInt(1);
            String table = byteBuffer.readString(lv);


            lv = byteBuffer.readInt(1);
            String org_table = byteBuffer.readString(lv);

//            lv = byteBuffer.readInt(1);
//            String name = byteBuffer.readString(lv);
            String name = byteBuffer.readLengthEncodedString();


//            lv = byteBuffer.readInt(1);
//            String org_name = byteBuffer.readString(lv);
            String org_name = byteBuffer.readLengthEncodedString();


            int columnInfoLen = byteBuffer.readInt(1);
            int v_character_set = byteBuffer.readInt(2);
            int v_column_length = byteBuffer.readInt(4);
            int v_column_type = byteBuffer.readInt(1);
            int v_column_flags = byteBuffer.readInt(2);
            int v_column_decimals = byteBuffer.readInt(1);
            int v_column_filler = byteBuffer.readInt(2);


            ColumnType type = ColumnType.byCode(v_column_type);
            ColumnDefinition41 columnDefinition = new ColumnDefinition41(v_schema, table, name, type);

            columnDefinition.setIndex(i);
            columnDefinition.setCatalog(catalog);
            columnDefinition.setSchema(v_schema);
            columnDefinition.setTable(table);
            columnDefinition.setOrgTable(org_table);
            columnDefinition.setName(name);
            columnDefinition.setOrgName(org_name);
            columnDefinition.setCharacterSet(v_character_set);
            columnDefinition.setColumnLength(v_column_length);
            columnDefinition.setType(ColumnType.byCode(v_column_type));
            columnDefinition.setFlags(v_column_flags);
            columnDefinition.setDecimals(v_column_decimals);
            columnDefinition.setFiller(v_column_filler);

//            if(COM_FIELD_LIST) {
//                String defaultValues = byteBuffer.readLengthEncodedString();
//            }

            logger.trace(columnDefinition.toString());

            colMap.put(i, columnDefinition);

        }

        this.columns = new HashMap<>(colMap.size());
        for (Integer key : colMap.keySet()) {
            columns.put(key, colMap.get(key).copy());
        }
        logger.debug("columns:{}", columns);

//        logger.trace(">>>> remainingData: {}", Arrays.toString(byteBuffer.remainingData()));

//        logger.trace(String.format("remaining=%d, capacity=%d, size=%d",
//                byteBuffer.remaining(), byteBuffer.capacity(), data.length));

        //
        List<Entry[]> entries = new LinkedList<>();
        int vvl = 0xFE;  //254 EOF  [-2, 0, 0, 34, 0]
        String val = null;
        //NULL is sent as 0xfb   251
        while (byteBuffer.hasRemaining()){

            vvl = byteBuffer.readInt(3); //skip
            int seq = byteBuffer.readInt(1);

            //0xFE 254 EOF
            if(0xFE == (byteBuffer.peekByte() & 0xFF)){
                byteBuffer.skip(vvl);
                logger.trace("seq={}, len={}, val={}", seq, vvl, "EOF");
                continue;
            }
            Entry[] entry = new Entry[fieldCount];
            for (int i = 0; i < fieldCount; i++) {
                int ll = (int) byteBuffer.readEncodedInteger();
                if(ll == 0xFB) {
                    val = null; //"null";
                } else {
                    val = byteBuffer.readString(ll);
                }
                entry[i] = new Entry(colMap.get(i), val);

            }
            if(logger.isTraceEnabled()) {
                StringBuilder buf = new StringBuilder(256);
                for (Entry e : entry) {
                    buf.append(String.format("| %s=%s |", e.key.getName(), e.value));
                }
                logger.trace("seq={}, len={}, val={}", seq, vvl, buf.toString());
            }
            entries.add(entry);
            this.resultSet = new ResultSet(entries);
        }
    }

    public int getFieldCount() {
        return fieldCount;
    }

    public ResultSet getResultSet() {
        return resultSet;
    }

    @Override
    public String toString() {
        return "QueryResponse{" +
                "fieldCount=" + fieldCount +
                ", packet=" + packet +
                ", resultSet=" + resultSet +
                '}';
    }

    public ResponsePacket getPacket() {
        return packet;
    }

    public static class ResultSet {
        final List<Entry[]> entries;
        Entry[] cur;
        int pos = 0;
        public ResultSet(List<Entry[]> entries) {
            this.entries = entries;
        }
        public boolean next(){
            if (pos < entries.size()) {
                cur = entries.get(pos++);
                return true;
            }
            return false;
        }
        public int getInt(int index){
            return Integer.valueOf(cur[index].value);
        }
        public int getInt(String name){
            for (Entry entry: cur){
                if(name.equals(entry.key.getName())) {
                    return Integer.valueOf(entry.value);
                }
            }
            throw new RuntimeException("Not Fout Name: " + name);
        }

        public long getLong(int index){
            return Long.valueOf(cur[index].value);
        }
        public long getLong(String name){
            for (Entry entry: cur){
                if(name.equals(entry.key.getName())) {
                    return Long.valueOf(entry.value);
                }
            }
            throw new RuntimeException("Not Fout Name: " + name);
        }


        public String getString(int index){
            return cur[index].value;
        }
        public String getString(String name){
            for (Entry entry: cur){
                if(name.equals(entry.key.getName())) {
                    return entry.value;
                }
            }
            throw new RuntimeException("Not Fout Name: " + name);
        }

        @Override
        public String toString() {
            return "ResultSet{" +
                    "entries=" + entries +
                    ", cur=" + Arrays.toString(cur) +
                    ", pos=" + pos +
                    '}';
        }
    }
    private static class Entry {
        ColumnDefinition key;
        String value;
        public Entry(ColumnDefinition key, String value) {
            this.key = key;
            this.value = value;
        }
        @Override
        public String toString() {
            return "ResultSet{" +
                    "key='" + key + '\'' +
                    ", value='" + value + '\'' +
                    '}';
        }
    }
}
