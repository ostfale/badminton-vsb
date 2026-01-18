package de.ostfale.va.framework.out.filesystem;

import de.ostfale.va.application.domain.model.plannedournaments.PlannedTournamentDownloadTask;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("Planned Tournaments Download Config Adapter Tests")
class PlannedTournamentsDownloadConfigAdapterTest {

    private PlannedTournamentsDownloadConfigAdapter sut;

    @BeforeEach
    void setUp() {
        sut = new PlannedTournamentsDownloadConfigAdapter();
    }

    @Test
    @DisplayName("Should prepare correct download URL for a given year")
    void shouldPrepareDownloadUrl() {
        // given
        String year = "2025";
        var expectedYear = "year=" + year;
        var expectedURLStart = "https://turniere.badminton.de/download?";

        // when
        String result = sut.prepareDownloadUrl(year);

        // then
        assertAll(
                () -> assertThat(result.contains(expectedYear)).isTrue(),
                () -> assertThat(result.startsWith(expectedURLStart)).isTrue()
        );
    }

    @Test
    @DisplayName("Should prepare download file name with correct format")
    void shouldPrepareDownloadFileName() {
        // given
        String year = "2024";
        LocalDateTime downloadDateTime = LocalDateTime.of(2024, 12, 15, 10, 30);
        var expectedFileNameStart = "Tournament_2024_";
        var expectedFileName = "Tournament_2024_2024-12-15-10-30.csv";

        // when
        String result = sut.prepareDownloadFileName(year, downloadDateTime);

        // then
        assertAll(
                () -> assertThat(result).startsWith(expectedFileNameStart),
                () -> assertThat(result).endsWith(".csv"),
                () -> assertThat(result).isEqualTo(expectedFileName)
        );
    }

    @Test
    @DisplayName("Should return list of download tasks for current and next year")
    void shouldGetDownloadTasks() {
        // given
        var expectedNumberOfTasks = 2;
        var expectedTasthisYear = "year=" + sut.getCurrentCalendarYear();
        var expectedTaskNextYear = "year=" + sut.getNextCalendarYear();

        // when
        List<PlannedTournamentDownloadTask> tasks = sut.getDownloadTasks();

        // then
        var taskThisYear = tasks.get(0);
        var taskNextYear = tasks.get(1);

        assertAll(
                () -> assertThat(tasks).isNotNull(),
                () -> assertThat(tasks.size()).isEqualTo(expectedNumberOfTasks),
                () -> assertThat(taskThisYear.url()).contains(expectedTasthisYear),
                () -> assertThat(taskNextYear.url()).contains(expectedTaskNextYear),
                () -> assertThat(taskThisYear.destination().toString().contains("tournament")).isTrue()
        );
    }

    @Test
    @DisplayName("Should prepare target path using app directory name")
    void shouldPrepareTargetPath() {
        // given
        String dirName = "test-dir";

        // when
        String result = sut.prepareDownloadTargetPath(dirName);

        // then
        assertAll(
                () -> assertThat(result.contains(dirName)).isTrue(),
                () -> assertThat(result.endsWith(File.separator)).isTrue()
        );
    }

    @Test
    @DisplayName("Should prepare download file name with different years")
    void shouldPrepareDownloadFileNameWithDifferentYears() {
        // given
        String year2025 = "2025";
        String year2026 = "2026";
        LocalDateTime downloadDateTime = LocalDateTime.of(2025, 1, 1, 0, 0);

        // when
        String result2025 = sut.prepareDownloadFileName(year2025, downloadDateTime);
        String result2026 = sut.prepareDownloadFileName(year2026, downloadDateTime);

        // then
        assertAll(
                () -> assertThat(result2025).contains("2025"),
                () -> assertThat(result2026).contains("2026"),
                () -> assertThat(result2025).contains("2025-01-01-00-00"),
                () -> assertThat(result2026).contains("2025-01-01-00-00")
        );
    }

    @Test
    @DisplayName("Should prepare download file name with different timestamps")
    void shouldPrepareDownloadFileNameWithDifferentTimestamps() {
        // given
        String year = "2025";
        LocalDateTime morning = LocalDateTime.of(2025, 3, 15, 8, 45);
        LocalDateTime evening = LocalDateTime.of(2025, 3, 15, 20, 30);

        // when
        String resultMorning = sut.prepareDownloadFileName(year, morning);
        String resultEvening = sut.prepareDownloadFileName(year, evening);

        // then
        assertAll(
                () -> assertThat(resultMorning).isEqualTo("Tournament_2025_2025-03-15-08-45.csv"),
                () -> assertThat(resultEvening).isEqualTo("Tournament_2025_2025-03-15-20-30.csv"),
                () -> assertThat(resultMorning).isNotEqualTo(resultEvening)
        );
    }

    @Test
    @DisplayName("Should prepare download URL containing all required parameters")
    void shouldPrepareDownloadUrlWithAllParameters() {
        // given
        String year = "2024";

        // when
        String result = sut.prepareDownloadUrl(year);

        // then
        assertAll(
                () -> assertThat(result).contains("year=" + year),
                () -> assertThat(result).contains("federation"),
                () -> assertThat(result).contains("agegroup"),
                () -> assertThat(result).contains("category"),
                () -> assertThat(result).contains("discipline")
        );
    }

    @Test
    @DisplayName("Should create download tasks with unique file names")
    void shouldCreateDownloadTasksWithUniqueFileNames() {
        // when
        List<PlannedTournamentDownloadTask> tasks = sut.getDownloadTasks();

        // then
        var taskThisYear = tasks.get(0);
        var taskNextYear = tasks.get(1);

        assertAll(
                () -> assertThat(taskThisYear.destination()).isNotEqualTo(taskNextYear.destination()),
                () -> assertThat(taskThisYear.url()).isNotEqualTo(taskNextYear.url())
        );
    }

    @Test
    @DisplayName("Should read date time from valid file name")
    void shouldReadDateTimeFromFileName() {
        // given
        String fileName = "Tournament_2025_2025-03-15-08-45.csv";
        String expectedDateTime = "15.03.2025 08:45";

        // when
        var result = sut.readDateTimeFromFileName(fileName);

        // then
        assertThat(result).isEqualTo(expectedDateTime);
    }

    @Test
    @DisplayName("Should read date time from file name with different timestamps")
    void shouldReadDateTimeFromFileNameWithDifferentTimestamps() {
        // given
        String fileNameMorning = "Tournament_2024_2024-12-01-09-30.csv";
        String fileNameEvening = "Tournament_2024_2024-12-01-23-59.csv";
        String expectedMorning = "01.12.2024 09:30";
        String expectedEvening = "01.12.2024 23:59";

        // when
        var resultMorning = sut.readDateTimeFromFileName(fileNameMorning);
        var resultEvening = sut.readDateTimeFromFileName(fileNameEvening);

        // then
        assertAll(
                () -> assertThat(resultMorning).isEqualTo(expectedMorning),
                () -> assertThat(resultEvening).isEqualTo(expectedEvening),
                () -> assertThat(resultMorning).isNotEqualTo(resultEvening)
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when file name has invalid format")
    void shouldThrowExceptionWhenFileNameIsInvalid() {
        // given
        String invalidFileName = "invalid-file-name.csv";

        // when & then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> sut.readDateTimeFromFileName(invalidFileName)
        );

        assertThat(exception.getMessage()).contains("Cannot parse datetime from filename");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when file name has no datetime pattern")
    void shouldThrowExceptionWhenDateTimePatternIsMissing() {
        // given
        String fileNameWithoutDateTime = "Tournament_2025.csv";

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> sut.readDateTimeFromFileName(fileNameWithoutDateTime)
        );
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when file name is empty")
    void shouldThrowExceptionWhenFileNameIsEmpty() {
        // given
        String emptyFileName = "";

        // when & then
        assertThrows(IllegalArgumentException.class,
                () -> sut.readDateTimeFromFileName(emptyFileName)
        );
    }

}
