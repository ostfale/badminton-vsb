package de.ostfale.va.framework.out.web;

import com.vaadin.flow.server.*;
import de.ostfale.va.application.domain.model.UserData;
import de.ostfale.va.application.domain.model.plannedournaments.vo.UserIdendityVO;
import de.ostfale.va.application.port.in.ForTrackingUserRegistrations;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

@Component
public class CloudflareSessionInitializer implements VaadinServiceInitListener, UseLogging {

    private static final String CLOUDFLARE_EMAIL_HEADER = "Cf-Access-Authenticated-User-Email";
    private final ForTrackingUserRegistrations trackRegistrations;

    private final SessionUserContextProviderAdapter sessionUserContextProviderAdapter;

    public CloudflareSessionInitializer(ForTrackingUserRegistrations trackRegistrationService,
                                        SessionUserContextProviderAdapter sessionUserContextProviderAdapter) {
        this.trackRegistrations = trackRegistrationService;
        this.sessionUserContextProviderAdapter = sessionUserContextProviderAdapter;
    }

    @Override
    public void serviceInit(ServiceInitEvent event) {
        log().info("CloudflareSessionInitializer :: Initializing");
        event.getSource().addSessionInitListener(this::handleSessionInit);
    }

    private void handleSessionInit(SessionInitEvent sessionInitEvent) {
        VaadinSession session = sessionInitEvent.getSession();
        VaadinRequest request = sessionInitEvent.getRequest();

        // Load user when session is initialized
        session.access(() -> {
            String email = request.getHeader(CLOUDFLARE_EMAIL_HEADER);

            if (email == null || email.isBlank()) {
                log().error("CloudflareSessionInitializer :: No Cloudflare header found!");
                return;
            }

            var userData = new UserData(UserIdendityVO.fromEmail(email));
            log().info("CloudflareSessionInitializer :: Current user:: {}", userData);
            sessionUserContextProviderAdapter.setCurrentUserData(userData);
            trackRegistrations.trackRegistration(email);
        });
    }
}
