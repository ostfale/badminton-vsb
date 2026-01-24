package de.ostfale.va.application.port.out;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.routing.Coordinates;
import de.ostfale.va.application.domain.model.routing.RouteInfo;

import java.util.Optional;

public interface ForCalculatingTournamentRoutes {

    Coordinates HAMBURG_COORDINATES = new Coordinates(53.5511, 9.9937);

    Optional<RouteInfo> calculateRouteFromHamburg(PlannedTournament tournament);

    Optional<Coordinates> getTournamentCoordinates(PlannedTournament tournament);
}
