package elsys.bookingapi.service.Impl;

import elsys.bookingapi.dto.ClientReservationRequest;
import elsys.bookingapi.dto.ProcessPendingReservation;
import elsys.bookingapi.entity.Reservation;
import elsys.bookingapi.entity.ReservationStatus;
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

    @Value("${kafka.reservation-process-topic}")
    private String reservationProcessTopic;

    private final ReservationRepository reservationRepository;
    private final PropertyApiServiceImpl propertyApiService;
    private final KafkaTemplate<String, Reservation> notificationDataKafkaTemplate;

    @Override
    public List<String> getBookedRooms(String propertyUuid, LocalDate checkInDate, LocalDate checkOutDate) {
        List<Reservation> reservations = reservationRepository.getAllByPropertyUuidAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(propertyUuid, checkOutDate, checkInDate);

        return reservations.stream()
                .map(Reservation::getRoomUuid)
                .toList();
    }

    @Override
    public void requestReservation(ClientReservationRequest reservationRequest) {
        Reservation reservation = reservationRepository.findByRoomUuidAndCheckInLessThanEqualAndCheckOutGreaterThanEqual(reservationRequest.roomUuid(), reservationRequest.checkOut(), reservationRequest.checkIn());

        if (reservation != null) {
            throw new IllegalArgumentException("Room is already booked");
        }

        float roomPrice = propertyApiService.getRoomPrice(reservationRequest.propertyUuid(), reservationRequest.roomUuid());

        reservation = ReservationMapper.reservationMapper.fromClientReservationRequest(reservationRequest);
        reservation.setStatus(ReservationStatus.PENDING);

        int nights = (int) (reservationRequest.checkOut().toEpochDay() - reservationRequest.checkIn().toEpochDay());
        reservation.setTotalPrice(roomPrice * nights);

        reservationRepository.save(reservation);
        notificationDataKafkaTemplate.send(reservationRequestTopic, reservation);
    }

    @Override
    public List<Reservation> getPendingReservationsByProperty(String propertyUuid) {
        return reservationRepository.getAllByPropertyUuidAndStatus(propertyUuid, ReservationStatus.PENDING);
    }

    @Override
    public void processReservation(String reservationUuid, ProcessPendingReservation processData) {
        Reservation reservation = reservationRepository.findById(reservationUuid).orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Reservation is not pending");
        }

        if (processData.isApproved()) {
            reservation.setStatus(ReservationStatus.CONFIRMED);
            reservationRepository.save(reservation);
        } else {
            reservation.setStatus(ReservationStatus.REJECTED);
            reservationRepository.delete(reservation);
        }

        notificationDataKafkaTemplate.send(reservationProcessTopic, reservation);
    }

    @Override
    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }
}
