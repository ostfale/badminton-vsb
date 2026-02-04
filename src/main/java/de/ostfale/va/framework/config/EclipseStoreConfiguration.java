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

    private EmbeddedStorageManager storageManager;

    @Bean
    public EmbeddedStorageManager storageManager() {
        this.storageManager = EmbeddedStorage.start(Paths.get(resolveStorageDirectory()));

        if (this.storageManager.root() == null) {
            DataRoot root = new DataRoot();
            this.storageManager.setRoot(root);
            this.storageManager.storeRoot();
        }
        return this.storageManager;
    }

    @Bean
    public DataRoot dataRoot(EmbeddedStorageManager storageManager) {
        DataRoot root = (DataRoot) storageManager.root();
        if (root == null) {
            root = new DataRoot();
            storageManager.setRoot(root);
            storageManager.storeRoot();
        }
        return root;
    }

    @PreDestroy
    public void shutdown() {
        if (this.storageManager != null) {
            this.storageManager.shutdown();
        }
    }

    // TODO replace with configuration property
    private String resolveStorageDirectory() {
        return System.getProperty("user.home") + "/.badminton-vsb/data";
    }
}
