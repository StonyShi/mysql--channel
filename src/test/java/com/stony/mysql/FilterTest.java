package com.stony.mysql;


import com.stony.mysql.event.BinlogEvent;
import com.stony.mysql.event.DeleteRowsEvent;
import com.stony.mysql.event.EventHeader;
import com.stony.mysql.filter.DatabaseFilter;
import com.stony.mysql.filter.EventFilter;
import com.stony.mysql.protocol.EventType;
import org.junit.Assert;
import org.junit.Test;


import static com.stony.mysql.protocol.EventType.*;
import static com.stony.mysql.event.ColumnValue.*;

/**
 * <p>mysql-x
 * <p>com.mysql.test
 *
 * @author stony
 * @version 上午10:38
 * @since 2018/10/25
 */
public class FilterTest {

    @Test
    public void test_event(){

        EventFilter f = new EventFilter(new EventType[]{UPDATE_ROWS_EVENT, DELETE_ROWS_EVENT, WRITE_ROWS_EVENT});

        EventHeader header = new EventHeader();
        header.setEventType(UPDATE_ROWS_EVENT_V0);
        BinlogEvent event = new BinlogEvent(header, null);

        Assert.assertTrue(f.test(event));


        header.setEventType(WRITE_ROWS_EVENT_V1);
        event = new BinlogEvent(header, null);
        Assert.assertTrue(f.test(event));

        header.setEventType(DELETE_ROWS_EVENT_V2);
        event = new BinlogEvent(header, null);
        Assert.assertTrue(f.test(event));


        header.setEventType(TABLE_MAP_EVENT);
        event = new BinlogEvent(header, null);
        Assert.assertFalse(f.test(event));


    }
    @Test
    public void test_db(){
        DatabaseFilter f = new DatabaseFilter(new String[]{"test"});

        EventHeader header = new EventHeader();
        header.setEventType(UPDATE_ROWS_EVENT_V0);
        DeleteRowsEvent rowsEvent = new DeleteRowsEvent(1);
        rowsEvent.setSchema("test");
        rowsEvent.setTableName("t2");

        BinlogEvent event = new BinlogEvent(header, rowsEvent);


        Assert.assertTrue(f.test(event));


        rowsEvent = new DeleteRowsEvent(1);
        rowsEvent.setSchema("k");
        rowsEvent.setTableName("t2");

        event = new BinlogEvent(header, rowsEvent);
        Assert.assertFalse(f.test(event));

        rowsEvent = new DeleteRowsEvent(1);
        event = new BinlogEvent(header, rowsEvent);
        Assert.assertFalse(f.test(event));
    }
}
