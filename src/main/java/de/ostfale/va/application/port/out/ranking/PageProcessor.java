package de.ostfale.va.application.port.out.ranking;

import com.microsoft.playwright.Page;

import java.util.Optional;

@FunctionalInterface
public interface PageProcessor<T> {

    Optional<T> process(Page page);
}
