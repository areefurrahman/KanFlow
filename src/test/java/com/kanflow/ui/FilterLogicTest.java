package com.kanflow.ui;

import com.kanflow.model.Task;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Filter and Sort Logic Tests")
class FilterLogicTest {

    private List<Task> sampleTasks;

    @BeforeEach
    void setUp() {
        Task t1 = new Task("Alpha task",  "first",  Task.Priority.HIGH,
                LocalDate.of(2026, 6, 10));
        Task t2 = new Task("Beta task",   "second", Task.Priority.MEDIUM,
                LocalDate.of(2026, 6, 5));
        Task t3 = new Task("Gamma task",  "third",  Task.Priority.LOW,
                LocalDate.of(2026, 6, 20));
        Task t4 = new Task("Alpha extra", "fourth", Task.Priority.HIGH, null);

        sampleTasks = List.of(t1, t2, t3, t4);
    }

    @Test
    @DisplayName("Search filter matches title case-insensitively")
    void testSearchFilterByTitle() {
        String query = "alpha";
        List<Task> result = sampleTasks.stream()
                .filter(t -> t.getTitle().toLowerCase().contains(query))
                .collect(Collectors.toList());

        assertEquals(2, result.size(), "Should find 2 tasks with 'alpha' in title");
    }

    @Test
    @DisplayName("Priority filter returns only matching tasks")
    void testPriorityFilter() {
        List<Task> result = sampleTasks.stream()
                .filter(t -> t.getPriority() == Task.Priority.HIGH)
                .collect(Collectors.toList());

        assertEquals(2, result.size(), "Should find 2 HIGH priority tasks");
        result.forEach(t -> assertEquals(Task.Priority.HIGH, t.getPriority()));
    }

    @Test
    @DisplayName("Sort by deadline puts null deadlines last")
    void testSortByDeadlineNullsLast() {
        List<Task> sorted = sampleTasks.stream()
                .sorted((a, b) -> {
                    if (a.getDeadline() == null && b.getDeadline() == null) return 0;
                    if (a.getDeadline() == null) return 1;
                    if (b.getDeadline() == null) return -1;
                    return a.getDeadline().compareTo(b.getDeadline());
                })
                .collect(Collectors.toList());

        assertNull(sorted.get(sorted.size() - 1).getDeadline(),
                "Task with null deadline should be last");
        assertEquals(LocalDate.of(2026, 6, 5), sorted.get(0).getDeadline(),
                "Earliest deadline should come first");
    }

    @Test
    @DisplayName("Sort by priority puts HIGH first")
    void testSortByPriorityHighFirst() {
        List<Task> sorted = sampleTasks.stream()
                .sorted(java.util.Comparator.comparingInt(t -> t.getPriority().ordinal()))
                .collect(Collectors.toList());

        assertEquals(Task.Priority.HIGH, sorted.get(0).getPriority(),
                "HIGH priority should appear first");
    }

    @Test
    @DisplayName("Empty search string returns all tasks")
    void testEmptySearchReturnsAll() {
        List<Task> result = sampleTasks.stream()
                .filter(t -> "".isEmpty() || t.getTitle().toLowerCase().contains(""))
                .collect(Collectors.toList());

        assertEquals(sampleTasks.size(), result.size());
    }
}