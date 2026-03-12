package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestWithAnswersDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final String headerName = "X-Sharer-User-Id";
    private ItemRequestDto itemRequestDto;
    private ItemRequestWithAnswersDto itemRequestWithAnswersDto;

    @BeforeEach
    void setUp() {
        itemRequestDto = new ItemRequestDto(1L, "Description", LocalDateTime.now());
        itemRequestWithAnswersDto = new ItemRequestWithAnswersDto(1L, "Description", 1L, LocalDateTime.now(), List.of());
    }

    @Test
    void createItemRequest_shouldReturn200() throws Exception {
        ItemRequestRequestDto inputDto = new ItemRequestRequestDto("Description");

        when(itemRequestService.saveItemRequest(any(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .header(headerName, 1L)
                        .content(mapper.writeValueAsString(inputDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getItemRequestsWithItems_shouldReturnList() throws Exception {
        when(itemRequestService.getAllItemRequestsWithAnswers(anyLong()))
                .thenReturn(List.of(itemRequestWithAnswersDto));

        mvc.perform(get("/requests")
                        .header(headerName, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].description", is(itemRequestWithAnswersDto.getDescription())));
    }

    @Test
    void getAllItemRequests_shouldReturnList() throws Exception {
        when(itemRequestService.getAllItemRequests(anyLong()))
                .thenReturn(List.of(itemRequestDto));

        mvc.perform(get("/requests/all")
                        .header(headerName, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(itemRequestDto.getId()), Long.class));
    }

    @Test
    void getItemRequestById_shouldReturnRequest() throws Exception {
        when(itemRequestService.getItemRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestWithAnswersDto);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .header(headerName, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.description", is("Description")));
    }

    @Test
    void saveItem_whenNoHeader_shouldReturn400() throws Exception {
        // Тестируем отсутствие заголовка
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(new ItemRequestRequestDto()))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest()); // Ожидаем 400 Bad Request
    }
}