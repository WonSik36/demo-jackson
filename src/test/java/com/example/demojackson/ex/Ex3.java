package com.example.demojackson.ex;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


/*
    https://www.baeldung.com/jackson-annotations 예제를 바탕으로 작성
    Serialize / Deserialize Case 에 대해서 작성
 */
@Log4j2
public class Ex3 {
    final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void whenSerializingUsingJsonIgnoreProperties_thenCorrect() throws Exception {
        BeanWithIgnoreProperties bean = new BeanWithIgnoreProperties(1, "My bean");

        String result = objectMapper.writeValueAsString(bean);

        log.info(result);

        assertThat(result).contains("My bean");
        assertThat(result).doesNotContain("id");
    }

    @Test
    void whenDeserializingUsingJsonIgnoreProperties_thenCorrect() throws Exception {
        String json = "{\"name\":\"My bean\", \"id\":\"1\"}";

        BeanWithIgnoreProperties bean = objectMapper.readerFor(BeanWithIgnoreProperties.class)
                        .readValue(json);

        assertThat(bean.name).isEqualTo("My bean");
        assertThat(bean.id).isNotEqualTo(1);
        assertThat(bean.id).isEqualTo(0);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties({ "id" })
    public static class BeanWithIgnoreProperties {
        public int id;
        public String name;
    }

    @Test
    void whenSerializingUsingJsonIgnore_thenCorrect() throws Exception {
        BeanWithIgnoreProperties bean = new BeanWithIgnoreProperties(1, "My bean");

        String result = objectMapper.writeValueAsString(bean);

        log.info(result);

        assertThat(result).contains("My bean");
        assertThat(result).doesNotContain("id");
    }

    @Test
    void whenDeserializingUsingJsonIgnore_thenCorrect() throws Exception {
        String json = "{\"name\":\"My bean\", \"id\":\"1\"}";

        BeanWithIgnoreProperties bean = objectMapper.readerFor(BeanWithIgnoreProperties.class)
                .readValue(json);

        assertThat(bean.name).isEqualTo("My bean");
        assertThat(bean.id).isNotEqualTo(1);
        assertThat(bean.id).isEqualTo(0);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class BeanWithIgnore {
        @JsonIgnore
        public int id;
        public String name;
    }

    @Test
    public void whenSerializingUsingJsonIgnoreType_thenCorrect() throws Exception {

        User.Name name = new User.Name("John", "Doe");
        User user = new User(1, name);

        String result = objectMapper.writeValueAsString(user);

        log.info(result);

        assertThat(result).contains("1");
        assertThat(result).doesNotContain("name");
        assertThat(result).doesNotContain("John");
    }

    @Test
    public void whenDeserializingUsingJsonIgnoreType_thenCorrect() throws Exception {
        String json = "{\"name\": {\"firstName\": \"first\", \"lastName\": \"last\"}, \"id\":1 }";

        User user = objectMapper.readerFor(User.class)
                .readValue(json);

        assertThat(user.id).isEqualTo(1);
        assertThat(user.name).isNull();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class User {
        public int id;
        public Name name;

        @JsonIgnoreType
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Name {
            public String firstName;
            public String lastName;
        }
    }

    @Test
    public void whenSerializingUsingJsonInclude_thenCorrect() throws Exception {
        MyBean bean = new MyBean(1, null);

        String result = objectMapper.writeValueAsString(bean);

        assertThat(result).contains("1");
        assertThat(result).doesNotContain("name");
    }

    @Test
    public void whenDeserializingUsingJsonInclude_thenCorrect() throws Exception {
        String json = "{\"name\":\"My bean\", \"id\":\"1\"}";

        MyBean bean = objectMapper.readerFor(MyBean.class)
                .readValue(json);

        assertThat(bean.id).isEqualTo(1);
        assertThat(bean.name).isEqualTo("My bean");
    }

    /**
     * @JsonInclude 는 Serialization 에서만 사용됨
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyBean {
        public int id;
        public String name;
    }
}
