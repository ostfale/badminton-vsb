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
        var streamList = readAllFilesAsStreams(result);
        log().debug("FileSystemPlannedTournamentsStreamProvider :: Found {} files in {}", streamList.size(), result);
        return streamList;
    }

    @Override
    public String getDownloadDateInFileName() {
        var subDirName = ApplicationDirectoryConfiguration.TOURNAMENT_DIR_NAME;
        var result = getApplicationSubDir(subDirName);
        var filesList = readAllFiles(result);
        if (filesList.isEmpty()) {
            log().warn("FileSystemPlannedTournamentsStreamProvider :: No files found in {}", result);
            return "";
        }
        var firstFileName = filesList.getFirst().getName();
        log().info("FileSystemPlannedTornamentsStreamProvider :: First file name is {}", firstFileName);
        return firstFileName;
    }
}
