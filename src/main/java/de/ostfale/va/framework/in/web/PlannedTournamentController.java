package de.ostfale.va.framework.in.web;

import de.ostfale.va.application.port.in.ForLoadingPlannedTournaments;
import de.ostfale.va.common.UseLogging;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlannedTournamentController implements UseLogging {

    private final ForLoadingPlannedTournaments importUC;

    public PlannedTournamentController(ForLoadingPlannedTournaments importUC) {
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
