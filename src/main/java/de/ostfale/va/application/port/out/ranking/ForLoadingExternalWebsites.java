package de.ostfale.va.application.port.out.ranking;

public interface ForLoadingExternalWebsites {

    <T> T loadPageAndProcess(String url, PageProcessor<T> processor);
}
