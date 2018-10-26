package com.stony.mysql.io;

import java.util.*;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.net
 *
 * @author stony
 * @version 下午5:38
 * @since 2018/10/18
 */
public class LruCache<K, V> {
    private final LinkedHashMap<K, V> map;

    private int size;
    private int maxSize;


    public LruCache(final int maxSize) {
        if (maxSize <= 0) {
            throw new IllegalArgumentException("maxSize <= 0");
        }
        this.maxSize = maxSize;
        this.map = new LinkedHashMap<K, V>(0, 0.75f, true){
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > maxSize;
            }
        };
    }

    public final V get(K key) {
        if (key == null) {
            throw new NullPointerException("key == null");
        }
        V mapValue;
        synchronized (this) {
            mapValue = map.get(key);
        }
        return mapValue;
    }

    public final V put(K key, V value) {
        if (key == null || value == null) {
            throw new NullPointerException("key == null || value == null");
        }
        V previous;
        synchronized (this) {
            previous = this.map.putIfAbsent(key, value);
            size++;
        }
        return previous;
    }
    public Collection<V> values() {
        return this.map.values();
    }
    public Set<K> keySet() {
        return this.map.keySet();
    }
    public Set<K> keys() {
        return this.map.keySet();
    }
    public int size() {
        return this.map.size();
    }

}