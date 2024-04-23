package elsys.bookingapi.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDate;

public record ClientReservationRequest(
    @NotNull @UUID String propertyUuid,
    @NotNull @UUID String roomUuid,
    @NotNull @UUID String clientUuid,
    @Future LocalDate checkIn,
    @Future LocalDate checkOut,
    @NotNull @Positive int guests, String comment
) { }
