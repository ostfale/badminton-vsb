package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.domain.model.UserData;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;
import de.ostfale.va.application.port.in.ForHandlingFavorites;
import de.ostfale.va.application.port.out.ForStoringUserData;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FavoriteRankingService implements ForHandlingFavorites<PlayerId>, UseLogging {

    private final ForStoringUserData forStoringUserData;

    public FavoriteRankingService(ForStoringUserData forStoringUserData) {
        this.forStoringUserData = forStoringUserData;
    }

    @Override
    public Set<PlayerId> getFavorites(UserIdendityVO identity) {
        UserData userData = forStoringUserData.findUserByEmail(identity.email());
        Set<PlayerId> keys = userData != null ? userData.getFavoritePlayerIds() : Set.of();
        log().debug("FavoriteRankingService :: Favorites for user {}: {}", identity, keys);
        return keys;
    }
}
