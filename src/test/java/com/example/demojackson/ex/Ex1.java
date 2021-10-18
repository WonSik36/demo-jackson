package com.example.demojackson.ex;


import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/*
    https://www.baeldung.com/jackson-annotations 예제를 바탕으로 작성
    Getter Case 에 대해서 작성
 */
@Log4j2
public class Ex1 {
    final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenSerializingUsingJsonAnyGetter_thenCorrect() throws Exception {
        JsonAnyGetterObject o = new JsonAnyGetterObject();
        o.name = "name";
        o.add("key1", "value1");
        o.add("key2", "value2");

        String result = objectMapper.writeValueAsString(o);

        log.info(result);
        assertThat(result).isEqualTo("{\"name\":\"name\",\"key1\":\"value1\",\"key2\":\"value2\"}");
    }

    public static class JsonAnyGetterObject {
        @Setter
        public String name;
        private final Map<String, String> properties = new HashMap<>();

        public void add(String key, String value) {
            this.properties.put(key, value);
        }

        @JsonAnyGetter
        public Map<String, String> getProperties() {
            return properties;
        }
    }
}
