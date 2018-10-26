package com.stony.mysql.protocol;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 *
 * @author stony
 * @version 下午2:25
 * @since 2018/10/17
 * @see <a href="https://dev.mysql.com/doc/internals/en/packet-EOF_Packet.html">EOF_Packet</a>
 */
public class EOFPacket {
    int header; //fe 1
    int warnings;
    int statusFlags;

    public int getHeader() {
        return header;
    }

    public void setHeader(int header) {
        this.header = header;
    }

    public int getWarnings() {
        return warnings;
    }

    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }

    public int getStatusFlags() {
        return statusFlags;
    }

    public void setStatusFlags(int statusFlags) {
        this.statusFlags = statusFlags;
    }

    @Override
    public String toString() {
        return "EOFPacket{" +
                "header=" + header +
                ", warnings=" + warnings +
                ", statusFlags=" + statusFlags +
                '}';
    }
}
