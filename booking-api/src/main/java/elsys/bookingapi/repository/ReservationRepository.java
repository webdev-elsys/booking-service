package elsys.bookingapi.repository;

import elsys.bookingapi.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, String> {
    List<Reservation> getAllByPropertyUuidAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(String propertyUuid, LocalDate checkIn, LocalDate checkOut);
    Reservation findByRoomUuidAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(String roomUuid, LocalDate checkIn, LocalDate checkOut);
}
