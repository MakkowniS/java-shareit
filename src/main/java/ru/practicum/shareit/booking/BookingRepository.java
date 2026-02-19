package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // CURRENT USER BY STATE
    @Query("""
            select b
            from Booking b
            join fetch b.booker
            where b.booker.id = :bookerId
            and (
                    :state = 'ALL'
                or (:state = 'CURRENT' and b.start <= CURRENT_TIMESTAMP and b.end >= CURRENT_TIMESTAMP)
                or (:state = 'PAST' and b.end < CURRENT_TIMESTAMP)
                or (:state = 'FUTURE' and b.start > CURRENT_TIMESTAMP)
                or (:state = 'WAITING' and b.state = 'WAITING')
                or (:state = 'REJECTED' and b.state = 'REJECTED')
            )
            ORDER BY b.start DESC
            """)
    List<Booking> findCurrentUserBooking(Long bookerId, String state);

    // OWNER BY STATE
    @Query("""
            select distinct b
            from Booking b
            join fetch b.item i
            join fetch i.owner
            join fetch b.booker
            where i.owner.id = :ownerId
            and (
                    :state = 'ALL'
                or (:state = 'CURRENT' and b.start <= CURRENT_TIMESTAMP and b.end >= CURRENT_TIMESTAMP)
                or (:state = 'PAST' and b.end < CURRENT_TIMESTAMP)
                or (:state = 'FUTURE' and b.start > CURRENT_TIMESTAMP)
                or (:state = 'WAITING' and b.state = 'WAITING')
                or (:state = 'REJECTED' and b.state = 'REJECTED')
            )
            ORDER BY b.start DESC
            """)
    List<Booking> findOwnerBooking(Long ownerId, String state);

}
