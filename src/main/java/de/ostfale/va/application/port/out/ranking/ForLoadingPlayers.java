package de.ostfale.va.application.port.out.ranking;

import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;

import java.util.List;
import java.util.Optional;

public interface ForLoadingPlayers {

    List<Player> findAllPlayers();
    Optional<Player> findPlayerById(PlayerId id);
    List<Player> save(List<Player> players);
}
