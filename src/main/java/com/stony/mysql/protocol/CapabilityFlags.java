package com.stony.mysql.protocol;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 *
 * @author stony
 * @version 上午10:44
 * @since 2018/10/11
 * @see <a href="http://dev.mysql.com/doc/internals/en/capability-flags.html#packet-Protocol::CapabilityFlags">
 *      Capability Flags</a>
 */
public class CapabilityFlags {
    public static final int CLIENT_LONG_PASSWORD      = 0x00000001;
    public static final int CLIENT_FOUND_ROWS         = 0x00000002;
    public static final int CLIENT_LONG_FLAG          = 0x00000004;
    public static final int CLIENT_CONNECT_WITH_DB    = 0x00000008;
    public static final int CLIENT_NO_SCHEMA          = 0x00000010;
    public static final int CLIENT_COMPRESS           = 0x00000020;
    public static final int CLIENT_ODBC               = 0x00000040;
    public static final int CLIENT_LOCAL_FILES        = 0x00000080;
    public static final int CLIENT_IGNORE_SPACE       = 0x00000100;
    public static final int CLIENT_PROTOCOL_41        = 0x00000200;
    public static final int CLIENT_INTERACTIVE        = 0x00000400;
    public static final int CLIENT_SSL                = 0x00000800;
    public static final int CLIENT_IGNORE_SIGPIPE     = 0x00001000;
    public static final int CLIENT_TRANSACTIONS       = 0x00002000;
    public static final int CLIENT_RESERVED           = 0x00004000;
    public static final int CLIENT_SECURE_CONNECTION  = 0x00008000;
    public static final int CLIENT_MULTI_STATEMENTS   = 0x00010000;
    public static final int CLIENT_MULTI_RESULTS      = 0x00020000;
    public static final int CLIENT_PS_MULTI_RESULTS   = 0x00040000;

    public static final int CLIENT_PLUGIN_AUTH         = 0x00080000;
    public static final int CLIENT_CONNECT_ATTRS       = 0x00100000;


    public static final int CLIENT_SESSION_TRACK       = 0x00800000;



}
