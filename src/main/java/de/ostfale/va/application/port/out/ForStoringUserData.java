package de.ostfale.va.application.port.out;

import de.ostfale.va.application.domain.model.UserData;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentKey;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;

public interface ForStoringUserData {

    void updatePlannedTournamentFavorites(UserIdendityVO userIdendityVO, PlannedTournamentKey plannedTournamentKey);

    UserData findUserByEmail(String email);

}
