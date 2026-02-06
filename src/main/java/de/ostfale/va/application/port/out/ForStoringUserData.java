package de.ostfale.va.application.port.out;

import de.ostfale.va.application.domain.model.UserData;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentKey;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;

public interface ForStoringUserData {

    void addFavorite(UserIdendityVO userIdendityVO, PlannedTournamentKey plannedTournamentKey);

    void removeFavorite(UserIdendityVO userIdendityVO, PlannedTournamentKey plannedTournamentKey);

    UserData findUserByEmail(String email);

}
