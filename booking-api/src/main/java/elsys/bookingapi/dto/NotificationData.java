package elsys.bookingapi.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class NotificationData extends ClientReservationRequest {
    private Float totalPrice;
}
