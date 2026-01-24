package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.routing.Coordinates;
import de.ostfale.va.application.domain.model.routing.RouteInfo;
import de.ostfale.va.application.port.out.ForCalculatingRoutes;
import de.ostfale.va.application.port.out.ForCalculatingTournamentRoutes;
import de.ostfale.va.application.port.out.ForGeoCodingLocations;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CalculateTournamentRoutesService implements ForCalculatingTournamentRoutes, UseLogging {

    private final ForGeoCodingLocations geocodingAdapter;
    private final ForCalculatingRoutes routingAdapter;

    public CalculateTournamentRoutesService(ForGeoCodingLocations geocodingAdapter,
                                            ForCalculatingRoutes routingAdapter) {
        this.geocodingAdapter = geocodingAdapter;
        this.routingAdapter = routingAdapter;
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

        Optional<RouteInfo> route = routingAdapter.calculateRoute(
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
    public Optional<Coordinates> getTournamentCoordinates(PlannedTournament tournament) {
        // Try with city name and country code first (more precise)
        Optional<Coordinates> coords = geocodingAdapter.geocodeCity(
                tournament.location(),
                tournament.countryCode()
        );

        // Fallback to general geocoding with full location string
        if (coords.isEmpty()) {
            String fullLocation = tournament.location() + ", " + tournament.countryCode();
            coords = geocodingAdapter.geocode(fullLocation);
        }

        return coords;
    }
}
