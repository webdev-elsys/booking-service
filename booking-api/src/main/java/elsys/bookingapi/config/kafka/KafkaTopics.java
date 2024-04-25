package elsys.bookingapi.config.kafka;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class KafkaTopics {
    public static String PENDING;
    public static String CONFIRMED;
    public static String REJECTED;
    public static String CANCELLED;
    public static String COMPLETED;

    @Value("${kafka.reservation-pending-topic}")
    private String pending;

    @Value("${kafka.reservation-confirmed-topic}")
    private String confirmed;

    @Value("${kafka.reservation-rejected-topic}")
    private String rejected;

    @Value("${kafka.reservation-cancelled-topic}")
    private String cancelled;

    @Value("${kafka.reservation-completed-topic}")
    private String completed;

    @PostConstruct
    private void init() {
        PENDING = pending;
        CONFIRMED = confirmed;
        REJECTED = rejected;
        CANCELLED = cancelled;
        COMPLETED = completed;
    }
}
