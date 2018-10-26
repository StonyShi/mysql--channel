package com.stony.mysql.command;

import com.stony.mysql.io.LittleByteBuffer;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.command
 *
 * @author stony
 * @version 下午3:17
 * @since 2018/10/17
 */
public class PingCommand extends BaseCommand{
    public static final int COM_PING = 0x0E;

    @Override
    protected void fillToByteBuffer(LittleByteBuffer byteBuffer) {
        byteBuffer.writerInt(COM_PING, 1);
    }
}