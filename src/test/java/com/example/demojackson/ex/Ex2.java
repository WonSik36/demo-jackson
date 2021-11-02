package com.example.demojackson.ex;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.InjectableValues;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
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

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
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

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
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

        @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
        public static MultiplePropertyEnumWithCreator of(
                @JsonProperty("id") int id,
                @JsonProperty("theName") String name) {

            return TYPE1;
        }
    }

    @Test
    void whenDeserializingUsingJsonInject_thenCorrect() throws Exception {
        String json = "{\"name\":\"My bean\"}";

        InjectableValues inject = new InjectableValues.Std()
                .addValue(int.class, 1);
        BeanWithInject bean = objectMapper.reader(inject)
                .forType(BeanWithInject.class)
                .readValue(json);

        assertThat(bean.name).isEqualTo("My bean");
        assertThat(bean.id).isEqualTo(1);
    }

    public static class BeanWithInject {
        @JacksonInject
        public int id;

        public String name;
    }

    @Test
    void whenDeserializingUsingJsonAnySetter_thenCorrect() throws Exception {
        String json = "{\"name\":\"My bean\",\"attr2\":\"val2\",\"attr1\":\"val1\"}";

        ExtendableBean bean = objectMapper.readerFor(ExtendableBean.class)
                .readValue(json);

        assertThat(bean.name).isEqualTo("My bean");
        assertThat(bean.getProperties().get("attr1")).isEqualTo("val1");
        assertThat(bean.getProperties().get("attr2")).isEqualTo("val2");
    }

    @Getter
    public static class ExtendableBean {
        public String name;
        private final Map<String, String> properties = new HashMap<>();

        @JsonAnySetter
        public void add(String key, String value) {
            properties.put(key, value);
        }
    }

    @Test
    void whenDeserializingUsingJsonSetter_thenCorrect() throws Exception {
        String json = "{\"id\":1,\"name\":\"My bean\"}";

        JsonSetterBean bean = objectMapper.readerFor(JsonSetterBean.class)
                .readValue(json);

        assertThat(bean.getName()).isEqualTo("My bean");
    }

    @Getter
    public static class JsonSetterBean {
        public int id;
        private String name;

        @JsonSetter("name")
        public void setTheName(String name) {
            this.name = name;
        }
    }

    @Test
    public void whenDeserializingUsingJsonDeserialize_thenCorrect()
            throws IOException {

        String json = "{\"name\":\"party\",\"eventDate\":\"2021-11-02 02:30:00\"}";

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        EventWithSerializer event = objectMapper.readerFor(EventWithSerializer.class)
                .readValue(json);

        assertThat(df.format(event.eventDate)).isEqualTo("2021-11-02 02:30:00");
    }


    public static class EventWithSerializer {
        public String name;

        @JsonDeserialize(using = CustomDateDeserializer.class)
        public Date eventDate;
    }

    public static class CustomDateDeserializer extends StdDeserializer<Date> {
        private static final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        public CustomDateDeserializer() {
            super(Date.class);
        }

        @Override
        public Date deserialize(JsonParser jsonparser, DeserializationContext context) throws IOException {

            String date = jsonparser.getText();
            try {
                return formatter.parse(date);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Test
    public void whenDeserializingUsingJsonAlias_thenCorrect() throws IOException {
        String json = "{\"fName\": \"John\", \"lastName\": \"Green\"}";
        AliasBean aliasBean = objectMapper.readerFor(AliasBean.class).readValue(json);

        assertThat(aliasBean.getFirstName()).isEqualTo("John");
    }

    @Getter
    public static class AliasBean {
        @JsonAlias({ "fName", "f_name" })
        private String firstName;
        private String lastName;
    }
}
