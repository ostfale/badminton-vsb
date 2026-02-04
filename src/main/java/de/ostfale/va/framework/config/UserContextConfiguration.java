package de.ostfale.va.framework.config;

import com.vaadin.flow.spring.scopes.VaadinSessionScope;
import de.ostfale.va.application.domain.model.UserContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;

@Configuration
public class UserContextConfiguration {

    @Bean
    @Scope(scopeName = VaadinSessionScope.VAADIN_SESSION_SCOPE_NAME, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public UserContext userContext() {
        return new UserContext();
    }
}
