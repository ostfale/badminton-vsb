package de.ostfale.va.application.domain.model.playerrankings;

import de.ostfale.va.common.UseLogging;
import jakarta.persistence.Id;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class Player implements UseLogging {

    @Id
    private PlayerId playerId;

    private boolean isFavorite;
    private PlayerTournamentId playerTournamentId;
    private String firstName;
    private String lastName;
    private String ageClassGeneral;
    private String ageClassDetail;
    private String clubName;
    private String districtName;
    private String stateName;
    private Group stateGroup;
    private GenderType gender;
    private int yearOfBirth;
    private Integer singlePoints = 0;
    private Integer singleRanking = 0;
    private Integer singleAgeRanking = 0;
    private Integer singleTournaments = 0;
    private Integer doublePoints = 0;
    private Integer doubleRanking = 0;
    private Integer doubleAgeRanking = 0;
    private Integer doubleTournaments = 0;
    private Integer mixedPoints = 0;
    private Integer mixedRanking = 0;
    private Integer mixedAgeRanking = 0;
    private Integer mixedTournaments = 0;
    private PlayerRankingRelevantTournaments relevantTournaments;
    private Map<HistoryTimestamp, HistoryStatistics> history = new HashMap<>();

    public Player(String playerId,
                  String firstName,
                  String lastName,
                  GenderType gender,
                  int yearOfBirth,
                  String ageClassGeneral,
                  String ageClassDetail,
                  String clubName,
                  String districtName,
                  String stateName,
                  Group stateGroup) {
        setPlayerId(playerId);
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.yearOfBirth = yearOfBirth;
        this.ageClassGeneral = ageClassGeneral;
        this.ageClassDetail = ageClassDetail;
        this.clubName = clubName;
        this.districtName = districtName;
        this.stateName = stateName;
        this.stateGroup = stateGroup;
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }

    // history handling
    public Map<HistoryTimestamp, HistoryStatistics> getHistory() {
        return Collections.unmodifiableMap(history);
    }

    // Adds a historical entry for a specific date and discipline
    public void addHistoryEntry(String fileName, DisciplineType type, RankingSnapshot snapshot) {
        var historytimestamp = new HistoryTimestamp(fileName);
        if (!history.containsKey(historytimestamp)) {
            var historyEntry = new HistoryStatistics();
            historyEntry.updateStatistics(type, snapshot);
            history.put(historytimestamp, historyEntry);
            log().trace("Player {} added history entry for {} and discipline {}", this, historytimestamp, type);
            return;
        }

        var historyEntry = history.get(historytimestamp);
        historyEntry.updateStatistics(type, snapshot);
    }


    public void setSinglePointsAndRanking(Integer singlePoints, Integer singleRanking, Integer ageRanking, Integer noOfTournaments) {
        this.singlePoints = singlePoints;
        this.singleRanking = singleRanking;
        this.singleAgeRanking = ageRanking;
        this.singleTournaments = noOfTournaments;
    }

    public void setDoublePointsAndRanking(Integer doublePoints, Integer doubleRanking, Integer ageRanking, Integer noOfTournaments) {
        this.doublePoints = doublePoints;
        this.doubleRanking = doubleRanking;
        this.doubleAgeRanking = ageRanking;
        this.doubleTournaments = noOfTournaments;
    }

    public void setMixedPointsAndRanking(Integer mixedPoints, Integer mixedRanking, Integer ageRanking, Integer noOfTournaments) {
        this.mixedPoints = mixedPoints;
        this.mixedRanking = mixedRanking;
        this.mixedAgeRanking = ageRanking;
        this.mixedTournaments = noOfTournaments;
    }

    public PlayerRankingRelevantTournaments getRelevantTournaments() {
        return relevantTournaments;
    }

    public void setRelevantTournaments(PlayerRankingRelevantTournaments relevantTournaments) {
        this.relevantTournaments = relevantTournaments;
    }

    public PlayerTournamentId getPlayerTournamentId() {
        return playerTournamentId;
    }

    public void setPlayerTournamentId(PlayerTournamentId playerTournamentId) {
        this.playerTournamentId = playerTournamentId;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
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

    public Group getStateGroup() {
        return stateGroup;
    }

    public void setStateGroup(Group stateGroup) {
        this.stateGroup = stateGroup;
    }

    public Integer getSinglePoints() {
        return singlePoints;
    }

    public Integer getSingleRanking() {
        return singleRanking;
    }

    public Integer getSingleAgeRanking() {
        return singleAgeRanking;
    }

    public Integer getSingleTournaments() {
        return singleTournaments;
    }

    public Integer getDoublePoints() {
        return doublePoints;
    }

    public Integer getDoubleRanking() {
        return doubleRanking;
    }

    public Integer getDoubleAgeRanking() {
        return doubleAgeRanking;
    }

    public Integer getDoubleTournaments() {
        return doubleTournaments;
    }

    public Integer getMixedPoints() {
        return mixedPoints;
    }

    public Integer getMixedRanking() {
        return mixedRanking;
    }

    public Integer getMixedAgeRanking() {
        return mixedAgeRanking;
    }

    public Integer getMixedTournaments() {
        return mixedTournaments;
    }
}
