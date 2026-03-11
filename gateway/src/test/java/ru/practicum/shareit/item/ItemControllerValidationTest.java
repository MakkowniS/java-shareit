package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDtoRequest;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerValidationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemClient itemClient; // Мокаем клиент,

    @Test
    void createItem_whenNameIsBlank_shouldReturnBadRequest() throws Exception {
        ItemDtoRequest invalidDto = new ItemDtoRequest();
        invalidDto.setName(""); // Пустое имя — нарушение @NotBlank
        invalidDto.setDescription("Desc");
        invalidDto.setAvailable(true);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(invalidDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Должен вернуть 400
        // Проверяем что не было взаимодействия
        verifyNoInteractions(itemClient);
    }

    @Test
    void createItem_whenAvailableIsNull_shouldReturnBadRequest() throws Exception {
        ItemDtoRequest invalidDto = new ItemDtoRequest();
        invalidDto.setName("Name");
        invalidDto.setDescription("Desc");
        invalidDto.setAvailable(null); // Нарушение @NotNull

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(invalidDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}