package de.ostfale.va.application.port.out;

import de.ostfale.va.application.domain.model.routing.Coordinates;

import java.util.Optional;

public interface ForGeoCodingLocations {

    Optional<Coordinates> geocode(String location);

    Optional<Coordinates> geocodeCity(String cityName, String countryCode);
}
