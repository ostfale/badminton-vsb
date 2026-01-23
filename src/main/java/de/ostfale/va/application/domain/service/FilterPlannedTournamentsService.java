package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentsFilter;
import de.ostfale.va.application.domain.model.plannedournaments.vo.PlannedTournamentCategoriesVO;
import de.ostfale.va.application.domain.model.plannedournaments.vo.TournamentAgeClassesVO;
import de.ostfale.va.application.port.in.ForFilteringPlannedTournaments;
import de.ostfale.va.application.port.in.ForLoadingPlannedTournaments;
import de.ostfale.va.common.UseLogging;
import de.ostfale.va.common.UseTimeHandling;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

@Service
public class FilterPlannedTournamentsService implements ForFilteringPlannedTournaments, UseTimeHandling, UseLogging {

    private List<PlannedTournament> tournaments;
    private final ForLoadingPlannedTournaments loadingService;

    public FilterPlannedTournamentsService(ForLoadingPlannedTournaments loadingService) {
        this.loadingService = loadingService;
        this.tournaments = loadingService.loadFromSource();
    }

    @Override
    public Stream<PlannedTournament> fetch(PlannedTournamentsFilter filter, int offset, int limit) {
        return tournaments.stream()
                .filter(tournament -> matchesFilter(tournament, filter))
                .skip(offset)
                .limit(limit);
    }

    @Override
    public int count(PlannedTournamentsFilter filter) {
        return (int) tournaments.stream()
                .filter(tournament -> matchesFilter(tournament, filter))
                .count();
    }

    private boolean matchesFilter(PlannedTournament tournament, PlannedTournamentsFilter filter) {
        if (filter == null) {
            return true;
        }

        if (filter.isValidTournamentsOnly() && isTournamentBeforeToday(tournament)) {
            return false;
        }

        if (filter.onlyThisYearsTournaments() && !isTournamentInThisYear(tournament)) {
            return false;
        }

        if (!matchesAgeClass(filter.ageClasses(), tournament)) {
            return false;
        }

        if (!matchesAnyCheckedTournamentCategory(filter.tourCategories(), tournament)) {
            return false;
        }

        return matches(filter.name().orElse(null), tournament.tournamentName())
                && matches(filter.location().orElse(null), tournament.location());
    }

    private boolean matchesAgeClass(Set<TournamentAgeClassesVO> filterAgeClasses, PlannedTournament tournament) {
        if (filterAgeClasses == null || filterAgeClasses.isEmpty()) {
            return true;
        }
        return filterAgeClasses.stream().anyMatch(tournament::isForAgeClass);
    }

    private boolean matchesAnyCheckedTournamentCategory(Set<PlannedTournamentCategoriesVO> checkedAgeClasses, PlannedTournament tournament) {
        if (checkedAgeClasses == null || checkedAgeClasses.isEmpty()) {
            return true;
        }
        return checkedAgeClasses.stream().anyMatch(tc -> tc.name().equalsIgnoreCase(tournament.tourCategory().getBaseCategory()));
    }

    private boolean matches(String filterValue, String actualValue) {
        if (filterValue == null || filterValue.isEmpty()) {
            return true;
        }
        return actualValue != null && actualValue.toLowerCase().contains(filterValue.toLowerCase());
    }

    private boolean isTournamentInThisYear(PlannedTournament tournament) {
        log().trace("LoadTournamentsService  ::isTournamentInThisYear :: tournament = {}", tournament.startDate());
        return LocalDate.now().getYear() == tournament.startDate().getYear();
    }

    private boolean isTournamentBeforeToday(PlannedTournament tournament) {
        log().trace("LoadTournamentsService ::isTournamentBeforeToday :: tournament = {}", tournament.startDate());
        var today = LocalDate.now();
        return tournament.startDate().isBefore(today);
    }
}
