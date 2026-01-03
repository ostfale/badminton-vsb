package de.ostfale.va.application.domain.model.plannedournaments;

import de.ostfale.va.common.UseTimeHandling;

import java.time.LocalDate;
import java.util.List;

public record PlannedTournament(
        LocalDate startDate,
        LocalDate endDate,
        String tournamentName,
        PlannedTournamentTypesVO tournamentType,
        int tournamentOrderNo,
        String countryCode,
        String location,
        String postalCode,
        String region,
        String openName,
        String organizer,
        PlannedTournamentCategoriesVO tourCategory,
        String closingDate,
        String webLinkUrl,
        String pdfLinkUrl,
        String pdfAvailable,
        List<PlannedTournamentAgeClassDisciplines> ageClassDisciplines
) implements UseTimeHandling {

    public boolean isFromCurrentYear() {
        return startDate.getYear() == getCurrentCalendarYear();
    }

    public boolean isFromNextYear() {
        return startDate.getYear() == getNextCalendarYear();
    }

    public boolean isOpenTournament() {
        LocalDate today = LocalDate.now();
        return isFromCurrentYear() && startDate.isAfter(today);
    }

    public boolean isForAgeClass(TournamentAgeClassesVO ageClass) {
        return ageClassDisciplines.stream()
                .anyMatch(ageClassDiscipline -> hasDisciplinesForAgeClass(ageClassDiscipline, ageClass));
    }

    private boolean hasDisciplinesForAgeClass(PlannedTournamentAgeClassDisciplines discipline, TournamentAgeClassesVO ageClass) {
        return discipline.ageClass().equals(ageClass) && discipline.anyDisciplineForThisAgeClass();
    }
}
