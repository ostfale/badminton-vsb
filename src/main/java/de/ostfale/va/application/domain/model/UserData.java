package de.ostfale.va.application.domain.model;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentKey;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;

import java.util.HashSet;
import java.util.Set;

public class UserData {
    private final String email;
    private final String name;
    private final Set<PlannedTournamentKey> favoriteKeys = new HashSet<>();
    private  Set<PlayerId> favoritePlayerIds;

    public UserData(UserIdendityVO identity) {
        this.email = identity.email();
        this.name = identity.name();
    }

    public Set<PlannedTournamentKey> getFavoriteKeys() {
        return favoriteKeys;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public boolean isFavorite(PlannedTournamentKey key) {
        return favoriteKeys.contains(key);
    }

    public void removeFavorite(PlannedTournamentKey key) {
        favoriteKeys.remove(key);
    }

    public void addFavorite(PlannedTournamentKey key) {
        favoriteKeys.add(key);
    }

    public Set<PlayerId> getFavoritePlayerIds() {
        init();
        return favoritePlayerIds;
    }

    public boolean isPlayerFavorite(PlayerId id) {
        init();
        return favoritePlayerIds.contains(id);
    }

    public void addPlayerFavorite(PlayerId id) {
        init();
        favoritePlayerIds.add(id);
    }

    public void removePlayerFavorite(PlayerId id) {
        init();
        favoritePlayerIds.remove(id);
    }

    private void init() {
        if (favoritePlayerIds == null) {
            favoritePlayerIds = new HashSet<>();
        }
    }

    @Override
    public String toString() {
        return "UserData{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
