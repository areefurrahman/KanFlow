package com.kanflow.ui;

import com.kanflow.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class TaskDialog extends JDialog {

    private boolean confirmed = false;

    private final JTextField titleField       = new JTextField();
    private final JTextArea  descArea         = new JTextArea(3, 20);
    private final JComboBox<Task.Priority> priorityBox =
            new JComboBox<>(Task.Priority.values());
    private final JTextField deadlineField    = new JTextField("YYYY-MM-DD");

    // Result holder
    private String       resultTitle;
    private String       resultDesc;
    private Task.Priority resultPriority;
    private LocalDate    resultDeadline;

    public TaskDialog(Frame parent, String dialogTitle, Task existingTask) {
        super(parent, dialogTitle, true); // modal
        buildUI(existingTask);
        pack();
        setMinimumSize(new Dimension(380, 320));
        setLocationRelativeTo(parent);
    }


    private void buildUI(Task existing) {
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets  = new Insets(6, 0, 6, 0);
        gbc.fill    = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        if (existing != null) {
            titleField.setText(existing.getTitle());
            descArea.setText(existing.getDescription());
            priorityBox.setSelectedItem(existing.getPriority());
            if (existing.getDeadline() != null) {
                deadlineField.setText(existing.getDeadline().toString());
            }
        }

        // Real-time title validation
        JLabel titleError = new JLabel(" ");
        titleError.setForeground(Color.RED);
        titleError.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        titleField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { validateTitle(titleError); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { validateTitle(titleError); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateTitle(titleError); }
        });

        // Real-time deadline validation
        JLabel deadlineError = new JLabel(" ");
        deadlineError.setForeground(Color.RED);
        deadlineError.setFont(new Font("Segoe UI", Font.PLAIN, 11));

        deadlineField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e)  { validateDeadline(deadlineError); }
            public void removeUpdate(javax.swing.event.DocumentEvent e)  { validateDeadline(deadlineError); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { validateDeadline(deadlineError); }
        });

        addRow(content, gbc, 0, "Title *",              titleField);
        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 4, 0);
        content.add(titleError, gbc);

        addRow(content, gbc, 2, "Description",           new JScrollPane(descArea));
        addRow(content, gbc, 3, "Priority",               priorityBox);
        addRow(content, gbc, 4, "Deadline (YYYY-MM-DD)",  deadlineField);
        gbc.gridy = 5; gbc.insets = new Insets(0, 0, 4, 0);
        content.add(deadlineError, gbc);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        JButton cancelBtn = new JButton("Cancel");
        JButton saveBtn   = new JButton(existing == null ? "Add Task" : "Save Changes");

        saveBtn.setBackground(new Color(99, 102, 241));
        saveBtn.setForeground(Color.WHITE);
        saveBtn.setFocusPainted(false);
        saveBtn.setBorderPainted(false);
        saveBtn.setFont(new Font("Segoe UI", Font.BOLD, 12));

        cancelBtn.addActionListener(e -> dispose());
        saveBtn.addActionListener(e -> onSave());

        btnRow.add(cancelBtn);
        btnRow.add(saveBtn);

        gbc.gridy  = 6;
        gbc.insets = new Insets(16, 0, 0, 0);
        content.add(btnRow, gbc);

        add(content);
        getRootPane().setDefaultButton(saveBtn);
    }

    // Add these two helpers inside TaskDialog class:
    private void validateTitle(JLabel errorLabel) {
        String text = titleField.getText().trim();
        if (text.isEmpty()) {
            titleField.setBorder(BorderFactory.createLineBorder(Color.RED));
            errorLabel.setText("Title is required.");
        } else if (text.length() > 80) {
            titleField.setBorder(BorderFactory.createLineBorder(Color.RED));
            errorLabel.setText("Title too long (max 80 chars). Current: " + text.length());
        } else {
            titleField.setBorder(UIManager.getLookAndFeel()
                    .getDefaults().getBorder("TextField.border"));
            errorLabel.setText(" ");
        }
    }

    private void validateDeadline(JLabel errorLabel) {
        String text = deadlineField.getText().trim();
        if (text.isEmpty() || text.equals("YYYY-MM-DD")) {
            deadlineField.setBorder(UIManager.getLookAndFeel()
                    .getDefaults().getBorder("TextField.border"));
            errorLabel.setText(" ");
            return;
        }
        try {
            LocalDate.parse(text);
            deadlineField.setBorder(BorderFactory.createLineBorder(new Color(40, 167, 69)));
            errorLabel.setText(" ");
        } catch (DateTimeParseException ex) {
            deadlineField.setBorder(BorderFactory.createLineBorder(Color.RED));
            errorLabel.setText("Use format YYYY-MM-DD (e.g. 2026-06-10)");
        }
    }

    private void addRow(JPanel panel, GridBagConstraints gbc, int row,
                        String labelText, JComponent field) {
        gbc.gridy  = row;
        gbc.insets = new Insets(6, 0, 0, 0);

        JPanel wrapper = new JPanel(new BorderLayout(0, 4));
        wrapper.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(new Color(80, 80, 80));

        wrapper.add(label, BorderLayout.NORTH);
        wrapper.add(field, BorderLayout.CENTER);
        panel.add(wrapper, gbc);
    }

    private void onSave() {
        // Validate title
        String title = titleField.getText().trim();
        if (title.isEmpty()) {
            showError("Title is required.");
            titleField.requestFocus();
            return;
        }

        // Validate deadline
        LocalDate deadline = null;
        String deadlineText = deadlineField.getText().trim();
        if (!deadlineText.isEmpty() && !deadlineText.equals("YYYY-MM-DD")) {
            try {
                deadline = LocalDate.parse(deadlineText);
            } catch (DateTimeParseException ex) {
                showError("Invalid date format. Use YYYY-MM-DD (e.g. 2026-06-10).");
                deadlineField.requestFocus();
                return;
            }
        }

        resultTitle    = title;
        resultDesc     = descArea.getText().trim();
        resultPriority = (Task.Priority) priorityBox.getSelectedItem();
        resultDeadline = deadline;
        confirmed      = true;
        dispose();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error",
                JOptionPane.WARNING_MESSAGE);
    }

    // --- Public API ---

    public boolean isConfirmed()          { return confirmed; }
    public String       getResultTitle()    { return resultTitle; }
    public String       getResultDesc()     { return resultDesc; }
    public Task.Priority getResultPriority(){ return resultPriority; }
    public LocalDate    getResultDeadline() { return resultDeadline; }
}