package com.example.demojackson.ex;


import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
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

    @Test
    void whenSerializingUsingJsonGetter_thenCorrect() throws Exception {
        JsonGetterObject o = new JsonGetterObject();
        o.firstName = "firstName";
        o.lastName = "lastName";

        String result = objectMapper.writeValueAsString(o);

        log.info(result);
        assertThat(result).isEqualTo("{\"firstName\":\"firstName\",\"lastName\":\"lastName\"}");
    }

    public static class JsonGetterObject {
        public String firstName;
        private String lastName;

        @JsonGetter("lastName")
        public String getLastName() {
            return lastName;
        }
    }

    @Test
    void whenSerializingUsingJsonPropertyOrder_thenCorrect() throws Exception {
        JsonPropertyOrderObject o = new JsonPropertyOrderObject();
        o.firstName = "firstName";
        o.lastName = "lastName";

        String result = objectMapper.writeValueAsString(o);

        log.info(result);
        assertThat(result).isEqualTo("{\"lastName\":\"lastName\",\"firstName\":\"firstName\"}");
    }

    @JsonPropertyOrder({"lastName","firstName"})
    public static class JsonPropertyOrderObject {
        public String firstName;
        public String lastName;
    }

    @Test
    void whenSerializingUsingJsonValue_thenCorrect() throws Exception {
        String result = objectMapper.writeValueAsString(TypeEnumWithValue.TYPE1);

        log.info(result);
        assertThat(result).isEqualTo("\"Type A\"");
    }

    @AllArgsConstructor
    public enum TypeEnumWithValue {
        TYPE1(1, "Type A"), TYPE2(2, "Type 2");

        private Integer id;
        private String name;

        @JsonValue
        public String getName() {
            return name;
        }
    }

    @Test
    void whenSerializingUsingJsonRootName_thenCorrect() throws Exception {
        JsonRootNameObject o = new JsonRootNameObject();
        o.id = 123;
        o.name = "name";

        String result = objectMapper.writeValueAsString(o);

        log.info(result);
        assertThat(result).isEqualTo("{\"id\":123,\"name\":\"name\"}");
    }

    @JsonRootName("user")
    public static class JsonRootNameObject {
        public Integer id;
        public String name;
    }

    @Test
    void whenSerializingUsingJsonSerialize_thenCorrect() throws Exception {
        JsonSerializeObject o = new JsonSerializeObject();
        o.id = 123;
        o.name = "name";

        String result = objectMapper.writeValueAsString(o);

        log.info(result);
        assertThat(result).isEqualTo("{\"id\":123,\"name\":\"name\"}");
    }

    @JsonSerialize(using = ObjectSerializer.class)
    public static class JsonSerializeObject {
        public Integer id;
        public String name;
    }

    public static class ObjectSerializer extends StdSerializer<JsonSerializeObject> {
        public ObjectSerializer() {
            super(JsonSerializeObject.class);
        }

        @Override
        public void serialize(JsonSerializeObject value, JsonGenerator gen, SerializerProvider provider) throws IOException {
            gen.writeStartObject();

            gen.writeNumberField("id", value.id);
            gen.writeStringField("name", value.name);

            gen.writeEndObject();
        }
    }
}
