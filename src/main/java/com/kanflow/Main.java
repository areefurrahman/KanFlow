package com.kanflow;

import com.kanflow.model.Task;
import com.kanflow.util.JsonStorage;

import java.time.LocalDate;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Create a test task and save it
        Task t = new Task("Build KanFlow UI", "Set up the Swing board", Task.Priority.HIGH, LocalDate.now().plusDays(3));
        JsonStorage.saveTasks(List.of(t));

        // Load it back and print
        List<Task> loaded = JsonStorage.loadTasks();
        loaded.forEach(System.out::println);
    }
}