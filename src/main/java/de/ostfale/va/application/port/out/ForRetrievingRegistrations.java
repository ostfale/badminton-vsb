package de.ostfale.va.application.port.out;

import de.ostfale.va.application.domain.model.RegistrationRecord;

import java.util.List;

public interface ForRetrievingRegistrations {

    List<RegistrationRecord> findAll();
    long count();
}
