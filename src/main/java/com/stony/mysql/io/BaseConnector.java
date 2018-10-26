package com.stony.mysql.io;

import com.stony.mysql.event.BinlogEvent;
import com.stony.mysql.event.EventListener;
import com.stony.mysql.filter.Filter;

import java.util.concurrent.CountDownLatch;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.io
 *
 * @author stony
 * @version 上午11:32
 * @since 2018/10/25
 */
public abstract class BaseConnector implements Connector, Filter{


    EventListener[] listeners;

    Filter[] filters;

    volatile boolean listenEvent;

    CountDownLatch latch = new CountDownLatch(1);

    @Override
    public boolean test(BinlogEvent event) {
        final Filter[] doFilters = getFilters();
        if(hasArray(doFilters)) {
            for (Filter filter : doFilters) {
                if(!filter.test(event)) {
                    return false;
                }
            }
            return true;
        }
        return true;
    }

    public void onEvent(final BinlogEvent event) {
        if(event == null) {
            return;
        }
        if(this.test(event)) {
            EventListener[] listeners = getListeners();
            if(hasArray(listeners)) {
                for (EventListener listener : listeners) {
                    listener.onEvent(event);
                }
            }
        }
    }
    public void addFilters(Filter[] filters) {
        if(hasArray(filters)) {
            for(Filter filter: filters) {
                addFilter(filter);
            }
        }
    }
    public void addFilter(Filter filter) {
        if(filter == null) {
            return;
        }
        Filter[] filters = getFilters();
        if(isArrayEmpty(filters)){
            Filter[] temp = new Filter[1];
            temp[0] = filter;
            setFilters(temp);
        } else {
            Filter[] temp = new Filter[filters.length + 1];
            System.arraycopy(filters, 0, temp, 0, filters.length);
            temp[filters.length] = filter;
            setFilters(temp);
        }
    }
    public void registerListeners(EventListener[] listeners) {
        if(hasArray(listeners)) {
            for(EventListener listener: listeners) {
                registerListener(listener);
            }
        }
    }
    public void registerListener(EventListener listener) {
        if(listener == null) {
            return;
        }
        EventListener[] listeners = getListeners();
        if(isArrayEmpty(listeners)){
            EventListener[] temp = new EventListener[1];
            temp[0] = listener;
            setListeners(temp);
        } else {
            EventListener[] temp = new EventListener[listeners.length + 1];
            System.arraycopy(listeners, 0, temp, 0, listeners.length);
            temp[listeners.length] = listener;
            setListeners(temp);
        }
    }

    public final void signal() {
        latch.countDown();
    }
    void shutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(){
            @Override
            public void run() {
                try {
                    shutdown();
                } catch (XException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void startAndAwait() throws XException {
        start();
        shutdownHook();
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public EventListener[] getListeners() {
        return this.listeners;
    }

    @Override
    public void setListeners(EventListener[] listeners) {
        this.listeners = listeners;
    }

    @Override
    public Filter[] getFilters() {
        return filters;
    }
    @Override
    public void setFilters(Filter[] filters) {
        this.filters = filters;
    }
}