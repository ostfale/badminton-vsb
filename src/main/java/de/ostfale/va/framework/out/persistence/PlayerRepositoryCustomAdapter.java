package de.ostfale.va.framework.out.persistence;

import de.ostfale.va.application.domain.model.DataRoot;
import de.ostfale.va.application.port.out.ranking.PlayerRepositoryCustom;
import de.ostfale.va.common.UseLogging;
import org.eclipse.store.storage.types.StorageManager;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class PlayerRepositoryCustomAdapter implements PlayerRepositoryCustom , UseLogging {

    private final DataRoot dataRoot;
    private final StorageManager storageManager;

    public PlayerRepositoryCustomAdapter(DataRoot dataRoot, StorageManager storageManager) {
        this.dataRoot = dataRoot;
        this.storageManager = storageManager;
    }

    @Override
    public LocalDateTime getLastUpdate() {
        return dataRoot.getLastRankingUpdate();
    }

    @Override
    public void setLastUpdate(LocalDateTime timestamp) {
        dataRoot.setLastRankingUpdate(timestamp);
        // Explicitly store the dataRoot object to persist the new timestamp
        storageManager.store(dataRoot);
    }
}
