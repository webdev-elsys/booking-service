package elsys.bookingapi.service.Impl;

import elsys.bookingapi.dto.ClientReservationRequest;
import elsys.bookingapi.entity.Reservation;
import elsys.bookingapi.entity.ReservationStatus;
import elsys.bookingapi.dto.NotificationData;
import elsys.bookingapi.repository.ReservationRepository;
import elsys.bookingapi.service.ReservationService;
import elsys.bookingapi.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    @Value("${kafka.reservation-request-topic}")
    private String reservationRequestTopic;

    private final ReservationRepository reservationRepository;
    private final PropertyApiServiceImpl propertyApiService;
    private final KafkaTemplate<String, NotificationData> notificationDataKafkaTemplate;

    @Override
    public List<String> getBookedRooms(String propertyUuid, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Reservation> reservations = reservationRepository.getAllByPropertyUuidAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(propertyUuid, checkOutDate, checkInDate);

        return reservations.stream()
                .map(Reservation::getRoomUuid)
                .toList();
    }

    @Override
    public void requestReservation(ClientReservationRequest reservationRequest) {
        Reservation reservation = reservationRepository.findByRoomUuidAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(reservationRequest.getRoomUuid(), reservationRequest.getCheckOut(), reservationRequest.getCheckIn());

        if (reservation != null) {
            throw new IllegalArgumentException("Room is already booked");
        }

        float roomPrice = propertyApiService.getRoomPrice(reservationRequest.getPropertyUuid(), reservationRequest.getRoomUuid());

        reservation = ReservationMapper.reservationMapper.fromClientReservationRequest(reservationRequest);
        reservation.setStatus(ReservationStatus.PENDING);

        int nights = (int) (reservationRequest.getCheckOut().toEpochDay() - reservationRequest.getCheckIn().toEpochDay());
        reservation.setTotalPrice(roomPrice * nights);

        NotificationData notificationData = ReservationMapper.reservationMapper.toNotificationData(reservation);

        reservationRepository.save(reservation);
        notificationDataKafkaTemplate.send(reservationRequestTopic, notificationData);
    }

    @Override
    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }
}
