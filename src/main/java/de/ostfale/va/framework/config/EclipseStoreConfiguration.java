package de.ostfale.va.framework.config;

import de.ostfale.va.application.domain.model.DataRoot;
import de.ostfale.va.common.UseLogging;
import jakarta.annotation.PreDestroy;
import org.eclipse.store.storage.embedded.types.EmbeddedStorage;
import org.eclipse.store.storage.embedded.types.EmbeddedStorageManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
public class EclipseStoreConfiguration implements UseLogging {

    @Bean
    public DataRoot dataRoot(EmbeddedStorageManager storageManager) {
        if (storageManager.root() == null) {
            DataRoot root = new DataRoot();
            storageManager.setRoot(root);
            storageManager.storeRoot();
            return root;
        }
        return (DataRoot) storageManager.root();
    }
}
