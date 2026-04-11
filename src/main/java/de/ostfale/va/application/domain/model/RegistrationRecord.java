package de.ostfale.va.application.domain.model;

import jakarta.persistence.Id;

import java.time.LocalDateTime;

public record RegistrationRecord(
        @Id
        String email,
        LocalDateTime timestamp
) {
    public static RegistrationRecord of(String email) {
        return new RegistrationRecord(email, LocalDateTime.now());
    }
}
