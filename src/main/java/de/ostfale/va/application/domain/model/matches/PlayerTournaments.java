package de.ostfale.va.application.domain.model.matches;

import java.util.ArrayList;
import java.util.List;

public class PlayerTournaments {

    private final List<Tournament> playerTournaments = new ArrayList<>();

    public List<Tournament> getPlayerTournaments() {
        return playerTournaments;
    }

    public void addPlayerTournament(Tournament tournament) {
        playerTournaments.add(tournament);
    }
}
