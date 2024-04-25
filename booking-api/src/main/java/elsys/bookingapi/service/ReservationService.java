package elsys.bookingapi.service;

import elsys.bookingapi.dto.ClientReservationRequest;
import elsys.bookingapi.dto.UpdateReservationStatus;
import elsys.bookingapi.entity.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    public List<String> getBookedRooms(String propertyUuid, LocalDate checkInDate, LocalDate checkOutDate);
    public void requestReservation(ClientReservationRequest reservationRequest);
    public List<Reservation> getPendingReservationsByProperty(String propertyUuid);
    public void updateReservationStatus(String reservationUuid, UpdateReservationStatus updateData);
}
