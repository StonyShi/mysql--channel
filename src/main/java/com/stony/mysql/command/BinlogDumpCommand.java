package com.stony.mysql.command;

import com.stony.mysql.io.LittleByteBuffer;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.command
 *<pre>
     1              [12] COM_BINLOG_DUMP
     4              binlog-pos
     2              flags
     4              server-id
     string[EOF]    binlog-filename
 *</pre>
 * @author stony
 * @version 下午5:34
 * @since 2018/10/17
 */
public class BinlogDumpCommand extends BaseCommand{
    public static final int COM_BINLOG_DUMP = 18;

    final int serverId;
    final long position;
    final String binlogFilename;
    int flags = 0; //0x01 BINLOG_DUMP_NON_BLOCK if there is no more event to send send a EOF_Packet instead of blocking the connection

    public BinlogDumpCommand(int serverId, long position, String binlogFilename) {
        this.serverId = serverId;
        this.position = position;
        this.binlogFilename = binlogFilename;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    @Override
    protected void fillToByteBuffer(LittleByteBuffer byteBuffer) {
        byteBuffer.writerInt(COM_BINLOG_DUMP, 1); //18 COM_BINLOG_DUMP
        byteBuffer.writerLong(position, 4);
        byteBuffer.writerInt(flags, 2);
        byteBuffer.writerInt(serverId, 4);
        byteBuffer.writerString(binlogFilename);
    }
}