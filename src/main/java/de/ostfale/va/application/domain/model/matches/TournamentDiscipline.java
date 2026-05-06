package de.ostfale.va.application.domain.model.matches;

import java.util.ArrayList;
import java.util.List;

public class TournamentDiscipline implements TournamentNode{

    private DisciplineInfo disciplineInfo;

    private final List<DisciplineMatch> eliminationMatches = new ArrayList<>();
    private final List<DisciplineMatch> groupMatches = new ArrayList<>();

    // provide discipline name (h5) and group name (for combined tournaments)
    private String disciplineName ="";
    private String groupName = "";

    public TournamentDiscipline() {
    }

    public DisciplineInfo getDisciplineInfo() {
        return disciplineInfo;
    }

    public DisciplineType getDisciplineType() {
        return disciplineInfo.disciplineType() != null ? disciplineInfo.disciplineType() : null;
    }

    public boolean hasEliminationMatches() {
        return !eliminationMatches.isEmpty();
    }

    public boolean hasGroupMatches() {
        return !groupMatches.isEmpty();
    }

    public List<DisciplineMatch> getEliminationMatches() {
        return eliminationMatches;
    }

    public List<DisciplineMatch> getGroupMatches() {
        return groupMatches;
    }

    public void setDisciplineInfo(DisciplineInfo disciplineInfo) {
        this.disciplineInfo = disciplineInfo;
    }

    public AgeClass getDisciplineAgeClass() {
        return disciplineInfo.ageClass();
    }

    public void setDisciplineName(String disciplineName) {
        this.disciplineName = disciplineName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDisciplineName() {
        return disciplineName;
    }

    public String getGroupName() {
        return groupName;
    }
}


