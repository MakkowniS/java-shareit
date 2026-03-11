package ru.practicum.shareit.booking;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {

    private final BookingService bookingService;
    private final EntityManager em;

    @Test
    void getBookingsByState_shouldReturnPaginatedAndSorted() {
        User booker = new User(null, "Booker", "booker@mail.ru");
        em.persist(booker);
        User owner = new User(null, "Owner", "owner@mail.ru");
        em.persist(owner);
        Item item = new Item(null, "Item", "Desc", true, owner, null);
        em.persist(item);

        // Создаем два бронирования
        Booking b1 = new Booking(null, Instant.now().plus(Duration.ofDays(1)),
                Instant.now().plus(Duration.ofDays(2)), item, booker, BookingStatus.WAITING);
        Booking b2 = new Booking(null, Instant.now().plus(Duration.ofDays(3)),
                Instant.now().plus(Duration.ofDays(4)), item, booker, BookingStatus.WAITING);
        em.persist(b1);
        em.persist(b2);
        em.flush();

        // Вызываем сервис from=0, size=10, state="ALL"
        List<BookingDtoResponse> result = bookingService.getBookingsByState(booker.getId(), "ALL", 0, 10);

        assertThat(result, hasSize(2));
        assertThat(result.get(0).getId(), equalTo(b2.getId()));
        assertThat(result.get(1).getId(), equalTo(b1.getId()));
    }
}