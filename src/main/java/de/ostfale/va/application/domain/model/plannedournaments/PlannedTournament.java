package de.ostfale.va.application.domain.model.plannedournaments;

import de.ostfale.va.application.domain.model.plannedournaments.vo.PlannedTournamentCategoriesVO;
import de.ostfale.va.application.domain.model.plannedournaments.vo.PlannedTournamentTypesVO;
import de.ostfale.va.application.domain.model.plannedournaments.vo.TournamentAgeClassesVO;
import de.ostfale.va.common.UseTimeHandling;

import java.time.LocalDate;
import java.util.List;

public record PlannedTournament(
        Boolean isFavorite,
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

    public PlannedTournament(LocalDate startDate, LocalDate endDate, String tournamentName, PlannedTournamentTypesVO tournamentType, int tournamentOrderNo, String countryCode, String location, String postalCode, String region, String openName, String organizer, PlannedTournamentCategoriesVO tourCategory, String closingDate, String webLinkUrl, String pdfLinkUrl, String pdfAvailable, List<PlannedTournamentAgeClassDisciplines> ageClassDisciplines) {
        this(Boolean.FALSE, startDate, endDate, tournamentName, tournamentType, tournamentOrderNo, countryCode, location, postalCode, region, openName, organizer, tourCategory, closingDate, webLinkUrl, pdfLinkUrl, pdfAvailable, ageClassDisciplines);
    }

    public PlannedTournament setFavorite(boolean isFavorite) {
        return new PlannedTournament(isFavorite, startDate, endDate, tournamentName, tournamentType, tournamentOrderNo, countryCode, location, postalCode, region, openName, organizer, tourCategory, closingDate, webLinkUrl, pdfLinkUrl, pdfAvailable, ageClassDisciplines);
    }

    public PlannedTournamentKey createKey() {
        return new PlannedTournamentKey(startDate, tournamentName, location);
    }

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
