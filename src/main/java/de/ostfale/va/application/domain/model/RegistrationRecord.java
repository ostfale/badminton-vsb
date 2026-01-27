package de.ostfale.va.application.domain.model;

import java.time.LocalDateTime;

public record RegistrationRecord(
        String email,
        LocalDateTime timestamp
) {
    public static RegistrationRecord of(String email) {
        return new RegistrationRecord(email, LocalDateTime.now());
    }
}
