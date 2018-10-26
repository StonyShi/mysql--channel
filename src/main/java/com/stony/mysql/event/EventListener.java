package com.stony.mysql.event;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.event
 *
 * @author stony
 * @version 下午3:31
 * @since 2018/10/24
 */
public interface EventListener {
    void onEvent(BinlogEvent event);
}