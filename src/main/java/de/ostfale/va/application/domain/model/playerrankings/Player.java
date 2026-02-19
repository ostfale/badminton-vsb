package de.ostfale.va.application.domain.model.playerrankings;

import de.ostfale.va.common.UseLogging;

public class Player implements UseLogging {

    private PlayerId playerId;
    private String firstName;
    private String lastName;
    private String ageClassGeneral;
    private String clubName;
    private String districtName;
    private String stateName;
    private String stateGroup;
    private GenderType gender;
    private int yearOfBirth;

    public Player(String playerId,
                  String firstName,
                  String lastName,
                  GenderType gender,
                  int yearOfBirth,
                  String ageClassGeneral,
                  String clubName,
                  String districtName,
                  String stateName,
                  String stateGroup) {
        setPlayerId(playerId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.yearOfBirth = yearOfBirth;
        this.ageClassGeneral = ageClassGeneral;
        this.clubName = clubName;
        this.districtName = districtName;
        this.stateName = stateName;
        this.stateGroup = stateGroup;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
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

    public void setPlayerId(String playerId) {
        this.playerId = new PlayerId(playerId);
    }

    public String getAgeClassGeneral() {
        return ageClassGeneral;
    }

    public void setAgeClassGeneral(String ageClassGeneral) {
        this.ageClassGeneral = ageClassGeneral;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getDistrictName() {
        return districtName;
    }

    public void setDistrictName(String districtName) {
        this.districtName = districtName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getStateGroup() {
        return stateGroup;
    }

    public void setStateGroup(String stateGroup) {
        this.stateGroup = stateGroup;
    }
}
