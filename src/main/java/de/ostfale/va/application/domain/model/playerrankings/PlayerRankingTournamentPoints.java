package de.ostfale.va.application.domain.model.playerrankings;


public record PlayerRankingTournamentPoints(
        String tournamentName,
        String tournamentDiscipline,
        String tournamentWeek,
        String tournamentPlacement,
        int tournamentPoints,
        boolean isRelevant
) {

    public String getDisplayText() {
        return tournamentWeek + " / " + tournamentPoints;
    }
}
