package com.kanflow.model;

import org.junit.jupiter.api.*;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Task Model Tests")
class TaskTest {

    // ── Construction ──────────────────────────────────────────────

    @Test
    @DisplayName("Valid task is created with correct defaults")
    void testValidTaskCreation() {
        Task task = new Task("Fix bug", "Critical issue", Task.Priority.HIGH,
                LocalDate.now().plusDays(2));

        assertNotNull(task.getId(),               "ID should be auto-generated");
        assertEquals("Fix bug", task.getTitle());
        assertEquals("Critical issue", task.getDescription());
        assertEquals(Task.Priority.HIGH, task.getPriority());
        assertEquals(Task.Column.TODO, task.getColumn(), "New task should default to TODO");
    }

    @Test
    @DisplayName("Task with null title throws IllegalArgumentException")
    void testNullTitleThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Task(null, "desc", Task.Priority.LOW, null),
                "Null title should throw");
    }

    @Test
    @DisplayName("Task with blank title throws IllegalArgumentException")
    void testBlankTitleThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> new Task("   ", "desc", Task.Priority.LOW, null),
                "Blank title should throw");
    }

    @Test
    @DisplayName("Task with null description defaults to empty string")
    void testNullDescriptionDefaultsToEmpty() {
        Task task = new Task("Title", null, Task.Priority.LOW, null);
        assertEquals("", task.getDescription(), "Null description should become empty string");
    }

    @Test
    @DisplayName("Each task gets a unique ID")
    void testUniqueIds() {
        Task t1 = new Task("Task 1", "", Task.Priority.LOW, null);
        Task t2 = new Task("Task 2", "", Task.Priority.LOW, null);
        assertNotEquals(t1.getId(), t2.getId(), "IDs must be unique");
    }

    // ── Setters ───────────────────────────────────────────────────

    @Test
    @DisplayName("setTitle with blank value throws IllegalArgumentException")
    void testSetBlankTitleThrows() {
        Task task = new Task("Valid", "", Task.Priority.MEDIUM, null);
        assertThrows(IllegalArgumentException.class,
                () -> task.setTitle(""),
                "Empty title via setter should throw");
    }

    @Test
    @DisplayName("setColumn correctly updates column")
    void testSetColumn() {
        Task task = new Task("Task", "", Task.Priority.MEDIUM, null);
        task.setColumn(Task.Column.IN_PROGRESS);
        assertEquals(Task.Column.IN_PROGRESS, task.getColumn());
    }

    // ── isOverdue ─────────────────────────────────────────────────

    @Test
    @DisplayName("Task with past deadline is overdue")
    void testOverdueTask() {
        Task task = new Task("Overdue", "", Task.Priority.HIGH,
                LocalDate.now().minusDays(1));
        assertTrue(task.isOverdue(), "Past deadline task should be overdue");
    }

    @Test
    @DisplayName("Task with future deadline is not overdue")
    void testNotOverdue() {
        Task task = new Task("Future", "", Task.Priority.LOW,
                LocalDate.now().plusDays(5));
        assertFalse(task.isOverdue());
    }

    @Test
    @DisplayName("DONE task is never overdue even with past deadline")
    void testDoneTaskNotOverdue() {
        Task task = new Task("Done task", "", Task.Priority.HIGH,
                LocalDate.now().minusDays(3));
        task.setColumn(Task.Column.DONE);
        assertFalse(task.isOverdue(), "DONE tasks should never be marked overdue");
    }

    @Test
    @DisplayName("Task with no deadline is not overdue")
    void testNoDeadlineNotOverdue() {
        Task task = new Task("No deadline", "", Task.Priority.MEDIUM, null);
        assertFalse(task.isOverdue());
    }

    // ── toString ──────────────────────────────────────────────────

    @Test
    @DisplayName("toString contains priority and title")
    void testToString() {
        Task task = new Task("Deploy app", "", Task.Priority.HIGH, null);
        String result = task.toString();
        assertTrue(result.contains("HIGH"),       "toString should contain priority");
        assertTrue(result.contains("Deploy app"), "toString should contain title");
    }
}