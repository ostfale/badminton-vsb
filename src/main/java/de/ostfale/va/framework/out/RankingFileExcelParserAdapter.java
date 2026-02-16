package de.ostfale.va.framework.out;

import de.ostfale.va.application.domain.model.playerrankings.GenderType;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import de.ostfale.va.application.port.out.ranking.ForParsingRankingFile;
import de.ostfale.va.common.UseLogging;
import org.dhatim.fastexcel.reader.ReadableWorkbook;
import org.dhatim.fastexcel.reader.Row;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

enum ExcelFileRankingColIndex {
    GENDER_INDEX(0),
    LAST_NAME_INDEX(4),
    FIRST_NAME_INDEX(5),
    PLAYER_ID_INDEX(6),
    BIRTH_YEAR_INDEX(7);

    final int index;

    ExcelFileRankingColIndex(int index) {
        this.index = index;
    }
}

@Component
public class RankingFileExcelParserAdapter implements ForParsingRankingFile, UseLogging {
    @Override
    public List<Player> parseRankingFile(Path filePath) {
        log().debug("RankingFileExcelParserAdapter :: Parsing ranking file {}", filePath);
        final Map<String, Player> playerMap = new HashMap<>();
        try (InputStream is = Files.newInputStream(filePath);
             ReadableWorkbook wb = new ReadableWorkbook(is)) {
            try (Stream<Row> rows = wb.getFirstSheet().openStream()) {
                rows.forEach(row -> {
                    if (row.getRowNum() == 1) return; // skip header
                    mapToDomainModel(row, playerMap);
                });
            }
        } catch (Exception e) {
            log().error("RankingFileExcelParserAdapter :: Failed to parse ranking file {}", filePath, e);
        }
        return List.copyOf(playerMap.values());
    }

    private void mapToDomainModel(Row row, Map<String, Player> playerMap) {
        String playerId = row.getCellText(ExcelFileRankingColIndex.PLAYER_ID_INDEX.index);
        String firstName = row.getCellText(ExcelFileRankingColIndex.FIRST_NAME_INDEX.index);
        String lastName = row.getCellText(ExcelFileRankingColIndex.LAST_NAME_INDEX.index);
        String gender = row.getCellText(ExcelFileRankingColIndex.GENDER_INDEX.index);
        GenderType genderType = GenderType.lookup(gender);
        int yearOfBirth = row.getCellAsNumber(ExcelFileRankingColIndex.BIRTH_YEAR_INDEX.index)
                .orElse(BigDecimal.ONE)
                .intValue();

        playerMap.computeIfAbsent(playerId, id -> new Player(id, firstName, lastName, genderType, yearOfBirth));
    }
}
