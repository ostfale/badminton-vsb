package de.ostfale.va.application.port.out;

import de.ostfale.va.application.domain.model.download.DownloadTask;

import java.util.List;

public interface ForDownloadingFiles {

    void downloadFiles(List<DownloadTask> tasks);
}
