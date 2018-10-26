package com.stony.mysql;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.test
 *
 * @author stony
 * @version 下午2:40
 * @since 2018/10/12
 */
public abstract class AbstractMainTest {
    static String host = "10.0.11.172";
    static int port = 3311;
    static String username = "slave";
    static String password = "slave";
    static String schema = null;


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
    /** 小端 **/
    public static int byteArrayToInt(byte[] b) {
        return  b[0] & 0xFF |
                (b[1] & 0xFF) << 8 |
                (b[2] & 0xFF) << 16 |
                (b[3] & 0xFF) << 24;
    }
}