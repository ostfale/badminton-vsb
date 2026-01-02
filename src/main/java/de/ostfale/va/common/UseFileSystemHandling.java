package de.ostfale.va.common;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
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
        var result = getHomeDir();
        log().debug("UseFileSystemHandling :: Application home directory is {}", result);
        return result;
    }

    default String getApplicationSubDir(String subDirName) {
        log().debug("UseFileSystemHandling :: Getting subdirectory {} ", subDirName);
        return Paths.get(getApplicationHomeDir(), subDirName).toString();
    }

    default List<Path> readAllFiles(String dirPath) {
        log().debug("UseFileSystemHandling :: Reading all files in {}", dirPath);
        try (Stream<Path> stream = Files.list(Paths.get(dirPath))) {
            return stream.filter(Files::isRegularFile).toList();
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    default boolean deleteAllFiles(String dirPath) {
        log().debug("UseFileSystemHandling :: Deleting all files in {}", dirPath);
        List<Path> filesToDelete = readAllFiles(dirPath);
        if (filesToDelete.isEmpty()) {
            return true;
        }
        return filesToDelete.stream().allMatch(path -> {
            try {
                return Files.deleteIfExists(path);
            } catch (IOException e) {
                return false;
            }
        });
    }
}
