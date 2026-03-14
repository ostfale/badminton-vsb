package de.ostfale.va.framework.in.ui.playerranking;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingRelevantTournaments;
import de.ostfale.va.application.domain.model.playerrankings.PlayerRankingTournamentPoints;
import de.ostfale.va.common.UseLogging;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class PlayerDetailsMatrixComponent implements UseLogging {

    private static final int MAX_RANKING_TOURNAMENTS = 5;

    private final GetPlayerDetailsService playerDetailsService;

    private Grid<PlayerMatrixRow> rankingGrid;
    private List<PlayerMatrixRow> matrixData;

    public PlayerDetailsMatrixComponent(GetPlayerDetailsService playerDetailsService) {
        this.playerDetailsService = playerDetailsService;
    }

    public Component getComponent() {
        rankingGrid = new Grid<>();
        rankingGrid.addClassName("ranking-grid");
        rankingGrid.setWidth("100%");
        rankingGrid.setAllRowsVisible(true); // show all rows without scroll bar

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
        rankingGrid.addColumn(PlayerMatrixRow::getKwPoints1).setHeader("KW / Points");
        rankingGrid.addColumn(PlayerMatrixRow::getKwPoints2).setHeader("KW / Points");
        rankingGrid.addColumn(PlayerMatrixRow::getKwPoints3).setHeader("KW / Points");
        rankingGrid.addColumn(PlayerMatrixRow::getKwPoints4).setHeader("KW / Points");
        rankingGrid.addColumn(PlayerMatrixRow::getKwPoints5).setHeader("KW / Points");


        // init data
        matrixData = List.of(
                new PlayerMatrixRow("Einzel"),
                new PlayerMatrixRow("Doppel"),
                new PlayerMatrixRow("Mixed")
        );
        rankingGrid.setItems(matrixData);

        rankingGrid.addThemeVariants(GridVariant.LUMO_NO_BORDER, GridVariant.LUMO_ROW_STRIPES);
        return rankingGrid;
    }

    public void clearDetails() {
        matrixData.forEach(PlayerMatrixRow::reset);

        rankingGrid.getDataProvider().refreshAll();
    }

    public void updateRanking(Player player, List<Player> allPlayers) {
        readValidRankingPoints(player);
        var rankingPoints = playerDetailsService.getRelevantRankingPoints(player);

        populateDisciplineRow(matrixData.get(0), player, allPlayers,
                Player::getSingleTournaments, Player::getSinglePoints, Player::getSingleRanking,
                rankingPoints, PlayerRankingRelevantTournaments::getRelevantSingleTournaments);

        populateDisciplineRow(matrixData.get(1), player, allPlayers,
                Player::getDoubleTournaments, Player::getDoublePoints, Player::getDoubleRanking,
                rankingPoints, PlayerRankingRelevantTournaments::getRelevantDoubleTournaments);

        populateDisciplineRow(matrixData.get(2), player, allPlayers,
                Player::getMixedTournaments, Player::getMixedPoints, Player::getMixedRanking,
                rankingPoints, PlayerRankingRelevantTournaments::getRelevantMixedTournaments);

        rankingGrid.getDataProvider().refreshAll();
    }

    private <T> void populateDisciplineRow(PlayerMatrixRow row, Player player, List<Player> allPlayers,
                                           Function<Player, Integer> tournamentsGetter,
                                           Function<Player, Integer> pointsGetter,
                                           Function<Player, Integer> rankGetter,
                                           Optional<T> rankingPoints,
                                           Function<T, List<PlayerRankingTournamentPoints>> tournamentsExtractor) {
        row.setTournaments(String.valueOf(tournamentsGetter.apply(player)));
        row.setPoints(String.valueOf(pointsGetter.apply(player)));
        row.setRank(String.valueOf(rankGetter.apply(player)));
        row.setRankAk(String.valueOf(calculateAkRank(player, allPlayers, pointsGetter)));

        rankingPoints.ifPresent(rp -> {
            List<PlayerRankingTournamentPoints> tournaments = tournamentsExtractor.apply(rp);
            row.setKwPoints1(getRankingPoints(tournaments, MAX_RANKING_TOURNAMENTS - 1));
            row.setKwPoints2(getRankingPoints(tournaments, MAX_RANKING_TOURNAMENTS - 2));
            row.setKwPoints3(getRankingPoints(tournaments, MAX_RANKING_TOURNAMENTS - 3));
            row.setKwPoints4(getRankingPoints(tournaments, MAX_RANKING_TOURNAMENTS - 4));
            row.setKwPoints5(getRankingPoints(tournaments, MAX_RANKING_TOURNAMENTS - 5));
        });
    }

    private String getRankingPoints(List<PlayerRankingTournamentPoints> tournament, int i) {
        if (tournament.size() <= i) return "";
        return tournament.get(i).getDisplayText();
    }

    private void readValidRankingPoints(Player player) {
        log().info("PlayerDetailsView :: readValidRankingPoints for player {}", player);

        if (player.getPlayerTournamentId() != null) {
            log().info("PlayerDetailsView :: readValidRankingPoints found playerTournamentId {}", player.getPlayerTournamentId());
            return;
        }

        playerDetailsService.addPlayerTournamentIdToPlayer(player);

        log().warn("PlayerDetailsView :: player tournamentId could not be scraped for player {}", player);
    }

    private int calculateAkRank(Player target, List<Player> allPlayers, Function<Player, Integer> pointGetter) {
        int targetPoints = pointGetter.apply(target);

        return (int) allPlayers.stream()
                .filter(p -> p.getGender() == target.getGender())
                .filter(p -> p.getAgeClassGeneral().equals(target.getAgeClassGeneral()))
                .filter(p -> pointGetter.apply(p) > targetPoints)
                .count() + 1;
    }

    public static class PlayerMatrixRow {
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

        public PlayerMatrixRow(String discipline) {
            this.discipline = discipline;
        }

        public void reset() {
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
