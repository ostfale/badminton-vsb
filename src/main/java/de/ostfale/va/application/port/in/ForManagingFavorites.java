package de.ostfale.va.application.port.in;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentKey;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;

import java.util.Set;

public interface ForManagingFavorites {

    void toggleFavorite(UserIdendityVO identity, PlannedTournamentKey key);

    boolean isFavorite(UserIdendityVO identity, PlannedTournamentKey key);

    Set<PlannedTournamentKey> getFavorites(UserIdendityVO identity);

    PlannedTournament syncFavoriteState(PlannedTournament tournament, Set<PlannedTournamentKey> favoriteKeys);
}
