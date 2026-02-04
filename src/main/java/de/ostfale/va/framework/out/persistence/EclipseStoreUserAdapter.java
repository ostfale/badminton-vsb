package de.ostfale.va.framework.out.persistence;

import de.ostfale.va.application.domain.model.DataRoot;
import de.ostfale.va.application.domain.model.UserData;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentKey;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;
import de.ostfale.va.application.port.out.ForStoringUserData;
import de.ostfale.va.common.UseLogging;
import org.eclipse.store.storage.types.StorageManager;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class EclipseStoreUserAdapter implements ForStoringUserData, UseLogging {

    private final DataRoot dataRoot;
    private final StorageManager storageManager;

    public EclipseStoreUserAdapter(StorageManager storageManager, DataRoot dataRoot) {
        this.storageManager = storageManager;
        this.dataRoot = dataRoot;
    }

    @Override
    public void updatePlannedTournamentFavorites(UserIdendityVO identity, PlannedTournamentKey key) {

        Map<UserIdendityVO, UserData> map = dataRoot.getUsersMap();
        UserData data = map.get(identity);
        if (data == null) {
            data = new UserData(identity);
            data.getFavoriteKeys().add(key);
            map.put(identity, data);
            storageManager.store(map);
            log().debug("EclipseStoreUserAdapter :: Created new UserData for user {}", identity);
        } else {
            data.getFavoriteKeys().add(key);
            storageManager.store(data);
            log().debug("EclipseStoreUserAdapter :: Updated UserData for user {}", identity);
        }
    }

    @Override
    public UserData findUserByEmail(String email) {
        log().debug("EclipseStoreUserAdapter :: Finding user by email: {}", email);
        var usersFound = dataRoot.getUsersMap();
        return usersFound.get(UserIdendityVO.fromEmail(email));
    }

    public UserData getUserData(UserIdendityVO identity) {
        return dataRoot.getUsersMap().get(identity);
    }
}
