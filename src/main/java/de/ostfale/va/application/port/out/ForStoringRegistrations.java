package de.ostfale.va.application.port.out;

import de.ostfale.va.application.domain.model.RegistrationRecord;

public interface ForStoringRegistrations {

    void store(RegistrationRecord record);
}
