package de.ostfale.va.application.port.in;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentKey;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;

import java.util.Set;

public interface ForManagingFavorites {

    void toggleFavorite(UserIdendityVO identity, PlannedTournamentKey key);

    void togglePlayerFavorite(UserIdendityVO identity, PlayerId playerId);

    Set<PlannedTournamentKey> getFavorites(UserIdendityVO identity);

    Set<PlayerId> getFavoritePlayers(UserIdendityVO identity);

    PlannedTournament syncFavoriteState(PlannedTournament tournament, Set<PlannedTournamentKey> favoriteKeys);

    Player syncFavoritePlayerState(Player player, Set<PlayerId> favoritePlayerIds);
}
