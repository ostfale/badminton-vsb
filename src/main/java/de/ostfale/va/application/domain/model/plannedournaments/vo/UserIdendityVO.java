package de.ostfale.va.application.domain.model.plannedournaments.vo;

import java.util.Arrays;

public enum UserIdendityVO {
    gisbert("gisbert.benecke@hamburg-badminton.de", "Gisbert Benecke"),
    johannes("meyer@multicash-solutions.de", "Johannes Meyer"),
    marianne("marianneflato@gmx.de", "Marianne Flato"),
    uwe("info@uwe-sauerbrei.de", "Uwe Sauerbrei");

    private final String email;
    private final String fullName;

    UserIdendityVO(String mail, String fullName) {
        this.email = mail;
        this.fullName = fullName;
    }

    public static UserIdendityVO fromEmail(String email) {
        return Arrays.stream(values())
                .filter(v -> v.email.equalsIgnoreCase(email))
                .findFirst()
                .orElse(uwe);
    }

    public String email() {
        return email;
    }

    public String fullName() {
        return fullName;
    }
}
