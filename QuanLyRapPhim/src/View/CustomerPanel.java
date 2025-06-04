package View;

import Controller.CustomerController;
import Model.Customer;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CustomerPanel extends JPanel {
    // Màu sắc từ hệ thống chính
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color accentColor = new Color(231, 76, 60);
    private Color bgColor = Color.WHITE;
    private Color darkColor = new Color(44, 62, 80);
    private Color lightTextColor = Color.WHITE;
    private Color tableHeaderColor = new Color(52, 73, 94);
    private Color tableHeaderTextColor = Color.BLACK;
    private Color tableRowColor = Color.BLACK;
    private Color tableAlternateRowColor = new Color(245, 245, 245);
    private Color tableSelectionColor = new Color(232, 240, 254);
    private Color formButtonColor = new Color(46, 204, 113);
    private Color cancelButtonColor = new Color(149, 165, 166);

    // Font chữ thống nhất
    private Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
    private Font headerFont = new Font("Segoe UI", Font.BOLD, 18);
    private Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
    private Font textFont = new Font("Segoe UI", Font.PLAIN, 14);
    private Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

    // Components
    private JTable customersTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    
    // Form components
    private JTextField idField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextField createdAtField;
    
    // Controller
    private CustomerController customerController;
    
    private JButton viewDeletedButton;
    private JDialog deletedCustomersDialog;
    private JTable deletedCustomersTable;
    private DefaultTableModel deletedTableModel;
    
    public CustomerPanel() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Initialize controller
        customerController = new CustomerController();
        
        // Initialize components
        initComponents();
        
        // Load initial data
        loadCustomerData();
    }
    
    private void initComponents() {
        // Title Panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Main Content Panel with split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(800);
        splitPane.setBackground(bgColor);
        splitPane.setBorder(null);
        
        // Table Panel (Left side)
        JPanel tablePanel = createTablePanel();
        
        // Form Panel (Right side)
        JPanel formPanel = createFormPanel();
        
        splitPane.setLeftComponent(tablePanel);
        splitPane.setRightComponent(formPanel);
        
        add(splitPane, BorderLayout.CENTER);
    }
    
    private void loadCustomerData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load data from database
        List<Customer> customers = customerController.getAllCustomers();
        
        // Add data to table
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{
                customer.getCustomerId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                sdf.format(customer.getCreatedAt())
            });
        }
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim();
        
        if (searchText.isEmpty()) {
            loadCustomerData();
            return;
        }
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Search customers
        List<Customer> customers = customerController.searchCustomers(searchText);
        
        // Add results to table
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Customer customer : customers) {
            tableModel.addRow(new Object[]{
                customer.getCustomerId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                sdf.format(customer.getCreatedAt())
            });
        }
    }
    
    private void saveCustomer() {
        try {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            
            // Validation
            if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng điền đầy đủ thông tin khách hàng!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Basic email validation
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                JOptionPane.showMessageDialog(this, 
                    "Email không hợp lệ!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Basic phone validation (Vietnamese phone number)
            if (!phone.matches("^(0|\\+84)[0-9]{9,10}$")) {
                JOptionPane.showMessageDialog(this, 
                    "Số điện thoại không hợp lệ!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Confirm before saving
            int option = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn lưu thông tin khách hàng này?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);
                
            if (option != JOptionPane.YES_OPTION) {
                return;
            }
            
            Customer customer = new Customer();
            customer.setFullName(name);
            customer.setEmail(email);
            customer.setPhone(phone);
            customer.setCreatedAt(new Date());
            
            boolean success;
            String errorMessage = "";
            
            if (id.isEmpty()) {
                // Add new customer
                try {
                    success = customerController.addCustomer(customer);
                    if (!success) {
                        errorMessage = "Không thể thêm khách hàng mới. Vui lòng kiểm tra lại thông tin.";
                    }
                } catch (Exception e) {
                    success = false;
                    errorMessage = "Lỗi khi thêm khách hàng: " + e.getMessage();
                }
            } else {
                // Update existing customer
                try {
                    customer.setCustomerId(Integer.parseInt(id));
                    success = customerController.updateCustomer(customer);
                    if (!success) {
                        errorMessage = "Không thể cập nhật thông tin khách hàng. Vui lòng kiểm tra lại thông tin.";
                    }
                } catch (Exception e) {
                    success = false;
                    errorMessage = "Lỗi khi cập nhật khách hàng: " + e.getMessage();
                }
            }
            
            if (success) {
                loadCustomerData();
                clearForm();
                // Disable editing after saving
                nameField.setEditable(false);
                emailField.setEditable(false);
                phoneField.setEditable(false);
                
                JOptionPane.showMessageDialog(this, 
                    id.isEmpty() ? "Thêm khách hàng mới thành công!" : "Cập nhật thông tin khách hàng thành công!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    errorMessage, 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Có lỗi xảy ra: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void deleteSelectedCustomer() {
        try {
            int selectedRow = customersTable.getSelectedRow();
            
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn một khách hàng để xóa!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int option = JOptionPane.showConfirmDialog(this, 
                "Bạn có chắc chắn muốn xóa khách hàng này?", 
                "Xác nhận xóa", 
                JOptionPane.YES_NO_OPTION);
                
            if (option == JOptionPane.YES_OPTION) {
                int customerId = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
                boolean success = false;
                String errorMessage = "";
                
                try {
                    success = customerController.deleteCustomer(customerId);
                    if (!success) {
                        errorMessage = "Không thể xóa khách hàng. Vui lòng kiểm tra lại.";
                    }
                } catch (Exception e) {
                    success = false;
                    errorMessage = "Lỗi khi xóa khách hàng: " + e.getMessage();
                }
                
                if (success) {
                    loadCustomerData();
                    clearForm();
                    JOptionPane.showMessageDialog(this, 
                        "Xóa khách hàng thành công!", 
                        "Thông báo", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        errorMessage, 
                        "Lỗi", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Có lỗi xảy ra khi xóa khách hàng: " + e.getMessage(), 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void refreshData() {
        loadCustomerData();
        clearForm();
        searchField.setText("");
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Quản lý Khách hàng");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(darkColor);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Action toolbar panel
        JPanel toolbarPanel = new JPanel(new BorderLayout());
        toolbarPanel.setBackground(Color.WHITE);
        toolbarPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(Color.WHITE);
        
        JLabel searchLabel = new JLabel("Tìm kiếm: ");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchLabel.setForeground(darkColor);
        
        searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                performSearch();
            }
        });
        
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        viewDeletedButton = createStyledButton("Xem khách hàng đã xóa", new Color(155, 89, 182));
        viewDeletedButton.addActionListener(e -> showDeletedCustomers());
        
        refreshButton = createStyledButton("Làm mới", darkColor);
        refreshButton.addActionListener(e -> refreshData());
        
        buttonsPanel.add(viewDeletedButton);
        buttonsPanel.add(refreshButton);
        
        toolbarPanel.add(searchPanel, BorderLayout.WEST);
        toolbarPanel.add(buttonsPanel, BorderLayout.EAST);
        
        // Table panel
        String[] columnNames = {"ID", "Họ tên", "Email", "Số điện thoại", "Ngày tạo"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        customersTable = new JTable(tableModel);
        customersTable.setFont(new Font("Arial", Font.PLAIN, 14));
        customersTable.setRowHeight(35);
        customersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        customersTable.setSelectionBackground(tableSelectionColor);
        customersTable.setGridColor(new Color(230, 230, 230));
        customersTable.setShowGrid(true);
        customersTable.setIntercellSpacing(new Dimension(0, 0));
        
        // Customize table header
        JTableHeader header = customersTable.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(tableHeaderColor);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        
        // Add selection listener
        customersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && customersTable.getSelectedRow() != -1) {
                displaySelectedCustomer();
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(customersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        panel.add(toolbarPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        // Form title
        JLabel formTitleLabel = new JLabel("Thông tin khách hàng");
        formTitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        formTitleLabel.setForeground(darkColor);
        formTitleLabel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        // Form fields panel
        JPanel formFieldsPanel = new JPanel();
        formFieldsPanel.setLayout(new GridBagLayout());
        formFieldsPanel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // ID field
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel idLabel = createFormLabel("ID:");
        formFieldsPanel.add(idLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        idField = createFormTextField();
        idField.setEditable(false);
        formFieldsPanel.add(idField, gbc);
        
        // Name field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        JLabel nameLabel = createFormLabel("Họ tên:");
        formFieldsPanel.add(nameLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        nameField = createFormTextField();
        formFieldsPanel.add(nameField, gbc);
        
        // Email field
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        JLabel emailLabel = createFormLabel("Email:");
        formFieldsPanel.add(emailLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        emailField = createFormTextField();
        formFieldsPanel.add(emailField, gbc);
        
        // Phone field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        JLabel phoneLabel = createFormLabel("Số điện thoại:");
        formFieldsPanel.add(phoneLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 1.0;
        phoneField = createFormTextField();
        formFieldsPanel.add(phoneField, gbc);
        
        // Created At field
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        JLabel createdAtLabel = createFormLabel("Ngày tạo:");
        formFieldsPanel.add(createdAtLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.weightx = 1.0;
        createdAtField = createFormTextField();
        createdAtField.setEditable(false);
        formFieldsPanel.add(createdAtField, gbc);
        
        // Action buttons
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 10, 10, 10);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionPanel.setBackground(Color.WHITE);
        
        addButton = createStyledButton("Thêm mới", secondaryColor);
        addButton.addActionListener(e -> {
            clearForm();
            nameField.setEditable(true);
            emailField.setEditable(true);
            phoneField.setEditable(true);
            nameField.requestFocus();
        });
        
        editButton = createStyledButton("Sửa", formButtonColor);
        editButton.addActionListener(e -> {
            if (customersTable.getSelectedRow() != -1) {
                displaySelectedCustomer();
                nameField.setEditable(true);
                emailField.setEditable(true);
                phoneField.setEditable(true);
                nameField.requestFocus();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng chọn một khách hàng để sửa!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });
        
        deleteButton = createStyledButton("Xóa", accentColor);
        deleteButton.addActionListener(e -> deleteSelectedCustomer());
        
        JButton saveButton = createStyledButton("Lưu", formButtonColor);
        saveButton.addActionListener(e -> saveCustomer());
        
        JButton cancelButton = createStyledButton("Hủy", cancelButtonColor);
        cancelButton.addActionListener(e -> {
            clearForm();
            nameField.setEditable(false);
            emailField.setEditable(false);
            phoneField.setEditable(false);
        });
        
        actionPanel.add(addButton);
        actionPanel.add(editButton);
        actionPanel.add(deleteButton);
        actionPanel.add(saveButton);
        actionPanel.add(cancelButton);
        
        formFieldsPanel.add(actionPanel, gbc);
        
        panel.add(formTitleLabel, BorderLayout.NORTH);
        panel.add(formFieldsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        
        // Tạo góc tròn cho nút
        button.putClientProperty("JButton.buttonType", "roundRect");
        
        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    // Method to display selected customer
    private void displaySelectedCustomer() {
        int selectedRow = customersTable.getSelectedRow();
        
        if (selectedRow != -1) {
            // Convert to model row in case table is sorted
            int modelRow = customersTable.convertRowIndexToModel(selectedRow);
            
            idField.setText(tableModel.getValueAt(modelRow, 0).toString());
            nameField.setText(tableModel.getValueAt(modelRow, 1).toString());
            emailField.setText(tableModel.getValueAt(modelRow, 2).toString());
            phoneField.setText(tableModel.getValueAt(modelRow, 3).toString());
            createdAtField.setText(tableModel.getValueAt(modelRow, 4).toString());
        }
    }
    
    // Method to clear form
    private void clearForm() {
        idField.setText("");
        nameField.setText("");
        emailField.setText("");
        phoneField.setText("");
        createdAtField.setText(getCurrentDateTime());
        
        // Remove selection from table
        customersTable.clearSelection();
        
        // Disable editing by default
        nameField.setEditable(false);
        emailField.setEditable(false);
        phoneField.setEditable(false);
    }
    
    // Helper method to get current date time
    private String getCurrentDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(darkColor);
        return label;
    }
    
    private JTextField createFormTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            new EmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }
    
    private void showDeletedCustomers() {
        if (deletedCustomersDialog == null) {
            deletedCustomersDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Khách hàng đã xóa", true);
            deletedCustomersDialog.setSize(800, 500);
            deletedCustomersDialog.setLocationRelativeTo(this);
            
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(new EmptyBorder(15, 15, 15, 15));
            
            // Create table
            String[] columnNames = {"ID", "Họ tên", "Email", "Số điện thoại", "Ngày tạo"};
            deletedTableModel = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            deletedCustomersTable = new JTable(deletedTableModel);
            deletedCustomersTable.setFont(textFont);
            deletedCustomersTable.setRowHeight(40);
            deletedCustomersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            deletedCustomersTable.setSelectionBackground(tableSelectionColor);
            deletedCustomersTable.setGridColor(new Color(230, 230, 230));
            deletedCustomersTable.setShowGrid(true);
            
            JScrollPane scrollPane = new JScrollPane(deletedCustomersTable);
            panel.add(scrollPane, BorderLayout.CENTER);
            
            // Buttons panel
            JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonsPanel.setBackground(Color.WHITE);
            
            JButton restoreButton = createStyledButton("Khôi phục", new Color(155, 89, 182));
            restoreButton.addActionListener(e -> restoreSelectedDeletedCustomer());
            
            JButton closeButton = createStyledButton("Đóng", cancelButtonColor);
            closeButton.addActionListener(e -> deletedCustomersDialog.dispose());
            
            buttonsPanel.add(restoreButton);
            buttonsPanel.add(closeButton);
            panel.add(buttonsPanel, BorderLayout.SOUTH);
            
            deletedCustomersDialog.add(panel);
        }
        
        // Load deleted customers
        loadDeletedCustomers();
        deletedCustomersDialog.setVisible(true);
    }

    private void loadDeletedCustomers() {
        deletedTableModel.setRowCount(0);
        List<Customer> deletedCustomers = customerController.getDeletedCustomers();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Customer customer : deletedCustomers) {
            deletedTableModel.addRow(new Object[]{
                customer.getCustomerId(),
                customer.getFullName(),
                customer.getEmail(),
                customer.getPhone(),
                sdf.format(customer.getCreatedAt())
            });
        }
    }

    private void restoreSelectedDeletedCustomer() {
        int selectedRow = deletedCustomersTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(deletedCustomersDialog, 
                "Vui lòng chọn một khách hàng để khôi phục!", 
                "Thông báo", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int option = JOptionPane.showConfirmDialog(deletedCustomersDialog, 
            "Bạn có chắc chắn muốn khôi phục khách hàng này?", 
            "Xác nhận khôi phục", 
            JOptionPane.YES_NO_OPTION);
            
        if (option == JOptionPane.YES_OPTION) {
            int customerId = Integer.parseInt(deletedTableModel.getValueAt(selectedRow, 0).toString());
            boolean success = customerController.restoreCustomer(customerId);
            
            if (success) {
                loadDeletedCustomers();
                loadCustomerData();
                JOptionPane.showMessageDialog(deletedCustomersDialog, 
                    "Khôi phục khách hàng thành công!", 
                    "Thông báo", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(deletedCustomersDialog, 
                    "Không thể khôi phục khách hàng. Vui lòng thử lại!", 
                    "Lỗi", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}