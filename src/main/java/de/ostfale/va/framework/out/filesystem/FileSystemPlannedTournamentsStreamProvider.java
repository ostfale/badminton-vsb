package de.ostfale.va.framework.out.filesystem;

import de.ostfale.va.application.port.in.ForProvidingPlannedTournamentStreams;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class FileSystemPlannedTournamentsStreamProvider implements ForProvidingPlannedTournamentStreams, UseLogging {

    @Override
    public List<InputStream> getPlannedTournamentStreams() {
        return List.of();
    }
}
