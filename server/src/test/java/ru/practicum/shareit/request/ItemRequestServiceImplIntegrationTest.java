package ru.practicum.shareit.request;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {

    private final ItemRequestService service;
    private final EntityManager em;

    @Test
    void getAllItemRequests_shouldReturnRequestsInCorrectOrder() {
        // Создаем пользователя
        User user = new User(null, "Requester", "req@mail.ru");
        em.persist(user);

        // Создаем два запроса с разным временем
        ItemRequest req1 = new ItemRequest(null, "Oldest", user, LocalDateTime.now().minusDays(1));
        ItemRequest req2 = new ItemRequest(null, "Newest", user, LocalDateTime.now());
        em.persist(req1);
        em.persist(req2);
        em.flush();

        // Вызываем метод
        List<ItemRequestDto> result = service.getAllItemRequests(user.getId());

        // Проверяем порядок (Newest должен быть первым)
        assertThat(result, hasSize(2));
        assertThat(result.get(0).getDescription(), equalTo("Newest"));
        assertThat(result.get(1).getDescription(), equalTo("Oldest"));
    }
}