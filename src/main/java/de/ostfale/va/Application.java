package de.ostfale.va;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.lumo.Lumo;
import de.ostfale.va.common.UseLogging;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import software.xdev.spring.data.eclipse.store.repository.config.EnableEclipseStoreRepositories;

@SpringBootApplication
@EnableScheduling
@EnableEclipseStoreRepositories
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet(Lumo.UTILITY_STYLESHEET)
@StyleSheet("./css/styles.css")
public class Application implements AppShellConfigurator, UseLogging {

    private static final String LOG_MSG_CONFIGURE = "AppShell :: configurePage";

    static void main(String[] args) {
        UseLogging.staticLog().info(LOG_MSG_CONFIGURE);
        SpringApplication.run(Application.class, args);
    }
}
