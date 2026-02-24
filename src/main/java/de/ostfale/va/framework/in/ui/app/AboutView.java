package de.ostfale.va.framework.in.ui.app;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;

@Route(value = AboutView.PATH, layout = MainLayout.class)
public class AboutView extends VerticalLayout {

    public static final String PATH = "about";

    public AboutView(@Autowired(required = false) BuildProperties buildProperties) {
        String buildVersion = buildProperties != null ? buildProperties.getVersion() : "dev";
        String projectName = buildProperties != null ? buildProperties.getName() : "badminton-vsb";

        H1 title = new H1("About");
        H2 subtitle = new H2("Badminton Statistik");

        Paragraph version = new Paragraph(projectName + " v" + buildVersion);
        Paragraph author = new Paragraph("Created by: Uwe Sauerbrei");

        setSpacing(true);
        setPadding(true);
        setAlignItems(Alignment.CENTER);

        add(title, subtitle, version, author);
    }

}
