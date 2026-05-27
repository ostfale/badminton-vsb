package de.ostfale.va.framework.in.ui.playerinfo.ranking;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.grid.contextmenu.GridContextMenu;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingRelevantTournaments;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingTournamentPoints;
import de.ostfale.va.application.port.in.ranking.ForLoadingRankings;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.playerinfo.signal.PlayerSelectionState;
import de.ostfale.va.framework.in.ui.playerranking.GetPlayerDetailsService;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

@SpringComponent
@UIScope
public class PlayerRankingTabComponent extends VerticalLayout implements UseLogging {

    private static final int MAX_RANKING_TOURNAMENTS = 5;
    private static final String DISCIPLINE_SINGLE = "Einzel";
    private static final String DISCIPLINE_DOUBLE = "Doppel";
    private static final String DISCIPLINE_MIXED = "Mixed";

    private final ForLoadingRankings rankingService;
    private final GetPlayerDetailsService playerDetailsService;
    private final Grid<PlayerMatrixRow> rankingGrid = new Grid<>();
    private final List<PlayerMatrixRow> matrixData = List.of(
            new PlayerMatrixRow(DISCIPLINE_SINGLE),
            new PlayerMatrixRow(DISCIPLINE_DOUBLE),
            new PlayerMatrixRow(DISCIPLINE_MIXED)
    );
    private final AtomicLong updateVersion = new AtomicLong(0);
    private volatile Optional<PlayerRankingRelevantTournaments> rankingPoints = Optional.empty();

    public PlayerRankingTabComponent(PlayerSelectionState selectionState,
                                     ForLoadingRankings rankingService,
                                     GetPlayerDetailsService playerDetailsService) {
        this.rankingService = rankingService;
        this.playerDetailsService = playerDetailsService;
        initLayout();
        Signal.effect(this, () -> refreshContent(selectionState.getSelectedPlayer().get()));
    }

    private void initLayout() {
        setPadding(false);
        setSpacing(false);
        setSizeFull();
        configureGrid();
        add(rankingGrid);
    }

    private void configureGrid() {
        rankingGrid.addClassName("ranking-grid");
        rankingGrid.setWidth("100%");
        rankingGrid.setAllRowsVisible(true);
        rankingGrid.setItems(matrixData);
        rankingGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);

        createContextMenu(rankingGrid);
        rankingGrid.addColumn(new ComponentRenderer<>(item -> {
                    Span span = new Span(item.getDiscipline());
                    span.getStyle().set("font-weight", "bold");
                    return span;
                }))
                .setHeader("Disziplin")
                .setFlexGrow(0)
                .setWidth("150px");
        rankingGrid.addColumn(PlayerMatrixRow::getTournaments).setHeader("Turniere");
        rankingGrid.addColumn(PlayerMatrixRow::getPoints).setHeader("Punkte");
        rankingGrid.addColumn(PlayerMatrixRow::getRank).setHeader("Rang");
        rankingGrid.addColumn(PlayerMatrixRow::getRankAk).setHeader("Rang AK");
        rankingGrid.addColumn(PlayerMatrixRow::getKwPoints1).setHeader("KW / Punkte");
        rankingGrid.addColumn(PlayerMatrixRow::getKwPoints2).setHeader("KW / Punkte");
        rankingGrid.addColumn(PlayerMatrixRow::getKwPoints3).setHeader("KW / Punkte");
        rankingGrid.addColumn(PlayerMatrixRow::getKwPoints4).setHeader("KW / Punkte");
        rankingGrid.addColumn(PlayerMatrixRow::getKwPoints5).setHeader("KW / Punkte");
    }

    private void createContextMenu(Grid<PlayerMatrixRow> grid) {
        GridContextMenu<PlayerMatrixRow> menu = grid.addContextMenu();
        menu.setDynamicContentHandler(row -> row != null && !row.getPoints().isEmpty());
        menu.addItem("Zeige alle Turniere", event -> event.getItem().ifPresent(this::showAllTournamentsForDiscipline));
    }

    private void showAllTournamentsForDiscipline(PlayerMatrixRow row) {
        if (rankingPoints.isEmpty()) {
            Notification.show("Keine Turnierdaten verfuegbar.");
            return;
        }

        List<PlayerRankingTournamentPoints> tournaments = switch (row.getDiscipline()) {
            case DISCIPLINE_SINGLE -> rankingPoints.get().singleTournaments();
            case DISCIPLINE_DOUBLE -> rankingPoints.get().doubleTournaments();
            case DISCIPLINE_MIXED -> rankingPoints.get().mixedTournaments();
            default -> List.of();
        };

        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Alle Turniere fuer Disziplin: " + row.getDiscipline());
        dialog.setWidth("1000px");

        Grid<PlayerRankingTournamentPoints> detailGrid = new Grid<>();
        detailGrid.setItems(tournaments);
        detailGrid.addThemeVariants(GridVariant.LUMO_COMPACT, GridVariant.LUMO_ROW_STRIPES);
        detailGrid.addColumn(PlayerRankingTournamentPoints::tournamentWeek).setHeader("KW").setWidth("80px").setFlexGrow(0);
        detailGrid.addColumn(PlayerRankingTournamentPoints::tournamentDiscipline).setHeader("Disziplin").setWidth("100px").setFlexGrow(0);
        detailGrid.addColumn(PlayerRankingTournamentPoints::tournamentPlacement).setHeader("Platz").setWidth("75px").setFlexGrow(0);
        detailGrid.addColumn(PlayerRankingTournamentPoints::tournamentPoints).setHeader("Punkte").setWidth("75px").setFlexGrow(0);
        detailGrid.addColumn(PlayerRankingTournamentPoints::tournamentName).setHeader("Turnier").setFlexGrow(1);
        detailGrid.setPartNameGenerator(item -> item.isRelevant() ? "relevant-tournament" : null);
        dialog.add(detailGrid);
        dialog.getFooter().add(new Button("Schliessen", e -> dialog.close()));
        dialog.open();
    }

    private void refreshContent(Player player) {
        if (player == null) {
            updateVersion.incrementAndGet();
            clearDetails();
            return;
        }
        long requestVersion = updateVersion.incrementAndGet();
        CompletableFuture
                .supplyAsync(() -> buildRankingUpdate(player))
                .thenAccept(update -> getUI().ifPresent(ui -> ui.access(() -> {
                    if (requestVersion == updateVersion.get()) {
                        applyRankingUpdate(update);
                    }
                })));
    }

    private RankingUpdate buildRankingUpdate(Player player) {
        readValidRankingPoints(player);
        Optional<PlayerRankingRelevantTournaments> relevantPoints = playerDetailsService.getRelevantRankingPoints(player);
        List<Player> allPlayers = rankingService.getAllPlayers();

        List<PlayerMatrixRow> updatedRows = List.of(
                createDisciplineRow(DISCIPLINE_SINGLE, player, allPlayers,
                        Player::getSingleTournaments, Player::getSinglePoints, Player::getSingleRanking,
                        relevantPoints, PlayerRankingRelevantTournaments::getRelevantSingleTournaments),
                createDisciplineRow(DISCIPLINE_DOUBLE, player, allPlayers,
                        Player::getDoubleTournaments, Player::getDoublePoints, Player::getDoubleRanking,
                        relevantPoints, PlayerRankingRelevantTournaments::getRelevantDoubleTournaments),
                createDisciplineRow(DISCIPLINE_MIXED, player, allPlayers,
                        Player::getMixedTournaments, Player::getMixedPoints, Player::getMixedRanking,
                        relevantPoints, PlayerRankingRelevantTournaments::getRelevantMixedTournaments)
        );
        return new RankingUpdate(updatedRows, relevantPoints);
    }

    private void applyRankingUpdate(RankingUpdate update) {
        rankingPoints = update.relevantPoints();
        for (int i = 0; i < matrixData.size(); i++) {
            matrixData.get(i).copyFrom(update.rows().get(i));
        }
        rankingGrid.getDataProvider().refreshAll();
    }

    private void clearDetails() {
        rankingPoints = Optional.empty();
        matrixData.forEach(PlayerMatrixRow::reset);
        rankingGrid.getDataProvider().refreshAll();
    }

    private <T> PlayerMatrixRow createDisciplineRow(String discipline,
                                                    Player player,
                                                    List<Player> allPlayers,
                                                    Function<Player, Integer> tournamentsGetter,
                                                    Function<Player, Integer> pointsGetter,
                                                    Function<Player, Integer> rankGetter,
                                                    Optional<T> relevantPoints,
                                                    Function<T, List<PlayerRankingTournamentPoints>> tournamentsExtractor) {
        PlayerMatrixRow row = new PlayerMatrixRow(discipline);
        row.setTournaments(String.valueOf(tournamentsGetter.apply(player)));
        row.setPoints(String.valueOf(pointsGetter.apply(player)));
        row.setRank(String.valueOf(rankGetter.apply(player)));
        row.setRankAk(String.valueOf(calculateAkRank(player, allPlayers, pointsGetter)));

        relevantPoints.ifPresent(rp -> {
            List<PlayerRankingTournamentPoints> tournaments = tournamentsExtractor.apply(rp);
            row.setKwPoints1(getRankingPoints(tournaments, MAX_RANKING_TOURNAMENTS - 1));
            row.setKwPoints2(getRankingPoints(tournaments, MAX_RANKING_TOURNAMENTS - 2));
            row.setKwPoints3(getRankingPoints(tournaments, MAX_RANKING_TOURNAMENTS - 3));
            row.setKwPoints4(getRankingPoints(tournaments, MAX_RANKING_TOURNAMENTS - 4));
            row.setKwPoints5(getRankingPoints(tournaments, MAX_RANKING_TOURNAMENTS - 5));
        });
        return row;
    }

    private String getRankingPoints(List<PlayerRankingTournamentPoints> tournament, int index) {
        if (tournament.size() <= index) {
            return "";
        }
        return tournament.get(index).getDisplayText();
    }

    private void readValidRankingPoints(Player player) {
        if (player.getPlayerTournamentId() != null) {
            return;
        }
        playerDetailsService.addPlayerTournamentIdToPlayer(player);
    }

    private int calculateAkRank(Player target, List<Player> allPlayers, Function<Player, Integer> pointGetter) {
        int targetPoints = pointGetter.apply(target);
        return (int) allPlayers.stream()
                .filter(p -> p.getGender() == target.getGender())
                .filter(p -> p.getAgeClassGeneral().equals(target.getAgeClassGeneral()))
                .filter(p -> pointGetter.apply(p) > targetPoints)
                .count() + 1;
    }

    private record RankingUpdate(List<PlayerMatrixRow> rows, Optional<PlayerRankingRelevantTournaments> relevantPoints) {
    }

    private static class PlayerMatrixRow {
        private final String discipline;
        private String tournaments = "";
        private String points = "";
        private String rank = "";
        private String rankAk = "";
        private String kwPoints1 = "";
        private String kwPoints2 = "";
        private String kwPoints3 = "";
        private String kwPoints4 = "";
        private String kwPoints5 = "";

        private PlayerMatrixRow(String discipline) {
            this.discipline = discipline;
        }

        private void copyFrom(PlayerMatrixRow other) {
            tournaments = other.tournaments;
            points = other.points;
            rank = other.rank;
            rankAk = other.rankAk;
            kwPoints1 = other.kwPoints1;
            kwPoints2 = other.kwPoints2;
            kwPoints3 = other.kwPoints3;
            kwPoints4 = other.kwPoints4;
            kwPoints5 = other.kwPoints5;
        }

        private void reset() {
            tournaments = "";
            points = "";
            rank = "";
            rankAk = "";
            kwPoints1 = "";
            kwPoints2 = "";
            kwPoints3 = "";
            kwPoints4 = "";
            kwPoints5 = "";
        }

        public String getDiscipline() {
            return discipline;
        }

        public String getTournaments() {
            return tournaments;
        }

        public void setTournaments(String tournaments) {
            this.tournaments = tournaments;
        }

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getRankAk() {
            return rankAk;
        }

        public void setRankAk(String rankAk) {
            this.rankAk = rankAk;
        }

        public String getKwPoints1() {
            return kwPoints1;
        }

        public void setKwPoints1(String kwPoints1) {
            this.kwPoints1 = kwPoints1;
        }

        public String getKwPoints2() {
            return kwPoints2;
        }

        public void setKwPoints2(String kwPoints2) {
            this.kwPoints2 = kwPoints2;
        }

        public String getKwPoints3() {
            return kwPoints3;
        }

        public void setKwPoints3(String kwPoints3) {
            this.kwPoints3 = kwPoints3;
        }

        public String getKwPoints4() {
            return kwPoints4;
        }

        public void setKwPoints4(String kwPoints4) {
            this.kwPoints4 = kwPoints4;
        }

        public String getKwPoints5() {
            return kwPoints5;
        }

        public void setKwPoints5(String kwPoints5) {
            this.kwPoints5 = kwPoints5;
        }
    }
}
