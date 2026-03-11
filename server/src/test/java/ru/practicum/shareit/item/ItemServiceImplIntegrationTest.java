package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplIntegrationTest {

    private final ItemService itemService;
    private final EntityManager em;

    @Test
    void getItems_shouldReturnItemsWithLastAndNextBookings() {
        // 1. Создание Владелец - вещь
        User owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@test.ru");
        em.persist(owner);

        Item item = new Item();
        item.setName("Drill");
        item.setDescription("Powerful drill");
        item.setAvailable(true);
        item.setOwner(owner);
        em.persist(item);

        // 2. Создание Booker
        User booker = new User();
        booker.setName("Booker");
        booker.setEmail("booker@test.ru");
        em.persist(booker);

        // 3. Создаем прошлое бронирование
        Booking lastBooking = new Booking();
        lastBooking.setItem(item);
        lastBooking.setBooker(booker);
        lastBooking.setStart(Instant.now().minus(java.time.Duration.ofDays(1)));
        lastBooking.setEnd(Instant.now().minus(java.time.Duration.ofHours(20)));
        lastBooking.setState(BookingStatus.APPROVED);
        em.persist(lastBooking);

        // 4. Создаем будущее бронирование
        Booking nextBooking = new Booking();
        nextBooking.setItem(item);
        nextBooking.setBooker(booker);
        nextBooking.setStart(Instant.now().plus(java.time.Duration.ofDays(1)));
        nextBooking.setEnd(Instant.now().plus(java.time.Duration.ofDays(2)));
        nextBooking.setState(BookingStatus.APPROVED);
        em.persist(nextBooking);

        em.flush();
        em.clear(); // Очищаем кэш EntityManager, чтобы данные реально пошли из БД

        // 5. Вызываем сервис
        List<ItemDtoResponseWithBooking> result = itemService.getItems(owner.getId());

        // 6. Проверки
        assertThat(result, hasSize(1));
        ItemDtoResponseWithBooking itemDto = result.getFirst();

        assertThat(itemDto.getName(), equalTo("Drill"));

        // Проверяем, что LastBooking подтянулся
        assertThat(itemDto.getLastBooking(), notNullValue());
        assertThat(itemDto.getLastBooking().getId(), equalTo(lastBooking.getId()));
        assertThat(itemDto.getLastBooking().getBooker().getId(), equalTo(booker.getId()));

        // Проверяем, что NextBooking подтянулся
        assertThat(itemDto.getNextBooking(), notNullValue());
        assertThat(itemDto.getNextBooking().getId(), equalTo(nextBooking.getId()));
    }
}

