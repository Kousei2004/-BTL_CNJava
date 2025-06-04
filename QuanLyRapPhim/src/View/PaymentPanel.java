package View;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.github.lgooddatepicker.components.DatePicker;
import java.util.Date;
import Controller.PaymentController;
import Model.Booking;
import Controller.BookingController;
import Model.Customer;

public class PaymentPanel extends JPanel {
    // UI Components
    private JTabbedPane tabbedPane;
    private JPanel processPanel, promotionsPanel, reportsPanel;
    
    // Payment Process Tab
    private JComboBox<String> bookingComboBox;
    private JTextField customerField, totalAmountField, discountAmountField, finalAmountField;
    private JComboBox<String> paymentMethodComboBox;
    private JButton applyPromoButton, processPaymentButton, resetButton;
    private JLabel currentPromoLabel;
    private String currentPromoCode = null;
    private double currentDiscount = 0;
    
    // Promotions Tab
    private JTable promotionsTable;
    private DefaultTableModel promotionsTableModel;
    private JTextField promoCodeField, promoDescField, discountPercentField, minTotalField;
    private JButton addPromoButton, updatePromoButton, deletePromoButton, clearPromoFormButton;
    
    // Reports Tab
    private DatePicker fromDateChooser, toDateChooser;
    private JComboBox<String> reportTypeComboBox;
    private JButton generateReportButton;
    private JTable reportsTable;
    private DefaultTableModel reportsTableModel;
    private JLabel totalRevenueLabel;
    
    // Colors
    private Color primaryColor = new Color(41, 128, 185);
    private Color accentColor = new Color(231, 76, 60);
    private Color bgColor = new Color(236, 240, 241);
    private Color darkColor = new Color(44, 62, 80);
    
    private PaymentController paymentController = new PaymentController();
    
    // Đầu class, khai báo:
    private DatePicker validFromDate, validToDate;
    
    public PaymentPanel() {
        setLayout(new BorderLayout());
        setBackground(bgColor);
        
        // Tạo title panel
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, BorderLayout.NORTH);
        
        // Tạo tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Tạo các tab
        processPanel = createProcessPaymentPanel();
        promotionsPanel = createPromotionsPanel();
        reportsPanel = createReportsPanel();
        
        tabbedPane.addTab("Xử lý thanh toán", new ImageIcon(), processPanel);
        tabbedPane.addTab("Khuyến mãi", new ImageIcon(), promotionsPanel);
        tabbedPane.addTab("Báo cáo doanh thu", new ImageIcon(), reportsPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
        
        // Sau khi tạo xong UI, load danh sách booking chờ thanh toán
        loadBookings();
    }
    
    private JPanel createTitlePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Thanh toán & Khuyến mãi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(darkColor);
        
        panel.add(titleLabel);
        return panel;
    }
    
    private JPanel createProcessPaymentPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 5, 10, 5);
        gc.weightx = 0.1;
        gc.weighty = 0;
        gc.gridwidth = 1;
        
        // Booking ID
        gc.gridx = 0;
        gc.gridy = 0;
        JLabel bookingLabel = new JLabel("Mã đặt vé:");
        bookingLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(bookingLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 0;
        gc.weightx = 0.9;
        bookingComboBox = new JComboBox<>();
        bookingComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(bookingComboBox, gc);
        
        bookingComboBox.addActionListener(e -> loadBookingDetails());
        
        // Customer
        gc.gridx = 0;
        gc.gridy = 1;
        gc.weightx = 0.1;
        JLabel customerLabel = new JLabel("Khách hàng:");
        customerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(customerLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 1;
        gc.weightx = 0.9;
        customerField = new JTextField(20);
        customerField.setFont(new Font("Arial", Font.PLAIN, 14));
        customerField.setEditable(false);
        formPanel.add(customerField, gc);
        
        // Total Amount
        gc.gridx = 0;
        gc.gridy = 2;
        gc.weightx = 0.1;
        JLabel totalLabel = new JLabel("Tổng tiền:");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(totalLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 2;
        gc.weightx = 0.9;
        totalAmountField = new JTextField(20);
        totalAmountField.setFont(new Font("Arial", Font.PLAIN, 14));
        totalAmountField.setEditable(false);
        formPanel.add(totalAmountField, gc);
        
        // Current Promo
        gc.gridx = 0;
        gc.gridy = 3;
        gc.weightx = 0.1;
        JLabel promoLabel = new JLabel("Mã giảm giá:");
        promoLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(promoLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 3;
        gc.weightx = 0.7;
        JPanel promoPanel = new JPanel(new BorderLayout(5, 0));
        promoPanel.setBackground(Color.WHITE);
        
        JTextField promoCodeInput = new JTextField(15);
        promoCodeInput.setFont(new Font("Arial", Font.PLAIN, 14));
        
        applyPromoButton = new JButton("Áp dụng");
        applyPromoButton.setFont(new Font("Arial", Font.PLAIN, 12));
        stylizeButton(applyPromoButton, primaryColor);
        applyPromoButton.addActionListener(e -> {
            String promoCode = promoCodeInput.getText().trim();
            if (promoCode.isEmpty() || bookingComboBox.getSelectedItem() == null) return;
            int bookingId = Integer.parseInt(bookingComboBox.getSelectedItem().toString().substring(2));
            double total = paymentController.getBookingTotalAmount(bookingId);
            double discount = paymentController.calculateDiscount(promoCode, total);
            if (discount > 0) {
                discountAmountField.setText(formatCurrency(discount));
                finalAmountField.setText(formatCurrency(total - discount));
                currentPromoCode = promoCode;
                currentDiscount = discount;
                currentPromoLabel.setText("Đã áp dụng mã: " + promoCode);
            } else {
                discountAmountField.setText("0đ");
                finalAmountField.setText(formatCurrency(total));
                currentPromoLabel.setText("Mã không hợp lệ hoặc không đủ điều kiện!");
                currentPromoCode = null;
                currentDiscount = 0;
            }
        });
        
        promoPanel.add(promoCodeInput, BorderLayout.CENTER);
        promoPanel.add(applyPromoButton, BorderLayout.EAST);
        formPanel.add(promoPanel, gc);
        
        // Discount Amount
        gc.gridx = 0;
        gc.gridy = 4;
        gc.weightx = 0.1;
        JLabel discountLabel = new JLabel("Giảm giá:");
        discountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(discountLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 4;
        gc.weightx = 0.9;
        discountAmountField = new JTextField(20);
        discountAmountField.setFont(new Font("Arial", Font.PLAIN, 14));
        discountAmountField.setEditable(false);
        formPanel.add(discountAmountField, gc);
        
        // Final Amount
        gc.gridx = 0;
        gc.gridy = 5;
        gc.weightx = 0.1;
        JLabel finalLabel = new JLabel("Thanh toán:");
        finalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(finalLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 5;
        gc.weightx = 0.9;
        finalAmountField = new JTextField(20);
        finalAmountField.setFont(new Font("Arial", Font.PLAIN, 14));
        finalAmountField.setEditable(false);
        formPanel.add(finalAmountField, gc);
        
        // Payment Method
        gc.gridx = 0;
        gc.gridy = 6;
        gc.weightx = 0.1;
        JLabel methodLabel = new JLabel("Phương thức:");
        methodLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(methodLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 6;
        gc.weightx = 0.9;
        paymentMethodComboBox = new JComboBox<>(new String[]{"Tiền mặt", "Thẻ tín dụng", "MoMo", "Chuyển khoản"});
        paymentMethodComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(paymentMethodComboBox, gc);
        
        // Current Promo display
        gc.gridx = 0;
        gc.gridy = 7;
        gc.gridwidth = 2;
        currentPromoLabel = new JLabel("Chưa áp dụng mã giảm giá");
        currentPromoLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        currentPromoLabel.setForeground(new Color(150, 150, 150));
        formPanel.add(currentPromoLabel, gc);
        
        // Buttons panel
        gc.gridx = 0;
        gc.gridy = 8;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);
        
        processPaymentButton = new JButton("Xác nhận thanh toán");
        processPaymentButton.setFont(new Font("Arial", Font.PLAIN, 14));
        stylizeButton(processPaymentButton, accentColor);
        processPaymentButton.addActionListener(e -> handleProcessPayment());
        
        resetButton = new JButton("Làm mới");
        resetButton.setFont(new Font("Arial", Font.PLAIN, 14));
        stylizeButton(resetButton, new Color(52, 73, 94));
        resetButton.addActionListener(e -> loadBookings());
        
        buttonsPanel.add(processPaymentButton);
        buttonsPanel.add(resetButton);
        formPanel.add(buttonsPanel, gc);
        
        panel.add(formPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPromotionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(650);
        splitPane.setBackground(bgColor);
        
        // Table panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        String[] columns = {"ID", "Mã khuyến mãi", "Giảm giá (%)", "Từ ngày", "Đến ngày", "Đơn tối thiểu"};
        promotionsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        promotionsTable = new JTable(promotionsTableModel); 
        promotionsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        promotionsTable.setRowHeight(25);
        promotionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        promotionsTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        promotionsTable.getTableHeader().setBackground(primaryColor);
        promotionsTable.getTableHeader().setForeground(Color.BLACK);
        
        // Add selection listener
        promotionsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && promotionsTable.getSelectedRow() != -1) {
                int row = promotionsTable.getSelectedRow();
                promoCodeField.setText(promotionsTable.getValueAt(row, 1).toString());
                discountPercentField.setText(promotionsTable.getValueAt(row, 2).toString());
                validFromDate.setDate(((java.sql.Date)promotionsTable.getValueAt(row, 3)).toLocalDate());
                validToDate.setDate(((java.sql.Date)promotionsTable.getValueAt(row, 4)).toLocalDate());
                minTotalField.setText(promotionsTable.getValueAt(row, 5).toString());
            }
        });
        
        JScrollPane tableScrollPane = new JScrollPane(promotionsTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 5, 10, 5);
        gc.weightx = 1.0;
        gc.weighty = 0;
        gc.gridwidth = 2;
        
        // Title
        gc.gridx = 0;
        gc.gridy = 0;
        JLabel formTitle = new JLabel("Thông tin khuyến mãi");
        formTitle.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(formTitle, gc);
        
        // Promo Code
        gc.gridx = 0;
        gc.gridy = 1;
        gc.gridwidth = 1;
        gc.weightx = 0.3;
        JLabel codeLabel = new JLabel("Mã khuyến mãi:");
        codeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(codeLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 1;
        gc.weightx = 0.7;
        promoCodeField = new JTextField(15);
        promoCodeField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(promoCodeField, gc);
        
        // Description
        gc.gridx = 0;
        gc.gridy = 2;
        gc.gridwidth = 1;
        gc.weightx = 0.3;
        JLabel descLabel = new JLabel("Mô tả:");
        descLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(descLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 2;
        gc.weightx = 0.7;
        promoDescField = new JTextField(15);
        promoDescField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(promoDescField, gc);
        
        // Discount Percent
        gc.gridx = 0;
        gc.gridy = 3;
        gc.gridwidth = 1;
        gc.weightx = 0.3;
        JLabel discountLabel = new JLabel("Giảm giá (%):");
        discountLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(discountLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 3;
        gc.weightx = 0.7;
        discountPercentField = new JTextField(15);
        discountPercentField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(discountPercentField, gc);
        
        // Min Total
        gc.gridx = 0;
        gc.gridy = 4;
        gc.gridwidth = 1;
        gc.weightx = 0.3;
        JLabel minLabel = new JLabel("Đơn tối thiểu:");
        minLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(minLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 4;
        gc.weightx = 0.7;
        minTotalField = new JTextField(15);
        minTotalField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(minTotalField, gc);
        
        // Valid From
        gc.gridx = 0;
        gc.gridy = 5;
        gc.gridwidth = 1;
        gc.weightx = 0.3;
        JLabel fromLabel = new JLabel("Từ ngày:");
        fromLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(fromLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 5;
        gc.weightx = 0.7;
        validFromDate = new DatePicker();
        validFromDate.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(validFromDate, gc);
        
        // Valid To
        gc.gridx = 0;
        gc.gridy = 6;
        gc.gridwidth = 1;
        gc.weightx = 0.3;
        JLabel toLabel = new JLabel("Đến ngày:");
        toLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(toLabel, gc);
        
        gc.gridx = 1;
        gc.gridy = 6;
        gc.weightx = 0.7;
        validToDate = new DatePicker();
        validToDate.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(validToDate, gc);
        
        // Buttons panel
        gc.gridx = 0;
        gc.gridy = 7;
        gc.gridwidth = 2;
        gc.anchor = GridBagConstraints.CENTER;
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonsPanel.setBackground(Color.WHITE);
        
        addPromoButton = new JButton("Thêm mới");
        addPromoButton.setFont(new Font("Arial", Font.PLAIN, 14));
        stylizeButton(addPromoButton, primaryColor);
        addPromoButton.addActionListener(e -> {
            addPromotionFromForm();
        });
        
        updatePromoButton = new JButton("Cập nhật");
        updatePromoButton.setFont(new Font("Arial", Font.PLAIN, 14));
        stylizeButton(updatePromoButton, new Color(46, 204, 113));
        updatePromoButton.addActionListener(e -> {
            updatePromotionFromForm();
        });
        
        deletePromoButton = new JButton("Xóa");
        deletePromoButton.setFont(new Font("Arial", Font.PLAIN, 14));
        stylizeButton(deletePromoButton, accentColor);
        deletePromoButton.addActionListener(e -> {
            deletePromotionFromForm();
        });
        
        clearPromoFormButton = new JButton("Làm mới");
        clearPromoFormButton.setFont(new Font("Arial", Font.PLAIN, 14));
        stylizeButton(clearPromoFormButton, new Color(52, 73, 94));
        
        buttonsPanel.add(addPromoButton);
        buttonsPanel.add(updatePromoButton);
        buttonsPanel.add(deletePromoButton);
        buttonsPanel.add(clearPromoFormButton);
        
        formPanel.add(buttonsPanel, gc);
        
 
        // Add panels to split pane
        splitPane.setLeftComponent(tablePanel);
        splitPane.setRightComponent(formPanel);
        
        panel.add(splitPane, BorderLayout.CENTER);

        // Gọi loadPromotions khi khởi tạo panel
        loadPromotions();

        return panel;
    }
    
    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0;

        JLabel fromLabel = new JLabel("Từ ngày:");
        fromLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        filterPanel.add(fromLabel, gbc);

        fromDateChooser = new DatePicker();
        fromDateChooser.setFont(new Font("Arial", Font.PLAIN, 14));
        fromDateChooser.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 1;
        filterPanel.add(fromDateChooser, gbc);

        JLabel toLabel = new JLabel("Đến ngày:");
        toLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 2;
        filterPanel.add(toLabel, gbc);

        toDateChooser = new DatePicker();
        toDateChooser.setFont(new Font("Arial", Font.PLAIN, 14));
        toDateChooser.setPreferredSize(new Dimension(150, 30));
        gbc.gridx = 3;
        filterPanel.add(toDateChooser, gbc);

        JLabel typeLabel = new JLabel("Loại báo cáo:");
        typeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 4;
        filterPanel.add(typeLabel, gbc);

        reportTypeComboBox = new JComboBox<>(new String[]{"Tất cả", "Theo ngày", "Theo phương thức thanh toán"});
        reportTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        reportTypeComboBox.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 5;
        filterPanel.add(reportTypeComboBox, gbc);

        generateReportButton = new JButton("Tạo báo cáo");
        generateReportButton.setFont(new Font("Arial", Font.PLAIN, 14));
        stylizeButton(generateReportButton, primaryColor);
        generateReportButton.addActionListener(e -> loadPaidInvoices());
        gbc.gridx = 6;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        filterPanel.add(generateReportButton, gbc);
        
        // Reports table
        JPanel tablePanel = new JPanel(new BorderLayout(10, 10));
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        String[] columns = {"Mã thanh toán", "Mã đặt vé", "Khách hàng", "Phương thức", "Số tiền", "Thời gian"};
        reportsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reportsTable = new JTable(reportsTableModel);
        reportsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        reportsTable.setRowHeight(25);
        reportsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportsTable.getTableHeader().setFont(new Font("Arial", Font.PLAIN, 14));
        reportsTable.getTableHeader().setBackground(primaryColor);
        reportsTable.getTableHeader().setForeground(Color.BLACK);
        
        JScrollPane tableScrollPane = new JScrollPane(reportsTable);
        tablePanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Total revenue panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);
        
        totalRevenueLabel = new JLabel("Tổng doanh thu: 0đ");
        totalRevenueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        totalRevenueLabel.setForeground(accentColor);
        totalPanel.add(totalRevenueLabel);
        
        tablePanel.add(totalPanel, BorderLayout.SOUTH);
        

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void stylizeButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        
        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color.darker());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
            }
        });
    }
    
    private void loadBookings() {
        bookingComboBox.removeAllItems();
        java.util.List<Booking> bookings = paymentController.getPendingBookings();
        for (Booking b : bookings) {
            bookingComboBox.addItem("BK" + String.format("%06d", b.getBookingId()));
        }
        // Reset các trường thông tin
        customerField.setText("");
        totalAmountField.setText("");
        discountAmountField.setText("");
        finalAmountField.setText("");
    }
    
    private void loadBookingDetails() {
        if (bookingComboBox.getSelectedItem() == null) return;
        String selected = bookingComboBox.getSelectedItem().toString();
        if (!selected.startsWith("BK")) return;
        int bookingId = Integer.parseInt(selected.substring(2));

        // Lấy booking từ danh sách đã load
        Booking booking = null;
        java.util.List<Booking> bookings = paymentController.getPendingBookings();
        for (Booking b : bookings) {
            if (b.getBookingId() == bookingId) {
                booking = b;
                break;
            }
        }
        if (booking != null) {
            BookingController bookingController = new BookingController();
            Customer customer = bookingController.getCustomerById(booking.getCustomerId());
            if (customer != null) {
                customerField.setText(customer.getFullName());
            } else {
                customerField.setText("");
            }
        } else {
            customerField.setText("");
        }

        // Tổng tiền
        double total = paymentController.getBookingTotalAmount(bookingId);
        totalAmountField.setText(formatCurrency(total));
        discountAmountField.setText("0đ");
        finalAmountField.setText(formatCurrency(total));
    }
    
    private void handleProcessPayment() {
        if (bookingComboBox.getSelectedItem() == null) return;
        String selected = bookingComboBox.getSelectedItem().toString();
        if (!selected.startsWith("BK")) return;
        int bookingId = Integer.parseInt(selected.substring(2));
        double amount = paymentController.getBookingTotalAmount(bookingId) - currentDiscount;
        // Ánh xạ phương thức thanh toán sang ENUM đúng với DB
        String display = paymentMethodComboBox.getSelectedItem().toString();
        String method;
        switch (display) {
            case "Tiền mặt": method = "cash"; break;
            case "Thẻ tín dụng": method = "credit_card"; break;
            case "MoMo": method = "momo"; break;
            case "Chuyển khoản": method = "bank_transfer"; break;
            default: method = "cash";
        }
        boolean ok = paymentController.processPayment(bookingId, amount, method);
        if (ok) {
            // Lưu mã khuyến mãi nếu có
            if (currentPromoCode != null) {
                paymentController.savePromotionDetail(currentPromoCode, bookingId);
            }
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
            loadBookings();
            currentPromoCode = null;
            currentDiscount = 0;
            currentPromoLabel.setText("Chưa áp dụng mã giảm giá");
        } else {
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại!");
        }
    }
    
    private String formatCurrency(double amount) {
        DecimalFormat df = new DecimalFormat("#,###.00");
        return df.format(amount) + "đ";
    }
    
    private void addPromotionFromForm() {
        String code = promoCodeField.getText().trim();
        String desc = promoDescField.getText().trim();
        int percent;
        double minTotal;
        java.time.LocalDate validFrom, validTo;
        try {
            percent = Integer.parseInt(discountPercentField.getText().trim());
            minTotal = Double.parseDouble(minTotalField.getText().trim());
            validFrom = validFromDate.getDate();
            validTo = validToDate.getDate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (code.isEmpty() || percent <= 0 || minTotal < 0 || validFrom == null || validTo == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.util.Date from = java.sql.Date.valueOf(validFrom);
        java.util.Date to = java.sql.Date.valueOf(validTo);
        boolean ok = paymentController.addPromotion(code, desc, percent, minTotal, from, to);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Thêm mã khuyến mãi thành công!");
            promoCodeField.setText("");
            promoDescField.setText("");
            discountPercentField.setText("");
            minTotalField.setText("");
            validFromDate.clear();
            validToDate.clear();
            loadPromotions();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm mã khuyến mãi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updatePromotionFromForm() {
        String code = promoCodeField.getText().trim();
        String desc = promoDescField.getText().trim();
        int percent;
        double minTotal;
        java.time.LocalDate validFrom, validTo;
        try {
            percent = Integer.parseInt(discountPercentField.getText().trim());
            minTotal = Double.parseDouble(minTotalField.getText().trim());
            validFrom = validFromDate.getDate();
            validTo = validToDate.getDate();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đúng định dạng dữ liệu!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (code.isEmpty() || percent <= 0 || minTotal < 0 || validFrom == null || validTo == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        java.util.Date from = java.sql.Date.valueOf(validFrom);
        java.util.Date to = java.sql.Date.valueOf(validTo);
        boolean ok = paymentController.updatePromotion(code, desc, percent, minTotal, from, to);
        if (ok) {
            JOptionPane.showMessageDialog(this, "Cập nhật mã khuyến mãi thành công!");
            loadPromotions();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật mã khuyến mãi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deletePromotionFromForm() {
        String code = promoCodeField.getText().trim();
        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn mã khuyến mãi để xóa!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc muốn xóa mã khuyến mãi này?", "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = paymentController.deletePromotion(code);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Xóa mã khuyến mãi thành công!");
                loadPromotions();
                promoCodeField.setText("");
                promoDescField.setText("");
                discountPercentField.setText("");
                minTotalField.setText("");
                validFromDate.clear();
                validToDate.clear();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa mã khuyến mãi thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void loadPromotions() {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) promotionsTable.getModel();
        model.setRowCount(0);
        java.util.List<Object[]> promos = paymentController.getAllPromotions();
        for (Object[] row : promos) {
            model.addRow(row);
        }
    }
    
    private void loadPaidInvoices() {
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) reportsTable.getModel();
        model.setRowCount(0);
        // Lấy ngày đã chọn từ giao diện
        java.time.LocalDate fromDate = fromDateChooser.getDate();
        java.time.LocalDate toDate = toDateChooser.getDate();
        String reportType = reportTypeComboBox.getSelectedItem().toString();
        java.util.Date from = fromDate != null ? java.sql.Date.valueOf(fromDate) : null;
        java.util.Date to = toDate != null ? java.sql.Date.valueOf(toDate) : null;
        // Gọi controller với tham số ngày và loại báo cáo
        java.util.List<Object[]> invoices = paymentController.getPaidInvoices(from, to, reportType);
        double tongDoanhThu = 0;
        for (Object[] row : invoices) {
            model.addRow(row);
            if (row[4] instanceof Double) {
                tongDoanhThu += (Double) row[4];
            }
        }
        totalRevenueLabel.setText("Tổng doanh thu: " + formatCurrency(tongDoanhThu));
        reportsTable.setModel(model);
    }
}