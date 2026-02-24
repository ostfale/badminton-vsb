package de.ostfale.va.common;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Use File System Handling Tests")
class UseFileSystemHandlingTest {

    private UseFileSystemHandling sut;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        sut = new UseFileSystemHandling() {
            @Override
            public Logger log() {
                return LoggerFactory.getLogger(UseFileSystemHandlingTest.class);
            }
        };
    }

    @Test
    @DisplayName("Should get home directory from system property")
    void shouldGetHomeDirFromSystemProperty() {
        // When
        String homeDir = sut.getHomeDir();

        // Then
        assertThat(homeDir).isNotNull();
        assertThat(homeDir).isEqualTo(System.getProperty("user.home"));
    }

    @Test
    @DisplayName("Should get application home directory")
    void shouldGetApplicationHomeDir() {
        // When
        String appHomeDir = sut.getApplicationHomeDir();

        // Then
        assertThat(appHomeDir).isNotNull();
        assertThat(appHomeDir).contains(System.getProperty("user.home"));
        assertThat(appHomeDir).endsWith(".badminton-vsb");
    }

    @Test
    @DisplayName("Should get application subdirectory")
    void shouldGetApplicationSubDir() {
        // Given
        String subDirName = "ranking";

        // When
        String subDir = sut.getApplicationSubDir(subDirName);

        // Then
        assertThat(subDir).isNotNull();
        assertThat(subDir).contains(".badminton-vsb");
        assertThat(subDir).endsWith("ranking");
    }

    @Test
    @DisplayName("Should read all files from directory")
    void shouldReadAllFilesFromDirectory() throws IOException {
        // Given
        Files.createFile(tempDir.resolve("file1.txt"));
        Files.createFile(tempDir.resolve("file2.txt"));
        Files.createDirectory(tempDir.resolve("subdir"));

        // When
        List<File> files = sut.readAllFiles(tempDir.toString());

        // Then
        assertThat(files).hasSize(2);
        assertThat(files).allMatch(File::isFile);
    }

    @Test
    @DisplayName("Should return empty list when directory is empty")
    void shouldReturnEmptyListWhenDirectoryIsEmpty() {
        // When
        List<File> files = sut.readAllFiles(tempDir.toString());

        // Then
        assertThat(files).isEmpty();
    }

    @Test
    @DisplayName("Should get first file timestamp from file")
    void shouldGetFirstFileTimestampFromFile() throws IOException {
        // Given
        Path testFile = tempDir.resolve("test.txt");
        Files.createFile(testFile);

        // When
        LocalDateTime timestamp = sut.getFirstFileTimestamp(testFile);

        // Then
        assertThat(timestamp).isNotNull();
        assertThat(timestamp).isNotEqualTo(LocalDateTime.MIN);
        assertThat(timestamp).isBefore(LocalDateTime.now().plusSeconds(1));
    }

    @Test
    @DisplayName("Should get first file timestamp from directory")
    void shouldGetFirstFileTimestampFromDirectory() throws IOException {
        // Given
        Path testFile = tempDir.resolve("test.txt");
        Files.createFile(testFile);

        // When
        LocalDateTime timestamp = sut.getFirstFileTimestamp(tempDir);

        // Then
        assertThat(timestamp).isNotNull();
        assertThat(timestamp).isNotEqualTo(LocalDateTime.MIN);
    }

    @Test
    @DisplayName("Should return MIN timestamp when directory is empty")
    void shouldReturnMinTimestampWhenDirectoryIsEmpty() {
        // When
        LocalDateTime timestamp = sut.getFirstFileTimestamp(tempDir);

        // Then
        assertThat(timestamp).isEqualTo(LocalDateTime.MIN);
    }

    @Test
    @DisplayName("Should return MIN timestamp when file does not exist")
    void shouldReturnMinTimestampWhenFileDoesNotExist() {
        // Given
        Path nonExistentFile = tempDir.resolve("nonexistent.txt");

        // When
        LocalDateTime timestamp = sut.getFirstFileTimestamp(nonExistentFile);

        // Then
        assertThat(timestamp).isEqualTo(LocalDateTime.MIN);
    }

    @Test
    @DisplayName("Should throw exception when path is null for timestamp")
    void shouldThrowExceptionWhenPathIsNullForTimestamp() {
        // When & Then
        assertThrows(NullPointerException.class, () -> sut.getFirstFileTimestamp(null));
    }

    @Test
    @DisplayName("Should read all files as input streams")
    void shouldReadAllFilesAsInputStreams() throws IOException {
        // Given
        Files.writeString(tempDir.resolve("file1.txt"), "content1");
        Files.writeString(tempDir.resolve("file2.txt"), "content2");

        // When
        List<InputStream> streams = sut.readAllFilesAsStreams(tempDir.toString());

        // Then
        assertThat(streams).hasSize(2);
        assertThat(streams).allMatch(stream -> stream != null);

        // Clean up streams
        streams.forEach(stream -> {
            try {
                stream.close();
            } catch (IOException e) {
                // Ignore
            }
        });
    }

    @Test
    @DisplayName("Should create directory when reading streams from non-existent directory")
    void shouldCreateDirectoryWhenReadingStreamsFromNonExistentDirectory() throws IOException {
        // Given
        Path nonExistentDir = tempDir.resolve("newdir");

        // When
        List<InputStream> streams = sut.readAllFilesAsStreams(nonExistentDir.toString());

        // Then
        assertThat(Files.exists(nonExistentDir)).isTrue();
        assertThat(streams).isEmpty();
    }

    @Test
    @DisplayName("Should return empty list when reading streams from empty directory")
    void shouldReturnEmptyListWhenReadingStreamsFromEmptyDirectory() {
        // When
        List<InputStream> streams = sut.readAllFilesAsStreams(tempDir.toString());

        // Then
        assertThat(streams).isEmpty();
    }

    @Test
    @DisplayName("Should delete all files in directory")
    void shouldDeleteAllFilesInDirectory() throws IOException {
        // Given
        Path file1 = tempDir.resolve("file1.txt");
        Path file2 = tempDir.resolve("file2.txt");
        Files.createFile(file1);
        Files.createFile(file2);

        // When
        boolean result = sut.deleteAllFiles(tempDir.toString());

        // Then
        assertThat(result).isTrue();
        assertThat(Files.exists(file1)).isFalse();
        assertThat(Files.exists(file2)).isFalse();
    }

    @Test
    @DisplayName("Should not delete subdirectories")
    void shouldNotDeleteSubdirectories() throws IOException {
        // Given
        Path file = tempDir.resolve("file.txt");
        Path subdir = tempDir.resolve("subdir");
        Files.createFile(file);
        Files.createDirectory(subdir);

        // When
        boolean result = sut.deleteAllFiles(tempDir.toString());

        // Then
        assertThat(result).isTrue();
        assertThat(Files.exists(file)).isFalse();
        assertThat(Files.exists(subdir)).isTrue();
    }

    @Test
    @DisplayName("Should return true when deleting from empty directory")
    void shouldReturnTrueWhenDeletingFromEmptyDirectory() {
        // When
        boolean result = sut.deleteAllFiles(tempDir.toString());

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("Should handle non-existent directory when deleting")
    void shouldHandleNonExistentDirectoryWhenDeleting() {
        // Given
        Path nonExistentDir = tempDir.resolve("nonexistent");

        // When
        boolean result = sut.deleteAllFiles(nonExistentDir.toString());

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("Should filter out directories when reading files")
    void shouldFilterOutDirectoriesWhenReadingFiles() throws IOException {
        // Given
        Files.createFile(tempDir.resolve("file.txt"));
        Files.createDirectory(tempDir.resolve("dir1"));
        Files.createDirectory(tempDir.resolve("dir2"));

        // When
        List<File> files = sut.readAllFiles(tempDir.toString());

        // Then
        assertThat(files).hasSize(1);
        assertThat(files.get(0).getName()).isEqualTo("file.txt");
    }

    @Test
    @DisplayName("Should handle multiple files with different timestamps")
    void shouldHandleMultipleFilesWithDifferentTimestamps() throws IOException, InterruptedException {
        // Given
        Path file1 = tempDir.resolve("file1.txt");
        Files.createFile(file1);
        Thread.sleep(10); // Ensure different timestamp
        Path file2 = tempDir.resolve("file2.txt");
        Files.createFile(file2);

        // When
        LocalDateTime timestamp = sut.getFirstFileTimestamp(tempDir);

        // Then
        assertThat(timestamp).isNotNull();
        assertThat(timestamp).isNotEqualTo(LocalDateTime.MIN);
    }
}
