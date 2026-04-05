package de.ostfale.va.application.port.out.ranking;

import de.ostfale.va.application.domain.model.playerrankings.Player;
import software.xdev.spring.data.eclipse.store.repository.interfaces.EclipseStoreRepository;

// Output port for player and ranking persistence
public interface PlayerRepository extends EclipseStoreRepository<Player, String>, PlayerRepositoryCustom {
}
