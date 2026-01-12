package de.ostfale.va.framework.in.ui.app;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.dom.Style;
import com.vaadin.flow.router.Layout;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.dashboard.DashboardView;
import de.ostfale.va.framework.in.ui.PlayerStats;
import de.ostfale.va.framework.in.ui.plannedtournaments.PlannedTournamentsView;
import de.ostfale.va.framework.in.ui.playerranking.PlayerRankingView;

import static com.vaadin.flow.component.icon.VaadinIcon.*;

@Layout
public final class MainLayout extends AppLayout implements UseLogging {

    public MainLayout() {
        log().info("MainLayout :: Created");
        setPrimarySection(Section.DRAWER);

        // Add a toggle to the header so users can open/close the drawer
        var drawerToggle = new DrawerToggle();
        //    drawerToggle.getElement().getStyle().set("margin-inline-start", "var(--lumo-space-m)");
        addToNavbar(drawerToggle);
        addToDrawer(createHeader(), new Scroller(createSideNav()));
    }

    private Component createHeader() {
        Image appLogo = new Image("images/shuttle_logo.png", "Application Logo");
        appLogo.setWidth("60px");
        appLogo.setHeight(null);

        // Ensure no background or border is forced by CSS
        appLogo.getStyle().set("background-color", "transparent");
        appLogo.getStyle().set("object-fit", "contain");

        var appName = new Span("Badminton Stats");
        appName.getStyle().setFontWeight(Style.FontWeight.BOLD);
        appName.getStyle().set("font-size", "1.2rem"); // Scale text to match larger logo

        var header = new VerticalLayout(appLogo, appName);
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(true); // Add some space around the larger logo
        header.setSpacing(false);
        return header;
    }

    private SideNav createSideNav() {
        var sideNav = new SideNav();
        sideNav.addItem(
                new SideNavItem("Dashboard", "/" + DashboardView.PATH, DASHBOARD.create()),
                new SideNavItem("Geplante Turniere", "/" + PlannedTournamentsView.PATH, CALENDAR.create()),
                new SideNavItem("Spieler Statistik", "/" + PlayerStats.PATH, LINE_CHART.create()),
                new SideNavItem("Spieler Ranglisten", "/" + PlayerRankingView.PATH, USER_CARD.create()),
                new SideNavItem("About", "/" + AboutView.PATH, QUESTION_CIRCLE.create())
        );
        //   MenuConfiguration.getMenuEntries().forEach(entry -> nav.addItem(createSideNavItem(entry)));
        return sideNav;
    }
}
