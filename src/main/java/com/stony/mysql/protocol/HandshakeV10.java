package com.stony.mysql.protocol;

import com.stony.mysql.io.LittleByteBuffer;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 *
 *<pre>
 *   1              [0a] protocol version
     string[NUL]    server version
     4              connection id
     string[8]      auth-plugin-data-part-1
     1              [00] filler
     2              capability flags (lower 2 bytes)
     if more data in the packet:
     1              character set
     2              status flags
     2              capability flags (upper 2 bytes)
     if capabilities & CLIENT_PLUGIN_AUTH {
     1              length of auth-plugin-data
     } else {
     1              [00]
     }
     string[10]     reserved (all [00])
     if capabilities & CLIENT_SECURE_CONNECTION {
     string[$len]   auth-plugin-data-part-2 ($len=MAX(13, length of auth-plugin-data - 8))
     if capabilities & CLIENT_PLUGIN_AUTH {
     string[NUL]    auth-plugin name
     }
 *</pre>
 *
 * @author stony
 * @version 下午3:51
 * @since 2018/10/11
 * @see <a href="https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::HandshakeV10">
 *     HandshakeV10</a>
 */
public class HandshakeV10 {

    int protocolVersion;
    String serverVersion; //string[NUL]
    int connectionId;
    String authPluginDataPart;
    int filler;
    int capabilityFlags;
    int characterSet;
    int statusFlags;
    String authPluginName; // if capabilities & CLIENT_PLUGIN_AUTH {


    public HandshakeV10(byte[] data) {
        LittleByteBuffer byteBuffer = LittleByteBuffer.warp(data);

        this.protocolVersion = byteBuffer.readInt(1); //1
        this.serverVersion = byteBuffer.readStringEndZero(); //2

        this. connectionId = byteBuffer.readInt(); //3

        String authPluginDataPart1 = byteBuffer.readString(8); //readStringEndZero(); //4
        this.filler = byteBuffer.readInt(1);  //5

        byte[] capabilityFlagsLower = byteBuffer.readBytes(2); //6

        this.characterSet = byteBuffer.readInt(1);
        this.statusFlags = byteBuffer.readInt(2);

        byte[] capabilityFlagsUpper = byteBuffer.readBytes(2);

        byte[] temp = new byte[4];
        System.arraycopy(capabilityFlagsUpper, 0, temp, 0, 2);
        System.arraycopy(capabilityFlagsLower, 0, temp, 2, 2);
        this.capabilityFlags =  (temp[0] & 0xFF)       |
                                (temp[1] & 0xFF) << 8  |
                                (temp[2] & 0xFF) << 16 |
                                (temp[3] & 0xFF) << 24;

        int authPluginDataLen = 0;
        if((capabilityFlags & CapabilityFlags.CLIENT_PLUGIN_AUTH) != 0){
            authPluginDataLen = byteBuffer.readByte2Int();
        }

        byteBuffer.skip(10); //reserved

        int part2len = 0;
        if((capabilityFlags & CapabilityFlags.CLIENT_SECURE_CONNECTION) != 0){
            part2len = Math.max(13, authPluginDataLen - 8);
        }

        String authPluginDataPart2 = byteBuffer.readStringEndZero();

        this.authPluginDataPart = authPluginDataPart1 + authPluginDataPart2;

        if((capabilityFlags & CapabilityFlags.CLIENT_PLUGIN_AUTH) != 0)  {
            authPluginName = byteBuffer.readStringEndZero();
        }
    }

    public int getProtocolVersion() {
        return protocolVersion;
    }

    public String getServerVersion() {
        return serverVersion;
    }

    public int getConnectionId() {
        return connectionId;
    }

    public String getAuthPluginDataPart() {
        return authPluginDataPart;
    }

    public int getFiller() {
        return filler;
    }

    public int getCapabilityFlags() {
        return capabilityFlags;
    }

    public int getCharacterSet() {
        return characterSet;
    }

    public int getStatusFlags() {
        return statusFlags;
    }

    public String getAuthPluginName() {
        return authPluginName;
    }

    @Override
    public String toString() {
        return "HandshakeV10{" +
                "protocolVersion=" + protocolVersion +
                ", serverVersion='" + serverVersion + '\'' +
                ", connectionId=" + connectionId +
                ", authPluginDataPart='" + authPluginDataPart + '\'' +
                ", filler=" + filler +
                ", capabilityFlags=" + capabilityFlags +
                ", characterSet=" + characterSet +
                ", statusFlags=" + statusFlags +
                ", authPluginName='" + authPluginName + '\'' +
                '}';
    }
}
