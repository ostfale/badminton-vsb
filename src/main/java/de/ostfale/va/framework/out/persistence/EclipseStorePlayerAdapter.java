package de.ostfale.va.framework.out.persistence;

import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;
import de.ostfale.va.application.port.out.ranking.ForLoadingPlayers;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class EclipseStorePlayerAdapter implements ForLoadingPlayers, UseLogging {

    private final PlayerRepository repository;

    public EclipseStorePlayerAdapter(PlayerRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Player> findAllPlayers() {
        return repository.findAll();
    }

    @Override
    public Optional<Player> findPlayerById(PlayerId id) {
        return repository.findById(id);
    }

    @Override
    public List<Player> save(List<Player> players) {
        return  repository.saveAll(players);
    }
}
