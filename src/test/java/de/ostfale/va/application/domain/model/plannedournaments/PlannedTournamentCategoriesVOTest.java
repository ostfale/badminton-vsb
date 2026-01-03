package de.ostfale.va.application.domain.model.plannedournaments;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Planned Tournament Categories Value Object Tests")
class PlannedTournamentCategoriesVOTest {

    @ParameterizedTest
    @CsvSource({
            "A-Level, A",
            "a-level, A",
            "BEC Event, BEC",
            "BEC-U15, BEC15",
            "BWF Event, BWF",
            "E-Level, E"
    })
    @DisplayName("Should convert display name to correct enum constant")
    void shouldConvertDisplayNameToEnum(String displayName, PlannedTournamentCategoriesVO expected) {
        // When
        PlannedTournamentCategoriesVO result = PlannedTournamentCategoriesVO.fromDisplayName(displayName);

        // Then
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Should throw exception when display name is unknown")
    void shouldThrowExceptionForUnknownDisplayName() {
        // Given
        String unknownName = "Unknown Level";

        // When & Then
        assertThrows(PlannedTournamentCategoryNotFoundException.class, () ->
                PlannedTournamentCategoriesVO.fromDisplayName(unknownName));
    }

    @Test
    @DisplayName("Should throw exception when display name is null")
    void shouldThrowExceptionForNullDisplayName() {
        // Given
        String nullInput = null;

        // When & Then
        assertThrows(PlannedTournamentCategoryNotFoundException.class, () ->
                PlannedTournamentCategoriesVO.fromDisplayName(nullInput));
    }

    @Test
    @DisplayName("Should return correct filter values")
    void shouldReturnCorrectFilterValues() {
        // Given
        PlannedTournamentCategoriesVO[] expectedFilters = {
                PlannedTournamentCategoriesVO.BEC,
                PlannedTournamentCategoriesVO.BWF,
                PlannedTournamentCategoriesVO.A,
                PlannedTournamentCategoriesVO.B,
                PlannedTournamentCategoriesVO.C,
                PlannedTournamentCategoriesVO.D,
                PlannedTournamentCategoriesVO.E
        };

        // When
        PlannedTournamentCategoriesVO[] actualFilters = PlannedTournamentCategoriesVO.getFilterValues();

        // Then
        assertArrayEquals(expectedFilters, actualFilters);
    }

    @Test
    @DisplayName("Should return correct base category")
    void shouldReturnCorrectBaseCategory() {
        // Given
        PlannedTournamentCategoriesVO category = PlannedTournamentCategoriesVO.BEC17;

        // When
        String baseCategory = category.getBaseCategory();

        // Then
        assertEquals("BEC", baseCategory);
    }
}
