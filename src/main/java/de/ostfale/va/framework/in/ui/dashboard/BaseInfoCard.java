package de.ostfale.va.framework.in.ui.dashboard;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import de.ostfale.va.common.UseFileSystemHandling;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.common.UseTimeHandling;

public abstract class BaseInfoCard extends VerticalLayout implements UseLogging, UseTimeHandling, UseFileSystemHandling {

    private final VerticalLayout contentContainer = new VerticalLayout();
    private final HorizontalLayout actionLayout = new HorizontalLayout();

    public BaseInfoCard(String imagePath, String imageAlt) {
        addClassName("info-card"); // goes to info-card.css
        setSpacing(false);
        setPadding(false);

        // image handling
        Image image = new Image(imagePath, imageAlt);

        // title
        H2 title = new H2(getTitle());
        title.getStyle().set("margin", "0");
        title.getStyle().set("padding", "1rem");

        // content area
        contentContainer.setPadding(true);
        contentContainer.setSpacing(false);
        contentContainer.getStyle().set("flex-grow", "1");

        // action bar
        actionLayout.setWidthFull();
        actionLayout.setPadding(true);
        actionLayout.setSpacing(false);
        actionLayout.setJustifyContentMode(JustifyContentMode.START);

        add(image, title, contentContainer, actionLayout);
    }

    protected abstract String getTitle();

    protected void clearContent() {
        contentContainer.removeAll();
    }

    protected void addContent(Component... components) {
        contentContainer.add(components);
    }

    protected void addAction(Button button) {
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        actionLayout.add(button);
    }

    protected Button createIconButton(VaadinIcon icon, String tooltip, Runnable clickHandler) {
        Button button = new Button(icon.create());
        button.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_TERTIARY);
        button.setTooltipText(tooltip);
        button.addClickListener(e -> clickHandler.run());
        return button;
    }

    protected HorizontalLayout createStatRow(String labelText, String valueText, boolean indented) {
        Span label = new Span(labelText);
        Span value = new Span(valueText);
        value.getStyle().set("font-weight", "normal");

        HorizontalLayout row = new HorizontalLayout(label, value);
        row.setWidthFull();
        row.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);


        if (indented) {
            // Apply a "tab" (indentation) to the left and keep grouped rows tight.
            row.getStyle().set("padding-left", "2rem");
            row.getStyle().set("margin-bottom", "0.20rem");
        } else {
            // Keep a stronger visual separation before grouped rows.
            row.getStyle().set("margin-bottom", "0.75rem");
        }
        return row;
    }
}
