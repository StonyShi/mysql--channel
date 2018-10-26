package com.stony.mysql.protocol;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 *<pre>
 Payload
 Type	Name	Description
 int<1>	header	[ff] header of the ERR packet
 int<2>	error_code	error-code
 if capabilities & CLIENT_PROTOCOL_41 {
 string[1]	sql_state_marker	# marker of the SQL State
 string[5]	sql_state	SQL State
 }
 string<EOF>	error_message	human readable error message
 *</pre>
 * @author stony
 * @version 下午2:08
 * @since 2018/10/17
 * @see <a href="https://dev.mysql.com/doc/internals/en/packet-ERR_Packet.html">ERR_Packet</a>
 */
public class ERRPacket {

    int header; //1 bit
    int errorCode; //2 bit
    //if capabilities & CLIENT_PROTOCOL_41 { sql_state_marker, sql_state }
    String sqlStateMarker; //1 bit # marker of the SQL State
    String SQLState; //5 bit

    String errorMessage; //string<EOF>

    public int getHeader() {
        return header;
    }

    public void setHeader(int header) {
        this.header = header;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getSqlStateMarker() {
        return sqlStateMarker;
    }

    public void setSqlStateMarker(String sqlStateMarker) {
        this.sqlStateMarker = sqlStateMarker;
    }

    public String getSQLState() {
        return SQLState;
    }

    public void setSQLState(String SQLState) {
        this.SQLState = SQLState;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString() {
        return "ERRPacket{" +
                "header=" + header +
                ", errorCode=" + errorCode +
                ", sqlStateMarker='" + sqlStateMarker + '\'' +
                ", SQLState='" + SQLState + '\'' +
                ", errorMessage='" + errorMessage + '\'' +
                '}';
    }
}
