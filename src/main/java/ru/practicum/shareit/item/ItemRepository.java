package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.error.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findByOwner_Id(Long ownerId);

    @Query("""
                    select i from Item i
                    where i.available = true
                    and (lower(i.name) like lower(concat('%', :text, '%'))
                    or lower(i.description) like lower(concat('%', :text, '%')))
            """)
    List<Item> searchItemsToRent(String text);

    Boolean existsByOwner_Id(Long ownerId);

    Boolean existsByIdAndOwner_Id(Long itemId, Long ownerId);

    List<Item> findAllByRequestIdIn(List<Long> requestIds);

    default Item findByIdOrThrow(Long id) {
        return findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id:" + id + " не найдена"));
    }

}
