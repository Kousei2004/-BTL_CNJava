package View;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import Controller.BookingController;
import Model.Movie;
import Model.Showtime;
import Model.Customer;
import Model.Room;
import Model.Seat;
import Controller.ShowtimeController;
import Model.Booking;
import Model.BookingDetail;

// Thêm imports cho PDF và Email
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;
import java.io.FileOutputStream;

public class TicketPanel extends JPanel {
    // Main Colors - matched to existing system
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color accentColor = new Color(231, 76, 60);
    private Color bgColor = new Color(236, 240, 241);
    private Color darkColor = new Color(44, 62, 80);
    private Color lightTextColor = Color.WHITE;
    private Color lightGreen = new Color(46, 204, 113);
    private Color purple = new Color(155, 89, 182);
    private Color orange = new Color(243, 156, 18);

    // Main panels
    private JPanel topPanel;
    private JTabbedPane tabbedPane;
    private JPanel bookingPanel;
    private JPanel ticketManagementPanel;
    private JPanel ticketHistoryPanel;
    private JPanel emailTicketPanel;

    // Booking panel components
    private JComboBox<String> movieComboBox;
    private JComboBox<String> showtimeComboBox;
    private JComboBox<String> customerComboBox;
    private JPanel seatSelectionPanel;
    private JLabel totalPriceLabel;
    private JLabel priceLabel;
    private JButton bookButton;
    private JButton clearSelectionButton;
    private JLabel selectedSeatsLabel;

    // In memory data (would be replaced with database fetches)
    private List<String> selectedSeats = new ArrayList<>();
    private double pricePerSeat = 0.0; // Sẽ được cập nhật từ base_price của suất chiếu

    private BookingController bookingController;
    private ShowtimeController showtimeController;

    // Custom renderer classes
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (value != null) {
                String status = value.toString();
                String display = status;
                switch (status) {
                    case "available":
                        c.setBackground(lightGreen);
                        display = "Ghế trống";
                        break;
                    case "booked":
                        c.setBackground(orange);
                        display = "Đã đặt";
                        break;
                    case "sold":
                        c.setBackground(accentColor);
                        display = "Đã bán";
                        break;
                    default:
                        c.setBackground(table.getBackground());
                }
                c.setForeground(Color.WHITE);
                setText(display);
            }
            return c;
        }
    }

    private class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton viewButton;
        private JButton editButton;
        private JButton deleteButton;

        public ButtonRenderer() {
            setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            setBackground(Color.WHITE);

            viewButton = new JButton("Xem");
            editButton = new JButton("Sửa");
            deleteButton = new JButton("Xóa");

            stylizeButton(viewButton, primaryColor);
            stylizeButton(editButton, secondaryColor);
            stylizeButton(deleteButton, accentColor);

            // Đảm bảo thứ tự: Xem - Sửa - Xóa
            add(viewButton);
            add(Box.createHorizontalStrut(5));
            add(editButton);
            add(Box.createHorizontalStrut(5));
            add(deleteButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewButton;
        private JButton editButton;
        private JButton deleteButton;
        private String label;
        private boolean isPushed;
        private int currentRow = -1;

        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.setBackground(Color.WHITE);

            viewButton = new JButton("Xem");
            editButton = new JButton("Sửa");
            deleteButton = new JButton("Xóa");

            stylizeButton(viewButton, primaryColor);
            stylizeButton(editButton, secondaryColor);
            stylizeButton(deleteButton, accentColor);

            // Đảm bảo thứ tự: Xem - Sửa - Xóa
            panel.add(viewButton);
            panel.add(Box.createHorizontalStrut(5));
            panel.add(editButton);
            panel.add(Box.createHorizontalStrut(5));
            panel.add(deleteButton);

            // Nút Xem: Hiển thị popup chi tiết vé
            viewButton.addActionListener(e -> {
                int row = currentRow;
                if (row >= 0 && row < ticketTable.getRowCount() && ticketTable.getColumnCount() >= 8) {
                    StringBuilder details = new StringBuilder();
                    details.append("Mã vé: ").append(ticketTable.getValueAt(row, 0)).append("\n");
                    details.append("Phim: ").append(ticketTable.getValueAt(row, 1)).append("\n");
                    details.append("Suất chiếu: ").append(ticketTable.getValueAt(row, 2)).append("\n");
                    details.append("Phòng/Ghế: ").append(ticketTable.getValueAt(row, 3)).append("\n");
                    details.append("Khách hàng: ").append(ticketTable.getValueAt(row, 4)).append("\n");
                    details.append("SĐT: ").append(ticketTable.getValueAt(row, 5)).append("\n");
                    details.append("Giá vé: ").append(ticketTable.getValueAt(row, 6)).append("\n");
                    details.append("Trạng thái: ").append(ticketTable.getValueAt(row, 7)).append("\n");
                    JOptionPane.showMessageDialog(null, details.toString(), "Chi tiết vé", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Không thể xem chi tiết vé này!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            });

            // Nút Sửa: Cập nhật trạng thái vé
            editButton.addActionListener(e -> {
                int row = currentRow;
                if (row != -1) {
                    String ticketId = ticketTable.getValueAt(row, 0).toString();
                    String[] displayStatuses = {"Ghế trống", "Đã đặt", "Đã bán"};
                    String[] dbStatuses = {"available", "booked", "sold"};
                    String newStatus = (String) JOptionPane.showInputDialog(
                        null, "Chọn trạng thái mới:", "Cập nhật trạng thái vé",
                        JOptionPane.QUESTION_MESSAGE, null, displayStatuses, displayStatuses[0]);
                    if (newStatus != null) {
                        int idx = Arrays.asList(displayStatuses).indexOf(newStatus);
                        boolean updated = bookingController.updateTicketStatus(Integer.parseInt(ticketId), dbStatuses[idx]);
                        if (updated) {
                            JOptionPane.showMessageDialog(null, "Cập nhật trạng thái thành công!");
                            if (ticketTable.isEditing()) {
                                ticketTable.getCellEditor().stopCellEditing();
                            }
                            refreshTicketTable();
                        } else {
                            JOptionPane.showMessageDialog(null, "Cập nhật thất bại!");
                        }
                    }
                }
            });

            // Xóa vé
            deleteButton.addActionListener(e -> {
                int row = ticketTable.getSelectedRow();
                if (row != -1) {
                    String ticketId = ticketTable.getValueAt(row, 0).toString();
                    int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa vé này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean deleted = bookingController.deleteTicket(Integer.parseInt(ticketId));
                        if (deleted) {
                            JOptionPane.showMessageDialog(null, "Xóa vé thành công!");
                            if (ticketTable.isEditing()) {
                                ticketTable.getCellEditor().stopCellEditing();
                            }
                            refreshTicketTable();
                        } else {
                            JOptionPane.showMessageDialog(null, "Xóa vé thất bại!");
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            isPushed = true;
            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }
    }

    // Thêm class HistoryButtonEditor cho bảng lịch sử đặt vé
    private class HistoryButtonEditor extends DefaultCellEditor {
        private JPanel panel;
        private JButton viewButton;
        private JButton editButton;
        private JButton deleteButton;
        private int currentRow = -1;

        public HistoryButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
            panel.setBackground(Color.WHITE);

            viewButton = new JButton("Xem");
            editButton = new JButton("Sửa");
            deleteButton = new JButton("Xóa");

            stylizeButton(viewButton, primaryColor);
            stylizeButton(editButton, secondaryColor);
            stylizeButton(deleteButton, accentColor);

            panel.add(viewButton);
            panel.add(Box.createHorizontalStrut(5));
            panel.add(editButton);
            panel.add(Box.createHorizontalStrut(5));
            panel.add(deleteButton);

            // Xem chi tiết booking
            viewButton.addActionListener(e -> {
                int row = currentRow;
                if (row >= 0 && row < historyTable.getRowCount()) {
                    StringBuilder details = new StringBuilder();
                    int bookingId = Integer.parseInt(historyTable.getValueAt(row, 0).toString());
                    List<String> seats = bookingController.getSeatNumbersByBookingId(bookingId);
                    details.append("Mã đặt vé: ").append(historyTable.getValueAt(row, 0)).append("\n");
                    details.append("Ngày giờ đặt: ").append(historyTable.getValueAt(row, 1)).append("\n");
                    details.append("Khách hàng: ").append(historyTable.getValueAt(row, 2)).append("\n");
                    details.append("Số lượng vé: ").append(historyTable.getValueAt(row, 3)).append("\n");
                    details.append("Tổng tiền: ").append(historyTable.getValueAt(row, 4)).append("\n");
                    details.append("Trạng thái: ").append(historyTable.getValueAt(row, 5)).append("\n");
                    details.append("Ghế đã đặt: ").append(String.join(", ", seats)).append("\n");
                    JOptionPane.showMessageDialog(null, details.toString(), "Chi tiết đặt vé", JOptionPane.INFORMATION_MESSAGE);
                }
            });

            // Sửa trạng thái booking
            editButton.addActionListener(e -> {
                int row = currentRow;
                if (row != -1) {
                    String bookingId = historyTable.getValueAt(row, 0).toString();
                    String[] statuses = {"pending", "confirmed", "cancelled"};
                    String newStatus = (String) JOptionPane.showInputDialog(
                        null, "Chọn trạng thái mới:", "Cập nhật trạng thái đặt vé",
                        JOptionPane.QUESTION_MESSAGE, null, statuses, statuses[0]);
                    if (newStatus != null) {
                        boolean updated = bookingController.updateBookingStatus(Integer.parseInt(bookingId), newStatus);
                        if (updated) {
                            JOptionPane.showMessageDialog(null, "Cập nhật trạng thái thành công!");
                            if (historyTable.isEditing()) {
                                historyTable.getCellEditor().stopCellEditing();
                            }
                            refreshHistoryTable();
                        } else {
                            JOptionPane.showMessageDialog(null, "Cập nhật thất bại!");
                        }
                    }
                }
            });

            // Xóa booking
            deleteButton.addActionListener(e -> {
                int row = historyTable.getSelectedRow();
                if (row != -1) {
                    String bookingId = historyTable.getValueAt(row, 0).toString();
                    int confirm = JOptionPane.showConfirmDialog(null, "Bạn có chắc muốn xóa đơn đặt vé này?", "Xác nhận xóa", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean deleted = bookingController.deleteBooking(Integer.parseInt(bookingId));
                        if (deleted) {
                            JOptionPane.showMessageDialog(null, "Xóa đơn đặt vé thành công!");
                            if (historyTable.isEditing()) {
                                historyTable.getCellEditor().stopCellEditing();
                            }
                            refreshHistoryTable();
                        } else {
                            JOptionPane.showMessageDialog(null, "Xóa thất bại!");
                        }
                    }
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "";
        }
    }

    // Utility methods
    private void stylizeButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 13));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMargin(new Insets(2, 8, 2, 8));
    }

    private JPanel createSummaryBox(String title, String value, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color, 2, true),
            new EmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 13));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 20));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(valueLabel);

        return panel;
    }

    private JButton createSeatButton(String seatNumber, boolean isSelected, boolean isBooked, boolean isSold) {
        JButton button = new JButton(seatNumber);
        button.setPreferredSize(new Dimension(40, 40));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));

        if (isSelected) {
            button.setBackground(secondaryColor);
            button.setForeground(Color.WHITE);
        } else if (isBooked) {
            button.setBackground(primaryColor);
            button.setForeground(Color.WHITE);
        } else if (isSold) {
            button.setBackground(accentColor);
            button.setForeground(Color.WHITE);
        } else {
            button.setBackground(lightGreen);
            button.setForeground(Color.WHITE);
        }

        return button;
    }

    private JTable ticketTable;
    private JTable historyTable;
    private JPanel ticketPanel;
    private JTextField emailField;

    // Biến lưu thông tin đặt vé hiện tại để xuất PDF/Email
    private Object[] currentBookingInfo;

    public TicketPanel() {
        bookingController = new BookingController();
        showtimeController = new ShowtimeController();
        setLayout(new BorderLayout());
        setBackground(bgColor);
        
        initComponents();
        layoutComponents();
    }

    private void initComponents() {
        // Initialize main components
        topPanel = createTopPanel();
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 14));
        tabbedPane.setBackground(Color.WHITE);
        
        // Initialize tab panels
        bookingPanel = createBookingPanel();
        ticketManagementPanel = createTicketManagementPanel();
        ticketHistoryPanel = createTicketHistoryPanel();
        emailTicketPanel = createEmailTicketPanel();
        
        // Add tabs
        tabbedPane.addTab("Đặt vé mới", new ImageIcon(), bookingPanel);
        tabbedPane.addTab("Quản lý vé", new ImageIcon(), ticketManagementPanel);
        tabbedPane.addTab("Lịch sử đặt vé", new ImageIcon(), ticketHistoryPanel);
        tabbedPane.addTab("Xuất vé", new ImageIcon(), emailTicketPanel);
    }

    private void layoutComponents() {
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Đặt vé & Quản lý vé");
        titleLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        titleLabel.setForeground(darkColor);
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightPanel.setBackground(bgColor);
        
        JButton refreshButton = new JButton("Làm mới");
        stylizeButton(refreshButton, secondaryColor);
        refreshButton.addActionListener(e -> refreshData());
        
        JButton helpButton = new JButton("Trợ giúp");
        stylizeButton(helpButton, primaryColor);
        
        rightPanel.add(refreshButton);
        rightPanel.add(helpButton);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }

    private void refreshData() {
        // Refresh movie list
        loadMoviesFromDB();
        
        // Refresh customer list
        loadCustomersFromDB();
        
        // Refresh showtimes for current movie
        String selectedMovie = (String) movieComboBox.getSelectedItem();
        if (selectedMovie != null) {
            loadShowtimesFromDB(selectedMovie);
        }
        
        // Refresh seat map
        updateSeatMap();
        
        // Refresh ticket management table
        refreshTicketTable();
        
        // Refresh ticket history table
        refreshHistoryTable();
        
        JOptionPane.showMessageDialog(this, 
            "Dữ liệu đã được cập nhật!", 
            "Thông báo", 
            JOptionPane.INFORMATION_MESSAGE);
    }

    private void refreshTicketTable() {
        DefaultTableModel model = (DefaultTableModel) ticketTable.getModel();
        model.setRowCount(0); // Clear existing data
        
        // Get fresh data from database
        List<Object[]> ticketData = bookingController.getAllTickets();
        for (Object[] row : ticketData) {
            model.addRow(row);
        }

        // Update summary boxes
        Container parent = ticketTable.getParent().getParent();
        for (Component comp : parent.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                if (panel.getComponentCount() == 4) { // Summary panel has 4 boxes
                    Component[] summaryComponents = panel.getComponents();
                    updateSummaryBoxes(
                        (JPanel) summaryComponents[0],
                        (JPanel) summaryComponents[1],
                        (JPanel) summaryComponents[2],
                        (JPanel) summaryComponents[3]
                    );
                    break;
                }
            }
        }
    }

    private void refreshHistoryTable() {
        DefaultTableModel model = (DefaultTableModel) historyTable.getModel();
        model.setRowCount(0); // Clear existing data
        List<Object[]> historyData = bookingController.getBookingHistory();
        for (Object[] row : historyData) {
            // Debug: In ra giá trị status thực tế
//            System.out.println("DEBUG - booking_id: " + row[0] + ", status raw: " + row[5]);
            // Chuyển đổi trạng thái sang tiếng Việt
            String status = (row[5] != null) ? row[5].toString() : "";
            String displayStatus;
            switch (status) {
                case "pending":
                    displayStatus = "Chờ xác nhận";
                    break;
                case "confirmed":
                    displayStatus = "Đã xác nhận";
                    break;
                case "cancelled":
                    displayStatus = "Đã hủy";
                case "paid":
                    displayStatus = "Đã thanh toán";
                    
                    break;
                default:
                    displayStatus = status;
            }
            Object[] displayRow = new Object[] {
                row[0], row[1], row[2], row[3], row[4], displayStatus, null
            };
            model.addRow(displayRow);
        }
    }

    private JPanel createBookingPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
     // Top selection panel
        JPanel selectionPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        selectionPanel.setBackground(Color.WHITE);

        // Tạo font cho title
        java.awt.Font titleFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 16); 
        TitledBorder titledBorder = new TitledBorder("Chọn phim và suất chiếu");
        titledBorder.setTitleFont(titleFont);
        titledBorder.setTitleColor(new Color(44, 62, 80));

        selectionPanel.setBorder(new CompoundBorder(
            titledBorder,
            new EmptyBorder(15, 10, 15, 10)
        ));

        // Font cho labels và combobox
        java.awt.Font labelFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 14); 
        java.awt.Font comboFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 14);

        // Movie selection
        JPanel moviePanel = new JPanel(new BorderLayout());
        moviePanel.setBackground(Color.WHITE);
        JLabel movieLabel = new JLabel("Chọn phim:");
        movieLabel.setFont(labelFont);
        moviePanel.add(movieLabel, BorderLayout.NORTH);
        movieComboBox = new JComboBox<>();
        movieComboBox.setFont(comboFont);
        loadMoviesFromDB();
        moviePanel.add(movieComboBox, BorderLayout.CENTER);

        // Showtime selection
        JPanel showtimePanel = new JPanel(new BorderLayout());
        showtimePanel.setBackground(Color.WHITE);
        JLabel showtimeLabel = new JLabel("Chọn suất chiếu:");
        showtimeLabel.setFont(labelFont);
        showtimePanel.add(showtimeLabel, BorderLayout.NORTH);
        showtimeComboBox = new JComboBox<>();
        showtimeComboBox.setFont(comboFont);
        showtimeComboBox.addActionListener(e -> updateSeatMap());
        showtimePanel.add(showtimeComboBox, BorderLayout.CENTER);

        // Customer selection
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setBackground(Color.WHITE);
        JLabel customerLabel = new JLabel("Khách hàng:");
        customerLabel.setFont(labelFont);
        customerPanel.add(customerLabel, BorderLayout.NORTH);
        customerComboBox = new JComboBox<>();
        customerComboBox.setFont(comboFont);
        loadCustomersFromDB();

        // Nút thêm khách hàng
        JButton newCustomerBtn = new JButton("+");
        newCustomerBtn.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 16)); 
        newCustomerBtn.setToolTipText("Thêm khách hàng mới");
        newCustomerBtn.addActionListener(e -> showAddCustomerDialog());
        
        JPanel customerComboPanel = new JPanel(new BorderLayout());
        customerComboPanel.setBackground(Color.WHITE);
        customerComboPanel.add(customerComboBox, BorderLayout.CENTER);
        customerComboPanel.add(newCustomerBtn, BorderLayout.EAST);
        customerPanel.add(customerComboPanel, BorderLayout.CENTER);
        
        selectionPanel.add(moviePanel);
        selectionPanel.add(showtimePanel);
        selectionPanel.add(customerPanel);
        
     // Center panel - Seat selection
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);

        seatSelectionPanel = new JPanel(new BorderLayout());
        seatSelectionPanel.setBackground(Color.WHITE);

        // Tạo font cho title sơ đồ ghế
        java.awt.Font seatMapTitleFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 16);
        TitledBorder seatMapBorder = new TitledBorder(new LineBorder(new Color(200, 200, 200)), "Sơ đồ ghế ngồi");
        seatMapBorder.setTitleFont(seatMapTitleFont);
        seatMapBorder.setTitleColor(new Color(44, 62, 80));

        seatSelectionPanel.setBorder(new CompoundBorder(
            seatMapBorder,
            new EmptyBorder(15, 10, 15, 10)
        ));
        
        JPanel screenPanel = new JPanel(new BorderLayout());
        screenPanel.setBackground(Color.WHITE);
        JPanel screen = new JPanel();
        screen.setBackground(new Color(200, 200, 200));
        screen.setPreferredSize(new Dimension(500, 20));
        JLabel screenLabel = new JLabel("MÀN HÌNH", JLabel.CENTER);
        screenLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        screen.add(screenLabel);
        screenPanel.add(screen, BorderLayout.CENTER);
        screenPanel.add(Box.createVerticalStrut(20), BorderLayout.SOUTH);
        
        JPanel legendPanel = createSeatLegendPanel();
        
        seatSelectionPanel.add(screenPanel, BorderLayout.NORTH);
        seatSelectionPanel.add(legendPanel, BorderLayout.SOUTH);
        
        centerPanel.add(seatSelectionPanel, BorderLayout.CENTER);
        
     // Bottom panel - Booking details
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);

        // Tạo font cho title thông tin đặt vé
        java.awt.Font bookingTitleFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 16);
        TitledBorder bookingBorder = new TitledBorder(new LineBorder(new Color(200, 200, 200)), "Thông tin đặt vé");
        bookingBorder.setTitleFont(bookingTitleFont);
        bookingBorder.setTitleColor(new Color(44, 62, 80));

        bottomPanel.setBorder(new CompoundBorder(
            bookingBorder,
            new EmptyBorder(15, 10, 15, 10)
        ));

        JPanel detailsPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        detailsPanel.setBackground(Color.WHITE);

        // Font cho các label thông tin
        java.awt.Font detailsFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 14);

        selectedSeatsLabel = new JLabel("Ghế đã chọn: ");
        selectedSeatsLabel.setFont(detailsFont);

        priceLabel = new JLabel("Giá vé: 0đ/ghế");
        priceLabel.setFont(detailsFont);

        totalPriceLabel = new JLabel("Tổng tiền: 0đ");
        totalPriceLabel.setFont(detailsFont);

        detailsPanel.add(selectedSeatsLabel);
        detailsPanel.add(new JLabel()); // Empty cell
        detailsPanel.add(priceLabel);
        detailsPanel.add(new JLabel()); // Empty cell
        detailsPanel.add(totalPriceLabel);
        detailsPanel.add(new JLabel()); // Empty cell

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        // Font cho các nút
        java.awt.Font buttonFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 14);

        clearSelectionButton = new JButton("Xóa lựa chọn");
        clearSelectionButton.setFont(buttonFont);
        stylizeButton(clearSelectionButton, orange);
        clearSelectionButton.addActionListener(e -> clearSeatSelection());

        bookButton = new JButton("Đặt vé");
        bookButton.setFont(buttonFont);
        stylizeButton(bookButton, accentColor);
        bookButton.addActionListener(e -> bookTickets());
        
        buttonPanel.add(clearSelectionButton);
        buttonPanel.add(bookButton);
        
        bottomPanel.add(detailsPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Add components to main panel
        panel.add(selectionPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void loadMoviesFromDB() {
        List<String> movies = bookingController.loadMovies();
        movieComboBox.removeAllItems();
        for (String movie : movies) {
            movieComboBox.addItem(movie);
        }
        
        // Thêm listener để load suất chiếu khi chọn phim
        movieComboBox.addActionListener(e -> {
            String selectedMovie = (String) movieComboBox.getSelectedItem();
            if (selectedMovie != null) {
                loadShowtimesFromDB(selectedMovie);
            }
        });
    }
    
    private void loadShowtimesFromDB(String movieTitle) {
        List<String> showtimes = bookingController.loadShowtimes(movieTitle);
        showtimeComboBox.removeAllItems();
        for (String showtime : showtimes) {
            showtimeComboBox.addItem(showtime);
        }
    }
    
    private void loadCustomersFromDB() {
        List<String> customers = bookingController.loadCustomers();
        customerComboBox.removeAllItems();
        for (String customer : customers) {
            customerComboBox.addItem(customer);
        }
    }

    private JPanel createSeatLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        
        // Available seat
        JPanel availablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        availablePanel.setBackground(Color.WHITE);
        JButton availableSample = createSeatButton("", false, false, false);
        availablePanel.add(availableSample);
        availablePanel.add(new JLabel("Ghế trống"));
        
        // Selected seat
        JPanel selectedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        selectedPanel.setBackground(Color.WHITE);
        JButton selectedSample = createSeatButton("", true, false, false);
        selectedPanel.add(selectedSample);
        selectedPanel.add(new JLabel("Ghế đang chọn"));
        
        // Booked seat
        JPanel bookedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        bookedPanel.setBackground(Color.WHITE);
        JButton bookedSample = createSeatButton("", false, true, false);
        bookedPanel.add(bookedSample);
        bookedPanel.add(new JLabel("Ghế đã đặt"));
        
        // Sold seat
        JPanel soldPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        soldPanel.setBackground(Color.WHITE);
        JButton soldSample = createSeatButton("", false, false, true);
        soldPanel.add(soldSample);
        soldPanel.add(new JLabel("Ghế đã bán"));
        
        legendPanel.add(availablePanel);
        legendPanel.add(selectedPanel);
        legendPanel.add(bookedPanel);
        legendPanel.add(soldPanel);
        
        return legendPanel;
    }

    private JPanel createTicketManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
     // Search panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);

        // Tạo font cho title tìm kiếm
        java.awt.Font searchTitleFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 16);
        TitledBorder searchBorder = new TitledBorder(new LineBorder(new Color(200, 200, 200)), "Tìm kiếm vé");
        searchBorder.setTitleFont(searchTitleFont);
        searchBorder.setTitleColor(new Color(44, 62, 80));

        searchPanel.setBorder(new CompoundBorder(
            searchBorder,
            new EmptyBorder(15, 10, 15, 10)
        ));

        JPanel searchInputPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        searchInputPanel.setBackground(Color.WHITE);

        // Font cho labels và text fields
        java.awt.Font labelFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 14);
        java.awt.Font textFieldFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 14);

        JTextField ticketIdField = new JTextField();
        ticketIdField.setFont(textFieldFont);

        JTextField customerField = new JTextField();
        customerField.setFont(textFieldFont);

        JTextField phoneField = new JTextField();
        phoneField.setFont(textFieldFont);

        JPanel ticketIdPanel = new JPanel(new BorderLayout());
        ticketIdPanel.setBackground(Color.WHITE);
        JLabel ticketIdLabel = new JLabel("Mã vé:");
        ticketIdLabel.setFont(labelFont);
        ticketIdPanel.add(ticketIdLabel, BorderLayout.NORTH);
        ticketIdPanel.add(ticketIdField, BorderLayout.CENTER);

        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setBackground(Color.WHITE);
        JLabel customerLabel = new JLabel("Tên khách hàng:");
        customerLabel.setFont(labelFont);
        customerPanel.add(customerLabel, BorderLayout.NORTH);
        customerPanel.add(customerField, BorderLayout.CENTER);

        JPanel phonePanel = new JPanel(new BorderLayout());
        phonePanel.setBackground(Color.WHITE);
        JLabel phoneLabel = new JLabel("Số điện thoại:");
        phoneLabel.setFont(labelFont);
        phonePanel.add(phoneLabel, BorderLayout.NORTH);
        phonePanel.add(phoneField, BorderLayout.CENTER);
        searchInputPanel.add(ticketIdPanel);
        searchInputPanel.add(customerPanel);
        searchInputPanel.add(phonePanel);
        
        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchButtonPanel.setBackground(Color.WHITE);
        
        JButton searchButton = new JButton("Tìm kiếm");
        stylizeButton(searchButton, primaryColor);
        searchButton.addActionListener(e -> {
            String ticketId = ticketIdField.getText().trim();
            String customer = customerField.getText().trim();
            String phone = phoneField.getText().trim();
            List<Object[]> ticketData = bookingController.searchTickets(ticketId, customer, phone);
            DefaultTableModel model1 = (DefaultTableModel) ticketTable.getModel();
            model1.setRowCount(0);
            for (Object[] row : ticketData) {
                model1.addRow(row);
            }
        });
        
        searchButtonPanel.add(searchButton);
        
        searchPanel.add(searchInputPanel, BorderLayout.CENTER);
        searchPanel.add(searchButtonPanel, BorderLayout.SOUTH);
        
        // Ticket table
        String[] columns = {"Mã vé", "Phim", "Suất chiếu", "Phòng/Ghế", "Khách hàng", "SĐT", "Giá vé", "Trạng thái", "Thao tác"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only action column is editable
            }
        };
        
        ticketTable = new JTable(model);
        ticketTable.setRowHeight(35);
        ticketTable.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 13));
        ticketTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 13));
        ticketTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set custom renderer for status column
        ticketTable.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());
        
        // Add action buttons to the last column
        ticketTable.getColumnModel().getColumn(8).setCellRenderer(new ButtonRenderer());
        ticketTable.getColumnModel().getColumn(8).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        ticketTable.getColumnModel().getColumn(8).setPreferredWidth(200);
        ticketTable.getColumnModel().getColumn(8).setMinWidth(180);
        ticketTable.getColumnModel().getColumn(8).setMaxWidth(250);
        
        JScrollPane scrollPane = new JScrollPane(ticketTable);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200)));
        
        // Load initial data
        refreshTicketTable();
        
     // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        summaryPanel.setBackground(Color.WHITE);

        // Tạo font cho title thống kê nhanh
        java.awt.Font summaryTitleFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 16);
        TitledBorder summaryBorder = new TitledBorder(new LineBorder(new Color(200, 200, 200)), "Thống kê nhanh");
        summaryBorder.setTitleFont(summaryTitleFont);
        summaryBorder.setTitleColor(new Color(44, 62, 80));

        summaryPanel.setBorder(new CompoundBorder(
            summaryBorder,
            new EmptyBorder(15, 10, 15, 10)
        ));
        
        JPanel totalTicketsPanel = createSummaryBox("Tổng số vé", "0", primaryColor);
        JPanel availableTicketsPanel = createSummaryBox("Vé còn trống", "0", lightGreen);
        JPanel bookedTicketsPanel = createSummaryBox("Vé đã đặt", "0", orange);
        JPanel soldTicketsPanel = createSummaryBox("Vé đã bán", "0", accentColor);
        
        summaryPanel.add(totalTicketsPanel);
        summaryPanel.add(availableTicketsPanel);
        summaryPanel.add(bookedTicketsPanel);
        summaryPanel.add(soldTicketsPanel);
        
        // Update summary boxes with real data
        updateSummaryBoxes(totalTicketsPanel, availableTicketsPanel, bookedTicketsPanel, soldTicketsPanel);
        
        // Add components to main panel
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(summaryPanel, BorderLayout.SOUTH);
        
        return panel;
    }

    private void updateSummaryBoxes(JPanel totalPanel, JPanel availablePanel, JPanel bookedPanel, JPanel soldPanel) {
        // Get counts from BookingController
        int totalCount = bookingController.getTotalTicketCount();
        int availableCount = bookingController.getAvailableTicketCount();
        int bookedCount = bookingController.getBookedTicketCount();
        int soldCount = bookingController.getSoldTicketCount();

        // Update value labels in each panel
        Component[] totalComponents = totalPanel.getComponents();
        Component[] availableComponents = availablePanel.getComponents();
        Component[] bookedComponents = bookedPanel.getComponents();
        Component[] soldComponents = soldPanel.getComponents();

        // Update total tickets
        for (Component c : totalComponents) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                if (label.getFont().getStyle() == java.awt.Font.BOLD) {
                    label.setText(String.valueOf(totalCount));
                }
            }
        }

        // Update available tickets
        for (Component c : availableComponents) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                if (label.getFont().getStyle() == java.awt.Font.BOLD) {
                    label.setText(String.valueOf(availableCount));
                }
            }
        }

        // Update booked tickets
        for (Component c : bookedComponents) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                if (label.getFont().getStyle() == java.awt.Font.BOLD) {
                    label.setText(String.valueOf(bookedCount));
                }
            }
        }

        // Update sold tickets
        for (Component c : soldComponents) {
            if (c instanceof JLabel) {
                JLabel label = (JLabel) c;
                if (label.getFont().getStyle() == java.awt.Font.BOLD) {
                    label.setText(String.valueOf(soldCount));
                }
            }
        }
    }

    private JPanel createTicketHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
     // Filter panel
        JPanel filterPanel = new JPanel(new BorderLayout());
        filterPanel.setBackground(Color.WHITE);

        // Tạo font cho title lọc lịch sử
        java.awt.Font filterTitleFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 16);
        TitledBorder filterBorder = new TitledBorder(new LineBorder(new Color(200, 200, 200)), "Lọc lịch sử đặt vé");
        filterBorder.setTitleFont(filterTitleFont);
        filterBorder.setTitleColor(new Color(44, 62, 80));

        filterPanel.setBorder(new CompoundBorder(
            filterBorder,
            new EmptyBorder(15, 10, 15, 10)
        ));

        JPanel filterInputPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        filterInputPanel.setBackground(Color.WHITE);

        // Font cho labels và input fields
        java.awt.Font labelFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 14);
        java.awt.Font inputFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 14);

        // Date filter
        JPanel datePanel = new JPanel(new BorderLayout());
        datePanel.setBackground(Color.WHITE);
        JLabel dateLabel = new JLabel("Ngày đặt vé:");
        dateLabel.setFont(labelFont);
        datePanel.add(dateLabel, BorderLayout.NORTH);
        JTextField dateField = new JTextField();
        dateField.setFont(inputFont);
        dateField.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        datePanel.add(dateField, BorderLayout.CENTER);

        // Customer filter
        JPanel customerPanel = new JPanel(new BorderLayout());
        customerPanel.setBackground(Color.WHITE);
        JLabel customerLabel = new JLabel("Khách hàng:");
        customerLabel.setFont(labelFont);
        customerPanel.add(customerLabel, BorderLayout.NORTH);
        JComboBox<String> customerFilterBox = new JComboBox<>();
        customerFilterBox.setFont(inputFont);
        customerFilterBox.addItem("Tất cả khách hàng");
        List<String> customers = bookingController.loadCustomers();
        for (String customer : customers) {
            customerFilterBox.addItem(customer);
        }
        customerPanel.add(customerFilterBox, BorderLayout.CENTER);

        // Status filter
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(Color.WHITE);
        JLabel statusLabel = new JLabel("Trạng thái:");
        statusLabel.setFont(labelFont);
        statusPanel.add(statusLabel, BorderLayout.NORTH);
        JComboBox<String> statusFilterBox = new JComboBox<>();
        statusFilterBox.setFont(inputFont);
        statusFilterBox.addItem("Tất cả trạng thái");
        statusFilterBox.addItem("Chờ xác nhận");
        statusFilterBox.addItem("Đã xác nhận");
        statusFilterBox.addItem("Đã hủy");
        statusPanel.add(statusFilterBox, BorderLayout.CENTER);

        filterInputPanel.add(datePanel);
        filterInputPanel.add(customerPanel);
        filterInputPanel.add(statusPanel);
        
        JPanel filterButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterButtonPanel.setBackground(Color.WHITE);
        
        JButton applyFilterButton = new JButton("Áp dụng");
        stylizeButton(applyFilterButton, primaryColor);
        applyFilterButton.addActionListener(e -> {
            // Lấy giá trị lọc
            String date = dateField.getText().trim();
            String customer = (String) customerFilterBox.getSelectedItem();
            String status = (String) statusFilterBox.getSelectedItem();
            // Định dạng lại ngày về yyyy-MM-dd nếu cần
            String sqlDate = "";
            if (!date.isEmpty()) {
                try {
                    java.util.Date utilDate = new SimpleDateFormat("dd/MM/yyyy").parse(date);
                    sqlDate = new SimpleDateFormat("yyyy-MM-dd").format(utilDate);
                } catch (Exception ex) {
                    sqlDate = "";
                }
            }
            // Gọi hàm tìm kiếm
            List<Object[]> historyData = bookingController.searchBookingHistory(sqlDate, customer, status);
            DefaultTableModel model1 = (DefaultTableModel) historyTable.getModel();
            model1.setRowCount(0);
            for (Object[] row : historyData) {
                // Chuyển đổi trạng thái sang tiếng Việt
                String st = (row[5] != null) ? row[5].toString() : "";
                String displayStatus;
                switch (st) {
                    case "pending":
                        displayStatus = "Chờ xác nhận";
                        break;
                    case "confirmed":
                        displayStatus = "Đã xác nhận";
                        break;
                    case "cancelled":
                        displayStatus = "Đã hủy";
                        break;
                    case "paid":
                        displayStatus = "Đã thanh toán";
                        break;
                    default:
                        displayStatus = st;
                }
                Object[] displayRow = new Object[] {
                    row[0], row[1], row[2], row[3], row[4], displayStatus, null
                };
                model1.addRow(displayRow);
            }
        });
        
        JButton resetFilterButton = new JButton("Đặt lại");
        stylizeButton(resetFilterButton, new Color(150, 150, 150));
        resetFilterButton.addActionListener(e -> {
            dateField.setText(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            customerFilterBox.setSelectedIndex(0);
            statusFilterBox.setSelectedIndex(0);
            refreshHistoryTable();
        });
        
        filterButtonPanel.add(resetFilterButton);
        filterButtonPanel.add(applyFilterButton);
        
        filterPanel.add(filterInputPanel, BorderLayout.CENTER);
        filterPanel.add(filterButtonPanel, BorderLayout.SOUTH);
        
        // History table
        String[] columns = {"Mã đặt vé", "Ngày giờ đặt", "Khách hàng", "Số lượng vé", "Tổng tiền", "Trạng thái", "Thao tác"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only action column is editable
            }
        };
        
        historyTable = new JTable(model);
        historyTable.setRowHeight(35);
        historyTable.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 13));
        historyTable.getTableHeader().setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 13));
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Set custom renderer for status column
        historyTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer());
        
        // Add action buttons to the last column
        historyTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        historyTable.getColumnModel().getColumn(6).setCellEditor(new HistoryButtonEditor(new JCheckBox()));
        
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(200);
        historyTable.getColumnModel().getColumn(6).setMinWidth(180);
        historyTable.getColumnModel().getColumn(6).setMaxWidth(250);
        
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(new LineBorder(new Color(200, 200, 200)));
        
        // Load initial data
        refreshHistoryTable();
        
        // Add components to main panel
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private JPanel createEmailTicketPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Search booking panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);

        // Tạo font cho title tìm đơn đặt vé
        java.awt.Font searchTitleFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 16);
        TitledBorder searchBorder = new TitledBorder(new LineBorder(new Color(200, 200, 200)), "Tìm đơn đặt vé");
        searchBorder.setTitleFont(searchTitleFont);
        searchBorder.setTitleColor(new Color(44, 62, 80));

        searchPanel.setBorder(new CompoundBorder(
            searchBorder,
            new EmptyBorder(15, 10, 15, 10)
        ));

        JPanel searchInputPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        searchInputPanel.setBackground(Color.WHITE);

        // Font cho labels và input fields
        java.awt.Font labelFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 14);
        java.awt.Font inputFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 14);
        java.awt.Font buttonFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 14);

        JPanel bookingIdPanel = new JPanel(new BorderLayout());
        bookingIdPanel.setBackground(Color.WHITE);
        JLabel bookingIdLabel = new JLabel("Mã đặt vé:");
        bookingIdLabel.setFont(labelFont);
        bookingIdPanel.add(bookingIdLabel, BorderLayout.NORTH);
        JTextField bookingIdField = new JTextField();
        bookingIdField.setFont(inputFont);
        bookingIdPanel.add(bookingIdField, BorderLayout.CENTER);

        JPanel phonePanel = new JPanel(new BorderLayout());
        phonePanel.setBackground(Color.WHITE);
        JLabel phoneLabel = new JLabel("Số điện thoại:");
        phoneLabel.setFont(labelFont);
        phonePanel.add(phoneLabel, BorderLayout.NORTH);
        JTextField phoneField = new JTextField();
        phoneField.setFont(inputFont);
        phonePanel.add(phoneField, BorderLayout.CENTER);

        searchInputPanel.add(bookingIdPanel);
        searchInputPanel.add(phonePanel);

        JPanel searchButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchButtonPanel.setBackground(Color.WHITE);

        JButton searchButton = new JButton("Tìm kiếm");
        searchButton.setFont(buttonFont);
        stylizeButton(searchButton, primaryColor);
        searchButton.addActionListener(e -> {
            String bookingId = bookingIdField.getText().trim();
            String phone = phoneField.getText().trim();
            
            if (bookingId.isEmpty() && phone.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "Vui lòng nhập mã đặt vé hoặc số điện thoại!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Tìm kiếm thông tin đặt vé
            Object[] bookingInfo = bookingController.searchBookingForPreview(bookingId, phone);
            if (bookingInfo != null) {
                // Cập nhật giao diện xem trước vé
                updateTicketPreview(bookingInfo);
                // Cập nhật email field nếu có
                if (bookingInfo[6] != null) {
                    emailField.setText(bookingInfo[6].toString());
                }
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Không tìm thấy thông tin đặt vé!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        searchButtonPanel.add(searchButton);

        searchPanel.add(searchInputPanel, BorderLayout.CENTER);
        searchPanel.add(searchButtonPanel, BorderLayout.SOUTH);

        // Ticket preview and send panel
        JPanel previewPanel = new JPanel(new BorderLayout());
        previewPanel.setBackground(Color.WHITE);

        // Tạo font cho title xem trước và gửi vé
        java.awt.Font previewTitleFont = new java.awt.Font("Arial", java.awt.Font.PLAIN, 16);
        TitledBorder previewBorder = new TitledBorder(new LineBorder(new Color(200, 200, 200)), "Xem trước và gửi vé");
        previewBorder.setTitleFont(previewTitleFont);
        previewBorder.setTitleColor(new Color(44, 62, 80));

        previewPanel.setBorder(new CompoundBorder(
            previewBorder,
            new EmptyBorder(15, 10, 15, 10)
        ));

        // Split into preview and send sections
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(600);

        // Preview section
        JPanel ticketPreviewPanel = new JPanel(new BorderLayout());
        ticketPreviewPanel.setBackground(Color.WHITE);

        JLabel previewLabel = new JLabel("Xem trước vé:");
        previewLabel.setFont(labelFont);
        ticketPreviewPanel.add(previewLabel, BorderLayout.NORTH);

        // Initialize ticket panel
        ticketPanel = new JPanel();
        ticketPanel.setLayout(new BoxLayout(ticketPanel, BoxLayout.Y_AXIS));
        ticketPanel.setBackground(Color.WHITE);
        JScrollPane ticketScrollPane = new JScrollPane(ticketPanel);
        ticketScrollPane.setBorder(new LineBorder(new Color(200, 200, 200)));

        ticketPreviewPanel.add(ticketScrollPane, BorderLayout.CENTER);

        // Send section
        JPanel sendOptionsPanel = new JPanel();
        sendOptionsPanel.setLayout(new BoxLayout(sendOptionsPanel, BoxLayout.Y_AXIS));
        sendOptionsPanel.setBackground(Color.WHITE);
        sendOptionsPanel.setBorder(new EmptyBorder(0, 15, 0, 0));

        JLabel sendLabel = new JLabel("Tùy chọn gửi vé:");
        sendLabel.setFont(labelFont);
        sendLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel emailPanel = new JPanel(new BorderLayout());
        emailPanel.setBackground(Color.WHITE);
        emailPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        emailPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        emailPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel emailLabel = new JLabel("Email khách hàng:");
        emailLabel.setFont(labelFont);
        emailPanel.add(emailLabel, BorderLayout.NORTH);
        emailField = new JTextField("customer@example.com");
        emailField.setFont(inputFont);
        emailPanel.add(emailField, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        JButton sendEmailBtn = new JButton("Gửi qua Email");
        stylizeButton(sendEmailBtn, primaryColor);
        updateEmailButtonListener(sendEmailBtn);
        
        JButton printBtn = new JButton("In vé");
        stylizeButton(printBtn, purple);
        
        JButton saveBtn = new JButton("Lưu vé (PDF)");
        stylizeButton(saveBtn, accentColor);
        saveBtn.addActionListener(e -> saveTicketAsPdf());
        
        buttonPanel.add(sendEmailBtn);
        buttonPanel.add(printBtn);
        buttonPanel.add(saveBtn);
        
        sendOptionsPanel.add(sendLabel);
        sendOptionsPanel.add(emailPanel);
        sendOptionsPanel.add(Box.createVerticalStrut(10));
        sendOptionsPanel.add(buttonPanel);
        sendOptionsPanel.add(Box.createVerticalGlue());
        
        splitPane.setLeftComponent(ticketPreviewPanel);
        splitPane.setRightComponent(sendOptionsPanel);
        
        previewPanel.add(splitPane, BorderLayout.CENTER);
        
        // Add components to main panel
        panel.add(searchPanel, BorderLayout.NORTH);
        panel.add(previewPanel, BorderLayout.CENTER);
        
        return panel;
    }

    private void updateSeatMap() {
        String selectedMovie = (String) movieComboBox.getSelectedItem();
        String selectedShowtime = (String) showtimeComboBox.getSelectedItem();
        
        if (selectedMovie == null) {
            return;
        }
        
        // Lấy thông tin phim
        Movie movie = bookingController.getMovieByTitle(selectedMovie);
        if (movie == null) {
            return;
        }
        
        // Clear existing seat map
        Component[] components = seatSelectionPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                seatSelectionPanel.remove(component);
            }
        }
        
        // Nếu không có suất chiếu được chọn, hiển thị thông báo
        if (selectedShowtime == null) {
            JPanel messagePanel = new JPanel(new BorderLayout());
            messagePanel.setBackground(Color.WHITE);
            
            JLabel messageLabel = new JLabel("Chưa có lịch chiếu", JLabel.CENTER);
            messageLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 16));
            messageLabel.setForeground(accentColor);
            
            messagePanel.add(messageLabel, BorderLayout.CENTER);
            seatSelectionPanel.add(messagePanel, BorderLayout.CENTER);
            seatSelectionPanel.revalidate();
            seatSelectionPanel.repaint();
            return;
        }
        
        // Lấy thông tin suất chiếu
        Showtime showtime = bookingController.getShowtimeByMovieAndTime(movie.getMovieId(), selectedShowtime);
        if (showtime == null) {
            return;
        }

        // Cập nhật giá vé từ base_price của suất chiếu
        pricePerSeat = showtime.getBasePrice().doubleValue();
        updatePriceDisplay();
        
        // Lấy thông tin phòng và ghế
        Room room = bookingController.getRoomById(showtime.getRoomId());
        if (room == null) {
            return;
        }
        
        // Lấy danh sách ghế của phòng
        List<Seat> seats = bookingController.getSeatsByRoomId(room.getRoomId());
        if (seats == null || seats.isEmpty()) {
            return;
        }
        
        // Lấy trạng thái các ghế
        Map<String, String> seatStatus = bookingController.getSeatStatus(room.getRoomId(), showtime.getShowtimeId());
        
        // Tạo lưới ghế mới
        JPanel seatGrid = new JPanel(new GridLayout(8, 10, 5, 5));
        seatGrid.setBackground(Color.WHITE);
        
        // Tạo các nút ghế dựa trên danh sách ghế thực tế của phòng
        for (Seat seat : seats) {
            String seatNumber = seat.getSeatNumber();
            String status = seatStatus.getOrDefault(seatNumber, "available");

            boolean isSelected = selectedSeats.contains(seatNumber);
            boolean isBooked = "booked".equals(status);
            boolean isSold = "sold".equals(status);

            JButton seatButton = createSeatButton(seatNumber, isSelected, isBooked, isSold);

            // Chỉ cho phép chọn ghế trống
            if ("available".equals(status)) {
                seatButton.addActionListener(e -> toggleSeatSelection(seatButton, seatNumber));
            } else {
                seatButton.setEnabled(false);
            }

            seatGrid.add(seatButton);
        }
        
        seatSelectionPanel.add(seatGrid, BorderLayout.CENTER);
        seatSelectionPanel.revalidate();
        seatSelectionPanel.repaint();
    }
    
    private void updatePriceDisplay() {
        priceLabel.setText(String.format("Giá vé: %.0fđ/ghế", pricePerSeat));
        updateSelectedSeatsLabel();
    }

    private void toggleSeatSelection(JButton seatButton, String seatNumber) {
        String selectedMovie = (String) movieComboBox.getSelectedItem();
        String selectedShowtime = (String) showtimeComboBox.getSelectedItem();
        
        if (selectedMovie == null || selectedShowtime == null) {
            return;
        }
        
        Movie movie = bookingController.getMovieByTitle(selectedMovie);
        Showtime showtime = bookingController.getShowtimeByMovieAndTime(movie.getMovieId(), selectedShowtime);
        Room room = bookingController.getRoomById(showtime.getRoomId());
        
        if (selectedSeats.contains(seatNumber)) {
            // Hủy chọn ghế
            selectedSeats.remove(seatNumber);
            seatButton.setBackground(lightGreen);
            bookingController.cancelSeat(room.getRoomId(), showtime.getShowtimeId(), seatNumber);
        } else {
            // Chọn ghế mới
            if (bookingController.bookSeat(room.getRoomId(), showtime.getShowtimeId(), seatNumber)) {
                selectedSeats.add(seatNumber);
                seatButton.setBackground(secondaryColor);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Ghế này không thể đặt được!", 
                    "Thông báo", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
        updateSelectedSeatsLabel();
    }

    private void updateSelectedSeatsLabel() {
        selectedSeatsLabel.setText("Ghế đã chọn: " + String.join(", ", selectedSeats));
        double total = selectedSeats.size() * pricePerSeat;
        totalPriceLabel.setText(String.format("Tổng tiền: %.0fđ", total));
    }

    private void clearSeatSelection() {
        selectedSeats.clear();
        updateSelectedSeatsLabel();
        updateSeatMap();
    }

    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Thêm khách hàng mới", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(Color.WHITE);

        formPanel.add(new JLabel("Họ tên:"));
        JTextField nameField = new JTextField();
        formPanel.add(nameField);

        formPanel.add(new JLabel("Số điện thoại:"));
        JTextField phoneField = new JTextField();
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email:"));
        JTextField emailField = new JTextField();
        formPanel.add(emailField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton saveButton = new JButton("Lưu");
        stylizeButton(saveButton, primaryColor);
        saveButton.addActionListener(e -> {
            if (bookingController.addCustomer(nameField.getText(), phoneField.getText(), emailField.getText())) {
                customerComboBox.addItem(nameField.getText());
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Không thể thêm khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelButton = new JButton("Hủy");
        stylizeButton(cancelButton, new Color(150, 150, 150));
        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void bookTickets() {
        if (selectedSeats.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một ghế!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String customerName = (String) customerComboBox.getSelectedItem();
        if (customerName == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String movieTitle = (String) movieComboBox.getSelectedItem();
        String showtimeInfo = (String) showtimeComboBox.getSelectedItem();
        
        if (movieTitle == null || showtimeInfo == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phim và suất chiếu!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy thông tin phim và suất chiếu
        Movie movie = bookingController.getMovieByTitle(movieTitle);
        Showtime showtime = bookingController.getShowtimeByMovieAndTime(movie.getMovieId(), showtimeInfo);
        Room room = bookingController.getRoomById(showtime.getRoomId());
        
        // Lấy thông tin khách hàng
        Customer customer = bookingController.getCustomerByName(customerName);
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin khách hàng!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lấy thông tin người dùng đang đăng nhập
        int currentUserId = bookingController.getCurrentUserId();
        if (currentUserId == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng đăng nhập để đặt vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo booking mới
        Booking booking = new Booking();
        booking.setCustomerId(customer.getCustomerId());
        booking.setUserId(currentUserId);
        booking.setBookingTime(new Date());
        booking.setStatus("pending");

        // Thêm booking vào database
        int bookingId = bookingController.createBooking(booking);
        if (bookingId == -1) {
            JOptionPane.showMessageDialog(this, "Không thể tạo đơn đặt vé!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Tạo vé cho từng ghế đã chọn
        boolean success = true;
        for (String seatNumber : selectedSeats) {
            // Lấy seat_id từ seat_number
            int seatId = bookingController.getSeatIdByNumber(room.getRoomId(), seatNumber);
            if (seatId == -1) {
                success = false;
                break;
            }

            // Tạo vé mới
            int ticketId = bookingController.createTicket(showtime.getShowtimeId(), seatId, pricePerSeat);
            if (ticketId == -1) {
                success = false;
                break;
            }

            // Tạo booking detail
            BookingDetail detail = new BookingDetail();
            detail.setBookingId(bookingId);
            detail.setTicketId(ticketId);
            detail.setUnitPrice(pricePerSeat);

            if (!bookingController.createBookingDetail(detail)) {
                success = false;
                break;
            }
        }

        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Đặt vé thành công!\nMã đặt vé: " + bookingId, 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);
            clearSeatSelection();
            refreshData(); // Cập nhật lại dữ liệu
        } else {
            // Nếu có lỗi, xóa booking đã tạo
            bookingController.deleteBooking(bookingId);
            JOptionPane.showMessageDialog(this, 
                "Có lỗi xảy ra khi đặt vé. Vui lòng thử lại!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMockTicketData(DefaultTableModel model) {
        String[][] data = {
            {"T001", "Avengers: Endgame", "09:00 01/01/2024", "A1", "Nguyễn Văn A", "0123456789", "85,000đ", "Đã xác nhận"},
            {"T002", "Spider-Man", "12:00 01/01/2024", "B3", "Trần Thị B", "0987654321", "85,000đ", "Chờ xác nhận"},
            {"T003", "The Batman", "15:00 01/01/2024", "C5", "Lê Văn C", "0123987456", "85,000đ", "Đã hủy"}
        };

        for (String[] row : data) {
            model.addRow(row);
        }
    }

    private void addMockHistoryData(DefaultTableModel model) {
        String[][] data = {
            {"B001", "01/01/2024 09:00", "Nguyễn Văn A", "Hoàng Thị D", "2", "170,000đ", "Tiền mặt", "Đã xác nhận"},
            {"B002", "01/01/2024 12:00", "Trần Thị B", "Ngô Văn E", "1", "85,000đ", "Chuyển khoản", "Chờ xác nhận"},
            {"B003", "01/01/2024 15:00", "Lê Văn C", "Hoàng Thị D", "3", "255,000đ", "Tiền mặt", "Đã hủy"}
        };

        for (String[] row : data) {
            model.addRow(row);
        }
    }

    private void saveTicketAsPdf() {
        try {
            // Tạo file PDF
            Document document = new Document();
            String fileName = "ticket_" + System.currentTimeMillis() + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(fileName));
            document.open();

            // Thêm logo và thông tin rạp
            // Load a Unicode font that supports Vietnamese characters
            BaseFont bfUnicode = null;
            boolean fontLoaded = false;
            try {
                // Assuming arial.ttf is in the project root or a known path
                bfUnicode = BaseFont.createFont("arial.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                fontLoaded = true;
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback to a standard font (will likely not support Vietnamese)
                try {
                    bfUnicode = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
                } catch (Exception ex) {
                    ex.printStackTrace();
                     // If even Helvetica fails, something is seriously wrong
                    JOptionPane.showMessageDialog(this, 
                        "Lỗi nghiêm trọng khi tải font PDF.", 
                        "Lỗi Font", 
                        JOptionPane.ERROR_MESSAGE);
                    return; // Exit the method if no font can be loaded
                }
            }

            // Inform the user if the preferred font failed to load
            if (!fontLoaded) {
                 JOptionPane.showMessageDialog(this, 
                     "Không tìm thấy hoặc không tải được font 'arial.ttf'.\n" +
                     "Ký tự tiếng Việt có thể không hiển thị đúng.", 
                     "Cảnh báo Font", 
                     JOptionPane.WARNING_MESSAGE);
            }

            com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(bfUnicode, 24, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(bfUnicode, 12, com.itextpdf.text.Font.NORMAL);
            com.itextpdf.text.Font contentFont = new com.itextpdf.text.Font(bfUnicode, 10, com.itextpdf.text.Font.NORMAL);

            Paragraph title = new Paragraph("CINEMA STAR", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            Paragraph address = new Paragraph("Ngách 25/22 Đường Phú Minh, Minh Khai, Bắc Từ Liêm, Hà Nội", headerFont);
            address.setAlignment(Element.ALIGN_CENTER);
            document.add(address);

            document.add(new Paragraph("\n"));

            // Thêm thông tin vé từ biến currentBookingInfo
            if (currentBookingInfo != null && currentBookingInfo.length >= 8) {
                 PdfPTable table = new PdfPTable(2);
                 table.setWidthPercentage(100);
                 table.setSpacingBefore(10f);
                 table.setSpacingAfter(10f);

                 addTicketDetailRow(table, "Mã đặt vé:", currentBookingInfo[0].toString(), contentFont);
                 addTicketDetailRow(table, "Phim:", currentBookingInfo[1].toString(), contentFont);
                 addTicketDetailRow(table, "Suất chiếu:", currentBookingInfo[2].toString(), contentFont);
                 addTicketDetailRow(table, "Phòng:", currentBookingInfo[3].toString(), contentFont);
                 addTicketDetailRow(table, "Ghế:", currentBookingInfo[4].toString(), contentFont);
                 addTicketDetailRow(table, "Khách hàng:", currentBookingInfo[5].toString(), contentFont);
                 // currentBookingInfo[6] is email, not needed in PDF content directly
                 addTicketDetailRow(table, "Tổng tiền:", currentBookingInfo[7].toString(), contentFont);

                 document.add(table);

                

            } else {
                 document.add(new Paragraph("Không có thông tin vé để hiển thị.", contentFont));
            }

            document.close();

            JOptionPane.showMessageDialog(this, 
                "Đã lưu vé thành công!\nFile: " + fileName, 
                "Thông báo", 
                JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, 
                "Có lỗi xảy ra khi tạo file PDF!", 
                "Lỗi", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void sendTicketByEmail() {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Vui lòng nhập địa chỉ email khách hàng!",
                "Thông báo",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // WARNING: Storing credentials directly in code is a security risk.
        // Consider using a configuration file or other secure methods.
        String sendingEmailAddress = "kousei2k4@gmail.com"; // Thay thế bằng email gửi đi thật
        String sendingEmailPassword = "rypnpzbwgqhchtae"; // Thay thế bằng mật khẩu hoặc App Password thật

        try {
            // Cấu hình email
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.port", "465");

            // Tạo session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(sendingEmailAddress, sendingEmailPassword);
                }
            });

            try {
                // Tạo message
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sendingEmailAddress));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
                message.setSubject("Vé xem phim - Cinema Star");

                // Tạo nội dung email từ currentBookingInfo
                String emailContent = "Kính gửi quý khách,\n\n";
                emailContent += "Cảm ơn quý khách đã đặt vé tại Cinema Star.\n";
                emailContent += "Dưới đây là thông tin vé của quý khách:\n\n";

                if (currentBookingInfo != null && currentBookingInfo.length >= 8) {
                    emailContent += String.format("Mã đặt vé: %s\n", currentBookingInfo[0]);
                    emailContent += String.format("Phim: %s\n", currentBookingInfo[1]);
                    emailContent += String.format("Suất chiếu: %s\n", currentBookingInfo[2]);
                    emailContent += String.format("Phòng: %s\n", currentBookingInfo[3]);
                    emailContent += String.format("Ghế: %s\n", currentBookingInfo[4]);
                    emailContent += String.format("Khách hàng: %s\n", currentBookingInfo[5]);
                    emailContent += String.format("Tổng tiền: %s\n", currentBookingInfo[7]);
                } else {
                    emailContent += "Không thể lấy thông tin chi tiết vé.\n";
                }

                emailContent += "\nTrân trọng,\nCinema Star";

                message.setText(emailContent);

                // Gửi email
                Transport.send(message);

                JOptionPane.showMessageDialog(this,
                    "Đã gửi vé thành công!",
                    "Thông báo",
                    JOptionPane.INFORMATION_MESSAGE);

            } catch (MessagingException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Có lỗi xảy ra khi gửi email!\nVui lòng kiểm tra cấu hình tài khoản gửi và kết nối mạng.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Có lỗi xảy ra khi cấu hình hoặc gửi email!",
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    // Cập nhật action listener cho nút gửi email
    private void updateEmailButtonListener(JButton sendEmailBtn) {
        sendEmailBtn.addActionListener(e -> sendTicketByEmail());
    }

    private void updateTicketPreview(Object[] bookingInfo) {
        // Gán thông tin vé vào biến member
        this.currentBookingInfo = bookingInfo;

        // Xóa panel cũ
        Component[] components = ticketPanel.getComponents();
        for (Component component : components) {
            ticketPanel.remove(component);
        }
        
        // Tạo panel mới với thông tin vé
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new CompoundBorder(
            new LineBorder(new Color(200, 200, 200), 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        // Cinema logo and info
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JLabel logoLabel = new JLabel("CINEMA STAR");
        logoLabel.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 24));
        logoLabel.setForeground(primaryColor);

        JLabel addressLabel = new JLabel("Ngách 25/22 Đường Phú Minh, Minh Khai, Bắc Từ Liêm, Hà Nội");
        addressLabel.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));
        addressLabel.setForeground(darkColor);

        headerPanel.add(logoLabel, BorderLayout.NORTH);
        headerPanel.add(addressLabel, BorderLayout.SOUTH);

        // Ticket details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBackground(Color.WHITE);
        detailsPanel.setBorder(new EmptyBorder(20, 0, 20, 0));

        String[][] details = {
            {"Mã vé:", bookingInfo[0].toString()},
            {"Phim:", bookingInfo[1].toString()},
            {"Suất chiếu:", bookingInfo[2].toString()},
            {"Phòng:", bookingInfo[3].toString()},
            {"Ghế:", bookingInfo[4].toString()},
            {"Khách hàng:", bookingInfo[5].toString()},
            {"Tổng tiền:", bookingInfo[7].toString()}
        };

        for (String[] detail : details) {
            JPanel rowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            rowPanel.setBackground(Color.WHITE);
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25));

            JLabel label = new JLabel(detail[0]);
            label.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
            label.setPreferredSize(new Dimension(100, 20));

            JLabel value = new JLabel(detail[1]);
            value.setFont(new java.awt.Font("Arial", java.awt.Font.PLAIN, 12));

            rowPanel.add(label);
            rowPanel.add(value);
            detailsPanel.add(rowPanel);
        }

        // QR Code placeholder
        JPanel qrPanel = new JPanel();
        qrPanel.setBackground(Color.WHITE);
        qrPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        qrPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel qrLabel = new JLabel("");
        qrLabel.setFont(new java.awt.Font("Arial", java.awt.Font.ITALIC, 12));
        qrLabel.setForeground(new Color(150, 150, 150));
        qrPanel.add(qrLabel);

        panel.add(headerPanel);
        panel.add(detailsPanel);
        panel.add(qrPanel);

        // Cập nhật panel
        ticketPanel.add(panel);
        ticketPanel.revalidate();
        ticketPanel.repaint();
    }

    // Helper method to add a row to the PDF table
    private void addTicketDetailRow(PdfPTable table, String label, String value, com.itextpdf.text.Font font) {
        PdfPCell labelCell = new PdfPCell(new Phrase(label, font));
        PdfPCell valueCell = new PdfPCell(new Phrase(value, font));
        labelCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        valueCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
        table.addCell(labelCell);
        table.addCell(valueCell);
    }
}