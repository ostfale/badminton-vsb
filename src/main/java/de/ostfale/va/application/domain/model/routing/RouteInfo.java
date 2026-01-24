package de.ostfale.va.application.domain.model.routing;

import java.util.List;

public record RouteInfo(
        double distanceInKm,
        double durationInMinutes,
        List<Coordinates> routeGeometry,
        Coordinates origin,
        Coordinates destination
) {
    public String getFormattedDistance() {
        return String.format("%.1f km", distanceInKm);
    }

    public String getFormattedDuration() {
        long hours = (long) (durationInMinutes / 60);
        long minutes = (long) (durationInMinutes % 60);
        return String.format("%d Std %d Min", hours, minutes);
    }
}
