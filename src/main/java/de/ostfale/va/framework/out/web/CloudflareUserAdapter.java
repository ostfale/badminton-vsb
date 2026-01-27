package de.ostfale.va.framework.out.web;

import com.vaadin.flow.server.VaadinSession;
import de.ostfale.va.application.port.out.ForGettingUserAuthEmail;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CloudflareUserAdapter implements ForGettingUserAuthEmail, UseLogging {

    @Override
    public Optional<String> getCurrentUserEmail() {
        return Optional.ofNullable((String) VaadinSession.getCurrent().getAttribute("cloudflare-user-email"));
    }

    @Override
    public boolean isAuthenticated() {
        return getCurrentUserEmail().isPresent();
    }
}
