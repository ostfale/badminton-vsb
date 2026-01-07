package de.ostfale.va.framework.out.filesystem;

import de.ostfale.va.application.port.out.ForDirectoryConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Application Directory Configuration Tests")
class ApplicationDirectoryConfigurationTest {

    private ApplicationDirectoryConfiguration config;

    @BeforeEach
    void setUp() {
        config = new ApplicationDirectoryConfiguration();
    }

    @Test
    @DisplayName("basePath should return user home with app name")
    void basePath_shouldReturnUserHomeWithAppName() {
        // given
        String expectedBasePath = System.getProperty("user.home") + File.separator + ".badminton-vsb";

        // when
        String result = config.basePath();

        // then
        assertEquals(expectedBasePath, result);
    }

    @Test
    @DisplayName("structure should return all required directories")
    void structure_shouldReturnAllRequiredDirectories() {
        // given
        var expectedNumberOfDirectories = 11;

        // when
        List<ForDirectoryConfiguration.DirectoryEntry> structure = config.structure();

        // then
        assertEquals(expectedNumberOfDirectories, structure.size());
    }

    @Test
    @DisplayName("structure should contain config directory")
    void structure_shouldContainConfigDirectory() {
        // given
        var searchName = "config";
        var expectedConfigEntryName = "config";
        var expectedConfigEntryPath = "config";

        // when
        List<ForDirectoryConfiguration.DirectoryEntry> structure = config.structure();

        // then
        ForDirectoryConfiguration.DirectoryEntry configEntry = findEntryByName(structure, searchName);
        assertNotNull(configEntry);
        assertEquals(expectedConfigEntryName, configEntry.name());
        assertEquals(expectedConfigEntryPath, configEntry.path());
        assertTrue(configEntry.createIfMissing());
        assertTrue(configEntry.required());
    }

    @Test
    @DisplayName("structure should contain tournament directory")
    void structure_shouldContainTournamentDirectory() {
        // given
        var expectedTournamentEntryName = "tournament";
        var expectedTournamentEntryPath = "tournament";
        var searchName = "tournament";

        // when
        List<ForDirectoryConfiguration.DirectoryEntry> structure = config.structure();

        // then
        ForDirectoryConfiguration.DirectoryEntry tournamentEntry = findEntryByName(structure, searchName);
        assertNotNull(tournamentEntry);
        assertEquals(expectedTournamentEntryName, tournamentEntry.name());
        assertEquals(expectedTournamentEntryPath, tournamentEntry.path());
        assertTrue(tournamentEntry.createIfMissing());
        assertTrue(tournamentEntry.required());
    }

    @Test
    @DisplayName("structure should contain nested directories")
    void structure_shouldContainNestedDirectories() {
        // given
        var searchNameDashboard = "dashboard";
        var searchNamePlayer = "favPlayerMatches";
        var expectedEntryPath = "data/favPlayer/matches";
        var expectedDashboardPath = "data/dashboard";

        // when
        List<ForDirectoryConfiguration.DirectoryEntry> structure = config.structure();

        // then
        ForDirectoryConfiguration.DirectoryEntry dashboardEntry = findEntryByName(structure, searchNameDashboard);
        assertNotNull(dashboardEntry);
        assertEquals(expectedDashboardPath, dashboardEntry.path());

        ForDirectoryConfiguration.DirectoryEntry favPlayerMatchesEntry = findEntryByName(structure, searchNamePlayer);
        assertNotNull(favPlayerMatchesEntry);
        assertEquals(expectedEntryPath, favPlayerMatchesEntry.path());
    }

    @Test
    @DisplayName("structure should contain all entries with createIfMissing=true")
    void structure_allEntriesShouldHaveCreateIfMissingTrue() {
        // when
        List<ForDirectoryConfiguration.DirectoryEntry> structure = config.structure();

        // then
        assertTrue(structure.stream().allMatch(ForDirectoryConfiguration.DirectoryEntry::createIfMissing));
    }

    @Test
    @DisplayName("structure should contain all entries with required=true")
    void structure_allEntriesShouldBeRequired() {
        // when
        List<ForDirectoryConfiguration.DirectoryEntry> structure = config.structure();

        // then
        assertTrue(structure.stream().allMatch(ForDirectoryConfiguration.DirectoryEntry::required));
    }

    @Test
    @DisplayName("structure should contain expected directory names")
    void structure_shouldContainExpectedDirectoryNames() {
        // when
        List<ForDirectoryConfiguration.DirectoryEntry> structure = config.structure();
        List<String> names = structure.stream().map(ForDirectoryConfiguration.DirectoryEntry::name).toList();

        // then
        assertAll(
                () -> assertTrue(names.contains("config")),
                () -> assertTrue(names.contains("db")),
                () -> assertTrue(names.contains("data")),
                () -> assertTrue(names.contains("dashboard")),
                () -> assertTrue(names.contains("favPlayer")),
                () -> assertTrue(names.contains("favPlayerMatches")),
                () -> assertTrue(names.contains("favTournament")),
                () -> assertTrue(names.contains("favTournamentFavorites")),
                () -> assertTrue(names.contains("logs")),
                () -> assertTrue(names.contains("tournament")),
                () -> assertTrue(names.contains("ranking"))
        );
    }

    private ForDirectoryConfiguration.DirectoryEntry findEntryByName(
            List<ForDirectoryConfiguration.DirectoryEntry> entries, String name) {
        return entries.stream()
                .filter(entry -> name.equals(entry.name()))
                .findFirst()
                .orElse(null);
    }
}
