package com.stony.mysql;

import static java.nio.charset.StandardCharsets.UTF_8;
import com.stony.mysql.command.CommandType;
import com.stony.mysql.protocol.StatusFlags;

import java.util.Arrays;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.test
 *
 * @author stony
 * @version 上午10:39
 * @since 2018/10/12
 */
public class CommandTest {

    public static void main(String[] args){


        System.out.println(CommandType.INIT_DB.ordinal());
        System.out.println(CommandType.PING.ordinal());
        System.out.println(CommandType.BINLOG_DUMP.ordinal());
        System.out.println(CommandType.BINLOG_DUMP_GTID.ordinal());

        System.out.println(Arrays.toString("test".getBytes(UTF_8)));
        System.out.println(StatusFlags.SERVER_STATUS_AUTOCOMMIT);

        System.out.println("------");
        for (int i = 0; i < 3; ++i) {
            System.out.println((i << 3));
        }
    }
}
