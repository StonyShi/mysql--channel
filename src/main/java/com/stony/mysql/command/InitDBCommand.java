package com.stony.mysql.command;

import com.stony.mysql.io.LittleByteBuffer;

/**
 * <p>mysql-x
 * <p>com.mysql.com.stony.mysql.command
 *
 * @author stony
 * @version 上午10:37
 * @since 2018/10/12
 * @see <a href="https://dev.mysql.com/doc/internals/en/com-init-db.html">COM_INIT_DB</a>
 */
public class InitDBCommand extends BaseCommand{
    public static final int COM_INIT_DB = 0x02;
    String schema; //string[EOF]

    public InitDBCommand(String schema) {
        this.schema = schema;
    }

    @Override
    protected void fillToByteBuffer(LittleByteBuffer byteBuffer) {
        byteBuffer.writerInt(COM_INIT_DB, 1);  //1 0x02 COM_INIT_DB
        byteBuffer.writerString(schema);
    }
}