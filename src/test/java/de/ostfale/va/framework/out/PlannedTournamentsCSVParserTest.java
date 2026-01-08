package de.ostfale.va.framework.out;

import de.ostfale.va.BaseTest;
import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournament;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Planned Tournaments CSV Parser Tests")
class PlannedTournamentsCSVParserTest extends BaseTest {

    private static final String TEST_FILE_NAME = "plannedtournaments/Tournament_2026_2025-12-27.csv";

    private PlannedTournamentsCSVParser parser;

    @BeforeEach
    void setUp() {
        parser = new PlannedTournamentsCSVParser();
    }

    @Test
    @DisplayName("Should parse planned tournaments from CSV file")
    void shouldParsePlannedTournaments() {
        // given
        InputStream inputStream = getInputStreamFromResources(TEST_FILE_NAME);
        var expectedNumberOfTournaments = 12;
        var exectedFirstTournamentName = "C-RLT SAH U11-U19";
        var exectedLastTournamentName = "2. D-RLT BAW NB O19 (2025/2026)";

        // when
        List<PlannedTournament> tournaments = parser.parsePlannedTournaments(inputStream);

        // then
        assertAll(
                () -> assertNotNull(tournaments),
                () -> assertFalse(tournaments.isEmpty()),
                () -> assertEquals(expectedNumberOfTournaments, tournaments.size()),
                () -> assertEquals(exectedFirstTournamentName, tournaments.getFirst().tournamentName()),
                () -> assertEquals(exectedLastTournamentName, tournaments.getLast().tournamentName())
        );
    }
}
