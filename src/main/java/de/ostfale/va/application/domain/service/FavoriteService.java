package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.domain.model.UserData;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentKey;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;
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

    @Override
    public void toggleFavorite(UserIdendityVO identity, PlannedTournamentKey key) {
        UserData userData = forStoringUserData.findUserByEmail(identity.email());

        if (userData != null && userData.isFavorite(key)) {
            forStoringUserData.removeFavorite(identity, key);
            log().debug("FavoriteService :: Removed favorite {} for user {}", key, identity.email());
        } else {
            forStoringUserData.addFavorite(identity, key);
            log().debug("FavoriteService :: Added favorite {} for user {}", key, identity.email());
        }
    }

    @Override
    public void togglePlayerFavorite(UserIdendityVO identity, PlayerId playerId) {
        UserData userData = forStoringUserData.findUserByEmail(identity.email());
       /* if (userData != null && userData.isPlayerFavorite(playerId)) {
            forStoringUserData.removePlayerFavorite(identity, playerId);
            log().debug("FavoriteService :: Removed favorite playerId {} for user {}", playerId, identity.email());
        } else {
            forStoringUserData.addPlayerFavorite(identity, playerId);
            log().debug("FavoriteService :: Added favorite playerId {} for user {}", playerId, identity.email());
        }*/
    }

    @Override
    public Set<PlannedTournamentKey> getFavorites(UserIdendityVO identity) {
        UserData userData = forStoringUserData.findUserByEmail(identity.email());
        return userData != null ? userData.getFavoriteKeys() : Set.of();
    }

    @Override
    public Set<PlayerId> getFavoritePlayers(UserIdendityVO identity) {
        UserData userData = forStoringUserData.findUserByEmail(identity.email());
        if (userData == null || userData.getFavoritePlayerIds() == null) {
            return Set.of();
        }

        return userData.getFavoritePlayerIds();
    }

    @Override
    public PlannedTournament syncFavoriteState(PlannedTournament tournament, Set<PlannedTournamentKey> favoriteKeys) {
        var tournamentKey = tournament.createKey();
        if (favoriteKeys.contains(tournamentKey)) {
            return tournament.setFavorite(true);
        }
        tournament.setFavorite(false);
        return tournament;
    }

    @Override
    public Player syncFavoritePlayerState(Player player, Set<PlayerId> favoritePlayerIds) {
        var playerId = player.getPlayerId();
        if (favoritePlayerIds.contains(playerId)) {
            player.setFavorite(true);
        } else {
            player.setFavorite(false);
        }
        return null;
    }
}
