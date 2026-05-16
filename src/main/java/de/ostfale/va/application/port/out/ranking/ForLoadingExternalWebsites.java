package de.ostfale.va.application.port.out.ranking;

import java.util.Optional;

public interface ForLoadingExternalWebsites {

    /**
     * Loads a page from the given URL and processes it, returning an Optional result.
     * By default, returns an empty Optional.
     *
     * @param url       The URL of the page to load
     * @param processor The processor to extract information
     * @param <T>       The type of the expected result
     * @return An Optional containing the result, or empty
     */
    <T> Optional<T> loadPageAndProcess(String url, PageProcessor<T> processor);

}
