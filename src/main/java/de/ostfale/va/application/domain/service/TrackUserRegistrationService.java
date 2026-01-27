package de.ostfale.va.application.domain.service;

import de.ostfale.va.application.domain.model.RegistrationRecord;
import de.ostfale.va.application.port.in.ForTrackingUserRegistrations;
import de.ostfale.va.application.port.out.ForStoringRegistrations;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

@Service
public class TrackUserRegistrationService implements ForTrackingUserRegistrations, UseLogging {

    private final ForStoringRegistrations registrationStorage;

    public TrackUserRegistrationService(ForStoringRegistrations registrationStorage) {
        this.registrationStorage = registrationStorage;
    }

    @Override
    public void trackRegistration(String email) {
        RegistrationRecord record = RegistrationRecord.of(email);
        registrationStorage.store(record);
        log().info("Tracked registration for email: {}", email);
    }
}
