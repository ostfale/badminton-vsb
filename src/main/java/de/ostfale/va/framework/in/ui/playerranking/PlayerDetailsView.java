package de.ostfale.va.framework.in.ui.playerranking;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.select.Select;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.common.UseLogging;

import java.util.List;
import java.util.stream.Stream;

public class PlayerDetailsView extends VerticalLayout implements UseLogging {

    private static final String PLAYER_SEPARATOR_SELECTION = "Spieler Suchen";
    private static final String PLAYER_SEPARATOR_DETAILS = "Spieler Details";
    private static final String PLAYER_RANKING_POINTS = "Spieler Ranglistenpunkte";

    ForLoadingRankings rankingService;

    private TextField playerNameField;
    private TextField playerIdField;
    private TextField playerGenderField;
    private TextField playerAgeClassField;
    private TextField playerYearOfBirthField;
    private TextField playerClubNameField;
    private TextField playerDistrictNameField;
    private TextField playerStateNameField;
    private TextField playerStateGroupField;

    public PlayerDetailsView(ForLoadingRankings rankingService) {
        log().debug("PlayerDetailsView :: constructor");
        this.rankingService = rankingService;
        initLayout();
    }

    public void initLayout() {
        add(new FormSectionHeader(PLAYER_SEPARATOR_SELECTION));

        Select<Player> cbPlayer = new Select<>();
        HorizontalLayout actionRow = createActionRow(cbPlayer);

        FormLayout dataBlock = new FormLayout();
        dataBlock.addFormItem(actionRow, "Favoriten");
        add(dataBlock);

        add(new FormSectionHeader(PLAYER_SEPARATOR_DETAILS));
        add(createDetailsBlock());

        add(new FormSectionHeader(PLAYER_RANKING_POINTS));
        add(createRankingMatrix());
    }

    private HorizontalLayout createActionRow(Select<Player> cbPlayer) {
        ComboBox<Player> searchPlayer = createSearchComboBox();

        // Listener für die ComboBox (Suche)
        searchPlayer.addValueChangeListener(event -> {
            Player selectedPlayer = event.getValue();
            if (selectedPlayer != null) {
                updatePlayerDetails(selectedPlayer);
                cbPlayer.setValue(null); // Optional: Favoriten-Auswahl leeren
            }
            else {
                clearDetails();
            }
        });

        Button reloadButton = createIconButton(VaadinIcon.REFRESH, "Neu laden");
        Button removeFavorite = createIconButton(VaadinIcon.ARROW_RIGHT, "Favoriten entfernen");
        Button addFavorite = createIconButton(VaadinIcon.ARROW_LEFT, "Favoriten hinzufügen");


        HorizontalLayout actionRow = new HorizontalLayout(cbPlayer, reloadButton, removeFavorite,
                addFavorite, searchPlayer);
        actionRow.setFlexGrow(1.0, cbPlayer, searchPlayer);
        actionRow.setFlexGrow(0, reloadButton, removeFavorite, addFavorite);
        actionRow.setVerticalComponentAlignment(FlexComponent.Alignment.BASELINE,
                cbPlayer, reloadButton, removeFavorite, addFavorite, searchPlayer);
        actionRow.setSpacing(true);
        actionRow.setWidthFull();

        return actionRow;
    }

    private Button createIconButton(VaadinIcon icon, String tooltip) {
        Button button = new Button();
        button.setIcon(icon.create());
        button.setTooltipText(tooltip);
        return button;
    }

    private ComboBox<Player> createSearchComboBox() {
        ComboBox<Player> searchPlayer = new ComboBox<>();
        searchPlayer.setPlaceholder("Spieler suchen...");
        searchPlayer.setHelperText("Mindestens 3 Buchstaben eingeben");
        searchPlayer.setClearButtonVisible(true);
        searchPlayer.setPrefixComponent(VaadinIcon.SEARCH.create());

        // define data provider
        searchPlayer.setItems(
                query -> {
                    String filter = query.getFilter().orElse("").trim();
                    // needed to be called -> contract
                    int offset = query.getOffset();
                    int limit = query.getLimit();

                    if (filter.length() < 3) return Stream.empty();

                    return rankingService.findPlayers(filter, offset, limit).stream();
                },
                query -> {
                    String filter = query.getFilter().orElse("").trim();
                    if (filter.length() < 3) return 0;

                    // needed to know the total number of items
                    return rankingService.countPlayers(filter);
                }
        );

        searchPlayer.setItemLabelGenerator(p -> p.getFirstName() + " " + p.getLastName());
        return searchPlayer;
    }

    private FormLayout createDetailsBlock() {
        FormLayout detailsBlock = new FormLayout();
        detailsBlock.setResponsiveSteps(
                new FormLayout.ResponsiveStep("0", 1),
                new FormLayout.ResponsiveStep("800px", 2),
                new FormLayout.ResponsiveStep("1000px", 3),
                new FormLayout.ResponsiveStep("1200px", 5)
        );

        List<TextField> detailsFields = List.of(
                playerNameField = new TextField("Name"),
                playerGenderField = new TextField("M / F"),
                playerIdField = new TextField("Spieler ID"),
                playerAgeClassField = new TextField("Altersklasse"),
                playerYearOfBirthField = new TextField("Geburtsjahr"),
                playerClubNameField = new TextField("Verein"),
                playerDistrictNameField = new TextField("Bezirk"),
                playerStateNameField = new TextField("Landesverband"),
                playerStateGroupField = new TextField("Gruppe")
        );

        detailsFields.forEach(field -> {
            field.setReadOnly(true);
            field.setWidthFull();
            field.addClassName("no-shadow-field");
            detailsBlock.add(field);
        });

        return detailsBlock;
    }

    private void updatePlayerDetails(Player player) {
        playerNameField.setValue(player.toString());
        playerGenderField.setValue(player.getGender().getDisplayName());
        playerIdField.setValue(player.getPlayerId().toString());
        playerAgeClassField.setValue(player.getAgeClassGeneral());
        playerYearOfBirthField.setValue(String.valueOf(player.getYearOfBirth()));
        playerClubNameField.setValue(player.getClubName());
        playerDistrictNameField.setValue(player.getDistrictName());
        playerStateNameField.setValue(player.getStateName());
        playerStateGroupField.setValue(player.getStateGroup());
    }

    private void clearDetails() {
        playerNameField.clear();
        playerGenderField.clear();
        playerIdField.clear();
        playerAgeClassField.clear();
        playerYearOfBirthField.clear();
        playerClubNameField.clear();
        playerDistrictNameField.clear();
        playerStateNameField.clear();
        playerStateGroupField.clear();
    }

    private Grid<RankingRow> rankingGrid;
    private List<RankingRow> matrixData;

    private Component createRankingMatrix() {
        rankingGrid = new Grid<>();
        rankingGrid.addClassName("ranking-grid");
        rankingGrid.setWidth("50%"); // Tabelle auf die Hälfte begrenzen
        rankingGrid.setAllRowsVisible(true); // Alle 3 Zeilen ohne Scrollbar anzeigen

        // 1. Spalte: Disziplin (Hier wird das Label FETT gemacht)
        rankingGrid.addColumn(new ComponentRenderer<>(item -> {
                    Span span = new Span(item.getDiscipline());
                    span.getStyle().set("font-weight", "bold");
                    return span;
                }))
                .setHeader("Disziplin")
                .setFlexGrow(0)
                .setWidth("150px");

        // Datenspalten
        rankingGrid.addColumn(RankingRow::getTournaments).setHeader("Turniere");
        rankingGrid.addColumn(RankingRow::getPoints).setHeader("Punkte");
        rankingGrid.addColumn(RankingRow::getRank).setHeader("Rang");
        rankingGrid.addColumn(RankingRow::getRankAk).setHeader("Rang AK");

        // Daten initialisieren
        matrixData = List.of(
                new RankingRow("Einzel"),
                new RankingRow("Doppel"),
                new RankingRow("Mixed")
        );
        rankingGrid.setItems(matrixData);

        rankingGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        return rankingGrid;
    }

    public static class RankingRow {
        private final String discipline;
        private String tournaments = "";
        private String points = "";
        private String rank = "";
        private String rankAk = "";

        public RankingRow(String discipline) { this.discipline = discipline; }
        public String getDiscipline() { return discipline; }
        public String getTournaments() { return tournaments; }
        public void setTournaments(String t) { this.tournaments = t; }
        public String getPoints() { return points; }
        public void setPoints(String p) { this.points = p; }
        public String getRank() { return rank; }
        public void setRank(String r) { this.rank = r; }
        public String getRankAk() { return rankAk; }
        public void setRankAk(String ra) { this.rankAk = ra; }
    }
}
