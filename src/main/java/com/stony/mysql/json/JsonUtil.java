package com.stony.mysql.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;
import java.util.BitSet;

/**
 * <p>mysql-x
 * <p>com.stony.mysql.json
 *
 * @author stony
 * @version 下午12:26
 * @since 2018/10/25
 */
public class JsonUtil {
    static JsonUtil util = new JsonUtil();
    ObjectMapper mapper;
    private JsonUtil() {
        mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);


        SimpleModule module = new SimpleModule();
        module.addSerializer(BitSet.class, new BitSetSerializer());
        module.addDeserializer(BitSet.class, new BitSetDeserializer());
        mapper.registerModule(module);
    }
    public static JsonUtil getUtil(){
        return util;
    }
    public String toString(Object value) throws JsonProcessingException {
        return mapper.writeValueAsString(value);
    }

    public <T> T toObject(String value, Class<T> clazz) throws IOException {
        return mapper.readValue(value, clazz);
    }
    public <T> T toObject(String value, TypeReference<T> valueTypeRef) throws IOException {
        return mapper.readValue(value, valueTypeRef);
    }

}
