package de.ostfale.va.framework.in.web;

import de.ostfale.va.application.port.in.ForImportingPlannedTournaments;
import de.ostfale.va.common.UseLogging;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

@RestController
public class PlannedTournamentController implements UseLogging {

    private final ForImportingPlannedTournaments importUC;

    public PlannedTournamentController(ForImportingPlannedTournaments importUC) {
        this.importUC = importUC;
    }

   /* @PostMapping("/import")
    public void importTournaments(@RequestParam("files") MultipartFile[] files) {
        InputStream[] streams = Arrays.stream(files)
                .map(this::convertToInputStream)
                .toArray(InputStream[]::new);
        importUC.importFromSource(streams);
    }

    private InputStream convertToInputStream(MultipartFile file) {
        try {
            return file.getInputStream();
        } catch (IOException e) {
            log().error("Failed to get input stream from file: {}", file.getOriginalFilename(), e);
            throw new RuntimeException("Error processing uploaded file", e);
        }
    }*/
}
