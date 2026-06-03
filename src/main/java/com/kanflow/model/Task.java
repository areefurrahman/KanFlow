package com.kanflow.model;

import java.time.LocalDate;
import java.util.UUID;

public class Task {

    public enum Priority { HIGH, MEDIUM, LOW }
    public enum Column   { TODO, IN_PROGRESS, DONE }

    private final String id;
    private String title;
    private String description;
    private Priority priority;
    private LocalDate deadline;
    private Column column;

    // Constructor for creating a NEW task
    public Task(String title, String description, Priority priority, LocalDate deadline) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title cannot be empty.");
        }
        this.id          = UUID.randomUUID().toString();
        this.title       = title;
        this.description = description == null ? "" : description;
        this.priority    = priority;
        this.deadline    = deadline;
        this.column      = Column.TODO; // always starts in To-Do
    }

    // Getters
    public String    getId()          { return id; }
    public String    getTitle()       { return title; }
    public String    getDescription() { return description; }
    public Priority  getPriority()    { return priority; }
    public LocalDate getDeadline()    { return deadline; }
    public Column    getColumn()      { return column; }

    // Setters (for edit functionality)
    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Task title cannot be empty.");
        }
        this.title = title;
    }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(Priority priority)      { this.priority = priority; }
    public void setDeadline(LocalDate deadline)     { this.deadline = deadline; }
    public void setColumn(Column column)            { this.column = column; }

    public boolean isOverdue() {
        return deadline != null && LocalDate.now().isAfter(deadline) && column != Column.DONE;
    }

    @Override
    public String toString() {
        return "[" + priority + "] " + title + (deadline != null ? " (due: " + deadline + ")" : "");
    }
}