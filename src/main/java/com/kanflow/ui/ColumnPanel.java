package com.kanflow.ui;

import com.kanflow.model.Task;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class ColumnPanel extends JPanel {

    private static final Color HEADER_BG = new Color(245, 246, 250);
    private static final Color COLUMN_BG = new Color(235, 237, 240);

    public interface CardClickListener {
        void onCardClicked(Task task);
    }

    private final Task.Column column;
    private final JPanel cardsPanel;
    private final JLabel countLabel;
    private CardClickListener cardClickListener;

    public ColumnPanel(Task.Column column) {
        this.column = column;
        setLayout(new BorderLayout(0, 0));
        setBackground(COLUMN_BG);
        setBorder(new EmptyBorder(0, 6, 6, 6));
        setPreferredSize(new Dimension(260, 0));

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(HEADER_BG);
        header.setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel titleLabel = new JLabel(getColumnTitle());
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        countLabel = new JLabel("0");
        countLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        countLabel.setForeground(Color.GRAY);

        header.add(titleLabel, BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);

        cardsPanel = new JPanel();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBackground(COLUMN_BG);
        cardsPanel.setBorder(new EmptyBorder(6, 0, 6, 0));

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.setBackground(COLUMN_BG);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        add(header,     BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setCardClickListener(CardClickListener listener) {
        this.cardClickListener = listener;
    }

    public void loadTasks(List<Task> tasks) {
        cardsPanel.removeAll();
        int count = 0;
        for (Task task : tasks) {
            if (task.getColumn() == column) {
                TaskCard card = new TaskCard(task);
                card.setAlignmentX(Component.LEFT_ALIGNMENT);

                // Click to open context menu
                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        if (cardClickListener != null) {
                            cardClickListener.onCardClicked(card.getTask());
                        }
                    }
                    @Override
                    public void mouseEntered(java.awt.event.MouseEvent e) {
                        card.setBackground(new Color(245, 245, 255));
                        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                    @Override
                    public void mouseExited(java.awt.event.MouseEvent e) {
                        card.setBackground(task.isOverdue()
                                ? new Color(255, 235, 235) : Color.WHITE);
                    }
                });

                cardsPanel.add(card);
                cardsPanel.add(Box.createVerticalStrut(6));
                count++;
            }
        }
        countLabel.setText(String.valueOf(count));
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private String getColumnTitle() {
        return switch (column) {
            case TODO        -> "To-Do";
            case IN_PROGRESS -> "In Progress";
            case DONE        -> "Done";
        };
    }

    public Task.Column getColumn() { return column; }
    public JPanel getCardsPanel()  { return cardsPanel; }
}