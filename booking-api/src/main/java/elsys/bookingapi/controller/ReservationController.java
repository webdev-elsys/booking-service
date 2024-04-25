package elsys.bookingapi.controller;

import elsys.bookingapi.dto.ClientReservationRequest;
import elsys.bookingapi.dto.UpdateReservationStatus;
import elsys.bookingapi.entity.Reservation;
import elsys.bookingapi.service.ReservationService;
import jakarta.validation.constraints.Future;
import lombok.RequiredArgsConstructor;
import org.hibernate.validator.constraints.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final ReservationService reservationService;

    @GetMapping("/booked-rooms")
    public ResponseEntity<List<String>> getBookedRooms(
        @RequestParam("propertyUuid") @UUID String propertyUuid,
        @RequestParam("checkInDate") @Future LocalDate checkInDate,
        @RequestParam("checkOutDate") @Future LocalDate checkOutDate
    ) {
        return ResponseEntity.ok(reservationService.getBookedRooms(propertyUuid, checkInDate, checkOutDate));
    }

    @PostMapping()
    public ResponseEntity<Void> requestReservation(@RequestBody ClientReservationRequest reservationRequest) {
        reservationService.requestReservation(reservationRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending")
    public ResponseEntity<List<Reservation>> getPendingReservationsByProperty(@RequestParam("propertyUuid") @UUID String propertyUuid) {
        return ResponseEntity.ok(reservationService.getPendingReservationsByProperty(propertyUuid));
    }

    @PatchMapping("/{reservationUuid}/status")
    public ResponseEntity<Void> updateReservationStatus(
        @PathVariable @UUID String reservationUuid,
        @RequestBody UpdateReservationStatus updateData
    ) {
        reservationService.updateReservationStatus(reservationUuid, updateData);
        return ResponseEntity.ok().build();
    }
}
