package de.ostfale.va.framework.out.routing;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.routing.Coordinates;
import de.ostfale.va.application.domain.model.routing.RouteInfo;
import de.ostfale.va.application.port.out.ForRoutingAndGeocoding;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class RoutingAndGeocodingAdapter implements ForRoutingAndGeocoding {

    private final OsrmRoutingAdapter routingAdapter;
    private final NominatimGeocodingAdapter geocodingAdapter;

    public RoutingAndGeocodingAdapter(OsrmRoutingAdapter routingAdapter,
                                      NominatimGeocodingAdapter geocodingAdapter) {
        this.routingAdapter = routingAdapter;
        this.geocodingAdapter = geocodingAdapter;
    }

    @Override
    public Optional<RouteInfo> calculateRoute(Coordinates origin, Coordinates destination) {
        return routingAdapter.calculateRoute(origin, destination);
    }

    @Override
    public Optional<RouteInfo> calculateRouteFromHamburg(PlannedTournament tournament) {
        Optional<Coordinates> destinationCoords = getTournamentCoordinates(tournament);
        if (destinationCoords.isEmpty()) {
            return Optional.empty();
        }
        return calculateRoute(HAMBURG_COORDINATES, destinationCoords.get());
    }

    @Override
    public Optional<Coordinates> geocode(String location) {
        return geocodingAdapter.geocode(location);
    }

    @Override
    public Optional<Coordinates> geocodeCity(String cityName, String countryCode) {
        return geocodingAdapter.geocodeCity(cityName, countryCode);
    }

    @Override
    public Optional<Coordinates> geocodeByPostalCode(String postalCode, String countryCode) {
        return geocodingAdapter.geocodeByPostalCode(postalCode, countryCode);
    }

    @Override
    public Optional<Coordinates> getTournamentCoordinates(PlannedTournament tournament) {
        // Try with postal code and country code first (most precise)
        if (tournament.postalCode() != null && !tournament.postalCode().isBlank()) {
            Optional<Coordinates> coords = geocodeByPostalCode(
                    tournament.postalCode(),
                    tournament.countryCode()
            );
            if (coords.isPresent()) {
                return coords;
            }
        }

        // Fallback to city name and country code
        Optional<Coordinates> coords = geocodeCity(
                tournament.location(),
                tournament.countryCode()
        );

        // Final fallback to general geocoding with full location string
        if (coords.isEmpty()) {
            String fullLocation = tournament.location() + ", " + tournament.countryCode();
            coords = geocode(fullLocation);
        }

        return coords;
    }
}
