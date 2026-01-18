package de.ostfale.va.framework.out.filesystem;

import de.ostfale.va.application.port.in.ForCreatingDirectoryStructure;
import de.ostfale.va.common.UseLogging;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ApplicationStartupRunner implements CommandLineRunner, UseLogging {

    private final ForCreatingDirectoryStructure directoryStructureService;

    public ApplicationStartupRunner(ForCreatingDirectoryStructure directoryStructureService) {
        log().info("ApplicationStartupRunner :: Created");
        this.directoryStructureService = directoryStructureService;
    }

    @Override
    public void run(String @NonNull ... args) throws Exception {
        directoryStructureService.validateAndCreateDirectoryStructure();
    }
}
