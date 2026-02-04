package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.routing.Coordinates;
import de.ostfale.va.application.domain.model.routing.RouteInfo;
import de.ostfale.va.application.port.out.ForRoutingAndGeocoding;
import de.ostfale.va.common.UseLogging;

import java.util.Optional;

public class CalculateTournamentRoutesService implements ForRoutingAndGeocoding, UseLogging {

    private final ForRoutingAndGeocoding routingAndGeocodingAdapter;

    public CalculateTournamentRoutesService(ForRoutingAndGeocoding routingAndGeocodingAdapter) {
        this.routingAndGeocodingAdapter = routingAndGeocodingAdapter;
    }

    @Override
    public Optional<RouteInfo> calculateRouteFromHamburg(PlannedTournament tournament) {
        log().debug("Calculating route from Hamburg to {}", tournament.location());
        Optional<Coordinates> destinationCoords = getTournamentCoordinates(tournament);

        if (destinationCoords.isEmpty()) {
            log().warn("Could not geocode location: {} for tournament: {}",
                    tournament.location(), tournament.tournamentName());
            return Optional.empty();
        }

        Optional<RouteInfo> route = routingAndGeocodingAdapter.calculateRoute(
                HAMBURG_COORDINATES,
                destinationCoords.get()
        );

        if (route.isEmpty()) {
            log().warn("Could not calculate route to: {}", tournament.location());
        } else {
            log().debug("Route calculated: {} km, {} minutes",
                    route.get().distanceInKm(),
                    route.get().durationInMinutes());
        }

        return route;
    }

    @Override
    public Optional<Coordinates> geocode(String location) {
        return routingAndGeocodingAdapter.geocode(location);
    }

    @Override
    public Optional<Coordinates> geocodeCity(String cityName, String countryCode) {
        return routingAndGeocodingAdapter.geocodeCity(cityName, countryCode);
    }

    @Override
    public Optional<Coordinates> geocodeByPostalCode(String postalCode, String countryCode) {
        return routingAndGeocodingAdapter.geocodeByPostalCode(postalCode, countryCode);
    }

    @Override
    public Optional<RouteInfo> calculateRoute(Coordinates origin, Coordinates destination) {
        return routingAndGeocodingAdapter.calculateRoute(origin, destination);
    }

    @Override
    public Optional<Coordinates> getTournamentCoordinates(PlannedTournament tournament) {
        // Try with postal code and country code first (most precise)
        if (tournament.postalCode() != null && !tournament.postalCode().isBlank()) {
            Optional<Coordinates> coords = routingAndGeocodingAdapter.geocodeByPostalCode(
                    tournament.postalCode(),
                    tournament.countryCode()
            );
            if (coords.isPresent()) {
                return coords;
            }
        }

        // Fallback to city name and country code
        Optional<Coordinates> coords = routingAndGeocodingAdapter.geocodeCity(
                tournament.location(),
                tournament.countryCode()
        );

        // Final fallback to general geocoding with full location string
        if (coords.isEmpty()) {
            String fullLocation = tournament.location() + ", " + tournament.countryCode();
            coords = routingAndGeocodingAdapter.geocode(fullLocation);
        }

        return coords;
    }
}
