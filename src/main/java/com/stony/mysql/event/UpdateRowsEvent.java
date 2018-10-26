package com.stony.mysql.event;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.stony.mysql.json.BitSetSerializer;

import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 上午9:37
 * @since 2018/10/19
 */
public class UpdateRowsEvent extends RowsEvent {

    @JsonSerialize(using = BitSetSerializer.class)
    private BitSet updateColumns;

    private List<UpdateRow> updateRows;

    public UpdateRowsEvent(int version) {
        super(version);
    }

    public List<UpdateRow> getUpdateRows() {
        return updateRows;
    }

    public void setUpdateRows(List<UpdateRow> updateRows) {
        this.updateRows = updateRows;
    }



    @Override
    public String toString() {
        return "UpdateRowsEvent {" +
                super.toString() +
                ", updateColumns=" + updateColumns +
                ", updateRows=" + updateRows +
                "}";
    }

    public void setUpdateColumns(BitSet updateColumns) {
        this.updateColumns = updateColumns;
    }

    public BitSet getUpdateColumns() {
        return updateColumns;
    }

    public static class UpdateRow {
        Object[] before;
        Object[] after;

        public UpdateRow(Object[] before, Object[] after) {
            this.before = before;
            this.after = after;
        }

        public Object[] getBefore() {
            return before;
        }

        public Object[] getAfter() {
            return after;
        }

        @Override
        public String toString() {
            return "UpdateRows{" +
                    "before=" + Arrays.toString(before) +
                    ", after=" + Arrays.toString(after) +
                    '}';
        }
    }
}