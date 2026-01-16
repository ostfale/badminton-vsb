package de.ostfale.va.framework.out.filesystem;

import de.ostfale.va.application.port.in.ForProvidingPlannedTournamentStreams;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class FileSystemPlannedTournamentsStreamProvider implements ForProvidingPlannedTournamentStreams, UseFileSystemHandling, UseLogging {

    @Override
    public List<InputStream> getPlannedTournamentStreams() {
        var subDirName = ApplicationDirectoryConfiguration.TOURNAMENT_DIR_NAME;
        var result = getApplicationSubDir(subDirName);
        var streamList = readAllFiles(result);
        log().info("FileSystemPlannedTournamentsStreamProvider :: Found {} files in {}", streamList.size(), result);
        return streamList;
    }
}
