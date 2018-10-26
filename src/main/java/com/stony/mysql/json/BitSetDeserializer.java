package com.stony.mysql.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.BitSet;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.json
 *
 * @author stony
 * @version 上午9:48
 * @since 2018/10/25
 */
public class BitSetDeserializer extends JsonDeserializer<BitSet> {

    @Override
    public BitSet deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken token;
        BitSet bitSet = new BitSet();
        while (!JsonToken.END_ARRAY.equals(token = jsonParser.nextValue())) {
            if (token.isNumeric()) {
                bitSet.set(jsonParser.getIntValue());
            }
        }
        return bitSet;
    }
}
