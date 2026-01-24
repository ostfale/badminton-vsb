package de.ostfale.va.application.port.out;

import de.ostfale.va.application.domain.model.routing.Coordinates;
import de.ostfale.va.application.domain.model.routing.RouteInfo;

import java.util.Optional;

public interface ForCalculatingRoutes {

    Optional<RouteInfo> calculateRoute(Coordinates origin, Coordinates destination);
}
