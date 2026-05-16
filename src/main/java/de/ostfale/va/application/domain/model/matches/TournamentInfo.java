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

    @Override
    public String tournamentDate() {
        if (tournamentDate.contains("bis")) {
            return tournamentDate.split("bis")[0];
        }

        return tournamentDate;
    }
}
