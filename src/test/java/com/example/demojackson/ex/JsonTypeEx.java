package com.example.demojackson.ex;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

/*
    https://www.baeldung.com/jackson-annotations 예제를 바탕으로 작성
    Polymorphic case 에 대해 작성
 */
@Log4j2
public class JsonTypeEx {

    final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void whenSerializingPolymorphic_thenCorrect() throws Exception {
        Dog dog = new Dog("lacy", 10);

        String result = objectMapper.writeValueAsString(dog);

        log.info(result);

        assertThat(result).contains("type");
        assertThat(result).contains("dog");
        assertThat(result).contains("name");
        assertThat(result).contains("lacy");
        assertThat(result).contains("barkVolume");
        assertThat(result).contains("10.0");
    }

    @Test
    public void whenDeserializingPolymorphic_thenCorrect() throws Exception {
        String json = "{\"type\":\"cat\",\"name\":\"lacy\",\"likesCream\":false, \"lives\":10 }";

        Animal animal = objectMapper.readerFor(Animal.class)
                .readValue(json);

        log.info(animal.getName());

        assertThat(animal).isInstanceOf(Cat.class);
        assertThat(animal.getName()).isEqualTo("cat: lacy");
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = As.PROPERTY, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Dog.class, name = "dog"),
            @JsonSubTypes.Type(value = Cat.class, name = "cat")
    })
    public static class Animal {

        public String name;

        public String getName() {
            return name;
        }
    }

    @JsonTypeName("dog")
    public static class Dog extends Animal {

        public double barkVolume;

        public Dog(String name, double barkVolume) {
            this.name = name;
            this.barkVolume = barkVolume;
        }
    }

    @JsonTypeName("cat")
    public static class Cat extends Animal {

        public boolean likesCream;
        public int lives;

        @Override
        public String getName() {
            return "cat: " + super.getName();
        }
    }
}
