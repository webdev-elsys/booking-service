package elsys.bookingapi.service;

import elsys.bookingapi.dto.ClientReservationRequest;
import elsys.bookingapi.dto.ProcessPendingReservation;
import elsys.bookingapi.entity.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationService {
    public List<String> getBookedRooms(String propertyUuid, LocalDate checkInDate, LocalDate checkOutDate);
    public void requestReservation(ClientReservationRequest reservationRequest);
    public List<Reservation> getPendingReservationsByProperty(String propertyUuid);
    public void processReservation(String reservationUuid, ProcessPendingReservation processData);
    public List<Reservation> getReservations();
}
