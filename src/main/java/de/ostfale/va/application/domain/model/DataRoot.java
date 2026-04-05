package de.ostfale.va.application.domain.model;

import de.ostfale.va.application.domain.model.playerrankings.Player;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DataRoot {
    private final List<UserData> users = new ArrayList<>();
    private final List<RegistrationRecord> registrations = new ArrayList<>();
    private final List<Player> players = new ArrayList<>();

    private LocalDateTime lastRankingUpdate;     // track last import

    public List<Player> getPlayers() {
        return players;
    }

    public LocalDateTime getLastRankingUpdate() {
        return lastRankingUpdate;
    }

    public void setLastRankingUpdate(LocalDateTime lastRankingUpdate) {
        this.lastRankingUpdate = lastRankingUpdate;
    }
}
