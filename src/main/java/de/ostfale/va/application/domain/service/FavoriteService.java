package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.domain.model.UserData;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentKey;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;
import de.ostfale.va.application.port.in.ForManagingFavorites;
import de.ostfale.va.application.port.out.ForStoringUserData;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FavoriteService implements ForManagingFavorites, UseLogging {

    private final ForStoringUserData forStoringUserData;

    public FavoriteService(ForStoringUserData forStoringUserData) {
        this.forStoringUserData = forStoringUserData;
    }

    public void toggleFavorite(UserIdendityVO identity, PlannedTournamentKey key) {
        UserData userData = forStoringUserData.findUserByEmail(identity.email());

        if (userData == null) {
            log().warn("FavoriteService :: User not found for identity {}", identity);
            return;
        }

        if (userData.isFavorite(key)) {
            forStoringUserData.removeFavorite(identity, key);
            log().debug("FavoriteService :: Removed favorite {} for user {}", key, identity.email());
        } else {
            forStoringUserData.addFavorite(identity, key);
            log().debug("FavoriteService :: Added favorite {} for user {}", key, identity.email());
        }
    }

    public boolean isFavorite(UserIdendityVO identity, PlannedTournamentKey key) {
        UserData userData = forStoringUserData.findUserByEmail(identity.email());
        return userData != null && userData.isFavorite(key);
    }

    public Set<PlannedTournamentKey> getFavorites(UserIdendityVO identity) {
        UserData userData = forStoringUserData.findUserByEmail(identity.email());
        return userData != null ? userData.getFavoriteKeys() : Set.of();
    }

    public PlannedTournament syncFavoriteState(PlannedTournament tournament, Set<PlannedTournamentKey> favoriteKeys) {
        var tournamentKey = tournament.createKey();
        if (favoriteKeys.contains(tournamentKey)) {
            return tournament.setFavorite(true);
        }
        tournament.setFavorite(false);
        return tournament;
    }
}
