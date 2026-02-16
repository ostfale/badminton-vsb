package de.ostfale.va.framework.out;

import de.ostfale.va.BaseTest;
import de.ostfale.va.application.domain.model.playerrankings.GenderType;
import de.ostfale.va.application.domain.model.playerrankings.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Ranking File Excel Parser Adapter Tests")
class RankingFileExcelParserAdapterTest extends BaseTest {

    private static final String TEST_FILE_NAME = "ranking/Ranking_2026_KW7.xlsx";

    private RankingFileExcelParserAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new RankingFileExcelParserAdapter();
    }

    @Test
    @DisplayName("Should successfully parse ranking file")
    void shouldParseRankingFile() throws Exception {
        // given
        Path testRankingFile = readFile(TEST_FILE_NAME);
        var expectedNumberOfPlayers = 9074;

        // when
        List<Player> players = sut.parseRankingFile(testRankingFile);

        // then
        assertAll(
                () -> assertThat(players).isNotNull(),
                () -> assertThat(players.size()).isEqualTo(expectedNumberOfPlayers)
        );
    }

    @Test
    @DisplayName("Check player details")
    void checkPlayerDetails() throws URISyntaxException {
        // given
        Path testRankingFile = readFile(TEST_FILE_NAME);
        var testPlayePlayerId = "06-153648";

        // when
        List<Player> players = sut.parseRankingFile(testRankingFile);
        var player = players.stream()
                .filter(p -> p.getPlayerId().playerId().equals(testPlayePlayerId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Player not found"));

        // then
        assertAll(
                () -> assertThat(player.getFirstName()).isEqualTo("Victoria"),
                () -> assertThat(player.getLastName()).isEqualTo("Braun"),
                () -> assertThat(player.getGender()).isEqualTo(GenderType.FEMALE),
                () -> assertThat(player.getYearOfBirth()).isEqualTo(2010)
        );

    }
}
