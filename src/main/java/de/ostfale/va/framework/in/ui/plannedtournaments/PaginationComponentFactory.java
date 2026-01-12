package de.ostfale.va.framework.in.ui.plannedtournaments;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Factory for creating PaginationComponent instances with Spring support.
 * Use this factory to create pagination components in Vaadin views.
 */
@Component
public class PaginationComponentFactory {

    private final ApplicationEventPublisher eventPublisher;

    public PaginationComponentFactory(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates a new PaginationComponent instance with Spring event publishing support.
     * @return a new PaginationComponent instance
     */
    public PaginationComponent create() {
        return new PaginationComponent(eventPublisher);
    }
}
