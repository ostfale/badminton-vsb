package de.ostfale.va.framework.in.ui.playerinfo.matches;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import de.ostfale.va.application.domain.model.matches.DisciplineMatch;
import de.ostfale.va.application.domain.model.matches.Tournament;
import de.ostfale.va.application.domain.model.matches.TournamentDiscipline;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.port.out.ForScrapingPlayerTournaments;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.playerinfo.signal.PlayerSelectionState;
import de.ostfale.va.framework.in.ui.playerranking.GetPlayerDetailsService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

@SpringComponent
@UIScope
public class PlayerMatchesTabComponent extends VerticalLayout implements UseLogging {

    private final TreeGrid<Object> treeGrid = new TreeGrid<>();
    private final GetPlayerDetailsService playerDetailsService;
    private final ForScrapingPlayerTournaments tournamentScraper;

    public PlayerMatchesTabComponent(
            PlayerSelectionState selectionState,
            GetPlayerDetailsService playerDetailsService,
            ForScrapingPlayerTournaments tournamentScraper
    ) {
        this.playerDetailsService = playerDetailsService;
        this.tournamentScraper = tournamentScraper;

        setSizeFull();
        setPadding(true);
        configureGrid();
        add(treeGrid);

        Signal.effect(this, () -> onPlayerSelectionChanged(selectionState.getSelectedPlayer().get()));
    }

    private void configureGrid() {
        // 1. Col: is the match/tournament date
        treeGrid.addHierarchyColumn(node -> {
            if (node instanceof Tournament t) return t.getTournamentInfo().tournamentDate();
            if (node instanceof DisciplineMatch m) return m.getMatchDate();
            return "";
        }).setHeader("Datum").setFlexGrow(1);

        // 2. Col: Name ->  colTournamentName
        treeGrid.addColumn(node -> {
            if (node instanceof Tournament t) return t.getTournamentInfo().tournamentName();
            return "";
        }).setHeader("Name").setFlexGrow(2);

        // 3. Col: Ort ->  colTournamentLocation
        treeGrid.addColumn(node -> {
            if (node instanceof Tournament t) return t.getTournamentInfo().tournamentLocation();
            return "";
        }).setHeader("Ort").setFlexGrow(1);

        // 4. Col: Disziplin ->  colDiscipline
        treeGrid.addColumn(node -> {
            if (node instanceof TournamentDiscipline d) return d.getDisciplineName();
            return "";
        }).setHeader("Disziplin");

        // 5. Col: Runde ->  colRoundName
        treeGrid.addColumn(node -> {
            if (node instanceof DisciplineMatch m) return m.getRoundName();
            return "";
        }).setHeader("Runde");

        // 6. Col: Team 1 ->  colTPOne
        treeGrid.addColumn(node -> {
            if (node instanceof DisciplineMatch m) return m.getFirstPlayerOrWithPartnerName();
            return "";
        }).setHeader("Player / Team");

        // 7. Col: Team 2 ->  colTPTwo
        treeGrid.addColumn(node -> {
            if (node instanceof DisciplineMatch m) return m.getSecondPlayerOrWithPartnerName();
            return "";
        }).setHeader("Player / Team");

        // 8. Col: Ergebnis ->  colMatchResult
        treeGrid.addColumn(node -> {
            if (node instanceof DisciplineMatch m) return String.join(", ", m.getSetResults());
            return "";
        }).setHeader("Gespielte Sätze").setFlexGrow(2);
    }

    private void onPlayerSelectionChanged(Player player) {
        if (player == null) {
            treeGrid.setItems(Collections.emptyList());
            return;
        }

        // 1. Sicherstellen, dass die ID vorhanden ist
        playerDetailsService.addPlayerTournamentIdToPlayer(player);

        if (player.getPlayerTournamentId() != null) {
            // 2. Daten für diesen Spieler laden (Scraping)
            List<Tournament> tournaments = tournamentScraper.scrapeTournaments(player.getPlayerTournamentId());

            // 3. Grid mit der hierarchischen Struktur befüllen
            updateGrid(tournaments);
        } else {
            treeGrid.setItems(Collections.emptyList());
        }
    }

    private void updateGrid(List<Tournament> tournaments) {
        treeGrid.setItems(tournaments.stream().map(Object.class::cast).toList(), node -> {
            if (node instanceof Tournament t) {
                return t.getDisciplines().stream().map(Object.class::cast).toList(); // Ebene 1: Disziplinen
            }
            if (node instanceof TournamentDiscipline d) {
                // Ebene 2: Kombination aus KO- und Gruppenspielen
                return Stream.concat(
                        d.getEliminationMatches().stream(),
                        d.getGroupMatches().stream()
                ).map(Object.class::cast).toList();
            }
            return Collections.emptyList(); // Matches sind Blätter
        });
    }
}
