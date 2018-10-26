package com.stony.mysql.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.BitSet;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.io
 *
 * @author stony
 * @version 下午5:38
 * @since 2018/10/24
 */
public class BitSetSerializer extends JsonSerializer<BitSet> {

    @Override
    public void serialize(BitSet value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartArray();

//        int i = value.nextSetBit(0);
//        if (i != -1) {
//            gen.writeNumber(i);
//            while (true) {
//                if (++i < 0) break;
//                if ((i = value.nextSetBit(i)) < 0) break;
//                int endOfRun = value.nextClearBit(i);
//                do {
//                    gen.writeNumber(i);
//                }
//                while (++i != endOfRun);
//            }
//        }


        //
        value.stream().filter(v -> v != -1).forEach(v -> {
            try {
                gen.writeNumber(v);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        gen.writeEndArray();
    }

    @Override
    public Class<BitSet> handledType() {
        return BitSet.class;
    }
}
