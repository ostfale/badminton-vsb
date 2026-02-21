package de.ostfale.va.framework.in.ui.playerranking;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.common.UseLogging;

import java.util.List;

public class PlayerDetailsView extends VerticalLayout implements UseLogging {

    private static final String PLAYER_SEPARATOR_SELECTION = "Spieler Suchen";
    private static final String PLAYER_SEPARATOR_DETAILS = "Spieler Details";
    private static final String PLAYER_RANKING_POINTS = "Spieler Ranglistenpunkte";

    private final PlayerDetailsSearchComponent searchComponent ;

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
        this.searchComponent = new PlayerDetailsSearchComponent(rankingService, this);
        log().debug("PlayerDetailsView :: constructor");
        this.rankingService = rankingService;
        initLayout();
    }

    public void initLayout() {
        add(new FormSectionHeader(PLAYER_SEPARATOR_SELECTION));

        HorizontalLayout actionRow = searchComponent.getComponent();

        add(actionRow);
        actionRow.setWidth("50%");

        add(new FormSectionHeader(PLAYER_SEPARATOR_DETAILS));
        add(createDetailsBlock());

        add(new FormSectionHeader(PLAYER_RANKING_POINTS));
        add(createRankingMatrix());
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

    public void updatePlayerDetails(Player player) {
        playerNameField.setValue(player.toString());
        playerGenderField.setValue(player.getGender().getDisplayName());
        playerIdField.setValue(player.getPlayerId().toString());
        playerAgeClassField.setValue(player.getAgeClassGeneral());
        playerYearOfBirthField.setValue(String.valueOf(player.getYearOfBirth()));
        playerClubNameField.setValue(player.getClubName());
        playerDistrictNameField.setValue(player.getDistrictName());
        playerStateNameField.setValue(player.getStateName());
        playerStateGroupField.setValue(player.getStateGroup().getDisplayName());

        // --- Ranking Matrix (Grid) befüllen ---

        List<Player> allPlayers = rankingService.loadPlayer();

        // Zeile 1: Einzel (Index 0)
        RankingRow einzelRow = matrixData.getFirst();
        einzelRow.setTournaments(String.valueOf(player.getSingleTournaments()));
        einzelRow.setPoints(String.valueOf(player.getSinglePoints()));
        einzelRow.setRank(String.valueOf(player.getSingleRanking()));
        einzelRow.setRankAk(String.valueOf(calculateAkRank(player, allPlayers, Player::getSinglePoints)));
        // Zeile 2: Doppel (Index 1)
        RankingRow doppelRow = matrixData.get(1);
        doppelRow.setTournaments(String.valueOf(player.getDoubleTournaments()));
        doppelRow.setPoints(String.valueOf(player.getDoublePoints()));
        doppelRow.setRank(String.valueOf(player.getDoubleRanking()));
        doppelRow.setRankAk(String.valueOf(calculateAkRank(player, allPlayers, Player::getDoublePoints)));

        // Zeile 3: Mixed (Index 2)
        RankingRow mixedRow = matrixData.get(2);
        mixedRow.setTournaments(String.valueOf(player.getMixedTournaments()));
        mixedRow.setPoints(String.valueOf(player.getMixedPoints()));
        mixedRow.setRank(String.valueOf(player.getMixedRanking()));
        mixedRow.setRankAk(String.valueOf(calculateAkRank(player, allPlayers, Player::getMixedPoints)));

        // Grid aktualisieren, um die Änderungen anzuzeigen
        rankingGrid.getDataProvider().refreshAll();
    }

    public void clearDetails() {
        playerNameField.clear();
        playerGenderField.clear();
        playerIdField.clear();
        playerAgeClassField.clear();
        playerYearOfBirthField.clear();
        playerClubNameField.clear();
        playerDistrictNameField.clear();
        playerStateNameField.clear();
        playerStateGroupField.clear();

        // Matrix-Daten leeren
        matrixData.forEach(row -> {
            row.setTournaments("");
            row.setPoints("");
            row.setRank("");
            row.setRankAk("");
        });

        // Grid aktualisieren
        rankingGrid.getDataProvider().refreshAll();
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

    private int calculateAkRank(Player target, List<Player> allPlayers, java.util.function.Function<Player, Integer> pointGetter) {
        int targetPoints = pointGetter.apply(target);

        return (int) allPlayers.stream()
                .filter(p -> p.getGender() == target.getGender())
                .filter(p -> p.getAgeClassGeneral().equals(target.getAgeClassGeneral()))
                .filter(p -> pointGetter.apply(p) > targetPoints)
                .count() + 1;
    }
}
