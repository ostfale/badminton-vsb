package de.ostfale.va.application.port.out;

import de.ostfale.va.application.domain.model.UserData;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;

import java.util.Optional;

public interface UserDataRepository extends EclipseStoreRepository<UserData, String> {

    Optional<UserData> findByEmail(String email);
}
