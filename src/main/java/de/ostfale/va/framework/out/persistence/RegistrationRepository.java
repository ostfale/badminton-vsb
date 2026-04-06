package de.ostfale.va.framework.out.persistence;

import de.ostfale.va.application.domain.model.RegistrationRecord;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;

public interface RegistrationRepository  extends EclipseStoreRepository<RegistrationRecord, String> {
}
