package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoRequestJsonTest {

    @Autowired
    private JacksonTester<ItemDtoRequest> json;

    @Test
    void testSerialize() throws Exception {
        ItemDtoRequest dto = new ItemDtoRequest();
        dto.setName("Дрель");
        dto.setDescription("Мощная дрель");
        dto.setAvailable(true);
        dto.setRequestId(1L);

        JsonContent<ItemDtoRequest> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("Дрель");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("Мощная дрель");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isTrue();
        assertThat(result).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
    }

    @Test
    void testDeserialize() throws Exception {
        String content = "{\"name\":\"Молоток\",\"description\":\"Тяжелый\",\"available\":false}";

        ItemDtoRequest dto = json.parseObject(content);

        assertThat(dto.getName()).isEqualTo("Молоток");
        assertThat(dto.getDescription()).isEqualTo("Тяжелый");
        assertThat(dto.getAvailable()).isFalse();
    }
}