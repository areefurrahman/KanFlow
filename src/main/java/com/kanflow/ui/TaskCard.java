package com.kanflow.ui;

import com.kanflow.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TaskCard extends JPanel {

    private static final Color HIGH_COLOR   = new Color(220, 53, 69);
    private static final Color MEDIUM_COLOR = new Color(255, 165, 0);
    private static final Color LOW_COLOR    = new Color(40, 167, 69);
    private static final Color OVERDUE_BG   = new Color(255, 235, 235);
    private static final Color NORMAL_BG    = Color.WHITE;

    private final Task task;

    public TaskCard(Task task) {
        this.task = task;
        buildUI();
    }

    private void buildUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
        setBackground(task.isOverdue() ? OVERDUE_BG : NORMAL_BG);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        // Priority dot + title row
        JPanel topRow = new JPanel(new BorderLayout(6, 0));
        topRow.setOpaque(false);

        JLabel priorityDot = new JLabel("●");
        priorityDot.setForeground(getPriorityColor());
        priorityDot.setFont(new Font("Arial", Font.PLAIN, 12));

        JLabel titleLabel = new JLabel(task.getTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));

        topRow.add(priorityDot, BorderLayout.WEST);
        topRow.add(titleLabel,  BorderLayout.CENTER);

        // Description (truncated)
        JLabel descLabel = new JLabel(truncate(task.getDescription(), 45));
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        descLabel.setForeground(Color.GRAY);

        // Deadline row
        String deadlineText = task.getDeadline() != null
                ? task.getDeadline().toString()
                : "No deadline";
        JLabel deadlineLabel = new JLabel(deadlineText);
        deadlineLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        deadlineLabel.setForeground(task.isOverdue() ? Color.RED : new Color(100, 100, 100));

        JPanel bottomRow = new JPanel(new BorderLayout());
        bottomRow.setOpaque(false);
        bottomRow.add(deadlineLabel, BorderLayout.WEST);

        add(topRow,    BorderLayout.NORTH);
        add(descLabel, BorderLayout.CENTER);
        add(bottomRow, BorderLayout.SOUTH);
    }

    private Color getPriorityColor() {
        return switch (task.getPriority()) {
            case HIGH   -> HIGH_COLOR;
            case MEDIUM -> MEDIUM_COLOR;
            case LOW    -> LOW_COLOR;
        };
    }

    private String truncate(String text, int max) {
        if (text == null || text.length() <= max) return text == null ? "" : text;
        return text.substring(0, max) + "...";
    }

    public Task getTask() {
        return task;
    }
}