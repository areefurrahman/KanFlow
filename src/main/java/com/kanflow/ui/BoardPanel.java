package com.kanflow.ui;

import com.kanflow.model.Task;
import com.kanflow.util.JsonStorage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class BoardPanel extends JPanel {

    private List<Task> tasks;

    private final ColumnPanel todoColumn;
    private final ColumnPanel inProgressColumn;
    private final ColumnPanel doneColumn;

    public BoardPanel() {
        setLayout(new BorderLayout());
        setBackground(new Color(225, 228, 232));

        tasks = JsonStorage.loadTasks();

        JPanel columnsArea = new JPanel(new GridLayout(1, 3, 8, 0));
        columnsArea.setBackground(new Color(225, 228, 232));
        columnsArea.setBorder(new EmptyBorder(16, 16, 16, 16));

        todoColumn       = new ColumnPanel(Task.Column.TODO);
        inProgressColumn = new ColumnPanel(Task.Column.IN_PROGRESS);
        doneColumn       = new ColumnPanel(Task.Column.DONE);

        // Wire click listeners
        todoColumn.setCardClickListener(task       -> onCardClicked(task));
        inProgressColumn.setCardClickListener(task -> onCardClicked(task));
        doneColumn.setCardClickListener(task       -> onCardClicked(task));

        columnsArea.add(todoColumn);
        columnsArea.add(inProgressColumn);
        columnsArea.add(doneColumn);

        add(columnsArea, BorderLayout.CENTER);
        refresh();
    }

    // Called from MainFrame's "+ Add Task" button
    public void showAddDialog(Frame parent) {
        TaskDialog dialog = new TaskDialog(parent, "Add New Task", null);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Task newTask = new Task(
                    dialog.getResultTitle(),
                    dialog.getResultDesc(),
                    dialog.getResultPriority(),
                    dialog.getResultDeadline()
            );
            tasks.add(newTask);
            persist();
        }
    }

    // Called when a card is clicked
    private void onCardClicked(Task task) {
        String[] options = {"Edit", "Move", "Delete", "Cancel"};
        int choice = JOptionPane.showOptionDialog(
                this,
                "What do you want to do with:\n\"" + task.getTitle() + "\"?",
                "Task Options",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null, options, options[0]
        );

        switch (choice) {
            case 0 -> showEditDialog(task);
            case 1 -> showMoveDialog(task);
            case 2 -> confirmDelete(task);
            default -> { /* cancelled */ }
        }
    }

    private void showEditDialog(Task task) {
        TaskDialog dialog = new TaskDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Edit Task", task
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            task.setTitle(dialog.getResultTitle());
            task.setDescription(dialog.getResultDesc());
            task.setPriority(dialog.getResultPriority());
            task.setDeadline(dialog.getResultDeadline());
            persist();
        }
    }

    private void showMoveDialog(Task task) {
        Task.Column current = task.getColumn();

        // Build only valid move targets (not current column)
        java.util.List<String> optionLabels = new java.util.ArrayList<>();
        java.util.List<Task.Column> optionValues = new java.util.ArrayList<>();

        for (Task.Column col : Task.Column.values()) {
            if (col != current) {
                optionLabels.add(columnLabel(col));
                optionValues.add(col);
            }
        }

        String selected = (String) JOptionPane.showInputDialog(
                this,
                "Move \"" + task.getTitle() + "\" to:",
                "Move Task",
                JOptionPane.PLAIN_MESSAGE,
                null,
                optionLabels.toArray(),
                optionLabels.get(0)
        );

        if (selected != null) {
            int idx = optionLabels.indexOf(selected);
            task.setColumn(optionValues.get(idx));
            persist();
        }
    }

    private void confirmDelete(Task task) {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Delete \"" + task.getTitle() + "\"? This cannot be undone.",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (confirm == JOptionPane.YES_OPTION) {
            tasks.removeIf(t -> t.getId().equals(task.getId()));
            persist();
        }
    }

    private void persist() {
        JsonStorage.saveTasks(tasks);
        refresh();
    }

    public void refresh() {
        tasks = JsonStorage.loadTasks();
        todoColumn.loadTasks(tasks);
        inProgressColumn.loadTasks(tasks);
        doneColumn.loadTasks(tasks);
    }

    private String columnLabel(Task.Column col) {
        return switch (col) {
            case TODO        -> "To-Do";
            case IN_PROGRESS -> "In Progress";
            case DONE        -> "Done";
        };
    }

    // Add this method to BoardPanel.java
    public void applyFilter(FilterToolbar.FilterState state) {
        List<Task> all = JsonStorage.loadTasks();

        // 1. Search filter
        List<Task> filtered = all.stream()
                .filter(t -> state.searchText().isEmpty()
                        || t.getTitle().toLowerCase().contains(state.searchText())
                        || t.getDescription().toLowerCase().contains(state.searchText()))
                .filter(t -> state.priorityFilter() == null
                        || t.getPriority() == state.priorityFilter())
                .collect(java.util.stream.Collectors.toList());

        // 2. Sort
        switch (state.sortOption()) {
            case DEADLINE_ASC -> filtered.sort((a, b) -> {
                if (a.getDeadline() == null && b.getDeadline() == null) return 0;
                if (a.getDeadline() == null) return 1;
                if (b.getDeadline() == null) return -1;
                return a.getDeadline().compareTo(b.getDeadline());
            });
            case PRIORITY_HIGH_FIRST -> filtered.sort(
                    java.util.Comparator.comparingInt(t -> t.getPriority().ordinal())
            );
            default -> { /* no sort */ }
        }

        // 3. Render filtered list
        todoColumn.loadTasks(filtered);
        inProgressColumn.loadTasks(filtered);
        doneColumn.loadTasks(filtered);
    }


    public List<Task> getTasks() { return tasks; }
}