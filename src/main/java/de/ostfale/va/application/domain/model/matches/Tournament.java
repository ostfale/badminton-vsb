package de.ostfale.va.application.domain.model.matches;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.ArrayList;
import java.util.List;

public class Tournament  implements TournamentNode{

    private final TournamentInfo tournamentInfo;
    private final List<TournamentDiscipline> disciplines = new ArrayList<>();

    public Tournament(TournamentInfo tournamentInfo) {
        this.tournamentInfo = tournamentInfo;
    }

    public Tournament() {
        this.tournamentInfo = new TournamentInfo();
    }

    public TournamentInfo getTournamentInfo() {
        return tournamentInfo;
    }

    public List<TournamentDiscipline> getDisciplines() {
        return disciplines;
    }

    @JsonIgnore
    public TournamentDiscipline getSingleDiscipline() {
        return getDisciplineByType(DisciplineType.SINGLE);
    }

    @JsonIgnore
    public TournamentDiscipline getDoubleDiscipline() {
        return getDisciplineByType(DisciplineType.DOUBLE);
    }

    @JsonIgnore
    public TournamentDiscipline getMixedDiscipline() {
        return getDisciplineByType(DisciplineType.MIXED);
    }

    @JsonIgnore
    public TournamentDiscipline getDisciplineByType(DisciplineType disciplineType) {
        return disciplines.stream()
                .filter(discipline -> discipline.getDisciplineType() == disciplineType)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("DisciplineType " + disciplineType + " is not supported."));
    }
}
