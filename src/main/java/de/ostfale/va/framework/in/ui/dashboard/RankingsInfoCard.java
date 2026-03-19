package de.ostfale.va.framework.in.ui.dashboard;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.time.LocalDateTime;


public class RankingsInfoCard extends BaseInfoCard {

    private static final String IMAGE_PATH = "images/info_card_ranking.png";

    public RankingsInfoCard() {
        super(IMAGE_PATH, "Infos Rangliste");

        addContent(createStatRow("Letzter Download", LocalDateTime.now().toString(), false));
        setupActions();
        refresh();
    }

    public void refresh() {
    }

    private void setupActions() {
        Button downloadButton = createIconButton(VaadinIcon.DOWNLOAD, "Geplante Turniere herunterladen", this::handleDownload);
        Button updateButton = createIconButton(VaadinIcon.REFRESH, "Geplante Turniere aktualisieren", this::handleUpdate);

        addAction(downloadButton);
        addAction(updateButton);
    }

    private void handleUpdate() {
    }

    private void handleDownload() {
    }

    @Override
    protected String getTitle() {
        return "Rangliste KW " + getCurrentCalendarWeek() ;
    }
}
