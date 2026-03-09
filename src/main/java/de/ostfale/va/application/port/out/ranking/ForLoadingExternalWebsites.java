package de.ostfale.va.application.port.out.ranking;

import java.util.Optional;

public interface ForLoadingExternalWebsites {

    <T> Optional<T> loadPageAndProcess(String url, PageProcessor<T> processor);
}
