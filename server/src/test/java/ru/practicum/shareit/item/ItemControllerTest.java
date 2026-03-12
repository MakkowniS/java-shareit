package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.comment.dto.CommentDtoRequest;
import ru.practicum.shareit.item.comment.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponseShort;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBooking;


import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private ItemDtoResponseShort itemDtoShort;
    private final String headerName = "X-Sharer-User-Id";

    @BeforeEach
    void setUp() {
        itemDtoShort = new ItemDtoResponseShort(1L, "Drill", "Powerful", true, null);
    }

    @Test
    void saveItem_shouldReturn200() throws Exception {
        ItemDtoRequest request = new ItemDtoRequest("Drill", "Powerful", true, null);

        when(itemService.saveItem(any(), anyLong())).thenReturn(itemDtoShort);

        mvc.perform(post("/items")
                        .header(headerName, 1L) // Передаем заголовок X-Sharer-User-Id
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoShort.getId()), Long.class))
                .andExpect(jsonPath("$.name", is("Drill")));
    }

    @Test
    void getItems_shouldReturnList() throws Exception {
        ItemDtoResponseWithBooking itemWithBooking = new ItemDtoResponseWithBooking();
        itemWithBooking.setId(1L);
        itemWithBooking.setName("Drill");

        when(itemService.getItems(anyLong())).thenReturn(List.of(itemWithBooking));

        mvc.perform(get("/items")
                        .header(headerName, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Drill")));
    }

    @Test
    void getItemById_shouldReturnItemWithBookings() throws Exception {
        ItemDtoResponseWithBooking responseDto = new ItemDtoResponseWithBooking();
        responseDto.setId(1L);
        responseDto.setName("Item Name");

        when(itemService.getItemById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Item Name")));

        verify(itemService).getItemById(1L, 1L);
    }

    @Test
    void searchItem_shouldUseRequestParam() throws Exception {
        when(itemService.searchItemToRent(anyString())).thenReturn(List.of(itemDtoShort));

        mvc.perform(get("/items/search")
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name", is("Drill")));
    }

    @Test
    void editItem_shouldReturnUpdatedItem() throws Exception {
        ItemDtoRequest updateDto = new ItemDtoRequest("New Name", "New Desc", true, null);
        ItemDtoResponseShort responseDto = new ItemDtoResponseShort(1L, "New Name", "New Desc", true, null);

        when(itemService.editItem(any(ItemDtoRequest.class), anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updateDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("New Name")));

        verify(itemService).editItem(any(), eq(1L), eq(1L));
    }

    @Test
    void saveComment_shouldReturnComment() throws Exception {
        CommentDtoResponse commentResponse = new CommentDtoResponse(1L, "Good", "Author", LocalDateTime.now());

        when(itemService.saveComment(any(), anyLong(), anyLong())).thenReturn(commentResponse);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header(headerName, 1L)
                        .content(mapper.writeValueAsString(new CommentDtoRequest())) // Пустой объект для краткости
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text", is("Good")));
    }

    @Test
    void saveItem_whenNoHeader_shouldReturn400() throws Exception {
        // Тестируем отсутствие заголовка
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(new ItemDtoRequest()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Ожидаем 400 Bad Request
    }
}