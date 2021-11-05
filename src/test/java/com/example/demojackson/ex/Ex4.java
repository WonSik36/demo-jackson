package com.example.demojackson.ex;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/*
    https://www.baeldung.com/jackson-annotations 예제를 바탕으로 작성
    Serialize / Deserialize Case 에 대해서 작성
 */
@Log4j2
public class Ex4 {
    static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    public void whenUsingJsonProperty_thenCorrect() throws Exception {
        MyBean bean = new MyBean(1, "My bean");

        String result = new ObjectMapper().writeValueAsString(bean);

        log.info(result);

        assertThat(result).contains("My bean");
        assertThat(result).contains("1");

        MyBean resultBean = objectMapper.readerFor(MyBean.class)
                .readValue(result);

        assertThat(resultBean.getTheName()).isEqualTo("My bean");
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class MyBean {
        public int id;
        private String name;

        @JsonProperty("name")
        public void setTheName(String name) {
            this.name = name;
        }

        @JsonProperty("name")
        public String getTheName() {
            return name;
        }
    }

    @Test
    public void whenSerializingUsingJsonFormat_thenCorrect() throws Exception {
        LocalDateTime date = LocalDateTime.of(2021,11,5,2,30,0);
        EventWithFormat event = new EventWithFormat("party", date);

        String result = objectMapper.writeValueAsString(event);

        log.info(result);

        assertThat(result).contains("05-11-2021 02:30:00");
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class EventWithFormat {
        public String name;

        @JsonFormat(pattern = "dd-MM-yyyy hh:mm:ss")
        public LocalDateTime eventDate;
    }

    @Test
    public void whenSerializingUsingJsonUnwrapped_thenCorrect() throws Exception {
        Money money = new Money(1000);
        UnwrapObject object = new UnwrapObject(money);

        String result = objectMapper.writeValueAsString(object);

        log.info(result);

        assertThat(result).contains("money");
        assertThat(result).doesNotContain("fee");

        object = objectMapper.readerFor(UnwrapObject.class)
                .readValue(result);

        assertThat(object.getFee().getMoney()).isEqualTo(1000);
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnwrapObject {
        @Getter
        @JsonUnwrapped
        public Money fee;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class Money {
        @Getter
        public long money;
    }
}
