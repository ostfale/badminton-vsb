package de.ostfale.va.application.domain.model.plannedournaments;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Tournament Age Classes Value Object Tests")
class TournamentAgeClassesVOTest {

    @ParameterizedTest
    @CsvSource({
            "U9, U9",
            "u09, U9",
            " U11 , U11",
            "u13_extra, U13",
            "O35, O35",
            "o35, O35",
            "XYZ, UOX"
    })
    @DisplayName("Should convert string to correct enum constant")
    void shouldConvertStringToEnum(String input, TournamentAgeClassesVO expected) {
        // Given
        String ageClassInput = input;

        // When
        TournamentAgeClassesVO result = TournamentAgeClassesVO.fromString(ageClassInput);

        // Then
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should throw exception when input is null")
    void shouldThrowExceptionWhenNull() {
        // Given
        String input = null;

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> TournamentAgeClassesVO.fromString(input));
    }

    @Test
    @DisplayName("Should throw exception when input is empty")
    void shouldThrowExceptionWhenEmpty() {
        // Given
        String input = "   ";

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> TournamentAgeClassesVO.fromString(input));
    }

    @Test
    @DisplayName("Should throw exception for unknown age class starting with U or O")
    void shouldThrowExceptionForUnknownValidPrefix() {
        // Given
        String input = "U99";

        // When & Then
        assertThrows(TournamentAgeClassNotFoundException.class, () -> TournamentAgeClassesVO.fromString(input));
    }
}
