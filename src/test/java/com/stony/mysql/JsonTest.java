package com.stony.mysql;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.stony.mysql.json.BitSetDeserializer;
import com.stony.mysql.json.BitSetSerializer;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.BitSet;

/**
 * <p>mysql-x
 * <p>com.mysql.test
 *
 * @author stony
 * @version 上午9:55
 * @since 2018/10/25
 */
public class JsonTest {

    @Test
    public void test_16() throws IOException {

        BitSet bs = new BitSet();
        bs.set(1);
        bs.set(10);


        Assert.assertEquals(bs.toString(),  "{1, 10}");
        Assert.assertArrayEquals(new int[]{1,10},  bs.stream().filter(v -> v != -1).toArray());

        ObjectMapper mapper = new ObjectMapper();
        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.enable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        SimpleModule module = new SimpleModule();
        module.addSerializer(BitSet.class, new BitSetSerializer());
        module.addDeserializer(BitSet.class, new BitSetDeserializer());
        mapper.registerModule(module);


        String val = mapper.writeValueAsString(bs);
        Assert.assertEquals(val, "[1,10]");


        Assert.assertEquals(bs, mapper.readValue(val, BitSet.class));
    }
}
