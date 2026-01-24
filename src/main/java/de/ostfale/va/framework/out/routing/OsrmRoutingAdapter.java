package de.ostfale.va.framework.out.routing;

import de.ostfale.va.application.domain.model.routing.Coordinates;
import de.ostfale.va.application.domain.model.routing.RouteInfo;
import de.ostfale.va.application.port.out.ForCalculatingRoutes;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class OsrmRoutingAdapter implements ForCalculatingRoutes, UseLogging {

    private static final String OSRM_URL = "https://router.project-osrm.org/route/v1/driving";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public OsrmRoutingAdapter() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Optional<RouteInfo> calculateRoute(Coordinates origin, Coordinates destination) {
        try {
            String url = buildOsrmUrl(origin, destination);
            log().debug("OSRM URL: {}", url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(5))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log().warn("OSRM request failed with status: {} for URL: {}", response.statusCode(), url);
                log().warn("Response body: {}", response.body());
                return Optional.empty();
            }

            return parseRoutingResponse(response.body(), origin, destination);

        } catch (Exception e) {
            log().error("Error calculating route from {} to {}", origin, destination, e);
            return Optional.empty();
        }
    }

    private String buildOsrmUrl(Coordinates origin, Coordinates destination) {
        // OSRM expects lon,lat format
        // Use Locale.US to ensure dots instead of commas in coordinates
        return String.format(java.util.Locale.US, "%s/%f,%f;%f,%f?overview=full&geometries=geojson",
                OSRM_URL,
                origin.longitude(), origin.latitude(),
                destination.longitude(), destination.latitude()
        );
    }

    private Optional<RouteInfo> parseRoutingResponse(String jsonResponse,
                                                     Coordinates origin,
                                                     Coordinates destination) throws Exception {
        JsonNode root = objectMapper.readTree(jsonResponse);

        if (!root.has("routes") || root.get("routes").isEmpty()) {
            return Optional.empty();
        }

        JsonNode route = root.get("routes").get(0);

        // Distance is in meters, convert to km
        double distanceInKm = route.get("distance").asDouble() / 1000.0;

        // Duration is in seconds, convert to minutes
        double durationInMinutes = route.get("duration").asDouble() / 60.0;

        // Parse geometry (GeoJSON format)
        List<Coordinates> routeGeometry = parseGeometry(route.get("geometry"));

        return Optional.of(new RouteInfo(
                distanceInKm,
                durationInMinutes,
                routeGeometry,
                origin,
                destination
        ));
    }

    private List<Coordinates> parseGeometry(JsonNode geometryNode) {
        List<Coordinates> coordinates = new ArrayList<>();

        JsonNode coordinatesArray = geometryNode.get("coordinates");
        for (JsonNode coord : coordinatesArray) {
            // GeoJSON is [lon, lat] format
            double lon = coord.get(0).asDouble();
            double lat = coord.get(1).asDouble();
            coordinates.add(new Coordinates(lat, lon));
        }

        return coordinates;
    }
}
