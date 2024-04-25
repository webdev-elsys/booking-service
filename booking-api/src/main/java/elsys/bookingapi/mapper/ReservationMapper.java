package elsys.bookingapi.mapper;

import elsys.bookingapi.dto.ClientReservationRequest;
import elsys.bookingapi.entity.Reservation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ReservationMapper {
    ReservationMapper reservationMapper = Mappers.getMapper(ReservationMapper.class);

    Reservation fromClientReservationRequest(ClientReservationRequest clientReservationRequest);
}
