package de.ostfale.va.framework.out.persistence;

import de.ostfale.va.application.domain.model.UserData;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentKey;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;
import de.ostfale.va.application.port.out.ForGettingUserConfiguration;
import de.ostfale.va.application.port.out.ForStoringUserData;
import de.ostfale.va.application.port.out.UserDataRepository;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

@Component
public class EclipseStoreUserAdapter implements ForStoringUserData, UseLogging {
    private final UserDataRepository repository;

    private final ForGettingUserConfiguration userConfiguration;

    public EclipseStoreUserAdapter(UserDataRepository repository, ForGettingUserConfiguration userConfiguration) {
        this.repository = repository;
        this.userConfiguration = userConfiguration;
    }

    @Override
    public void addFavorite(UserIdendityVO identity, PlannedTournamentKey key) {
        UserData user = userConfiguration.getCurrentUser();
        user.addFavorite(key);
        repository.save(user);
        log().debug("EclipseStoreUserAdapter :: Favorite added for {}", identity.email());
    }

    @Override
    public void addPlayerFavorite(PlayerId playerId) {
        UserData user = userConfiguration.getCurrentUser();
        user.addPlayerFavorite(playerId);
        repository.save(user);
        log().debug("EclipseStoreUserAdapter :: Player favorite {} added for {}", playerId, user.getEmail());
    }

    @Override
    public void removeFavorite(UserIdendityVO identity, PlannedTournamentKey key) {
        UserData user = userConfiguration.getCurrentUser();
        user.removeFavorite(key);
        repository.save(user);
        log().debug("EclipseStoreUserAdapter :: Removed favorite for user {}", identity);
    }

    @Override
    public void removePlayerFavorite(PlayerId playerId) {
        UserData user = userConfiguration.getCurrentUser();
        user.removePlayerFavorite(playerId);
        repository.save(user);
        log().debug("EclipseStoreUserAdapter :: Removed favorite playerId {} for user {}", playerId, user.getEmail());
    }

    @Override
    public UserData findUserByEmail(String email) {
        return repository.findByEmail(email).orElse(null);
    }
}
