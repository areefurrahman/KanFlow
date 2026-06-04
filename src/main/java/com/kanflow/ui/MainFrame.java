package com.kanflow.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MainFrame extends JFrame {

    private final BoardPanel boardPanel;
    private final FilterToolbar filterToolbar;

    public MainFrame() {
        setTitle("KanFlow");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(860, 600));
        setLocationRelativeTo(null); // center on screen

        // Top navbar
        JPanel navbar = new JPanel(new BorderLayout());
        navbar.setBackground(new Color(26, 32, 44));
        navbar.setBorder(new EmptyBorder(12, 20, 12, 20));
        navbar.setPreferredSize(new Dimension(0, 55));

        JLabel logo = new JLabel("KanFlow");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        logo.setForeground(Color.WHITE);

        JButton addBtn = new JButton("+ Add Task");
        addBtn.setBackground(new Color(99, 102, 241));
        addBtn.setForeground(Color.WHITE);
        addBtn.setFocusPainted(false);
        addBtn.setBorderPainted(false);
        addBtn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        addBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        navbar.add(logo,   BorderLayout.WEST);
        navbar.add(addBtn, BorderLayout.EAST);

        // Board
        boardPanel = new BoardPanel();

        filterToolbar = new FilterToolbar(state -> boardPanel.applyFilter(state));


        addBtn.addActionListener(e -> boardPanel.showAddDialog(this));

        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(navbar,         BorderLayout.NORTH);
        topSection.add(filterToolbar,  BorderLayout.SOUTH);

        add(topSection, BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);


        pack();
    }

    public BoardPanel getBoardPanel() {
        return boardPanel;
    }
}