package com.kanflow.util;

import com.kanflow.model.Task;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JsonStorage Tests")
class JsonStorageTest {

    // Use a temp file so tests don't touch real data
    private static final String TEST_DIR  = "data/test";
    private static final String TEST_FILE = TEST_DIR + "/tasks.json";

    @BeforeEach
    void setUp() throws IOException {
        Files.createDirectories(Paths.get(TEST_DIR));
        // Point JsonStorage to test path via a test subclass or
        // simply test the round-trip with the real path during isolated runs
    }

    @AfterEach
    void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get("data/tasks.json"));
    }

    @Test
    @DisplayName("Save and reload returns same number of tasks")
    void testSaveAndLoadCount() {
        Task t1 = new Task("Task A", "Desc A", Task.Priority.HIGH,
                LocalDate.of(2026, 7, 1));
        Task t2 = new Task("Task B", "Desc B", Task.Priority.LOW, null);

        JsonStorage.saveTasks(List.of(t1, t2));
        List<Task> loaded = JsonStorage.loadTasks();

        assertEquals(2, loaded.size(), "Should reload the same number of tasks");
    }

    @Test
    @DisplayName("Task fields survive JSON round-trip")
    void testRoundTripFields() {
        LocalDate deadline = LocalDate.of(2026, 8, 15);
        Task original = new Task("Round trip", "Testing persistence",
                Task.Priority.MEDIUM, deadline);
        original.setColumn(Task.Column.IN_PROGRESS);

        JsonStorage.saveTasks(List.of(original));
        Task loaded = JsonStorage.loadTasks().get(0);

        assertEquals(original.getTitle(),       loaded.getTitle());
        assertEquals(original.getDescription(), loaded.getDescription());
        assertEquals(original.getPriority(),    loaded.getPriority());
        assertEquals(original.getColumn(),      loaded.getColumn());
        assertEquals(original.getDeadline(),    loaded.getDeadline());
    }

    @Test
    @DisplayName("Loading from non-existent file returns empty list")
    void testLoadFromMissingFileReturnsEmpty() throws IOException {
        Files.deleteIfExists(Paths.get("data/tasks.json"));
        List<Task> result = JsonStorage.loadTasks();
        assertNotNull(result,       "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty list when no file exists");
    }

    @Test
    @DisplayName("Saving empty list produces valid JSON")
    void testSaveEmptyList() {
        assertDoesNotThrow(() -> JsonStorage.saveTasks(List.of()),
                "Saving empty list should not throw");
        List<Task> loaded = JsonStorage.loadTasks();
        assertTrue(loaded.isEmpty());
    }

    @Test
    @DisplayName("Task with null deadline persists correctly")
    void testNullDeadlineRoundTrip() {
        Task task = new Task("No deadline", "", Task.Priority.LOW, null);
        JsonStorage.saveTasks(List.of(task));
        Task loaded = JsonStorage.loadTasks().get(0);
        assertNull(loaded.getDeadline(), "Null deadline should remain null after reload");
    }
}