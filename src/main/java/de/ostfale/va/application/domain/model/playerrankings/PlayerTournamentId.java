package de.ostfale.va.application.domain.model.playerrankings;

import jakarta.annotation.Nonnull;

import java.util.Objects;

public record PlayerTournamentId(
        @Nonnull String tournamentId
) {

    private static final String PLAYER_TOURNAMENT_ID_ERROR = "tournamentId must not be null or blank";

    public PlayerTournamentId {
        validatePlayerId(tournamentId);
    }

    private static void validatePlayerId(String tournamentId) {
        if (Objects.isNull(tournamentId) || tournamentId.trim().isBlank()) {
            throw new IllegalArgumentException(PLAYER_TOURNAMENT_ID_ERROR);
        }
    }
}
