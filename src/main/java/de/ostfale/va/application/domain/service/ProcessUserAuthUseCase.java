package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.port.out.ForGettingUserAuthEmail;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

@Service
public class ProcessUserAuthUseCase implements UseLogging {

    private final ForGettingUserAuthEmail userAuthAdapter;

    public ProcessUserAuthUseCase(ForGettingUserAuthEmail userAuthAdapter) {
        this.userAuthAdapter = userAuthAdapter;
    }

    public void execute() {
        String email = userAuthAdapter.getCurrentUserEmail()
                .orElseThrow(() -> new SecurityException("Nicht autorisiert!"));

        log().warn("ProcessUserAuthUseCase :: Verarbeite Daten für: {}", email);
    }
}
