package de.ostfale.va.application.port.out.ranking;

import de.ostfale.va.application.domain.model.download.DownloadTask;

import java.util.List;

public interface ForRankingFileDownloadConfig {

    List<DownloadTask> getDownloadTasks();

}
