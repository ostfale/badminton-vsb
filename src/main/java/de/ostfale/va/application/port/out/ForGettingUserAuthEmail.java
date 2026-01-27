package de.ostfale.va.application.port.out;

import java.util.Optional;

public interface ForGettingUserAuthEmail {

    Optional<String> getCurrentUserEmail();
    boolean isAuthenticated();
}
