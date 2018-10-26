package com.stony.mysql.protocol;

import com.stony.mysql.io.LittleByteBuffer;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 *
 * @author stony
 * @version 上午11:58
 * @since 2018/10/12
 * @see <a href="https://dev.mysql.com/doc/internals/en/generic-response-packets.html">Response Packets</a>
 */
public class ResponsePacket {

    int header; //1 bit

    OKPacket ok;
    ERRPacket err;
    EOFPacket eof;
    public boolean isOk() {
        return header == 0;
    }
    public boolean isEOF() {
        return header == 0xFE; //254
    }
    public boolean isERR() {
        return header == 0xFF; //255
    }

    public ResponsePacket(byte[] data, int capabilityFlags) {
        LittleByteBuffer byteBuffer = LittleByteBuffer.warp(data);
        header = byteBuffer.readInt(1);
        if(header == 0X00) {
            ok = new OKPacket();
            ok.setHeader(header);

            int len = byteBuffer.readInt(1);
            ok.setAffectedRows(byteBuffer.readLong(len));


            len = byteBuffer.readInt(1);
            ok.setLastInsertId(byteBuffer.readLong(len));
            if((capabilityFlags & CapabilityFlags.CLIENT_PROTOCOL_41) == CapabilityFlags.CLIENT_PROTOCOL_41){
                ok.setStatusFlags(byteBuffer.readInt(2));
                ok.setWarnings(byteBuffer.readInt(2));
            }
            if((capabilityFlags & CapabilityFlags.CLIENT_SESSION_TRACK) == CapabilityFlags.CLIENT_SESSION_TRACK){
                ok.setInfo(byteBuffer.readLengthEncodedString());
            }

        }
        else if(header == 0XFF) {
            err = new ERRPacket();

            err.setHeader(header);
            err.setErrorCode(byteBuffer.readInt(2));

            if((capabilityFlags & CapabilityFlags.CLIENT_PROTOCOL_41) == CapabilityFlags.CLIENT_PROTOCOL_41){
                err.setSqlStateMarker(byteBuffer.readString(1));
                err.setSQLState(byteBuffer.readString(5));
            }

            err.setErrorMessage(byteBuffer.readString(byteBuffer.remaining()));
        }
        else if(header == 0xFE) {
            eof = new EOFPacket();
            eof.setHeader(header);
            if((capabilityFlags & CapabilityFlags.CLIENT_PROTOCOL_41) == CapabilityFlags.CLIENT_PROTOCOL_41){
                eof.setWarnings(byteBuffer.readInt(2));
                eof.setStatusFlags(byteBuffer.readInt(2));
            }
        }
    }

    public OKPacket getOk() {
        return ok;
    }

    public ERRPacket getErr() {
        return err;
    }

    public EOFPacket getEof() {
        return eof;
    }


    @Override
    public String toString() {
        if(isOk()) {
            return ok.toString();
        }
        else if(isERR()) {
            return err.toString();
        }
        else if(isEOF()) {
            return eof.toString();
        }
        return "ResponsePacket{" + "header=" + header + '}';
    }
}
