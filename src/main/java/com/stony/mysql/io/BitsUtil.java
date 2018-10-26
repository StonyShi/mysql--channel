package com.stony.mysql.io;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.net
 *
 * @author stony
 * @version 下午2:56
 * @since 2018/10/12
 */
public class BitsUtil {


    public static short getShortLittle(byte[] b, int off) {
        return makeShort(b[off + 3],
                b[off + 2]);
    }
    public static short getShortBig(byte[] b, int off) {
        return makeShort(b[off   ],
                        b[off + 1]);
    }

    public static int getIntLittle(byte[] b, int off) {
        return makeInt(b[off + 3],
                b[off + 2],
                b[off + 1],
                b[off    ]);
    }
    public static int getIntBig(byte[] b, int off) {
        return makeInt(b[off   ],
                b[off + 1],
                b[off + 2],
                b[off + 3]);
    }

    public static long getLongLittle(byte[] b, int off) {
        return makeLong(
                b[off + 7],
                b[off + 6],
                b[off + 5],
                b[off + 4],
                b[off + 3],
                b[off + 2],
                b[off + 1],
                b[off    ]);
    }
    public static long getLongBig(byte[] b, int off) {
        return makeLong(
                b[off   ],
                b[off + 1],
                b[off + 2],
                b[off + 3],
                b[off + 4],
                b[off + 5],
                b[off + 6],
                b[off + 7]);
    }

    private static short makeShort(byte b1, byte b0) {
        return (short)((b1 << 8) | (b0 & 0xff));
    }

    private static int makeInt(byte b3, byte b2, byte b1, byte b0) {
        return (((b3) << 24) |
                ((b2 & 0xff) << 16) |
                ((b1 & 0xff) << 8) |
                ((b0 & 0xff)));
    }

    private static long makeLong(byte b7, byte b6, byte b5, byte b4,
                                 byte b3, byte b2, byte b1, byte b0) {
        return ((((long) b7) << 56) |
                (((long) b6 & 0xff) << 48) |
                (((long) b5 & 0xff) << 40) |
                (((long) b4 & 0xff) << 32) |
                (((long) b3 & 0xff) << 24) |
                (((long) b2 & 0xff) << 16) |
                (((long) b1 & 0xff) << 8) |
                (((long) b0 & 0xff)));
    }

    /**** -------------------------------------------------------------------- **/
    public static void putShortBig(byte[] b, int off, short val) {
        b[off + 1] = (byte) (val      );
        b[off    ] = (byte) (val >>> 8);
    }

    public static void putIntBig(byte[] b, int off, int val) {
        b[off + 3] = (byte) (val       );
        b[off + 2] = (byte) (val >>>  8);
        b[off + 1] = (byte) (val >>> 16);
        b[off    ] = (byte) (val >>> 24);
    }


    public static void putLongBig(byte[] b, int off, long val) {
        b[off + 7] = (byte) (val       );
        b[off + 6] = (byte) (val >>>  8);
        b[off + 5] = (byte) (val >>> 16);
        b[off + 4] = (byte) (val >>> 24);
        b[off + 3] = (byte) (val >>> 32);
        b[off + 2] = (byte) (val >>> 40);
        b[off + 1] = (byte) (val >>> 48);
        b[off    ] = (byte) (val >>> 56);
    }

    public static void putShortLittle(byte[] b, int off, short val) {
        b[off    ] = (byte) (val      );
        b[off + 1] = (byte) (val >>> 8);
    }

    public static void putIntLittle(byte[] b, int off, int val) {
        b[off    ] = (byte) (val       );
        b[off + 1] = (byte) (val >>>  8);
        b[off + 2] = (byte) (val >>> 16);
        b[off + 3] = (byte) (val >>> 24);
    }

    public static void putLongLittle(byte[] b, int off, long val) {
        b[off    ] = (byte) (val       );
        b[off + 1] = (byte) (val >>>  8);
        b[off + 2] = (byte) (val >>> 16);
        b[off + 3] = (byte) (val >>> 24);
        b[off + 4] = (byte) (val >>> 32);
        b[off + 5] = (byte) (val >>> 40);
        b[off + 6] = (byte) (val >>> 48);
        b[off + 7] = (byte) (val >>> 56);
    }
}
