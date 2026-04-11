package de.ostfale.va.framework.out.persistence;

import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;

public interface PlayerRepository extends EclipseStoreRepository<Player, PlayerId> {
}
