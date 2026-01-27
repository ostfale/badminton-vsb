package de.ostfale.va.framework.out.web;

import com.vaadin.flow.server.*;
import de.ostfale.va.application.port.in.ForTrackingUserRegistrations;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

@Component
public class CloudflareSessionInitializer implements VaadinServiceInitListener, UseLogging {

    private static final String CLOUDFLARE_EMAIL_HEADER = "Cf-Access-Authenticated-User-Email";
    private static final String SESSION_EMAIL_ATTRIBUTE = "cloudflare-user-email";

    private final ForTrackingUserRegistrations trackRegistrations;

    public CloudflareSessionInitializer(ForTrackingUserRegistrations trackRegistrationService) {
        this.trackRegistrations = trackRegistrationService;
    }

    @Override
    public void serviceInit(ServiceInitEvent event) {
        log().info("CloudflareSessionInitializer :: Initializing");
        event.getSource().addSessionInitListener(this::handleSessionInit);
    }

    private void handleSessionInit(SessionInitEvent sessionInitEvent) {
        sessionInitEvent.getSession().addRequestHandler(this::handleRequest);
    }

    private boolean handleRequest(VaadinSession session, VaadinRequest request, VaadinResponse response) {
        String email = request.getHeader(CLOUDFLARE_EMAIL_HEADER);
        if (email != null && !email.isBlank()) {
            session.setAttribute(SESSION_EMAIL_ATTRIBUTE, email);
            trackRegistrations.trackRegistration(email);
        }
        return false;
    }
}
