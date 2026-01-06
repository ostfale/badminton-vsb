package de.ostfale.va.application.port.out;

import java.util.List;

public interface ForDirectoryConfiguration {

    String basePath();

    List<DirectoryEntry> structure();

    interface DirectoryEntry {
        String path();

        boolean createIfMissing();

        boolean required();

        String name();
    }
}
