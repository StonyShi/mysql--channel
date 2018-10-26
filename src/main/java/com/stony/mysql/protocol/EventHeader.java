package com.stony.mysql.protocol;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 * <h4>Binlog EventHeader header</h4>
 <pre>
 4              timestamp
 1              event type
 4              server-id
 4              event-size
 if binlog-version > 1:
 4              log pos
 2              flags
 </pre>
 *
 * @author stony
 * @version 上午10:04
 * @since 2018/10/17
 * @see <a href="https://dev.mysql.com/doc/internals/en/binlog-event-header.html">
 *     Binlog EventHeader header</a>
 */
public class EventHeader {

    int timestamp;// (4) -- seconds since unix epoch

    int eventType;// (1) -- see Binlog EventHeader Type

    int serverId;// (4) -- server-id of the originating mysql-server. Used to filter out events in circular replication.

    int eventSize;// (4) -- size of the event (header, post-header, body)

    // if binlog-version > 1: {log_pos, flags}
    int logPos;// (4) -- position of the next event

    int flags;// (2) -- see Binlog EventHeader Flag
}