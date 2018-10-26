package com.stony.mysql.filter;

import com.stony.mysql.event.BinlogEvent;

import java.util.function.Predicate;

/**
 * <p>mysql-x
 * <p>com.mysql
 *
 * @author stony
 * @version 上午9:46
 * @since 2018/10/25
 */
@FunctionalInterface
public interface Filter extends Predicate<BinlogEvent> {

    boolean test(BinlogEvent event);
}