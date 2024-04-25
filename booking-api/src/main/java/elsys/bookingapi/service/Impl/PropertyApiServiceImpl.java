package elsys.bookingapi.service.Impl;

import elsys.bookingapi.api.property_api.PropertyApiEndpoints;
import elsys.bookingapi.service.PropertyApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class PropertyApiServiceImpl implements PropertyApiService {
    private final WebClient webClient;

    @Autowired
    public PropertyApiServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    public Float getRoomPrice(String propertyUuid, String roomUuid) {
        String endpoint = PropertyApiEndpoints.getRoomPrice(propertyUuid, roomUuid);

        return webClient.get()
                .uri(endpoint)
                .retrieve()
                .bodyToMono(Float.class)
                .block();
    }
}
