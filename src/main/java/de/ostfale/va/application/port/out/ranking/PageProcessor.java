package de.ostfale.va.application.port.out.ranking;

import com.microsoft.playwright.Page;

@FunctionalInterface
public interface PageProcessor<T> {

    T process(Page page);
}
