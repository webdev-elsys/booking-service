package elsys.bookingapi.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import org.hibernate.validator.constraints.UUID;

import java.time.LocalDate;

@Data
public class ClientReservationRequest {
    @NotNull
    @UUID
    private String propertyUuid;

    @NotNull
    @UUID
    private String roomUuid;

    @NotNull
    @UUID
    private String clientUuid;

    @Future
    private LocalDate checkIn;

    @Future
    private LocalDate checkOut;

    @NotNull
    @Positive
    private int guests;

    private String comment;
}
