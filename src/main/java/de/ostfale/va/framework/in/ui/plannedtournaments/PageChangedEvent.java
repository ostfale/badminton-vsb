package de.ostfale.va.framework.in.ui.plannedtournaments;

import de.ostfale.va.common.UseLogging;
import org.springframework.context.ApplicationEvent;

/**
 * Event published when pagination state changes (page number or page size).
 */
public class PageChangedEvent extends ApplicationEvent implements UseLogging {

    private final int pageNumber;
    private final int pageSize;
    private final int offset;

    public PageChangedEvent(Object source, int pageNumber, int pageSize, int offset) {
        super(source);
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.offset = offset;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getOffset() {
        return offset;
    }
}
