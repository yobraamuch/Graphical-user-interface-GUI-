package com.university.system.view;

import javax.swing.*;
import java.awt.*;
import com.university.system.database.DatabaseConnection;

public class MainWindow extends JFrame {
    private JTabbedPane tabbedPane;
    private StudentPanel studentPanel;
    private LecturerPanel lecturerPanel;
    private LibraryPanel libraryPanel;

    public MainWindow() {
        initializeUI();
        setupMenuBar();
        initializeDatabase();
    }

    private void initializeUI() {
        setTitle("Student Library Management System");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        tabbedPane = new JTabbedPane();
        studentPanel = new StudentPanel();
        lecturerPanel = new LecturerPanel();
        libraryPanel = new LibraryPanel();
        tabbedPane.addTab("Students", studentPanel);
        tabbedPane.addTab("Lecturers", lecturerPanel);
        tabbedPane.addTab("Library", libraryPanel);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);
    }

    private void initializeDatabase() {
        DatabaseConnection.initializeDatabase();
    }

    private void showStudentResultSlip() {}
    private void showLecturerReport() {}
    private void showLibraryStats() {}
    private void showAbout() {}
    private void refreshAll() {}

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {}
        SwingUtilities.invokeLater(() -> {
            MainWindow window = new MainWindow();
            window.setVisible(true);
        });
    }
}
