package com.example.demojackson.ex;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    /*
        for Serialize Only
     */
    @Test
    public void whenSerializingUsingJacksonReferenceAnnotation_thenCorrect() throws Exception {
        UserWithRef user = new UserWithRef(1, "John");
        ItemWithRef item = new ItemWithRef(2, "book", user);
        user.addItem(item);

        String result = objectMapper.writeValueAsString(item);

        log.info(result);

        assertThat(result).contains("book");
        assertThat(result).contains("John");
        assertThat(result).doesNotContain("userItems");
    }

    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemWithRef {   // parent
        public int id;
        public String itemName;

        @JsonManagedReference
        public UserWithRef owner;
    }

    @NoArgsConstructor
    public static class UserWithRef {   // child
        public int id;
        public String name;

        public UserWithRef(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @JsonBackReference
        public List<ItemWithRef> userItems = new ArrayList<>();

        public void addItem(ItemWithRef item) {
            userItems.add(item);
        }
    }

    @Test
    public void whenSerializingUsingJsonIdentityInfo_thenCorrect() throws Exception {
        UserWithIdentity user = new UserWithIdentity(1, "John");
        ItemWithIdentity item = new ItemWithIdentity(2, "book", user);
        user.addItem(item);

        String result = objectMapper.writeValueAsString(item);

        log.info(result);

        assertThat(result).contains("book");
        assertThat(result).contains("John");
        assertThat(result).contains("userItems");

        item = objectMapper.readerFor(ItemWithIdentity.class)
                .readValue(result);

        assertThat(item.getId()).isEqualTo(2);
        assertThat(item.getOwner().getId()).isEqualTo(1);
        assertThat(item).isSameAs(item.getOwner().getUserItems().get(0));
    }

    @Getter
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ItemWithIdentity {
        public int id;
        public String itemName;
        public UserWithIdentity owner;
    }

    @Getter
    @JsonIdentityInfo(
            generator = ObjectIdGenerators.PropertyGenerator.class,
            property = "id")
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserWithIdentity {
        public int id;
        public String name;
        public List<ItemWithIdentity> userItems = new ArrayList<>();

        public UserWithIdentity(int id, String name) {
            this.id = id;
            this.name = name;
        }

        public void addItem(ItemWithIdentity item) {
            userItems.add(item);
        }
    }
}
