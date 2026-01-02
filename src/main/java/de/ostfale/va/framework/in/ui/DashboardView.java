package de.ostfale.va.framework.in.ui;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import de.ostfale.va.common.UseLogging;

@Route(value = "", layout = MainLayout.class)
public class DashboardView extends VerticalLayout implements UseLogging{
    public static final String PATH = "";

    public DashboardView() {
        log().info("OverviewView :: constructor");
        setSizeFull();
        setPadding(true);
        setSpacing(true);
    }
}
