package de.ostfale.va.application.domain.model.matches;

public record TournamentInfo(
        String tournamentName,
        String tournamentOrganizer,
        String tournamentLocation,
        String tournamentDate,
        Integer tournamentYear
) {
    public TournamentInfo() {
        this("", "", "", "", 1970);
    }
}
