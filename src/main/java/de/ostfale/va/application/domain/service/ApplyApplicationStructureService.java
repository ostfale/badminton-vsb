package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.port.in.ForCreatingDirectoryStructure;
import de.ostfale.va.application.port.out.ForDirectoryConfig;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplyApplicationStructureService implements ForCreatingDirectoryStructure, UseFileSystemHandling, UseLogging {

    private final ForDirectoryConfig directoryConfiguration;

    public ApplyApplicationStructureService(ForDirectoryConfig directoryConfiguration) {
        this.directoryConfiguration = directoryConfiguration;
    }

    @Override
    public List<String> validateAndCreateDirectoryStructure() {
        List<String> errors = new ArrayList<>();
        String basePath = directoryConfiguration.basePath();
        log().info("ApplyApplicationStructureService :: Validating directory structure for {}", basePath);

        Path baseDirectory = Paths.get(basePath);
        if (!validateAndCreateBaseDirectory(baseDirectory, errors)) {
            return errors; // Can't continue without base directory
        }

        processDirectoryStructure(baseDirectory, errors);
        return errors;
    }

    private boolean validateAndCreateBaseDirectory(Path baseDirectory, List<String> errors) {
        if (!Files.exists(baseDirectory)) {
            try {
                Files.createDirectories(baseDirectory);
                log().info("ApplyApplicationStructureService :: Created base directory: {}", baseDirectory);
            } catch (Exception e) {
                String error = "DirectoryManagerService :: Failed to create base directory: " + baseDirectory;
                log().error(error);
                errors.add(error);
                return false;
            }
        }
        return true;
    }

    private void processDirectoryStructure(Path baseDirectory, List<String> errors) {
        for (ForDirectoryConfig.DirectoryEntry entry : directoryConfiguration.structure()) {
            Path fullPath = baseDirectory.resolve(entry.path());
            processDirectoryEntry(entry, fullPath, errors);
        }
    }

    private void processDirectoryEntry(ForDirectoryConfig.DirectoryEntry entry, Path fullPath, List<String> errors) {
        if (!Files.exists(fullPath)) {
            handleMissingDirectory(entry, fullPath, errors);
        } else {
            validateExistingPath(fullPath, errors);
        }
    }

    private void handleMissingDirectory(ForDirectoryConfig.DirectoryEntry entry, Path fullPath, List<String> errors) {
        if (entry.createIfMissing()) {
            createDirectory(entry, fullPath, errors);
        } else if (entry.required()) {
            String error = "Required directory does not exist and creation is disabled: " + fullPath;
            log().error(error);
            errors.add(error);
        }
    }

    private void validateExistingPath(Path fullPath, List<String> errors) {
        log().trace("Validating existing directory: {}", fullPath);
        if (!Files.isDirectory(fullPath)) {
            String error = "Path exists but is not a directory: " + fullPath;
            log().error(error);
            errors.add(error);
        }
    }

    private void createDirectory(ForDirectoryConfig.DirectoryEntry entry, Path fullPath, List<String> errors) {
        try {
            Files.createDirectories(fullPath);
            log().info("Created directory: {}", fullPath);
        } catch (Exception e) {
            String error = "Failed to create directory: " + fullPath;
            log().error(error);
            errors.add(error);
            if (entry.required()) {
                log().error("Required directory creation failed: {}", fullPath);
            }
        }
    }
}
