package com.kanflow.ui;

import com.kanflow.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.function.Consumer;

public class FilterToolbar extends JPanel {

    public enum SortOption { NONE, DEADLINE_ASC, PRIORITY_HIGH_FIRST }

    private final JComboBox<String>     priorityFilter;
    private final JComboBox<String>     sortBox;
    private final JTextField            searchField;
    private final Consumer<FilterState> onFilterChanged;

    public record FilterState(
            String searchText,
            Task.Priority priorityFilter,  // null = show all
            SortOption sortOption
    ) {}

    public FilterToolbar(Consumer<FilterState> onFilterChanged) {
        this.onFilterChanged = onFilterChanged;

        setLayout(new FlowLayout(FlowLayout.LEFT, 12, 8));
        setBackground(new Color(240, 242, 245));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(210, 212, 215)),
                new EmptyBorder(0, 10, 0, 10)
        ));

        // Search
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        searchField = new JTextField(14);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        searchField.putClientProperty("JTextField.placeholderText", "Search tasks...");

        // Priority filter
        JLabel priorityLabel = new JLabel("Priority:");
        priorityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        priorityFilter = new JComboBox<>(new String[]{"All", "HIGH", "MEDIUM", "LOW"});
        priorityFilter.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Sort
        JLabel sortLabel = new JLabel("Sort by:");
        sortLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        sortBox = new JComboBox<>(new String[]{"None", "Deadline ↑", "Priority ↓"});
        sortBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        // Clear button
        JButton clearBtn = new JButton("Clear");
        clearBtn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        clearBtn.setFocusPainted(false);
        clearBtn.addActionListener(e -> clearFilters());

        // Wire listeners
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { fireChange(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { fireChange(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { fireChange(); }
        });
        priorityFilter.addActionListener(e -> fireChange());
        sortBox.addActionListener(e -> fireChange());

        add(searchLabel);
        add(searchField);
        add(Box.createHorizontalStrut(4));
        add(priorityLabel);
        add(priorityFilter);
        add(Box.createHorizontalStrut(4));
        add(sortLabel);
        add(sortBox);
        add(Box.createHorizontalStrut(4));
        add(clearBtn);
    }

    private void fireChange() {
        onFilterChanged.accept(buildState());
    }

    private void clearFilters() {
        searchField.setText("");
        priorityFilter.setSelectedIndex(0);
        sortBox.setSelectedIndex(0);
        fireChange();
    }

    private FilterState buildState() {
        String search = searchField.getText().trim().toLowerCase();

        Task.Priority priority = null;
        int pIdx = priorityFilter.getSelectedIndex();
        if (pIdx == 1) priority = Task.Priority.HIGH;
        else if (pIdx == 2) priority = Task.Priority.MEDIUM;
        else if (pIdx == 3) priority = Task.Priority.LOW;

        SortOption sort = switch (sortBox.getSelectedIndex()) {
            case 1  -> SortOption.DEADLINE_ASC;
            case 2  -> SortOption.PRIORITY_HIGH_FIRST;
            default -> SortOption.NONE;
        };

        return new FilterState(search, priority, sort);
    }
}