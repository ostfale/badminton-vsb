package de.ostfale.va.application.port.out;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.routing.Coordinates;
import de.ostfale.va.application.domain.model.routing.RouteInfo;

import java.util.Optional;

public interface ForRoutingAndGeocoding {

    Coordinates HAMBURG_COORDINATES = new Coordinates(53.5511, 9.9937);

    // Routing methods
    Optional<RouteInfo> calculateRoute(Coordinates origin, Coordinates destination);

    Optional<RouteInfo> calculateRouteFromHamburg(PlannedTournament tournament);

    // Geocoding methods
    Optional<Coordinates> geocode(String location);

    Optional<Coordinates> geocodeCity(String cityName, String countryCode);

    Optional<Coordinates> geocodeByPostalCode(String postalCode, String countryCode);

    Optional<Coordinates> getTournamentCoordinates(PlannedTournament tournament);
}
