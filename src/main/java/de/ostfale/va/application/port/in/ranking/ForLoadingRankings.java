package de.ostfale.va.application.port.in.ranking;

import de.ostfale.va.application.domain.model.playerrankings.Player;

import java.util.List;

public interface ForLoadingRankings {

    List<Player> loadPlayer();

    List<Player> loadFromSource();

    List<Player> findPlayers(String filert, int offset, int limit);

    int countPlayers(String filter);
}
