package de.ostfale.va.common;

import de.ostfale.va.framework.out.filesystem.ApplicationDirectoryConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public interface UseFileSystemHandling extends UseLogging {

    String SEPARATOR = File.separator;
    String USER_HOME_PROPERTY = "user.home";

    default String getHomeDir() {
        var result = System.getProperty(USER_HOME_PROPERTY);
        log().debug("UseFileSystemHandling :: User home directory is {}", result);
        return result;
    }

    default String getApplicationHomeDir() {
        var result = getHomeDir() + SEPARATOR + ApplicationDirectoryConfiguration.APP_NAME;
        log().debug("UseFileSystemHandling :: Application home directory is {}", result);
        return result;
    }

    default String getApplicationSubDir(String subDirName) {
        log().debug("UseFileSystemHandling :: Getting subdirectory {} ", subDirName);
        return Paths.get(getApplicationHomeDir(), subDirName).toString();
    }

    default List<File> readAllFiles(String dirPath) {
        var result = Stream.ofNullable(new File(dirPath).listFiles())
                .flatMap(Stream::of)
                .filter(File::isFile)
                .toList();
        log().debug("UseFileSystemHandling :: Read {} files from {}", result.size(), dirPath);
        return result;
    }

    default List<InputStream> readAllFilesAsStreams(String dirPath) {
        log().debug("UseFileSystemHandling :: Reading all files in {}", dirPath);
        Path rootPath = Paths.get(dirPath);

        try (Stream<Path> pathStream = Files.list(rootPath)) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .map(this::toInputStream)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (IOException e) {
            log().error("UseFileSystemHandling :: Failed to list files in {}", dirPath, e);
            return Collections.emptyList();
        }
    }

    private InputStream toInputStream(Path path) {
        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            log().warn("UseFileSystemHandling :: Could not open stream for file: {}", path, e);
            return null;
        }
    }

    default boolean deleteAllFiles(String dirPath) {
        String logTag = "UseFileSystemHandling :: ";
        log().debug("{} Deleting all files in {}", logTag, dirPath);
        try (Stream<Path> pathStream = Files.list(Paths.get(dirPath))) {
            return pathStream
                    .filter(Files::isRegularFile)
                    .allMatch(this::deleteFileQuietly);
        } catch (IOException e) {
            log().error("{} Failed to list files in {}", logTag, dirPath, e);
            return false;
        }
    }

    private boolean deleteFileQuietly(Path path) {
        try {
            return Files.deleteIfExists(path);
        } catch (IOException e) {
            log().error("UseFileSystemHandling :: Failed to delete file: {}", path, e);
            return false;
        }
    }
}
