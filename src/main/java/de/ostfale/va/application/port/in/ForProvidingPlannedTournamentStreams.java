package de.ostfale.va.application.port.in;

import java.io.InputStream;
import java.util.List;

public interface ForProvidingPlannedTournamentStreams {

    List<InputStream> getPlannedTournamentStreams();
}
