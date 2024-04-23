package elsys.bookingapi.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "reservations")
@Data
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String uuid;

    @Column(nullable = false, updatable = false)
    private String propertyUuid;

    @Column(nullable = false)
    private String roomUuid;

    @Column(nullable = false, updatable = false)
    private String clientUuid;

    @Column(nullable = false)
    private LocalDate checkIn;

    @Column(nullable = false)
    private LocalDate checkOut;

    @Column(nullable = false)
    private int guests;

    @Column(nullable = false, columnDefinition = "DECIMAL(10, 2)")
    private float totalPrice;

    @Column(nullable = false)
    private ReservationStatus status;

    @Column(columnDefinition = "TEXT")
    private String comment;
}
