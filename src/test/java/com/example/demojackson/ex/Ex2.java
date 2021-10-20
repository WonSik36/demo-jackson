package com.example.demojackson.ex;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/*
    https://www.baeldung.com/jackson-annotations 예제를 바탕으로 작성
    Deserialize Case 에 대해서 작성
 */
@Log4j2
public class Ex2 {
    final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenDeserializingUsingJsonCreator_withSingleProperty_thenCorrect() throws Exception {
        String json = "{\"theName\":\"My bean\"}";

        SinglePropertyBeanWithCreator bean = objectMapper.readerFor(SinglePropertyBeanWithCreator.class)
                .readValue(json);

        assertThat(bean.name).isEqualTo("My bean");
    }

    public static class SinglePropertyBeanWithCreator {
        public String name;

        @JsonCreator
        public SinglePropertyBeanWithCreator(@JsonProperty("theName") String name) {
            this.name = name;
        }
    }

    @Test
    void whenDeserializingUsingJsonCreator_withMultipleProperty_thenCorrect() throws Exception {
        String json = "{\"id\":1,\"theName\":\"My bean\"}";

        MultiplePropertyBeanWithCreator bean = objectMapper.readerFor(MultiplePropertyBeanWithCreator.class)
                .readValue(json);

        assertThat(bean.name).isEqualTo("My bean");
        assertThat(bean.id).isEqualTo(1);
    }

    public static class MultiplePropertyBeanWithCreator {
        public int id;
        public String name;

        @JsonCreator
        public MultiplePropertyBeanWithCreator(
                @JsonProperty("id") int id,
                @JsonProperty("theName") String name) {
            this.id = id;
            this.name = name;
        }
    }

    @Test
    void whenDeserializingUsingJsonCreator_withSinglePropertyEnum_thenCorrect() throws Exception {
        String json = "{\"theName\":\"My bean\"}";

        SinglePropertyEnumWithCreator bean = objectMapper.readerFor(SinglePropertyEnumWithCreator.class)
                .readValue(json);

        assertThat(bean).isEqualTo(SinglePropertyEnumWithCreator.TYPE1);
    }

    @AllArgsConstructor
    public enum SinglePropertyEnumWithCreator {
        TYPE1("name");

        public String name;

        @JsonCreator
        public static SinglePropertyEnumWithCreator of(@JsonProperty("theName") String name) {
            return TYPE1;
        }
    }

    @Test
    void whenDeserializingUsingJsonCreator_withMultiplePropertyEnum_thenCorrect() throws Exception {
        String json = "{\"id\":1,\"theName\":\"My bean\"}";

        MultiplePropertyEnumWithCreator bean = objectMapper.readerFor(MultiplePropertyEnumWithCreator.class)
                .readValue(json);

        assertThat(bean).isEqualTo(MultiplePropertyEnumWithCreator.TYPE1);
    }

    @AllArgsConstructor
    public enum MultiplePropertyEnumWithCreator {
        TYPE1(1, "name");

        public int id;
        public String name;

        @JsonCreator
        public static MultiplePropertyEnumWithCreator of(
                @JsonProperty("id") int id,
                @JsonProperty("theName") String name) {

            return TYPE1;
        }
    }
}
