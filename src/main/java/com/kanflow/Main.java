package com.kanflow;

import com.kanflow.ui.MainFrame;
import com.kanflow.util.AppLogger;
import org.slf4j.Logger;

import javax.swing.*;

public class Main {

    private static final Logger LOG = AppLogger.get(Main.class);

    public static void main(String[] args) {

        // Catch unhandled exceptions on the EDT
        Thread.setDefaultUncaughtExceptionHandler((thread, ex) -> {
            LOG.error("Unhandled exception on thread {}: {}", thread.getName(), ex.getMessage(), ex);
            SwingUtilities.invokeLater(() ->
                    JOptionPane.showMessageDialog(null,
                            "An unexpected error occurred:\n" + ex.getMessage(),
                            "KanFlow Error",
                            JOptionPane.ERROR_MESSAGE)
            );
        });

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                LOG.warn("Could not set system look and feel: {}", e.getMessage());
            }

            LOG.info("KanFlow starting up...");
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}



//package com.kanflow;
//
//import com.kanflow.model.Task;
//import com.kanflow.util.JsonStorage;
//
//import java.time.LocalDate;
//import java.util.List;
//
//public class Main {
//    public static void main(String[] args) {
//        // Create a test task and save it
//        Task t = new Task("Build KanFlow UI", "Set up the Swing board", Task.Priority.HIGH, LocalDate.now().plusDays(3));
//        JsonStorage.saveTasks(List.of(t));
//
//        // Load it back and print
//        List<Task> loaded = JsonStorage.loadTasks();
//        loaded.forEach(System.out::println);
//    }
//}