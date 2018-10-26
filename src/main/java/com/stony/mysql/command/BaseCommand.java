package com.stony.mysql.command;

import com.stony.mysql.io.LittleByteBuffer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.protocol
 *
 * @author stony
 * @version 下午3:23
 * @since 2018/10/17
 */
public abstract class BaseCommand {

    protected abstract void fillToByteBuffer(LittleByteBuffer byteBuffer);

    public void writeTo(OutputStream out, LittleByteBuffer byteBuffer, boolean restOffset) throws IOException {
        if(restOffset) {
            byteBuffer.restOffset();
        }
        fillToByteBuffer(byteBuffer);

        int responseSeq = 0;
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
}
