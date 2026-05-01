package de.ostfale.va.application.domain.service.ranking;

import de.ostfale.va.application.domain.model.playerrankings.HistoryTimestamp;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.domain.model.playerrankings.PlayerId;
import de.ostfale.va.common.UseLogging;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class InMemoryPlayerService implements UseLogging {

    private final ConcurrentHashMap<PlayerId, Player> players = new ConcurrentHashMap<>();

    /**
     * Merges a list of parsed players into the in-memory store.
     * Can be called during startup for historical files and during runtime for new files.
     *
     * @param parsedPlayers the list of players parsed from a single file
     * @param timestamp     the timestamp derived from the file name, used to track history
     */
    public void mergePlayers(List<Player> parsedPlayers, HistoryTimestamp timestamp) {
        if (parsedPlayers.isEmpty()) {
            return;
        }
        log().trace("InMemoryPlayerService :: Merging {} players into memory for timestamp {}", parsedPlayers.size(), timestamp.cwyear());
        String cwYear = timestamp.cwyear();

        for (Player parsedPlayer : parsedPlayers) {
            players.compute(parsedPlayer.getPlayerId(), (id, existingPlayer) -> {
                if (existingPlayer == null) {
                    return parsedPlayer;
                }

                // 1. Update weekly ranking history
                existingPlayer.getHistory().putAll(parsedPlayer.getHistory());
                existingPlayer.setLastUpdated(timestamp);


                // 2. Update current player data and record changes
                applyChange(cwYear, "Club", parsedPlayer::getClubName, existingPlayer::getClubName, existingPlayer::setClubName, existingPlayer);
                applyChange(cwYear, "Age Class", parsedPlayer::getAgeClassGeneral, existingPlayer::getAgeClassGeneral, existingPlayer::setAgeClassGeneral, existingPlayer);
                applyChange(cwYear, "Age Class Detail", parsedPlayer::getAgeClassDetail, existingPlayer::getAgeClassDetail, existingPlayer::setAgeClassDetail, existingPlayer);
                applyChange(cwYear, "District", parsedPlayer::getDistrictName, existingPlayer::getDistrictName, existingPlayer::setDistrictName, existingPlayer);
                applyChange(cwYear, "State", parsedPlayer::getStateName, existingPlayer::getStateName, existingPlayer::setStateName, existingPlayer);
                applyChange(cwYear, "State Group", parsedPlayer::getStateGroup, existingPlayer::getStateGroup, existingPlayer::setStateGroup, existingPlayer);

                // 3. Update rankings and points (no history tracking for these)
                existingPlayer.setSinglePointsAndRanking(parsedPlayer.getSinglePoints(), parsedPlayer.getSingleRanking(), parsedPlayer.getSingleAgeRanking(), parsedPlayer.getSingleTournaments());
                existingPlayer.setDoublePointsAndRanking(parsedPlayer.getDoublePoints(), parsedPlayer.getDoubleRanking(), parsedPlayer.getDoubleAgeRanking(), parsedPlayer.getDoubleTournaments());
                existingPlayer.setMixedPointsAndRanking(parsedPlayer.getMixedPoints(), parsedPlayer.getMixedRanking(), parsedPlayer.getMixedAgeRanking(), parsedPlayer.getMixedTournaments());

                return existingPlayer;
            });
        }
    }

    private <T> void applyChange(String cwYear, String fieldName, Supplier<T> newValueSupplier, Supplier<T> oldValueSupplier,
                                 Consumer<T> setter, Player existingPlayer) {
        T newValue = newValueSupplier.get();
        T oldValue = oldValueSupplier.get();

        if (newValue != null && !newValue.equals(oldValue)) {
            log().trace("Found change in {} for player {}: {} -> {}", fieldName, existingPlayer.getPlayerId().playerId(), oldValue, newValue);
            existingPlayer.addHistoryChange(cwYear, String.valueOf(oldValue), String.valueOf(newValue));
            setter.accept(newValue);
        }
    }

    public List<Player> getAllPlayers() {
        return new ArrayList<>(players.values());
    }

    public List<Player> findPlayers(String filter, int offset, int limit) {
        if (filter == null || filter.isBlank()) return Collections.emptyList();

        String[] tokens = filter.toLowerCase().split("\\s+");
        return players.values().stream()
                .filter(player -> matchPlayer(player, tokens))
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }

    public int countPlayers(String filter) {
        if (filter == null || filter.isBlank()) return 0;

        String[] tokens = filter.toLowerCase().split("\\s+");
        return (int) players.values().stream()
                .filter(player -> matchPlayer(player, tokens))
                .count();
    }

    private boolean matchPlayer(Player player, String[] tokens) {
        String firstName = (player.getFirstName() != null) ? player.getFirstName().toLowerCase() : "";
        String lastName = (player.getLastName() != null) ? player.getLastName().toLowerCase() : "";

        for (String token : tokens) {
            if (token.isEmpty()) continue;
            // The player must contain EVERY token somewhere (first name or last name)
            if (!firstName.contains(token) && !lastName.contains(token)) {
                return false;
            }
        }
        return true;
    }

    public void clear() {
        log().info("InMemoryPlayerService :: Clearing all players from memory");
        players.clear();
    }
}
