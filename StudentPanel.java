package com.university.system.view;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import com.university.system.controller.StudentController;
import com.university.system.model.Student;
import com.university.system.model.Course;
import java.util.List;

/**
 * Panel for managing students - add, edit, delete, search, enroll
 */
public class StudentPanel extends JPanel {
    
    private StudentController controller;
    
    // Components
    private JTextField searchField;
    private JTable studentTable;
    private DefaultTableModel tableModel;
    
    private JTextField regNumberField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField programmeField;
    
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton enrollButton;
    private JButton viewScoresButton;
    
    public StudentPanel() {
        controller = new StudentController();
        initializeUI();
        loadStudents();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top panel - Search
        add(createSearchPanel(), BorderLayout.NORTH);
        
        // Center panel - Table
        add(createTablePanel(), BorderLayout.CENTER);
        
        // Right panel - Form
        add(createFormPanel(), BorderLayout.EAST);
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBorder(BorderFactory.createTitledBorder("Search Students"));
        
        JLabel searchLabel = new JLabel(" Search:");
        searchField = new JTextField(30);
        
        // Real-time search as user types
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { performSearch(); }
        });
        
        JButton refreshButton = new JButton(" Refresh");
        refreshButton.addActionListener(e -> loadStudents());
        
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(refreshButton);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Students List"));
        
        // Create table
        String[] columnNames = {"Reg Number", "Name", "Email", "Phone", "Programme", "Courses"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        studentTable = new JTable(tableModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setRowHeight(25);
        studentTable.getTableHeader().setReorderingAllowed(false);
        
        // Add selection listener
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedStudent();
            }
        });
        
        // Double-click to edit
        studentTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    loadSelectedStudent();
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(studentTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Student Details"));
        panel.setPreferredSize(new Dimension(350, 0));
        
        // Form fields
        regNumberField = new JTextField(20);
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);
        programmeField = new JTextField(20);
        
        panel.add(createFieldPanel("Reg Number:", regNumberField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Name:", nameField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Email:", emailField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Phone:", phoneField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Programme:", programmeField));
        panel.add(Box.createVerticalStrut(20));
        
        // Buttons
        panel.add(createButtonPanel());
        
        return panel;
    }
    
    private JPanel createFieldPanel(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(100, 25));
        panel.add(lbl, BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 5, 5));
        
        addButton = new JButton("➕ Add Student");
        updateButton = new JButton("✏️ Update Student");
        deleteButton = new JButton("🗑️ Delete Student");
        clearButton = new JButton("🔄 Clear Form");
        enrollButton = new JButton("📚 Enroll in Courses");
        viewScoresButton = new JButton("📊 View Scores");
        
        // Action listeners
        addButton.addActionListener(e -> addStudent());
        updateButton.addActionListener(e -> updateStudent());
        deleteButton.addActionListener(e -> deleteStudent());
        clearButton.addActionListener(e -> clearForm());
        enrollButton.addActionListener(e -> enrollStudent());
        viewScoresButton.addActionListener(e -> viewScores());
        
        // Initially disable update and delete
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        enrollButton.setEnabled(false);
        viewScoresButton.setEnabled(false);
        
        panel.add(addButton);
        panel.add(updateButton);
        panel.add(deleteButton);
        panel.add(clearButton);
        panel.add(enrollButton);
        panel.add(viewScoresButton);
        
        return panel;
    }
    
    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            loadStudents();
            return;
        }
        
        List<Student> students = controller.searchStudentsByName(keyword);
        updateTable(students);
    }
    
    private void loadStudents() {
        List<Student> students = controller.getAllStudents();
        updateTable(students);
    }
    
    private void updateTable(List<Student> students) {
        tableModel.setRowCount(0);
        for (Student student : students) {
            Object[] row = {
                student.getRegistrationNumber(),
                student.getName(),
                student.getEmail(),
                student.getPhone(),
                student.getProgramme(),
                student.getCourses().size() + "/5"
            };
            tableModel.addRow(row);
        }
    }
    
    private void loadSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow >= 0) {
            String regNumber = (String) tableModel.getValueAt(selectedRow, 0);
            Student student = controller.getStudentById(regNumber);
            
            if (student != null) {
                regNumberField.setText(student.getRegistrationNumber());
                nameField.setText(student.getName());
                emailField.setText(student.getEmail());
                phoneField.setText(student.getPhone());
                programmeField.setText(student.getProgramme());
                
                regNumberField.setEditable(false);
                updateButton.setEnabled(true);
                deleteButton.setEnabled(true);
                addButton.setEnabled(false);
                enrollButton.setEnabled(true);
                viewScoresButton.setEnabled(true);
            }
        }
    }
    
    private void addStudent() {
        if (!validateForm()) return;
        
        Student student = new Student(
            regNumberField.getText().trim(),
            nameField.getText().trim(),
            emailField.getText().trim(),
            phoneField.getText().trim(),
            programmeField.getText().trim()
        );
        
        if (controller.addStudent(student)) {
            JOptionPane.showMessageDialog(this, "Student added successfully!");
            clearForm();
            loadStudents();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to add student!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateStudent() {
        if (!validateForm()) return;
        
        Student student = new Student(
            regNumberField.getText().trim(),
            nameField.getText().trim(),
            emailField.getText().trim(),
            phoneField.getText().trim(),
            programmeField.getText().trim()
        );
        
        if (controller.updateStudent(student)) {
            JOptionPane.showMessageDialog(this, "Student updated successfully!");
            clearForm();
            loadStudents();
        } else {
            JOptionPane.showMessageDialog(this, "Failed to update student!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteStudent() {
        int choice = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this student?",
            "Confirm Delete",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            String regNumber = regNumberField.getText().trim();
            if (controller.deleteStudent(regNumber)) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully!");
                clearForm();
                loadStudents();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void enrollStudent() {
        String regNumber = regNumberField.getText().trim();
        Student student = controller.getStudentById(regNumber);
        
        if (student != null) {
            if (student.getCourses().size() >= 5) {
                JOptionPane.showMessageDialog(
                    this,
                    "Student already enrolled in maximum 5 courses!",
                    "Cannot Enroll",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }
            
            EnrollmentDialog dialog = new EnrollmentDialog((Frame) SwingUtilities.getWindowAncestor(this), student);
            dialog.setVisible(true);
            loadStudents();
        }
    }
    
    private void viewScores() {
        String regNumber = regNumberField.getText().trim();
        ResultSlipDialog dialog = new ResultSlipDialog((Frame) SwingUtilities.getWindowAncestor(this), regNumber);
        dialog.setVisible(true);
    }
    
    private void clearForm() {
        regNumberField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        programmeField.setText("");
        
        regNumberField.setEditable(true);
        addButton.setEnabled(true);
        updateButton.setEnabled(false);
        deleteButton.setEnabled(false);
        enrollButton.setEnabled(false);
        viewScoresButton.setEnabled(false);
        
        studentTable.clearSelection();
    }
    
    private boolean validateForm() {
        if (regNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter registration number!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            regNumberField.requestFocus();
            return false;
        }
        if (nameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter name!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            nameField.requestFocus();
            return false;
        }
        if (programmeField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter programme!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            programmeField.requestFocus();
            return false;
        }
        return true;
    }
    
    public void refreshData() {
        loadStudents();
    }
}
