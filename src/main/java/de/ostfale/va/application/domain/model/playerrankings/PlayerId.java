package de.ostfale.va.application.domain.model.playerrankings;

import jakarta.annotation.Nonnull;

import java.util.Objects;

public record PlayerId(@Nonnull String playerId) {
    private static final String PLAYER_ID_ERROR = "playerId must not be null or blank";

    public PlayerId {
        Objects.requireNonNull(playerId, PLAYER_ID_ERROR);
        if (playerId.trim().isBlank()) {
            throw new IllegalArgumentException(PLAYER_ID_ERROR);
        }
    }
}
