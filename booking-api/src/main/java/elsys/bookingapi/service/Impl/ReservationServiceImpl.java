package elsys.bookingapi.service.Impl;

import elsys.bookingapi.config.kafka.KafkaTopics;
import elsys.bookingapi.dto.ClientReservationRequest;
import elsys.bookingapi.dto.UpdateReservationStatus;
import elsys.bookingapi.entity.Reservation;
import elsys.bookingapi.entity.ReservationStatus;
import elsys.bookingapi.repository.ReservationRepository;
import elsys.bookingapi.service.ReservationService;
import elsys.bookingapi.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
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

        notificationDataKafkaTemplate.send(KafkaTopics.PENDING, reservation);
    }

    @Override
    public List<Reservation> getPendingReservationsByProperty(String propertyUuid) {
        return reservationRepository.getAllByPropertyUuidAndStatus(propertyUuid, ReservationStatus.PENDING);
    }

    @Override
    public void updateReservationStatus(String reservationUuid, UpdateReservationStatus updateData) {
        Reservation reservation = reservationRepository.findById(reservationUuid).orElseThrow(() -> new IllegalArgumentException("Reservation not found"));

        if (updateData.status() == ReservationStatus.PENDING) {
            throw new IllegalArgumentException("Invalid status");
        }

        reservation.setStatus(updateData.status());

        switch (updateData.status()) {
            case REJECTED -> {
                notificationDataKafkaTemplate.send(KafkaTopics.REJECTED, reservation);
                reservationRepository.delete(reservation);
            }
            case CANCELLED -> {
                notificationDataKafkaTemplate.send(KafkaTopics.CANCELLED, reservation);
                reservationRepository.delete(reservation);
            }
            case COMPLETED -> {
                notificationDataKafkaTemplate.send(KafkaTopics.COMPLETED, reservation);
                reservationRepository.delete(reservation);
            }
            case CONFIRMED -> {
                notificationDataKafkaTemplate.send(KafkaTopics.CONFIRMED, reservation);
                reservationRepository.save(reservation);
            }
            case EXECUTING -> {
                reservationRepository.save(reservation);
            }
        }
    }

    @Override
    public List<Reservation> getReservations() {
        return reservationRepository.findAll();
    }
}
