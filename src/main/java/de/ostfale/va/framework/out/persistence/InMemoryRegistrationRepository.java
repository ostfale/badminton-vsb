package de.ostfale.va.framework.out.persistence;

import de.ostfale.va.application.domain.model.RegistrationRecord;
import de.ostfale.va.application.port.out.ForRetrievingRegistrations;
import de.ostfale.va.application.port.out.ForStoringRegistrations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class InMemoryRegistrationRepository implements ForStoringRegistrations, ForRetrievingRegistrations {

    private final List<RegistrationRecord> registrations = new CopyOnWriteArrayList<>();

    @Override
    public void store(RegistrationRecord record) {
        if (isThereAnyRegistrationWithinTheLastMinute(record)) {
            return;
        }
        registrations.add(record);
    }

    @Override
    public List<RegistrationRecord> findAll() {
        return List.copyOf(registrations);
    }

    @Override
    public long count() {
        return registrations.size();
    }

    private boolean isThereAnyRegistrationWithinTheLastMinute(RegistrationRecord registration) {
        var lastMinute = System.currentTimeMillis() - 60_000;
        return registrations.stream().
                anyMatch(r -> {
                    var diff = Duration.between(r.timestamp(), registration.timestamp()).getSeconds();
                    return Math.abs(diff) < 60;
                });
    }
}
