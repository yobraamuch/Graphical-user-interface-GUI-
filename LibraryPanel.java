package com.university.system.view;

import javax.swing.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import com.university.system.controller.LibraryController;
import com.university.system.model.Book;
import java.util.List;

/**
 * Library Panel with REAL-TIME SEARCH as user types
 * Key feature: Search results update while typing the book title
 */
public class LibraryPanel extends JPanel {
    
    private LibraryController controller;
    
    // Search components
    private JTextField searchField;
    private JTable searchResultsTable;
    private DefaultTableModel searchTableModel;
    private JLabel searchStatusLabel;
    
    // Book management components
    private JTextField isbnField;
    private JTextField titleField;
    private JTextField editionField;
    private JTextField versionField;
    private JTextField yearField;
    private JTextField copiesField;
    
    // Action buttons
    private JButton addBookButton;
    private JButton borrowButton;
    private JButton returnButton;
    private JButton reserveButton;
    private JButton clearButton;
    
    // Selected book
    private Book selectedBook;
    
    public LibraryPanel() {
        controller = new LibraryController();
        initializeUI();
    }
    
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Top: Search section with real-time results
        add(createSearchSection(), BorderLayout.NORTH);
        
        // Center: Search results table
        add(createSearchResultsPanel(), BorderLayout.CENTER);
        
        // Right: Book management form
        add(createBookFormPanel(), BorderLayout.EAST);
    }
    
    private JPanel createSearchSection() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("🔍 Real-Time Book Search"));
        
        // Search input area
        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Type book title:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        searchField = new JTextField(40);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // REAL-TIME SEARCH: Updates as user types!
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { performRealTimeSearch(); }
            public void removeUpdate(DocumentEvent e) { performRealTimeSearch(); }
            public void insertUpdate(DocumentEvent e) { performRealTimeSearch(); }
        });
        
        searchInputPanel.add(searchLabel);
        searchInputPanel.add(searchField);
        
        // Status label showing results count
        searchStatusLabel = new JLabel("Start typing to search...");
        searchStatusLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        searchStatusLabel.setForeground(Color.BLUE);
        
        panel.add(searchInputPanel, BorderLayout.CENTER);
        panel.add(searchStatusLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createSearchResultsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Search Results (Updates as you type)"));
        
        // Table columns
        String[] columnNames = {
            "ISBN", "Title", "Edition", "Year", 
            "Total", "Available", "Borrowed", "Overdue"
        };
        
        searchTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        searchResultsTable = new JTable(searchTableModel);
        searchResultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        searchResultsTable.setRowHeight(25);
        
        // Color coding for availability
        searchResultsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    int available = Integer.parseInt(table.getValueAt(row, 5).toString());
                    if (available == 0) {
                        c.setBackground(new Color(255, 200, 200)); // Light red
                    } else if (available <= 2) {
                        c.setBackground(new Color(255, 255, 200)); // Light yellow
                    } else {
                        c.setBackground(new Color(200, 255, 200)); // Light green
                    }
                }
                return c;
            }
        });
        
        // Selection listener
        searchResultsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                loadSelectedBook();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(searchResultsTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Legend
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        legendPanel.add(new JLabel("Legend: "));
        legendPanel.add(createColorBox(new Color(200, 255, 200), "Available"));
        legendPanel.add(createColorBox(new Color(255, 255, 200), "Low Stock"));
        legendPanel.add(createColorBox(new Color(255, 200, 200), "Out of Stock"));
        panel.add(legendPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JLabel createColorBox(Color color, String text) {
        JLabel label = new JLabel(" " + text + " ");
        label.setOpaque(true);
        label.setBackground(color);
        label.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        return label;
    }
    
    private JPanel createBookFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createTitledBorder("Book Management"));
        panel.setPreferredSize(new Dimension(350, 0));
        
        // Form fields
        isbnField = new JTextField(20);
        titleField = new JTextField(20);
        editionField = new JTextField(20);
        versionField = new JTextField(20);
        yearField = new JTextField(20);
        copiesField = new JTextField(20);
        
        panel.add(createFieldPanel("ISBN:", isbnField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Title:", titleField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Edition:", editionField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Version:", versionField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Year:", yearField));
        panel.add(Box.createVerticalStrut(10));
        panel.add(createFieldPanel("Total Copies:", copiesField));
        panel.add(Box.createVerticalStrut(20));
        
        // Buttons
        panel.add(createActionButtonPanel());
        
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
    
    private JPanel createActionButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 1, 5, 5));
        
        addBookButton = new JButton("➕ Add New Book");
        borrowButton = new JButton("📤 Borrow Book");
        returnButton = new JButton("📥 Return Book");
        reserveButton = new JButton("📋 Reserve Book");
        clearButton = new JButton("🔄 Clear Form");
        
        // Action listeners
        addBookButton.addActionListener(e -> addBook());
        borrowButton.addActionListener(e -> borrowBook());
        returnButton.addActionListener(e -> returnBook());
        reserveButton.addActionListener(e -> reserveBook());
        clearButton.addActionListener(e -> clearForm());
        
        // Initially disable borrow/return/reserve
        borrowButton.setEnabled(false);
        returnButton.setEnabled(false);
        reserveButton.setEnabled(false);
        
        panel.add(addBookButton);
        panel.add(borrowButton);
        panel.add(returnButton);
        panel.add(reserveButton);
        panel.add(clearButton);
        
        return panel;
    }
    
    /**
     * REAL-TIME SEARCH: This method is called as the user types!
     * Updates results instantly without pressing Enter
     */
    private void performRealTimeSearch() {
        String keyword = searchField.getText().trim();
        
        if (keyword.isEmpty()) {
            searchTableModel.setRowCount(0);
            searchStatusLabel.setText("Start typing to search...");
            searchStatusLabel.setForeground(Color.BLUE);
            return;
        }
        
        // Search books matching the keyword
        List<Book> books = controller.searchBooks(keyword);
        
        // Update table
        searchTableModel.setRowCount(0);
        for (Book book : books) {
            Object[] row = {
                book.getIsbn(),
                book.getTitle(),
                book.getEdition(),
                book.getYearPublished(),
                book.getTotalCopies(),
                book.getAvailableCopies(),
                book.getBorrowedCopies(),
                book.getOverdueCopies()
            };
            searchTableModel.addRow(row);
        }
        
        // Update status
        if (books.isEmpty()) {
            searchStatusLabel.setText("No books found matching '" + keyword + "'");
            searchStatusLabel.setForeground(Color.RED);
        } else {
            searchStatusLabel.setText("Found " + books.size() + " book(s) matching '" + keyword + "'");
            searchStatusLabel.setForeground(new Color(0, 128, 0));
        }
    }
    
    private void loadSelectedBook() {
        int selectedRow = searchResultsTable.getSelectedRow();
        if (selectedRow >= 0) {
            String isbn = (String) searchTableModel.getValueAt(selectedRow, 0);
            selectedBook = controller.getBookByIsbn(isbn);
            
            if (selectedBook != null) {
                isbnField.setText(selectedBook.getIsbn());
                titleField.setText(selectedBook.getTitle());
                editionField.setText(selectedBook.getEdition());
                versionField.setText(selectedBook.getVersion());
                yearField.setText(String.valueOf(selectedBook.getYearPublished()));
                copiesField.setText(String.valueOf(selectedBook.getTotalCopies()));
                
                // Enable appropriate buttons
                borrowButton.setEnabled(selectedBook.getAvailableCopies() > 0);
                returnButton.setEnabled(selectedBook.getBorrowedCopies() > 0);
                reserveButton.setEnabled(selectedBook.getAvailableCopies() == 0);
            }
        }
    }
    
    private void addBook() {
        if (!validateBookForm()) return;
        
        try {
            Book book = new Book(
                isbnField.getText().trim(),
                titleField.getText().trim(),
                editionField.getText().trim(),
                versionField.getText().trim(),
                Integer.parseInt(yearField.getText().trim()),
                Integer.parseInt(copiesField.getText().trim())
            );
            
            if (controller.addBook(book)) {
                JOptionPane.showMessageDialog(this, "Book added successfully!");
                clearForm();
                performRealTimeSearch();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to add book!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers for year and copies!", 
                "Validation Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void borrowBook() {
        if (selectedBook == null) return;
        
        String studentId = JOptionPane.showInputDialog(
            this,
            "Enter Student Registration Number:",
            "Borrow Book",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (studentId != null && !studentId.trim().isEmpty()) {
            if (controller.borrowBook(studentId.trim(), selectedBook.getIsbn())) {
                JOptionPane.showMessageDialog(this, "Book borrowed successfully!");
                performRealTimeSearch();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to borrow book!\nCheck if student exists and book is available.", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void returnBook() {
        if (selectedBook == null) return;
        
        String studentId = JOptionPane.showInputDialog(
            this,
            "Enter Student Registration Number:",
            "Return Book",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (studentId != null && !studentId.trim().isEmpty()) {
            if (controller.returnBook(studentId.trim(), selectedBook.getIsbn())) {
                JOptionPane.showMessageDialog(this, "Book returned successfully!");
                performRealTimeSearch();
                clearForm();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to return book!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void reserveBook() {
        if (selectedBook == null) return;
        
        String studentId = JOptionPane.showInputDialog(
            this,
            "Enter Student Registration Number:\n(Book will be reserved when available)",
            "Reserve Book",
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (studentId != null && !studentId.trim().isEmpty()) {
            if (controller.reserveBook(studentId.trim(), selectedBook.getIsbn())) {
                JOptionPane.showMessageDialog(this, "Book reserved successfully!\nYou will be notified when available.");
                performRealTimeSearch();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to reserve book!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void clearForm() {
        isbnField.setText("");
        titleField.setText("");
        editionField.setText("");
        versionField.setText("");
        yearField.setText("");
        copiesField.setText("");
        
        selectedBook = null;
        borrowButton.setEnabled(false);
        returnButton.setEnabled(false);
        reserveButton.setEnabled(false);
        
        searchResultsTable.clearSelection();
    }
    
    private boolean validateBookForm() {
        if (isbnField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter ISBN!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (titleField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter title!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (yearField.getText().trim().isEmpty() || copiesField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter year and copies!", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    public void refreshData() {
        performRealTimeSearch();
    }
}
