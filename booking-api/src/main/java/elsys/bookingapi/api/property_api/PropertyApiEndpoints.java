package elsys.bookingapi.api.property_api;

public class PropertyApiEndpoints {
    private static final String baseUrl = System.getenv("PROPERTY_SERVICE_API_URL");
    private static final String properties = "/properties";

   public static String getRoomPrice(String propertyUuid, String roomUuid) {
        return baseUrl + properties + "/" + propertyUuid + "/rooms/" + roomUuid + "/pricePerNight";
    }
}
