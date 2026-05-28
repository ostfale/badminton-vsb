package de.ostfale.va.application.domain.model.matches;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DisciplineMatch implements TournamentNode{

    private static final String PLAYER_SEPARATOR = " / ";

    private static final Map<String, String> NAME_TO_ABBREVIATION_MAP = Map.ofEntries(
            Map.entry("Finale", "F"),
            Map.entry("Final", "F"),
            Map.entry("Halbfinale", "HF"),
            Map.entry("Semi final", "HF"),
            Map.entry("Viertelfinale", "VF"),
            Map.entry("Quarter final", "VF"),
            Map.entry("Achtelfinale", "AF"),
            Map.entry("Runde der 16", "R16"),
            Map.entry("Round of 16", "R16"),
            Map.entry("Runde der 32", "R32"),
            Map.entry("Round of 32", "R32"),
            Map.entry("Runde der 64", "R64"),
            Map.entry("Round of 64", "R64"),
            Map.entry("Runde der 128", "R128"),
            Map.entry("Spiel um Platz 3", "Pl. 3/4"),
            Map.entry("3rd/4th place", "Pl. 3/4"),
            Map.entry("9th/16th place", "Pl. 9/16"),
            Map.entry("Spiel um Platz 7", "Pl. 7"),
            Map.entry("Spiel um Platz 15", "Pl. 15"),
            Map.entry("Qualifikation", "Qual."),
            Map.entry("Qual. 1", "Qual. 1"),
            Map.entry("Qual. 2", "Qual. 2"),
            Map.entry("Qual. F", "Qual. F")
    );

    private String matchDate;
    private String roundName;
    private String playerOneName;
    private String partnerOneName = null;
    private String playerTwoName;
    private String partnerTwoName = null;
    private String retirementMessage = null;
    
    private boolean isTeamOneWinner = false;
    private boolean isTeamTwoWinner = false;

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
        if (roundName == null) {
            return "";
        }
        return NAME_TO_ABBREVIATION_MAP.getOrDefault(roundName, roundName);
    }

    public String getMatchDate() {
        return matchDate != null ? matchDate : "";
    }

    public List<MatchSet> getMatchSets() {
        return matchSets;
    }

    public List<String> getSetResults() {
        List<String> results = new ArrayList<>(matchSets.stream().map(MatchSet::getDisplayString).toList());
        if (retirementMessage != null && !retirementMessage.isEmpty()) {
            results.add(retirementMessage);
        }
        return results;
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
    
    public boolean isTeamOneWinner() {
        return isTeamOneWinner;
    }

    public void setTeamOneWinner(boolean teamOneWinner) {
        isTeamOneWinner = teamOneWinner;
    }

    public boolean isTeamTwoWinner() {
        return isTeamTwoWinner;
    }

    public void setTeamTwoWinner(boolean teamTwoWinner) {
        isTeamTwoWinner = teamTwoWinner;
    }
    
    public String getRetirementMessage() {
        return retirementMessage;
    }

    public void setRetirementMessage(String retirementMessage) {
        this.retirementMessage = retirementMessage;
    }
}
