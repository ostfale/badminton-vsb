package de.ostfale.va.framework.in.ui.playerinfo.matches;

import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.signals.Signal;
import com.vaadin.flow.spring.annotation.SpringComponent;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.component.html.Span;
import de.ostfale.va.application.domain.model.matches.DisciplineMatch;
import de.ostfale.va.application.domain.model.matches.PlayerTournaments;
import de.ostfale.va.application.domain.model.matches.Tournament;
import de.ostfale.va.application.domain.model.matches.TournamentDiscipline;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.port.out.ranking.ForScrapingPlayerMatches;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.framework.in.ui.playerinfo.signal.PlayerSelectionState;
import de.ostfale.va.framework.in.ui.playerranking.GetPlayerDetailsService;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SpringComponent
@UIScope
public class PlayerMatchesTabComponent extends VerticalLayout implements UseLogging {

    private final TreeGrid<Object> treeGrid = new TreeGrid<>();
    private final GetPlayerDetailsService playerDetailsService;
    private final ForScrapingPlayerMatches scrapingPlayerMatches;

    public PlayerMatchesTabComponent(
            PlayerSelectionState selectionState,
            GetPlayerDetailsService playerDetailsService, ForScrapingPlayerMatches scrapingPlayerMatches
    ) {
        this.playerDetailsService = playerDetailsService;
        this.scrapingPlayerMatches = scrapingPlayerMatches;

        setSizeFull();
        setPadding(true);
        configureGrid();
        add(treeGrid);

        Signal.effect(this, () -> onPlayerSelectionChanged(selectionState.getSelectedPlayer().get()));
    }

    private void configureGrid() {
        // We will reduce some widths on less important columns to make sure the last one fits
        treeGrid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);

        // 1. Col: Match/Tournament date
        treeGrid.addHierarchyColumn(node -> {
            if (node instanceof Tournament t) return t.getTournamentInfo().tournamentDate();
            if (node instanceof DisciplineMatch m) return m.getMatchDate();
            return "";
        }).setHeader("Datum").setFlexGrow(0).setWidth("240px");

        // 2. Col: Name -> colTournamentName
        treeGrid.addColumn(node -> {
            if (node instanceof Tournament t) return t.getTournamentInfo().tournamentName();
            return "";
        }).setHeader("Name").setFlexGrow(2).setWidth("200px");

        // 3. Col: Location -> colTournamentLocation
        treeGrid.addColumn(node -> {
            if (node instanceof Tournament t) return t.getTournamentInfo().tournamentLocation();
            return "";
        }).setHeader("Ort").setFlexGrow(1).setWidth("160px");

        // 4. Col: Discipline -> colDiscipline
        treeGrid.addColumn(node -> {
            if (node instanceof TournamentDiscipline d) return d.getDisciplineName();
            return "";
        }).setHeader("Disziplin").setFlexGrow(0).setWidth("120px");

        // 5. Col: Round -> colRoundName
        treeGrid.addColumn(node -> {
            if (node instanceof DisciplineMatch m) return m.getRoundName();
            return "";
        }).setHeader("Runde").setFlexGrow(0).setWidth("120px");

        // 6. Col: Team 1 -> colTPOne
        treeGrid.addComponentColumn(node -> {
            Span span = new Span();
            if (node instanceof DisciplineMatch m) {
                span.setText(m.getFirstPlayerOrWithPartnerName());
                if (m.isTeamOneWinner()) {
                    span.getStyle().set("color", "var(--lumo-success-text-color)");
                    span.getStyle().set("font-weight", "bold");
                }
            }
            return span;
        }).setHeader("Player / Team").setFlexGrow(2).setWidth("200px");

        // 7. Col: Team 2 -> colTPTwo
        treeGrid.addComponentColumn(node -> {
            Span span = new Span();
            if (node instanceof DisciplineMatch m) {
                span.setText(m.getSecondPlayerOrWithPartnerName());
                if (m.isTeamTwoWinner()) {
                    span.getStyle().set("color", "var(--lumo-success-text-color)");
                    span.getStyle().set("font-weight", "bold");
                }
            }
            return span;
        }).setHeader("Player / Team").setFlexGrow(2).setWidth("200px");

        // 8. Col: Result -> colMatchResult
        treeGrid.addComponentColumn(node -> {
            Span span = new Span();
            if (node instanceof DisciplineMatch m) {
                String results = String.join(", ", m.getSetResults());
                if (m.getRetirementMessage() != null && !m.getRetirementMessage().isEmpty()) {
                    // Check if the results already include the retirement message
                    // (since getSetResults() appends it, we need to extract and format it)
                    int index = results.lastIndexOf(m.getRetirementMessage());
                    if (index != -1) {
                        String matchScores = results.substring(0, index);
                        if (matchScores.endsWith(", ")) {
                            matchScores = matchScores.substring(0, matchScores.length() - 2);
                        }
                        
                        Span scoresSpan = new Span(matchScores);
                        Span retirementSpan = new Span(matchScores.isEmpty() ? m.getRetirementMessage() : ", " + m.getRetirementMessage());
                        
                        // Set the retirement message color to orange (warning)
                        retirementSpan.getStyle().set("color", "var(--lumo-warning-text-color)");
                        retirementSpan.getStyle().set("font-weight", "bold");
                        
                        span.add(scoresSpan, retirementSpan);
                        return span;
                    }
                }
                span.setText(results);
            }
            return span;
        }).setHeader("Gespielte Sätze").setFlexGrow(3).setWidth("200px");
    }

    private void updateGrid(List<Tournament> tournaments) {
        treeGrid.setItems(tournaments.stream().map(Object.class::cast).toList(), node -> {
            if (node instanceof Tournament t) {
                return t.getDisciplines().stream().map(Object.class::cast).toList(); // Level 1: Disciplines
            }
            if (node instanceof TournamentDiscipline d) {
                // Level 2: Combination of elimination and group matches
                return Stream.concat(
                        d.getEliminationMatches().stream(),
                        d.getGroupMatches().stream()
                ).map(Object.class::cast).toList();
            }
            return Collections.emptyList(); // Matches are leaf nodes
        });
    }

    private void onPlayerSelectionChanged(Player player) {
        if (player == null) {
            updateGrid(Collections.emptyList());
            return;
        }

        // 1. Ensure the ID is available
        playerDetailsService.addPlayerTournamentIdToPlayer(player);

        String tournamentId = player.getPlayerTournamentId().tournamentId();

        if (tournamentId != null) {
            // *** BUILD THE URL HERE ***
            String url = "https://www.turnier.de/player-profile/" + tournamentId + "/2026";
            System.out.println("Built URL: " + url); // Replace this with what you intend to do with the URL (e.g., updating a link button)

            // 2. Load data for this player (Scraping)
            Optional<PlayerTournaments> tournaments = scrapingPlayerMatches.scrapePlayerMatches(player.getPlayerTournamentId());

            // 3. Fill grid with the hierarchical structure
            if (tournaments.isPresent()) {
                updateGrid(tournaments.get().getPlayerTournaments());
            } else {
                updateGrid(Collections.emptyList());
            }
        } else {
            updateGrid(Collections.emptyList());
        }
    }
}
