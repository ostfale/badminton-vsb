package de.ostfale.va.framework.out;

import de.ostfale.va.application.domain.model.playerrankings.DisciplineType;
import de.ostfale.va.application.domain.model.playerrankings.GenderType;
import de.ostfale.va.application.domain.model.playerrankings.Group;
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
    DISCIPLINE_INDEX(1),
    RANKING_INDEX(2),
    AGE_RANKING_INDEX(3),
    LAST_NAME_INDEX(4),
    FIRST_NAME_INDEX(5),
    PLAYER_ID_INDEX(6),
    BIRTH_YEAR_INDEX(7),
    AGE_CLASS_DETAIL_INDEX(8),
    AGE_CLASS_GENERAL_INDEX(9),
    VALID_POINTS_INDEX(10),
    TOURNAMENTS_INDEX(11),
    CLUB_NAME_INDEX(12),
    DISTRICT_NAME_INDEX(13),
    STATE_NAME_INDEX(14),
    STATE_GROUP_INDEX(15);
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
        try {
            String playerId = getCellText(row, ExcelFileRankingColIndex.PLAYER_ID_INDEX);
            String firstName = getCellText(row, ExcelFileRankingColIndex.FIRST_NAME_INDEX);
            String lastName = getCellText(row, ExcelFileRankingColIndex.LAST_NAME_INDEX);
            GenderType genderType = GenderType.lookup(getCellText(row, ExcelFileRankingColIndex.GENDER_INDEX));
            DisciplineType disciplineType = DisciplineType.lookup(getCellText(row, ExcelFileRankingColIndex.DISCIPLINE_INDEX));

            int yearOfBirth = getCellAsInt(row, ExcelFileRankingColIndex.BIRTH_YEAR_INDEX);
            int nofTournaments = getCellAsInt(row, ExcelFileRankingColIndex.TOURNAMENTS_INDEX);
            int rankingPosition = getCellAsInt(row, ExcelFileRankingColIndex.RANKING_INDEX);
            int ageRankingPosition = getCellAsInt(row, ExcelFileRankingColIndex.AGE_RANKING_INDEX);
            int validPoints = getCellAsInt(row, ExcelFileRankingColIndex.VALID_POINTS_INDEX);

            String ageClassGeneral = getCellText(row, ExcelFileRankingColIndex.AGE_CLASS_GENERAL_INDEX);
            String ageClassDetail = getCellText(row, ExcelFileRankingColIndex.AGE_CLASS_DETAIL_INDEX);
            String clubName = getCellText(row, ExcelFileRankingColIndex.CLUB_NAME_INDEX);
            String districtName = getCellText(row, ExcelFileRankingColIndex.DISTRICT_NAME_INDEX);
            String stateName = getCellText(row, ExcelFileRankingColIndex.STATE_NAME_INDEX);
            Group stateGroupEnum = Group.lookup(getCellText(row, ExcelFileRankingColIndex.STATE_GROUP_INDEX));

            Player player = getOrCreatePlayer(playerId, firstName, lastName, genderType, yearOfBirth, ageClassGeneral,
                    ageClassDetail, clubName, districtName, stateName, stateGroupEnum, playerMap);

            updatePlayerRanking(player, disciplineType, validPoints, rankingPosition, ageRankingPosition, nofTournaments);
        } catch (Exception e) {
            log().error("RankingFileExcelParserAdapter :: Failed to parse ranking file row: {}", row, e);
        }
    }

    private String getCellText(Row row, ExcelFileRankingColIndex colIndex) {
        return row.getCellText(colIndex.index).trim();
    }

    private int getCellAsInt(Row row, ExcelFileRankingColIndex colIndex) {
        return row.getCellAsNumber(colIndex.index)
                .map(BigDecimal::intValue)
                .orElse(0);
    }

    private void updatePlayerRanking(Player player, DisciplineType disciplineType, int validPoints,
                                     int rankingPosition, int ageRankingPosition, int nofTournaments) {
        switch (disciplineType) {
            case SINGLE -> player.setSinglePointsAndRanking(validPoints, rankingPosition, ageRankingPosition, nofTournaments);
            case DOUBLE -> player.setDoublePointsAndRanking(validPoints, rankingPosition, ageRankingPosition, nofTournaments);
            case MIXED -> player.setMixedPointsAndRanking(validPoints, rankingPosition, ageRankingPosition, nofTournaments);
        }
    }

    private Player getOrCreatePlayer(String playerId, String firstName, String lastName, GenderType genderType, Integer birthYear,
                                     String ageClassGeneral, String ageClassDetail, String clubName, String districtName,
                                     String stateName, Group group, Map<String, Player> playerMap) {
        return playerMap.computeIfAbsent(playerId, id -> new Player(
                id,
                firstName,
                lastName,
                genderType,
                birthYear,
                ageClassGeneral, ageClassDetail,
                clubName,
                districtName,
                stateName,
                group));
    }
}
