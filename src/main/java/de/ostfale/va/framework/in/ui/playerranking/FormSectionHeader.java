package de.ostfale.va.framework.in.ui.playerranking;

import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import de.ostfale.va.common.UseLogging;

public class FormSectionHeader extends HorizontalLayout implements UseLogging {
    public FormSectionHeader(String title) {
        setWidthFull();
        setAlignItems(Alignment.CENTER);
        setSpacing(false);

        H3 sectionTitle = new H3(title);
        Hr leftLine = new Hr();
        Hr rightLine = new Hr();

        add(leftLine, sectionTitle, rightLine);
        addClassName("form-section-header");
    }
}
