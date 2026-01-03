package de.ostfale.va.application.domain.model.plannedournaments;

public record PlannedTournamentAgeClassDisciplines(
        TournamentAgeClassesVO ageClass,
        boolean isSingle,
        boolean isDouble,
        boolean isMixed
) {

    public boolean anyDisciplineForThisAgeClass() {
        return isSingle || isDouble || isMixed;
    }
}

