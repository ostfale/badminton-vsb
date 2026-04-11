package de.ostfale.va.framework.in.web;

import de.ostfale.va.application.port.in.ranking.ForBatchProcessingRankingFiles;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ForBatchProcessingRankingFiles rankingFileProcessor;

    public AdminController(ForBatchProcessingRankingFiles rankingFileProcessor) {
        this.rankingFileProcessor = rankingFileProcessor;
    }

    @PostMapping("/process-rankings")
    public ResponseEntity<Void> triggerBatchProcessing() {
        rankingFileProcessor.processRankingFiles();

        // 202 Accepted is correct status for async jobs
        return ResponseEntity.accepted().build();
    }
}
