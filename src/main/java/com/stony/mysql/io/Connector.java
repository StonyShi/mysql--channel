package com.stony.mysql.io;

import com.stony.mysql.event.EventListener;
import com.stony.mysql.filter.Filter;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.io
 *
 * @author stony
 * @version 下午2:55
 * @since 2018/10/24
 */
public interface Connector{

    void connect() throws XException;
    void start() throws XException;
    void startAndAwait() throws XException;
    void shutdown() throws XException;

    EventListener[] getListeners();
    void setListeners(EventListener[] listeners);

    Filter[] getFilters();
    void setFilters(Filter[] filters);



    default boolean isArrayEmpty(Object[] arrays){
        return arrays == null || arrays.length == 0;
    }
    default boolean hasArray(Object[] arrays){
        return !isArrayEmpty(arrays);
    }

    default boolean isNotEmpty(String str){
        return !isEmpty(str);
    }
    default boolean isEmpty(String str){
        return str == null || str.length() == 0;
    }

}
