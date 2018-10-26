package com.stony.mysql.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.io
 *
 * @author stony
 * @version 下午6:03
 * @since 2018/10/24
 */
public class ByteArraySerializer extends JsonSerializer<byte[]> {

    @Override
    public void serialize(byte[] bytes, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartArray();

        for (int i = 0; i < bytes.length; i++) {
            gen.writeNumber(bytes[i]&0XFF);
        }
        gen.writeEndArray();
    }


    @Override
    public Class<byte[]> handledType() {
        return byte[].class;
    }
}
