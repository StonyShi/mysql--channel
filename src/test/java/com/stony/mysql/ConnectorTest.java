package com.stony.mysql;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.stony.mysql.event.BinlogEvent;
import com.stony.mysql.event.EventListener;
import com.stony.mysql.event.RowsEvent;
import com.stony.mysql.event.TableMapEvent;
import com.stony.mysql.filter.DatabaseFilter;
import com.stony.mysql.filter.EventFilter;
import com.stony.mysql.filter.Filter;
import com.stony.mysql.io.FileConnector;
import com.stony.mysql.io.SlaveConnector;
import com.stony.mysql.io.XException;
import com.stony.mysql.json.JsonUtil;
import com.stony.mysql.protocol.ChecksumType;
import com.stony.mysql.protocol.EventType;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import static com.stony.mysql.protocol.EventType.*;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.test
 *
 * @author stony
 * @version 下午1:51
 * @since 2018/10/11
 */
public class ConnectorTest {


    static String host = "10.0.11.172";
    static int port = 3311;
    static String username = "slave";
    static String password = "slave";




    @Test
    public void test_45() throws XException {

        SlaveConnector connector = new SlaveConnector(host, port, username, password);
        connector.setServerId(5);

        connector.addFilter(new EventFilter(new EventType[]{UPDATE_ROWS_EVENT, DELETE_ROWS_EVENT, WRITE_ROWS_EVENT}));
        connector.addFilter(new DatabaseFilter(new String[]{"test"}));

        connector.registerListener(event -> {
            try {
                System.out.println(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        connector.registerListener(new EventListener() {
            @Override
            public void onEvent(BinlogEvent event) {
                try {
                    System.out.println(JsonUtil.getUtil().toString(event));
                    System.out.println();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        });
        connector.startAndAwait();
        connector.shutdown();
    }

    @Test
    public void test_98() throws XException {
        FileConnector connector = new FileConnector("/Users/stony/Downloads/liangyanghe-bin.000032");

        connector.setChecksumType(ChecksumType.CRC32);

        connector.addFilter(new Filter() {
            @Override
            public boolean test(BinlogEvent event) {
                BinlogEvent.Event myEvent = event.getEvent();
                if ((myEvent instanceof RowsEvent) || (myEvent instanceof TableMapEvent)) {
                    String dbName;
                    String tName;
                    if (myEvent instanceof RowsEvent) {
                        dbName = ((RowsEvent) myEvent).getSchema();
                        tName = ((RowsEvent) myEvent).getTableName();
                    } else {
                        dbName = ((TableMapEvent) myEvent).getSchema();
                        tName = ((TableMapEvent) myEvent).getTable();
                    }
                    if (dbName != null && "employees".equals(dbName)) {
                        if (tName != null && "employees".equals(tName)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });
        connector.registerListener(event -> {
            try {
                System.out.println(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        connector.startAndAwait();
        connector.shutdown();
    }
}
