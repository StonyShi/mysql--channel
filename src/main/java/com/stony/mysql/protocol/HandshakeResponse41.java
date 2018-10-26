package com.stony.mysql.protocol;

import com.stony.mysql.io.LittleByteBuffer;

import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 * <h4>HandshakeResponse41</h4>
 * <pre>
 * 4              capability flags, CLIENT_PROTOCOL_41 always set
 * 4              max-packet size
 * 1              character set
 * string[23]     reserved (all [0])
 * string[NUL]    username
 * if capabilities & CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA {
 * lenenc-int     length of auth-response
 * string[n]      auth-response
 * } else if capabilities & CLIENT_SECURE_CONNECTION {
 * 1              length of auth-response
 * string[n]      auth-response
 * } else {
 * string[NUL]    auth-response
 * }
 * if capabilities & CLIENT_CONNECT_WITH_DB {
 * string[NUL]    database
 * }
 * if capabilities & CLIENT_PLUGIN_AUTH {
 * string[NUL]    auth plugin name
 * }
 * if capabilities & CLIENT_CONNECT_ATTRS {
 * lenenc-int     length of all key-values
 * lenenc-str     key
 * lenenc-str     value
 * if-more data in 'length of all key-values', more keys and value pairs
 * }
 * </pre>
 *
 * @author stony
 * @version 下午4:30
 * @see <a href="https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::HandshakeResponse">HandshakeResponse</a>
 * @since 2018/10/11
 */
public class HandshakeResponse41 {
    int capabilityFlags; //4, CLIENT_PROTOCOL_41 always set
    int maxPacketSize = 0; //4
    int characterSet; //1
    String reserved;  //string[23] (all [0])
    String username; //string[NUL]


    String password;
    String authPluginDataPart;

    String authResponse;

    //if capabilities&CLIENT_SECURE_CONNECTION
//    int authResponseLength; // 1  //lenenc-int if capabilities&CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA
//    String authResponse; //string[n]  //string[NUL] if not capabilities&CLIENT_SECURE_CONNECTION

    //if capabilities & CLIENT_CONNECT_WITH_DB { database }
    String database; //string[NUL]

    //if capabilities & CLIENT_PLUGIN_AUTH { auth plugin name }
    String authPluginName; //string[NUL]

    Map<String, String> attrs; //lenenc-str     key, lenenc-str     value


    public void setCapabilityFlags(int capabilityFlags) {
        this.capabilityFlags = capabilityFlags;
    }

    public void setMaxPacketSize(int maxPacketSize) {
        this.maxPacketSize = maxPacketSize;
    }

    public void setCharacterSet(int characterSet) {
        this.characterSet = characterSet;
    }

    public void setReserved(String reserved) {
        this.reserved = reserved;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public void setDatabase(String database) {
        this.database = database;
    }

    public void setAuthPluginName(String authPluginName) {
        this.authPluginName = authPluginName;
    }

    public void setAttrs(Map<String, String> attrs) {
        this.attrs = attrs;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setAuthPluginDataPart(String authPluginDataPart) {
        this.authPluginDataPart = authPluginDataPart;
    }

    private void fillToByteBuffer(LittleByteBuffer byteBuffer) {
        byteBuffer.writerInt(this.capabilityFlags);
        byteBuffer.writerInt(this.maxPacketSize); //max-packet size
        byteBuffer.writerInt(this.characterSet, 1);
        byteBuffer.writerZero(23);   //reserved (all [0])
        byteBuffer.writerStringEndZero(this.username); //username

        byte[] passwordSHA1 = passwordCalculate(this.password, authPluginDataPart);
        this.authResponse = new String(passwordSHA1);
        //capabilities & CLIENT_SECURE_CONNECTION
        byteBuffer.writerInt(passwordSHA1.length, 1);
        byteBuffer.writerBytes(passwordSHA1);  //string[n]  auth-response
        if(isNotEmpty(this.database)){
            //capabilities & CLIENT_CONNECT_WITH_DB
            byteBuffer.writerStringEndZero(this.database);  //string[NUL]    database
        }
    }

    public void writeTo(OutputStream out, LittleByteBuffer byteBuffer, boolean restOffset) throws IOException {
        if(restOffset) {
            byteBuffer.restOffset();
        }
        fillToByteBuffer(byteBuffer);

        int responseSeq = 1;
        int responseLen = byteBuffer.getLength();
        //write 3 bit
        for (int i = 0; i < 3; i++) {
            out.write((byte) (responseLen >>> (i << 3)));
        }
        //write 1 bit
        for (int i = 0; i < 1; i++) {
            out.write((byte) (responseSeq >>> (i << 3)));
        }
        if(restOffset) {
            out.write(byteBuffer.getData(), 0, responseLen);
        } else {
            out.write(byteBuffer.remainingData());
        }
        out.flush();
    }
    public void writeTo(OutputStream out, LittleByteBuffer byteBuffer) throws IOException {
        writeTo(out, byteBuffer, true);

    }


    @Override
    public String toString() {
        return "HandshakeResponse41{" +
                "capabilityFlags=" + capabilityFlags +
                ", maxPacketSize=" + maxPacketSize +
                ", characterSet=" + characterSet +
                ", reserved='" + reserved + '\'' +
                ", username='" + username + '\'' +
                ", authResponse='" + authResponse + '\'' +
                ", database='" + database + '\'' +
                ", authPluginName='" + authPluginName + '\'' +
                ", attrs=" + attrs +
                '}';
    }

    public static boolean isNotEmpty(String str){
        return !isEmpty(str);
    }
    public static boolean isEmpty(String str){
        return str == null || str.length() == 0;
    }

    /**
     * SHA1( password ) XOR SHA1( "20-bytes random data from server" <concat> SHA1( SHA1( password ) ) )
     * @see <a href="https://dev.mysql.com/doc/internals/en/secure-password-authentication.html">authentication</a>
     */
    public static byte[] passwordCalculate(String password, String salt) {
        if (isEmpty(password)) {
            return new byte[0];
        }
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] passwordHash = sha.digest(password.getBytes());
        return xor(passwordHash, sha.digest(union(salt.getBytes(), sha.digest(passwordHash))));
    }

    private static byte[] union(byte[] a, byte[] b) {
        byte[] r = new byte[a.length + b.length];
        System.arraycopy(a, 0, r, 0, a.length);
        System.arraycopy(b, 0, r, a.length, b.length);
        return r;
    }

    private static byte[] xor(byte[] a, byte[] b) {
        byte[] r = new byte[a.length];
        for (int i = 0; i < r.length; i++) {
            r[i] = (byte) (a[i] ^ b[i]);
        }
        return r;
    }
}