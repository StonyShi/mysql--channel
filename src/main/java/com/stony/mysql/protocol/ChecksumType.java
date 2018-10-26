package com.stony.mysql.protocol;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 * <p>
 * --binlog-checksum={NONE|CRC32}
 * Enabling this option causes the master to write checksums for events written to the binary log. Set to NONE to disable, or the name of the algorithm to be used for generating checksums; currently, only CRC32 checksums are supported. As of MySQL 5.6.6, CRC32 is the default.
 *
 * @author stony
 * @version 上午10:28
 * @since 2018/10/17
 */
public enum ChecksumType {

    NONE(0), CRC32(4);

    int length;

    private ChecksumType(int length) {
        this.length = length;
    }

    public int getLength() {
        return length;
    }
}