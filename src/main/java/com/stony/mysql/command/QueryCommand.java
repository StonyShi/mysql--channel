package com.stony.mysql.command;

import com.stony.mysql.io.LittleByteBuffer;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.command
 *
 * @author stony
 * @version 下午3:28
 * @since 2018/10/17
 */
public class QueryCommand extends BaseCommand{
    public static final int COM_QUERY = 0x03;
    String sql; //string[EOF]

    public QueryCommand(String sql) {
        this.sql = sql;
    }

    @Override
    protected void fillToByteBuffer(LittleByteBuffer byteBuffer) {
        byteBuffer.writerInt(COM_QUERY, 1);
        byteBuffer.writerString(sql);
    }
}
