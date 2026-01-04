package de.ostfale.va.application.domain.model.plannedournaments;

import de.ostfale.va.application.domain.model.plannedournaments.vo.TournamentAgeClassesVO;

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

