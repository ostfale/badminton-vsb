package de.ostfale.va.application.port.out;

import de.ostfale.va.application.domain.model.UserData;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentKey;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;

public interface ForStoringUserData {

    void addFavorite(UserIdendityVO userIdendityVO, PlannedTournamentKey plannedTournamentKey);

    void addPlayerFavorite(PlayerId playerId);

    void removeFavorite(UserIdendityVO userIdendityVO, PlannedTournamentKey plannedTournamentKey);

    void removePlayerFavorite(PlayerId playerId);

    UserData findUserByEmail(String email);

}
