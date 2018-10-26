package com.stony.mysql.filter;

import com.stony.mysql.event.BinlogEvent;
import com.stony.mysql.protocol.EventType;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.filter
 *
 * @author stony
 * @version 上午10:21
 * @since 2018/10/25
 */
public class EventFilter implements Filter{

    final EventType[] usedEvents;

    public EventFilter(EventType[] usedEvents) {
        this.usedEvents = usedEvents;
    }

    @Override
    public boolean test(BinlogEvent event) {
        EventType myType = event.getHeader().getEventType();
        for (EventType type : usedEvents) {
            if(type.is(myType)) {
                return true;
            }
        }
        return false;
    }
}
