package de.ostfale.va.framework.out.routing;

import de.ostfale.va.application.domain.model.routing.Coordinates;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Optional;

@Component
public class NominatimGeocodingAdapter implements UseLogging {

    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    private static final String USER_AGENT = "BadmintonVSB/1.0";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public NominatimGeocodingAdapter() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    public Optional<Coordinates> geocode(String location) {
        try {
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);
            String url = String.format("%s?q=%s&format=json&limit=1", NOMINATIM_URL, encodedLocation);
            log().info("Nominatim request URL: {}", url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log().warn("Geocoding request failed with status: {}. Response: {}", response.statusCode(), response.body());
                return Optional.empty();
            }

            log().debug("Nominatim response: {}", response.body());
            return parseGeocodingResponse(response.body());

        } catch (Exception e) {
            log().error("Error geocoding location: {}", location, e);
            return Optional.empty();
        }
    }

    public Optional<Coordinates> geocodeCity(String cityName, String countryCode) {
        String query = cityName + ", " + countryCode;
        return geocode(query);
    }

    public Optional<Coordinates> geocodeByPostalCode(String postalCode, String countryCode) {
        try {
            String encodedPostalCode = URLEncoder.encode(postalCode, StandardCharsets.UTF_8);
            String encodedCountryCode = URLEncoder.encode(countryCode, StandardCharsets.UTF_8);
            String url = String.format("%s?postalcode=%s&country=%s&format=json&limit=1",
                    NOMINATIM_URL, encodedPostalCode, encodedCountryCode);
            log().info("Nominatim request URL (postal code): {}", url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", USER_AGENT)
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log().warn("Geocoding request failed with status: {}. Response: {}", response.statusCode(), response.body());
                return Optional.empty();
            }

            log().debug("Nominatim response (postal code): {}", response.body());
            return parseGeocodingResponse(response.body());

        } catch (Exception e) {
            log().error("Error geocoding postal code: {} in country: {}", postalCode, countryCode, e);
            return Optional.empty();
        }
    }

    private Optional<Coordinates> parseGeocodingResponse(String jsonResponse) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);

        if (!root.isArray() || root.isEmpty()) {
            log().warn("Nominatim returned empty or non-array result.");
            return Optional.empty();
        }

        JsonNode firstResult = root.get(0);
        double lat = firstResult.get("lat").asDouble();
        double lon = firstResult.get("lon").asDouble();

        return Optional.of(new Coordinates(lat, lon));
    }
}
