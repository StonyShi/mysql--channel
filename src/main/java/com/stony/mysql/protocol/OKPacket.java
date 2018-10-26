package com.stony.mysql.protocol;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 *
 * <h4>Payload of OK Packet</h4>
 *<pre>

 Type	Name	Description
 int<1>	header	[00] or [fe] the OK packet header
 int<lenenc>	affected_rows	affected rows
 int<lenenc>	last_insert_id	last insert-id
 if capabilities & CLIENT_PROTOCOL_41 {
 int<2>	status_flags	Status Flags
 int<2>	warnings	number of warnings
 } elseif capabilities & CLIENT_TRANSACTIONS {
 int<2>	status_flags	Status Flags
 }
 if capabilities & CLIENT_SESSION_TRACK {
 string<lenenc>	info	human readable status information
 if status_flags & SERVER_SESSION_STATE_CHANGED {
 string<lenenc>	session_state_changes	session state info
 }
 } else {
 string<EOF>	info	human readable status information
 }
 *</pre>
 * @author stony
 * @version 下午2:02
 * @since 2018/10/17
 * @see <a href="https://dev.mysql.com/doc/internals/en/packet-OK_Packet.html">OK_Packet</a>
 */
public class OKPacket {
    int header; //1
    long affectedRows;  //int<lenenc>
    long lastInsertId;  //int<lenenc>

    //if capabilities & CLIENT_PROTOCOL_41 {
    int statusFlags; // 2
    int warnings; //2

    String info; // string<lenenc>

    String sessionStateChanges; //string<lenenc>

    public int getHeader() {
        return header;
    }

    public void setHeader(int header) {
        this.header = header;
    }

    public long getAffectedRows() {
        return affectedRows;
    }

    public void setAffectedRows(long affectedRows) {
        this.affectedRows = affectedRows;
    }

    public long getLastInsertId() {
        return lastInsertId;
    }

    public void setLastInsertId(long lastInsertId) {
        this.lastInsertId = lastInsertId;
    }

    public int getStatusFlags() {
        return statusFlags;
    }

    public void setStatusFlags(int statusFlags) {
        this.statusFlags = statusFlags;
    }

    public int getWarnings() {
        return warnings;
    }

    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getSessionStateChanges() {
        return sessionStateChanges;
    }

    public void setSessionStateChanges(String sessionStateChanges) {
        this.sessionStateChanges = sessionStateChanges;
    }

    @Override
    public String toString() {
        return "OKPacket{" +
                "header=" + header +
                ", affectedRows=" + affectedRows +
                ", lastInsertId=" + lastInsertId +
                ", statusFlags=" + statusFlags +
                ", warnings=" + warnings +
                ", info='" + info + '\'' +
                ", sessionStateChanges='" + sessionStateChanges + '\'' +
                '}';
    }
}