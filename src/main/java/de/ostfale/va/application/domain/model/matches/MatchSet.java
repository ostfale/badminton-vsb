package de.ostfale.va.application.domain.model.matches;

public class MatchSet {

    private SetNumber setNumber;
    private MatchResultType matchResultType = MatchResultType.REGULAR;
    private int firstValue;
    private int secondValue;

    public MatchSet(SetNumber setNumber, int firstValue, int secondValue) {
        this.setNumber = setNumber;
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }

    public MatchSet(MatchResultType matchResultType) {
        this.matchResultType = matchResultType;
    }

    public String getDisplayString() {
        if (matchResultType == MatchResultType.WALKOVER) {
            return MatchResultType.WALKOVER.getDisplayName();
        }

        String firstValueString = firstValue < 10 ? " " + firstValue : String.valueOf(firstValue);
        String secondValueString = secondValue < 10 ? " " + secondValue : String.valueOf(secondValue);
        return String.format(" %s : %s",  firstValueString, secondValueString);
    }

    public MatchResultType getMatchResultType() {
        return matchResultType;
    }

    public void setMatchResultType(MatchResultType matchResultType) {
        this.matchResultType = matchResultType;
    }
}
