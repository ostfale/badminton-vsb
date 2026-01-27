package de.ostfale.va.framework.in.web;

import de.ostfale.va.application.domain.model.RegistrationRecord;
import de.ostfale.va.application.port.out.ForRetrievingRegistrations;
import de.ostfale.va.common.UseLogging;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/registrations")
public class RegistrationController implements UseLogging {

    private final ForRetrievingRegistrations registrationRetriever;

    public RegistrationController(ForRetrievingRegistrations registrationRetriever) {
        this.registrationRetriever = registrationRetriever;
    }

    @GetMapping
    public List<RegistrationRecord> getAllRegistrations() {
        log().info("Fetching all registrations");
        return registrationRetriever.findAll();
    }

    @GetMapping("/count")
    public long getRegistrationCount() {
        return registrationRetriever.count();
    }
}
