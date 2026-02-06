package de.ostfale.va.framework.out.web;

import de.ostfale.va.application.domain.model.UserData;
import de.ostfale.va.application.port.out.ForGettingUserConfiguration;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

@Component
@SessionScope
public class SessionUserContextProviderAdapter implements ForGettingUserConfiguration, UseLogging {

    private UserData currentUserData;

    @Override
    public UserData getCurrentUser() {
        return currentUserData;
    }

    public void setCurrentUserData(UserData currentUserData) {
        this.currentUserData = currentUserData;
    }
}
