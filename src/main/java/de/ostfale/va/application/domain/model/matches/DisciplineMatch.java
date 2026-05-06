package de.ostfale.va.application.domain.model.matches;

import java.util.ArrayList;
import java.util.List;

public class DisciplineMatch implements TournamentNode{

    private static final String PLAYER_SEPARATOR = " / ";

    private String matchDate;
    private String roundName;
    private String playerOneName;
    private String partnerOneName = null;
    private String playerTwoName;
    private String partnerTwoName = null;

    private final List<MatchSet> matchSets = new ArrayList<>();

    public DisciplineMatch() {
    }

    public String getFirstPlayerOrWithPartnerName() {
        if (partnerOneName != null) {
            return playerOneName + PLAYER_SEPARATOR + partnerOneName;
        }
        return playerOneName;
    }

    public String prepareRoundNameForGroupMatch(String groupName) {
        return roundName + " (" + groupName + ")";
    }

    public String getSecondPlayerOrWithPartnerName() {
        if (partnerTwoName != null) {
            return playerTwoName + PLAYER_SEPARATOR + partnerTwoName;
        }
        return playerTwoName;
    }

    public String getRoundName() {
        return roundName;
    }

    public String getMatchDate() {
        return matchDate != null ? matchDate : "";
    }

    public List<MatchSet> getMatchSets() {
        return matchSets;
    }

    public List<String> getSetResults() {
        return matchSets.stream().map(MatchSet::getDisplayString).toList();
    }

    public void setMatchDate(String matchDate) {
        this.matchDate = matchDate;
    }

    public String getPlayerOneName() {
        return playerOneName;
    }

    public void setPlayerOneName(String playerOneName) {
        this.playerOneName = playerOneName;
    }

    public String getPlayerTwoName() {
        return playerTwoName;
    }

    public void setPlayerTwoName(String playerTwoName) {
        this.playerTwoName = playerTwoName;
    }

    public String getPartnerOneName() {
        return partnerOneName;
    }

    public void setPartnerOneName(String partnerOneName) {
        this.partnerOneName = partnerOneName;
    }

    public String getPartnerTwoName() {
        return partnerTwoName;
    }

    public void setPartnerTwoName(String partnerTwoName) {
        this.partnerTwoName = partnerTwoName;
    }

    public void setRoundName(String roundName) {
        this.roundName = roundName;
    }
}
