package de.ostfale.va.application.domain.model;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentKey;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;

import java.util.HashSet;
import java.util.Set;

public class UserData {
    private final String email;
    private final String name;
    private final Set<PlannedTournamentKey> favoriteKeys = new HashSet<>();

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

    @Override
    public String toString() {
        return "UserData{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
