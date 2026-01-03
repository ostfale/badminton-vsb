package de.ostfale.va.application.domain.model.plannedournaments;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Planned Tournament Types Value Object Tests")
class PlannedTournamentTypesVOTest {

    @ParameterizedTest(name = "Should return {1} when display string is {0}")
    @CsvSource({
            "Rangliste, RANKING",
            "RANGLISTE, RANKING",
            "rangliste, RANKING",
            "Meisterschaft, CHAMPIONSHIP",
            "MEISTERSCHAFT, CHAMPIONSHIP",
            "meisterschaft, CHAMPIONSHIP"
    })
    @DisplayName("Successfully resolve enum from valid display strings (case-insensitive)")
    void testFromDisplayStringSuccess(String input, PlannedTournamentTypesVO expected) {
        // Given: a valid display string (from ParameterizedTest)

        // When
        PlannedTournamentTypesVO result = PlannedTournamentTypesVO.fromDisplayString(input);

        // Then
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should throw exception when display string is unknown")
    void testFromDisplayStringUnknown() {
        // Given: an invalid tournament type string
        String unknownType = "Unknown Type";

        // When & Then
        assertThrows(PlannedTournamentTypeNotFoundException.class, () ->
                PlannedTournamentTypesVO.fromDisplayString(unknownType)
        );
    }

    @Test
    @DisplayName("Should throw exception when display string is null")
    void testFromDisplayStringNull() {
        // Given:
        String nullInput = null;

        // When & Then
        assertThrows(PlannedTournamentTypeNotFoundException.class, () ->
                PlannedTournamentTypesVO.fromDisplayString(nullInput)
        );
    }

    @Test
    @DisplayName("Should return the correct display string for each constant")
    void testGetDisplayString() {
        // Given
        PlannedTournamentTypesVO ranking = PlannedTournamentTypesVO.RANKING;
        PlannedTournamentTypesVO championship = PlannedTournamentTypesVO.CHAMPIONSHIP;

        // When & Then
        assertAll(
                () -> assertEquals("Rangliste", ranking.getDisplayString()),
                () -> assertEquals("Meisterschaft", championship.getDisplayString())
        );
    }
}
