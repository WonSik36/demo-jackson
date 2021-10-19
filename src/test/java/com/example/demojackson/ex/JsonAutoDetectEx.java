package com.example.demojackson.ex;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * JsonAutoDetect 에 대한 테스트
 * 기본 Jackson Serialize/Deserialize 정책
 * @see com.fasterxml.jackson.databind.introspect.VisibilityChecker.Std
 */
@Log4j2
public class JsonAutoDetectEx {
    static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void serializeWithoutGetter() throws Exception {
        SomeDto dto = new SomeDto("value1", "value2", "value3", "value4");

        String result = objectMapper.writeValueAsString(dto);

        log.info(result);
        assertThat(result).isEqualTo("{\"privateValue\":\"value1\",\"packagePrivateValue\":\"value2\",\"protectedValue\":\"value3\",\"publicValue\":\"value4\"}");
    }

    @JsonAutoDetect(fieldVisibility = Visibility.ANY)
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SomeDto {
        private String privateValue;
        String packagePrivateValue;
        protected String protectedValue;
        public String publicValue;
    }
}
