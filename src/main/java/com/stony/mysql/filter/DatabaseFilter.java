package com.stony.mysql.filter;

import com.stony.mysql.event.BinlogEvent;
import com.stony.mysql.event.RowsEvent;
import com.stony.mysql.event.TableMapEvent;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.filter
 *
 * @author stony
 * @version 上午10:47
 * @since 2018/10/25
 */
public class DatabaseFilter implements Filter {
    final String[] usedDatabases;

    public DatabaseFilter(String[] usedDatabases) {
        this.usedDatabases = usedDatabases;
    }
    @Override
    public boolean test(BinlogEvent event) {
        BinlogEvent.Event myEvent = event.getEvent();
        if ((myEvent instanceof RowsEvent) || (myEvent instanceof TableMapEvent)) {
            String dbName;
            if (myEvent instanceof RowsEvent) {
                dbName = ((RowsEvent) myEvent).getSchema();
            } else {
                dbName = ((TableMapEvent) myEvent).getSchema();
            }
            for (String db : usedDatabases) {
                if (dbName != null && db.equals(dbName)) {
                    return true;
                }
            }
        }
        return false;
    }
}
