package de.ostfale.va.application.domain.model.plannedournaments;

import de.ostfale.va.application.domain.model.plannedournaments.vo.PlannedTournamentCategoriesVO;
import de.ostfale.va.application.domain.model.plannedournaments.vo.TournamentAgeClassesVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Planned Tournaments Filter Tests")
class PlannedTournamentsFilterTest {

    @Test
    @DisplayName("Should create filter with all fields populated")
    void shouldCreateFilterWithAllFields() {
        // given
        String location = "Berlin";
        String name = "Summer Championship";
        boolean validOnly = true;
        boolean thisYear = true;
        boolean onlyFavorites = false;
        Set<TournamentAgeClassesVO> ageClasses = Set.of(TournamentAgeClassesVO.U19, TournamentAgeClassesVO.U15);
        Set<PlannedTournamentCategoriesVO> categories = Set.of(PlannedTournamentCategoriesVO.A);

        // when
        PlannedTournamentsFilter filter = new PlannedTournamentsFilter(
                location, name, validOnly, thisYear, onlyFavorites, ageClasses, categories
        );

        // then
        assertAll(
                () -> assertEquals(location, filter.location().orElseThrow()),
                () -> assertEquals(name, filter.name().orElseThrow()),
                () -> assertTrue(filter.isValidTournamentsOnly()),
                () -> assertTrue(filter.onlyThisYearsTournaments()),
                () -> assertEquals(ageClasses, filter.ageClasses()),
                () -> assertEquals(categories, filter.tourCategories())
        );
    }

    @Test
    @DisplayName("Should create filter with null location and name")
    void shouldCreateFilterWithNullValues() {
        // given
        String location = null;
        String name = null;
        boolean validOnly = false;
        boolean thisYear = false;
        boolean onlyFavorites = false;
        Set<TournamentAgeClassesVO> ageClasses = Set.of(TournamentAgeClassesVO.U13);
        Set<PlannedTournamentCategoriesVO> categories = Set.of(PlannedTournamentCategoriesVO.C);

        // when
        PlannedTournamentsFilter filter = new PlannedTournamentsFilter(
                location, name, validOnly, thisYear, onlyFavorites, ageClasses, categories
        );

        // then
        assertAll(
                () -> assertTrue(filter.location().isEmpty()),
                () -> assertTrue(filter.name().isEmpty()),
                () -> assertFalse(filter.isValidTournamentsOnly()),
                () -> assertFalse(filter.onlyThisYearsTournaments()),
                () -> assertEquals(ageClasses, filter.ageClasses()),
                () -> assertEquals(categories, filter.tourCategories())
        );
    }

    @Test
    @DisplayName("Should handle null age classes by returning empty set")
    void shouldHandleNullAgeClasses() {
        // given
        Set<TournamentAgeClassesVO> ageClasses = null;
        Set<PlannedTournamentCategoriesVO> categories = Set.of(PlannedTournamentCategoriesVO.A);

        // when
        PlannedTournamentsFilter filter = new PlannedTournamentsFilter(
                "Munich", "Winter Cup", true, false, false, ageClasses, categories
        );

        // then
        assertAll(
                () -> assertNotNull(filter.ageClasses()),
                () -> assertTrue(filter.ageClasses().isEmpty()),
                () -> assertEquals(categories, filter.tourCategories())
        );
    }

    @Test
    @DisplayName("Should handle null categories by returning empty set")
    void shouldHandleNullCategories() {
        // given
        Set<TournamentAgeClassesVO> ageClasses = Set.of(TournamentAgeClassesVO.U17);
        Set<PlannedTournamentCategoriesVO> categories = null;

        // when
        PlannedTournamentsFilter filter = new PlannedTournamentsFilter(
                "Hamburg", "Spring Open", false, true, false, ageClasses, categories
        );

        // then
        assertAll(
                () -> assertEquals(ageClasses, filter.ageClasses()),
                () -> assertNotNull(filter.tourCategories()),
                () -> assertTrue(filter.tourCategories().isEmpty())
        );
    }

    @Test
    @DisplayName("Should build filter using builder with all fields")
    void shouldBuildFilterUsingBuilder() {
        // given
        String location = "Frankfurt";
        String name = "National Championship";
        Set<TournamentAgeClassesVO> ageClasses = Set.of(TournamentAgeClassesVO.U15);
        Set<PlannedTournamentCategoriesVO> categories = Set.of(PlannedTournamentCategoriesVO.B);

        // when
        PlannedTournamentsFilter filter = PlannedTournamentsFilter.builder()
                .withLocation(location)
                .withName(name)
                .withValidTournamentsOnly(true)
                .withOnlyThisYearsTournaments(false)
                .withAgeClasses(ageClasses)
                .withTourCategories(categories)
                .build();

        // then
        assertAll(
                () -> assertEquals(location, filter.location().orElseThrow()),
                () -> assertEquals(name, filter.name().orElseThrow()),
                () -> assertTrue(filter.isValidTournamentsOnly()),
                () -> assertFalse(filter.onlyThisYearsTournaments()),
                () -> assertEquals(ageClasses, filter.ageClasses()),
                () -> assertEquals(categories, filter.tourCategories())
        );
    }

    @Test
    @DisplayName("Should build filter with partial fields using builder")
    void shouldBuildFilterWithPartialFields() {
        // given
        String location = "Cologne";

        // when
        PlannedTournamentsFilter filter = PlannedTournamentsFilter.builder()
                .withLocation(location)
                .withValidTournamentsOnly(true)
                .build();

        // then
        assertAll(
                () -> assertEquals(location, filter.location().orElseThrow()),
                () -> assertTrue(filter.name().isEmpty()),
                () -> assertTrue(filter.isValidTournamentsOnly()),
                () -> assertFalse(filter.onlyThisYearsTournaments()),
                () -> assertTrue(filter.ageClasses().isEmpty()),
                () -> assertTrue(filter.tourCategories().isEmpty())
        );
    }

    @Test
    @DisplayName("Should build filter with empty sets for null collections")
    void shouldBuildFilterWithNullCollections() {
        // given / when
        PlannedTournamentsFilter filter = PlannedTournamentsFilter.builder()
                .withName("Test Tournament")
                .withAgeClasses(null)
                .withTourCategories(null)
                .build();

        // then
        assertAll(
                () -> assertEquals("Test Tournament", filter.name().orElseThrow()),
                () -> assertNotNull(filter.ageClasses()),
                () -> assertTrue(filter.ageClasses().isEmpty()),
                () -> assertNotNull(filter.tourCategories()),
                () -> assertTrue(filter.tourCategories().isEmpty())
        );
    }

    @Test
    @DisplayName("Should produce correct toString output")
    void shouldProduceCorrectToStringOutput() {
        // given
        PlannedTournamentsFilter filter = PlannedTournamentsFilter.builder()
                .withLocation("Stuttgart")
                .withName("Regional Open")
                .withValidTournamentsOnly(true)
                .withOnlyThisYearsTournaments(false)
                .build();

        // when
        String result = filter.toString();

        // then
        assertAll(
                () -> assertNotNull(result),
                () -> assertTrue(result.contains("TournamentsFilter:")),
                () -> assertTrue(result.contains("valid tournaments: true")),
                () -> assertTrue(result.contains("only this year:    false")),
                () -> assertTrue(result.contains("name:              Regional Open")),
                () -> assertTrue(result.contains("location:          Stuttgart"))
        );
    }

    @Test
    @DisplayName("Should handle multiple age classes and categories")
    void shouldHandleMultipleAgeClassesAndCategories() {
        // given
        Set<TournamentAgeClassesVO> ageClasses = Set.of(
                TournamentAgeClassesVO.U11,
                TournamentAgeClassesVO.U13,
                TournamentAgeClassesVO.U15,
                TournamentAgeClassesVO.U17
        );
        Set<PlannedTournamentCategoriesVO> categories = Set.of(
                PlannedTournamentCategoriesVO.A,
                PlannedTournamentCategoriesVO.B,
                PlannedTournamentCategoriesVO.C
        );

        // when
        PlannedTournamentsFilter filter = PlannedTournamentsFilter.builder()
                .withAgeClasses(ageClasses)
                .withTourCategories(categories)
                .build();

        // then
        assertAll(
                () -> assertEquals(4, filter.ageClasses().size()),
                () -> assertEquals(3, filter.tourCategories().size()),
                () -> assertTrue(filter.ageClasses().containsAll(ageClasses)),
                () -> assertTrue(filter.tourCategories().containsAll(categories))
        );
    }
}
