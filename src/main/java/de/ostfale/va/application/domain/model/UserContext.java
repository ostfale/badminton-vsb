package de.ostfale.va.application.domain.model;

import de.ostfale.va.common.UseLogging;

public class UserContext implements UseLogging {

    private UserData currentUserData;

    public UserData getCurrentUser() {
        if (currentUserData == null) {
            log().warn("UserContext :: No current user set, returning null");
        }
        return currentUserData;
    }

    public void setCurrentUser(UserData currentUserData) {
        log().debug("UserContext :: Set current user to: {}", currentUserData.getName());
        this.currentUserData = currentUserData;
    }
}
