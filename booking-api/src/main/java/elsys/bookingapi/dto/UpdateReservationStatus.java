package elsys.bookingapi.dto;

import elsys.bookingapi.entity.ReservationStatus;

public record UpdateReservationStatus(
    ReservationStatus status
) { }
