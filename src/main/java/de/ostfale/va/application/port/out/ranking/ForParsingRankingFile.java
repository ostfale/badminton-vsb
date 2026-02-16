package de.ostfale.va.application.port.out.ranking;

import de.ostfale.va.application.domain.model.playerrankings.Player;

import java.nio.file.Path;
import java.util.List;

public interface ForParsingRankingFile {

    List<Player> parseRankingFile(Path filePath);
}
