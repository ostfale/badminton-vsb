package de.ostfale.va.application.domain.model.playerrankings;

import de.ostfale.va.common.UseLogging;

public class Player implements UseLogging {

    private PlayerId playerId;
    private String firstName;
    private String lastName;
    private GenderType gender;
    private int yearOfBirth;


    public Player(String playerId, String firstName, String lastName, GenderType gender, int yearOfBirth) {
        setPlayerId(playerId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.yearOfBirth = yearOfBirth;
    }

    public void setPlayerId(String playerId) {
        this.playerId = new PlayerId(playerId);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public GenderType getGender() {
        return gender;
    }

    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    public int getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(int yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public PlayerId getPlayerId() {
        return playerId;
    }
}
