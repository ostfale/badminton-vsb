package de.ostfale.va.framework.out.web.scraper.match;

import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.TimeoutError;
import com.microsoft.playwright.options.WaitForSelectorState;
import de.ostfale.va.application.domain.model.matches.*;
import de.ostfale.va.application.port.out.ranking.PageProcessor;
import de.ostfale.va.common.UseLogging;

import java.util.List;
import java.util.Optional;

public class ScrapePlayerMatches implements PageProcessor<PlayerTournaments>, UseLogging {

    @Override
    public Optional<PlayerTournaments> process(Page page) {
        try {
            page.waitForSelector(".module--card", new Page.WaitForSelectorOptions()
                    .setState(WaitForSelectorState.ATTACHED)
                    .setTimeout(10000));

            return Optional.of(parseTournaments(page));
        } catch (TimeoutError e) {
            log().warn("Timeout - No tournaments (.module--card) found on: {}", page.url());
            return Optional.of(new PlayerTournaments());
        } catch (Exception e) {
            log().error("Failed to parse tournaments from {}", page.url(), e);
            return Optional.of(new PlayerTournaments());
        }
    }

    private PlayerTournaments parseTournaments(Page page) {
        PlayerTournaments playerTournaments = new PlayerTournaments();
        List<ElementHandle> tournamentCards = page.querySelectorAll(".module.module--card");
        for (ElementHandle card : tournamentCards) {
            Tournament tournament = parseTournament(card);
            playerTournaments.addPlayerTournament(tournament);
        }
        return playerTournaments;
    }

    private Tournament parseTournament(ElementHandle card) {
        TournamentInfo tournamentInfo = parseTournamentInfo(card);
        Tournament tournament = new Tournament(tournamentInfo);

        List<ElementHandle> matchGroups = card.querySelectorAll("ol.match-group");
        for (ElementHandle matchGroup : matchGroups) {
            ElementHandle prevH4 = matchGroup.evaluateHandle("el => { let prev = el.previousElementSibling; while(prev && prev.tagName !== 'H4') { prev = prev.previousElementSibling; } return prev; }").asElement();
            String disciplineName = "Unknown Discipline";
            if (prevH4 != null) {
                ElementHandle span = prevH4.querySelector("span.module-divider__body");
                if (span != null) {
                    disciplineName = span.innerText().replace("Konkurrenz: ", "").trim();
                }
            }

            TournamentDiscipline discipline = new TournamentDiscipline();
            discipline.setDisciplineName(disciplineName);

            List<ElementHandle> matchItems = matchGroup.querySelectorAll("li.match-group__item");
            for (ElementHandle matchItem : matchItems) {
                DisciplineMatch match = parseMatch(matchItem);
                discipline.getEliminationMatches().add(match);
            }
            if (discipline.hasEliminationMatches()) {
                tournament.getDisciplines().add(discipline);
            }
        }

        return tournament;
    }

    private TournamentInfo parseTournamentInfo(ElementHandle card) {
        String name = "";
        String date = "";
        String location = "";

        ElementHandle nameEl = card.querySelector(".media__title a span");
        if (nameEl != null) name = nameEl.innerText().trim();

        List<ElementHandle> timeEls = card.querySelectorAll(".media__subheading--muted time");
        if (!timeEls.isEmpty()) {
            date = timeEls.getFirst().innerText().trim();
            if (timeEls.size() > 1) {
                date += " bis " + timeEls.get(1).innerText().trim();
            }
        }

        ElementHandle locationEl = card.querySelector(".media__subheading span.nav-link__value");
        if (locationEl != null) {
            String fullLocation = locationEl.innerText().trim();
            if (fullLocation.contains("|")) {
                location = fullLocation.substring(fullLocation.lastIndexOf('|') + 1).trim();
            } else {
                location = fullLocation;
            }
            // Remove everything starting from '[' if present
            if (location.contains("[")) {
                location = location.substring(0, location.indexOf('[')).trim();
            }
        }

        return new TournamentInfo(name, "", location, date, 2026);
    }

    private DisciplineMatch parseMatch(ElementHandle matchItem) {
        DisciplineMatch match = new DisciplineMatch();

        ElementHandle roundEl = matchItem.querySelector(".match__header-title-item .nav-link__value");
        if (roundEl != null) {
            match.setRoundName(roundEl.innerText().trim());
        }

        List<ElementHandle> matchRows = matchItem.querySelectorAll(".match__row");
        if (!matchRows.isEmpty()) {
            parseTeam(matchRows.getFirst(), match, true);
        }
        if (matchRows.size() >= 2) {
            parseTeam(matchRows.get(1), match, false);
        }

        List<ElementHandle> pointCells = matchItem.querySelectorAll(".match__result .points__cell");
        int setIndex = 1;
        for (int i = 0; i < pointCells.size(); i += 2) {
            if (i + 1 < pointCells.size() && setIndex <= SetNumber.values().length) {
                try {
                    int score1 = Integer.parseInt(pointCells.get(i).innerText().trim());
                    int score2 = Integer.parseInt(pointCells.get(i+1).innerText().trim());
                    match.getMatchSets().add(new MatchSet(SetNumber.values()[setIndex - 1], score1, score2));
                    setIndex++;
                } catch (NumberFormatException e) {
                    // Ignore non-numeric scores
                }
            }
        }
        
        // Look for match messages like "Retired.", "Walkover" or "Abgesagt"
        ElementHandle messageEl = matchItem.querySelector(".match__message");
        if (messageEl != null) {
            String message = messageEl.innerText().trim();
            if (message.endsWith(".")) {
                message = message.substring(0, message.length() - 1);
            }
            match.setRetirementMessage(message);
        }

        ElementHandle dateEl = matchItem.querySelector(".match__footer-list-item .icon-clock + .nav-link__value");
        if (dateEl != null) {
            match.setMatchDate(dateEl.innerText().trim());
        }

        return match;
    }

    private void parseTeam(ElementHandle row, DisciplineMatch match, boolean isFirstTeam) {
        // Check if this row represents the winning team
        String className = (String) row.evaluate("el => el.className");
        boolean isWinner = className != null && className.contains("has-won");
        
        if (isFirstTeam) {
            match.setTeamOneWinner(isWinner);
        } else {
            match.setTeamTwoWinner(isWinner);
        }

        List<ElementHandle> playerEls = row.querySelectorAll(".match__row-title-value-content a .nav-link__value");
        if (playerEls.isEmpty()) {
            String text = row.innerText().trim();
            if (isFirstTeam) {
                match.setPlayerOneName(text.isEmpty() ? "Rast" : text);
            } else {
                match.setPlayerTwoName(text.isEmpty() ? "Rast" : text);
            }
            return;
        }

        if (isFirstTeam) {
            match.setPlayerOneName(playerEls.getFirst().innerText().trim());
            if (playerEls.size() > 1) {
                match.setPartnerOneName(playerEls.get(1).innerText().trim());
            }
        } else {
            match.setPlayerTwoName(playerEls.getFirst().innerText().trim());
            if (playerEls.size() > 1) {
                match.setPartnerTwoName(playerEls.get(1).innerText().trim());
            }
        }
    }
}
