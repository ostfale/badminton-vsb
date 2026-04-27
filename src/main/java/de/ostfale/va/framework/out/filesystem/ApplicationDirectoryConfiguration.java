package de.ostfale.va.framework.out.filesystem;

import de.ostfale.va.application.port.out.ForDirectoryConfig;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ApplicationDirectoryConfiguration implements ForDirectoryConfig, UseFileSystemHandling, UseLogging {

    public static final String APP_NAME = ".badminton-vsb";
    public static final String TOURNAMENT_DIR_NAME = "tournament";
    public static final String RANKING_DIR_NAME = "ranking";
    public static final String HISTORY_DIR_NAME = "ranking/history";


    @Override
    public String basePath() {
        var basePath = System.getProperty(USER_HOME_PROPERTY) + SEPARATOR + APP_NAME;
        log().info("ApplicationDirectoryConfiguration :: Application base path: {}", basePath);
        return basePath;
    }

    @Override
    public List<DirectoryEntry> structure() {
        return List.of(
                createDirectoryEntry("config", "config"),
                createDirectoryEntry("data", "data"),
                createDirectoryEntry("logs", "logs"),
                createDirectoryEntry(TOURNAMENT_DIR_NAME, "tournament"),
                createDirectoryEntry(RANKING_DIR_NAME, "ranking"),
                createDirectoryEntry("history", HISTORY_DIR_NAME)
        );
    }

    private DirectoryEntry createDirectoryEntry(String name, String path) {
        return new DirectoryEntry() {
            @Override
            public String path() {
                return path;
            }

            @Override
            public boolean createIfMissing() {
                return true;
            }

            @Override
            public boolean required() {
                return true;
            }

            @Override
            public String name() {
                return name;
            }
        };
    }
}
