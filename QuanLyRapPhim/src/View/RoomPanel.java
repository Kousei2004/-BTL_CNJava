package View;

import Controller.RoomController;
import Model.Room;
import Model.Seat;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class RoomPanel extends JPanel {
    // Màu sắc chính của ứng dụng
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color accentColor = new Color(231, 76, 60);
    private Color bgColor = new Color(236, 240, 241);
    private Color darkColor = new Color(44, 62, 80);
    private Color lightTextColor = Color.WHITE;
    private Color menuBgColor = new Color(52, 73, 94);
    
    // Component cho panel phòng chiếu
    private JTable roomTable;
    private DefaultTableModel roomTableModel;
    private JTextField roomNameField;
    private JTextField totalSeatsField;
    private JButton addRoomButton;
    private JButton updateRoomButton;
    private JButton deleteRoomButton;
    private JButton clearRoomFormButton;
    
    // Component cho panel ghế ngồi
    private JTable seatTable;
    private DefaultTableModel seatTableModel;
    private JComboBox<String> roomComboBox;
    private JTextField seatNumberField;
    private JButton addSeatButton;
    private JButton updateSeatButton;
    private JButton deleteSeatButton;
    private JButton clearSeatFormButton;
    private JButton generateSeatsButton;
    
    private RoomController roomController;
    
    public RoomPanel() {
        try {
            roomController = new RoomController();
            setLayout(new BorderLayout());
            setBackground(bgColor);
            
            // Tạo panel tiêu đề
            JPanel titlePanel = createTitlePanel();
            add(titlePanel, BorderLayout.NORTH);
            
            // Tạo tabbed pane để chứa quản lý phòng và ghế
            JTabbedPane tabbedPane = new JTabbedPane();
            tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));
            tabbedPane.setBackground(Color.WHITE);
            
            // Tạo panel quản lý phòng
            JPanel roomManagementPanel = createRoomManagementPanel();
            tabbedPane.addTab("Quản lý phòng chiếu", roomManagementPanel);
            
            // Tạo panel quản lý ghế
            JPanel seatManagementPanel = createSeatManagementPanel();
            tabbedPane.addTab("Quản lý ghế ngồi", seatManagementPanel);
            
            add(tabbedPane, BorderLayout.CENTER);
            
            // Add event listeners
            addEventListeners();
            
            // Load initial data
            loadRoomData();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Không thể khởi tạo RoomPanel: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private JPanel createTitlePanel() {
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(bgColor);
        titlePanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Quản lý phòng chiếu & ghế ngồi");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(darkColor);
        
        titlePanel.add(titleLabel);
        
        return titlePanel;
    }
    
    private JPanel createRoomManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Panel form nhập liệu phòng chiếu
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JPanel formContentPanel = new JPanel(new GridBagLayout());
        formContentPanel.setBackground(Color.WHITE);
        formContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Tiêu đề form
        GridBagConstraints gbcTitle = new GridBagConstraints();
        gbcTitle.fill = GridBagConstraints.HORIZONTAL;
        gbcTitle.insets = new Insets(5, 5, 5, 5);
        gbcTitle.gridx = 0;
        gbcTitle.gridy = 0;
        gbcTitle.gridwidth = 2;
        JLabel formTitle = new JLabel("Thông tin phòng chiếu");
        formTitle.setFont(new Font("Arial", Font.BOLD, 16));
        formContentPanel.add(formTitle, gbcTitle);
        
        // Nhãn và trường nhập tên phòng
        GridBagConstraints gbcRoomNameLabel = new GridBagConstraints();
        gbcRoomNameLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcRoomNameLabel.insets = new Insets(5, 5, 5, 5);
        gbcRoomNameLabel.gridx = 0;
        gbcRoomNameLabel.gridy = 1;
        gbcRoomNameLabel.gridwidth = 1;
        JLabel roomNameLabel = new JLabel("Tên phòng:");
        roomNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formContentPanel.add(roomNameLabel, gbcRoomNameLabel);
        
        GridBagConstraints gbcRoomNameField = new GridBagConstraints();
        gbcRoomNameField.fill = GridBagConstraints.HORIZONTAL;
        gbcRoomNameField.insets = new Insets(5, 5, 5, 5);
        gbcRoomNameField.gridx = 1;
        gbcRoomNameField.gridy = 1;
        roomNameField = new JTextField(20);
        roomNameField.setFont(new Font("Arial", Font.PLAIN, 14));
        formContentPanel.add(roomNameField, gbcRoomNameField);
        
        // Nhãn và trường nhập số ghế
        GridBagConstraints gbcTotalSeatsLabel = new GridBagConstraints();
        gbcTotalSeatsLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcTotalSeatsLabel.insets = new Insets(5, 5, 5, 5);
        gbcTotalSeatsLabel.gridx = 0;
        gbcTotalSeatsLabel.gridy = 2;
        JLabel totalSeatsLabel = new JLabel("Tổng số ghế:");
        totalSeatsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        formContentPanel.add(totalSeatsLabel, gbcTotalSeatsLabel);
        
        GridBagConstraints gbcTotalSeatsField = new GridBagConstraints();
        gbcTotalSeatsField.fill = GridBagConstraints.HORIZONTAL;
        gbcTotalSeatsField.insets = new Insets(5, 5, 5, 5);
        gbcTotalSeatsField.gridx = 1;
        gbcTotalSeatsField.gridy = 2;
        totalSeatsField = new JTextField(20);
        totalSeatsField.setFont(new Font("Arial", Font.PLAIN, 14));
        formContentPanel.add(totalSeatsField, gbcTotalSeatsField);
        
        // Panel chứa các nút
        GridBagConstraints gbcButtonPanel = new GridBagConstraints();
        gbcButtonPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcButtonPanel.insets = new Insets(5, 5, 5, 5);
        gbcButtonPanel.gridx = 0;
        gbcButtonPanel.gridy = 3;
        gbcButtonPanel.gridwidth = 2;
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        clearRoomFormButton = createButton("Làm mới", new Color(155, 89, 182));
        addRoomButton = createButton("Thêm phòng", new Color(46, 204, 113));
        updateRoomButton = createButton("Cập nhật", new Color(52, 152, 219));
        deleteRoomButton = createButton("Xóa", accentColor);
        
        buttonPanel.add(clearRoomFormButton);
        buttonPanel.add(addRoomButton);
        buttonPanel.add(updateRoomButton);
        buttonPanel.add(deleteRoomButton);
        
        formContentPanel.add(buttonPanel, gbcButtonPanel);
        
        formPanel.add(formContentPanel, BorderLayout.CENTER);
        
        // Panel bảng danh sách phòng chiếu
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JLabel tableTitle = new JLabel("Danh sách phòng chiếu");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        tableTitle.setBorder(new EmptyBorder(15, 15, 15, 15));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        // Tạo model và bảng
        roomTableModel = new DefaultTableModel(
            new Object[][] {
                {1, "Phòng chiếu 1", 120},
                {2, "Phòng chiếu 2", 100},
                {3, "Phòng chiếu VIP", 80},
                {4, "Phòng chiếu 3D", 90}
            },
            new String[] {"ID", "Tên phòng", "Tổng số ghế"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        roomTable = new JTable(roomTableModel);
        roomTable.setFont(new Font("Arial", Font.PLAIN, 14));
        roomTable.setRowHeight(25);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        roomTable.getTableHeader().setBackground(darkColor);
        roomTable.getTableHeader().setForeground(Color.WHITE);
        roomTable.setGridColor(new Color(230, 230, 230));
        roomTable.setShowGrid(true);
        roomTable.setShowHorizontalLines(true);
        roomTable.setShowVerticalLines(true);
        
        // Tùy chỉnh header renderer để luôn hiển thị rõ
        roomTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(darkColor);
                c.setForeground(Color.WHITE);
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        // Tùy chỉnh độ rộng các cột
        roomTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        roomTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Tên phòng
        roomTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Tổng số ghế
        
        JScrollPane scrollPane = new JScrollPane(roomTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel tìm kiếm
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
        
        JLabel searchLabel = new JLabel("Tìm kiếm:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JTextField searchField = new JTextField(20);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton searchButton = createButton("Tìm", secondaryColor);
        
        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        tablePanel.add(searchPanel, BorderLayout.SOUTH);
        
        // Sắp xếp các panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(10);
        splitPane.setBorder(null);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSeatManagementPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(bgColor);
        panel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Panel form nhập liệu ghế ngồi
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JPanel formContentPanel = new JPanel(new GridBagLayout());
        formContentPanel.setBackground(Color.WHITE);
        formContentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Tiêu đề form
        JLabel formTitle = new JLabel("Thông tin ghế ngồi");
        formTitle.setFont(new Font("Arial", Font.BOLD, 16));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        formContentPanel.add(formTitle, gbc);
        
        // Nhãn và combobox chọn phòng
        JLabel roomLabel = new JLabel("Phòng chiếu:");
        roomLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        formContentPanel.add(roomLabel, gbc);
        
        roomComboBox = new JComboBox<>(new String[]{
            "1 - Phòng chiếu 1",
            "2 - Phòng chiếu 2",
            "3 - Phòng chiếu VIP",
            "4 - Phòng chiếu 3D"
        });
        roomComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 1;
        formContentPanel.add(roomComboBox, gbc);
        
        // Nhãn và trường nhập vị trí ghế
        JLabel seatNumberLabel = new JLabel("Vị trí ghế:");
        seatNumberLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 0;
        gbc.gridy = 2;
        formContentPanel.add(seatNumberLabel, gbc);
        
        seatNumberField = new JTextField(20);
        seatNumberField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        gbc.gridy = 2;
        formContentPanel.add(seatNumberField, gbc);
        
        // Panel chứa các nút
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        clearSeatFormButton = createButton("Làm mới", new Color(155, 89, 182));
        generateSeatsButton = createButton("Tạo ghế tự động", new Color(243, 156, 18));
        addSeatButton = createButton("Thêm ghế", new Color(46, 204, 113));
        updateSeatButton = createButton("Cập nhật", new Color(52, 152, 219));
        deleteSeatButton = createButton("Xóa", accentColor);
        
        buttonPanel.add(clearSeatFormButton);
        buttonPanel.add(generateSeatsButton);
        buttonPanel.add(addSeatButton);
        buttonPanel.add(updateSeatButton);
        buttonPanel.add(deleteSeatButton);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        formContentPanel.add(buttonPanel, gbc);
        
        formPanel.add(formContentPanel, BorderLayout.CENTER);
        
        // Panel bảng danh sách ghế ngồi
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JLabel tableTitle = new JLabel("Danh sách ghế ngồi");
        tableTitle.setFont(new Font("Arial", Font.BOLD, 16));
        tableTitle.setBorder(new EmptyBorder(15, 15, 15, 15));
        tablePanel.add(tableTitle, BorderLayout.NORTH);
        
        // Tạo model và bảng
        seatTableModel = new DefaultTableModel(
            new Object[][] {
                {1, 1, "Phòng chiếu 1", "A1", "available"},
                {2, 1, "Phòng chiếu 1", "A2", "booked"},
                {3, 1, "Phòng chiếu 1", "A3", "occupied"},
                {4, 1, "Phòng chiếu 1", "B1", "available"},
                {5, 1, "Phòng chiếu 1", "B2", "available"},
                {6, 2, "Phòng chiếu 2", "A1", "available"},
                {7, 2, "Phòng chiếu 2", "A2", "available"},
                {8, 3, "Phòng chiếu VIP", "VIP1", "available"},
                {9, 3, "Phòng chiếu VIP", "VIP2", "available"}
            },
            new String[] {"ID", "ID Phòng", "Tên phòng", "Vị trí ghế", "Trạng thái"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        seatTable = new JTable(seatTableModel);
        seatTable.setFont(new Font("Arial", Font.PLAIN, 14));
        seatTable.setRowHeight(25);
        seatTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        seatTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        seatTable.getTableHeader().setBackground(darkColor);
        seatTable.getTableHeader().setForeground(Color.WHITE);
        seatTable.setGridColor(new Color(230, 230, 230));
        seatTable.setShowGrid(true);
        seatTable.setShowHorizontalLines(true);
        seatTable.setShowVerticalLines(true);
        
        // Thêm renderer cho cột trạng thái
        seatTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = (String) value;
                if (status != null) {
                    switch (status.toLowerCase()) {
                        case "available":
                            c.setBackground(new Color(46, 204, 113)); // Màu xanh lá
                            c.setForeground(Color.WHITE);
                            break;
                        case "booked":
                            c.setBackground(new Color(243, 156, 18)); // Màu cam
                            c.setForeground(Color.WHITE);
                            break;
                        case "occupied":
                            c.setBackground(new Color(231, 76, 60)); // Màu đỏ
                            c.setForeground(Color.WHITE);
                            break;
                        default:
                            c.setBackground(table.getBackground());
                            c.setForeground(table.getForeground());
                    }
                }
                return c;
            }
        });
        
        // Tùy chỉnh header renderer để luôn hiển thị rõ
        seatTable.getTableHeader().setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(darkColor);
                c.setForeground(Color.WHITE);
                ((JLabel) c).setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });
        
        // Tùy chỉnh độ rộng các cột
        seatTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        seatTable.getColumnModel().getColumn(1).setPreferredWidth(80);  // ID Phòng
        seatTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Tên phòng
        seatTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Vị trí ghế
        seatTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Trạng thái
        
        JScrollPane scrollPane = new JScrollPane(seatTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Panel tìm kiếm và lọc
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(new EmptyBorder(10, 15, 15, 15));
        
        JLabel filterRoomLabel = new JLabel("Lọc theo phòng:");
        filterRoomLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JComboBox<String> filterRoomComboBox = new JComboBox<>(new String[]{
            "Tất cả",
            "Phòng chiếu 1",
            "Phòng chiếu 2",
            "Phòng chiếu VIP",
            "Phòng chiếu 3D"
        });
        filterRoomComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel searchLabel = new JLabel("Tìm ghế:");
        searchLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JTextField searchField = new JTextField(15);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JButton searchButton = createButton("Tìm", secondaryColor);
        
        filterPanel.add(filterRoomLabel);
        filterPanel.add(filterRoomComboBox);
        filterPanel.add(Box.createRigidArea(new Dimension(20, 0)));
        filterPanel.add(searchLabel);
        filterPanel.add(searchField);
        filterPanel.add(searchButton);
        
        tablePanel.add(filterPanel, BorderLayout.SOUTH);
        
        // Sắp xếp các panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, formPanel, tablePanel);
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(10);
        splitPane.setBorder(null);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createButton(String text, Color color) {
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
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }
    
    private void addEventListeners() {
        // Room management event listeners
        addRoomButton.addActionListener(e -> handleAddRoom());
        updateRoomButton.addActionListener(e -> handleUpdateRoom());
        deleteRoomButton.addActionListener(e -> handleDeleteRoom());
        clearRoomFormButton.addActionListener(e -> clearRoomForm());
        
        // Seat management event listeners
        addSeatButton.addActionListener(e -> handleAddSeat());
        updateSeatButton.addActionListener(e -> handleUpdateSeat());
        deleteSeatButton.addActionListener(e -> handleDeleteSeat());
        clearSeatFormButton.addActionListener(e -> clearSeatForm());
        generateSeatsButton.addActionListener(e -> handleGenerateSeats());
        
        // Table selection listeners
        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleRoomSelection();
            }
        });
        
        seatTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                handleSeatSelection();
            }
        });
        
        // Room combo box listener
        roomComboBox.addActionListener(e -> {
            String selectedRoom = (String) roomComboBox.getSelectedItem();
            if (selectedRoom != null) {
                int roomId = Integer.parseInt(selectedRoom.split(" - ")[0]);
                loadSeatData(roomId);
            }
        });
    }
    
    private void loadRoomData() {
        try {
            // Clear existing data
            roomTableModel.setRowCount(0);
            seatTableModel.setRowCount(0);
            
            // Load rooms
            List<Room> rooms = roomController.getAllRooms();
            for (Room room : rooms) {
                roomTableModel.addRow(new Object[]{
                    room.getRoomId(),
                    room.getRoomName(),
                    room.getTotalSeats()
                });
            }
            
            // Update room combo box
            updateRoomComboBox();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Không thể tải dữ liệu phòng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void updateRoomComboBox() {
        try {
            roomComboBox.removeAllItems();
            List<Room> rooms = roomController.getAllRooms();
            for (Room room : rooms) {
                roomComboBox.addItem(room.getRoomId() + " - " + room.getRoomName());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Không thể cập nhật danh sách phòng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleAddRoom() {
        try {
            String roomName = roomNameField.getText().trim();
            int totalSeats = Integer.parseInt(totalSeatsField.getText().trim());
            
            if (roomName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên phòng!");
                return;
            }
            
            Room newRoom = new Room(0, roomName, totalSeats);
            if (roomController.addRoom(newRoom)) {
                loadRoomData();
                clearRoomForm();
                JOptionPane.showMessageDialog(this, "Thêm phòng thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Tên phòng đã tồn tại!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số ghế hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi thêm phòng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleUpdateRoom() {
        try {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng cần cập nhật!");
                return;
            }
            
            int roomId = (int) roomTable.getValueAt(selectedRow, 0);
            String roomName = roomNameField.getText().trim();
            int totalSeats = Integer.parseInt(totalSeatsField.getText().trim());
            
            if (roomName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập tên phòng!");
                return;
            }
            
            // Get current room info
            Room currentRoom = roomController.getRoomById(roomId);
            if (currentRoom == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin phòng!");
                return;
            }
            
            // Check if reducing seats
            if (totalSeats < currentRoom.getTotalSeats()) {
                // Count booked seats
                List<Seat> seats = roomController.getSeatsByRoomId(roomId);
                int bookedSeats = 0;
                for (Seat seat : seats) {
                    if (seat.getStatus().equals("booked") || seat.getStatus().equals("occupied")) {
                        bookedSeats++;
                    }
                }
                
                if (bookedSeats > totalSeats) {
                    JOptionPane.showMessageDialog(this, 
                        String.format("Không thể giảm số ghế xuống %d vì có %d ghế đã được đặt chỗ!", 
                        totalSeats, bookedSeats));
                    return;
                }
            }
            
            // Check if room name already exists (excluding current room)
            List<Room> allRooms = roomController.getAllRooms();
            for (Room room : allRooms) {
                if (room.getRoomId() != roomId && room.getRoomName().equals(roomName)) {
                    JOptionPane.showMessageDialog(this, "Tên phòng đã tồn tại!");
                    return;
                }
            }
            
            Room updatedRoom = new Room(roomId, roomName, totalSeats);
            if (roomController.updateRoom(updatedRoom)) {
                loadRoomData();
                clearRoomForm();
                JOptionPane.showMessageDialog(this, "Cập nhật phòng thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể cập nhật phòng!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số ghế hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi cập nhật phòng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleDeleteRoom() {
        try {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng cần xóa!");
                return;
            }
            
            int roomId = (int) roomTable.getValueAt(selectedRow, 0);
            
            // Check if room has any showtimes
            if (roomController.hasShowtimes(roomId)) {
                JOptionPane.showMessageDialog(this, 
                    "Không thể xóa phòng này vì đang có suất chiếu!",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Check if room has any booked seats
            List<Seat> seats = roomController.getSeatsByRoomId(roomId);
            for (Seat seat : seats) {
                if (seat.getStatus().equals("booked") || seat.getStatus().equals("occupied")) {
                    JOptionPane.showMessageDialog(this, 
                        "Không thể xóa phòng này vì có ghế đã được đặt chỗ!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa phòng này?\nTất cả ghế trong phòng sẽ bị xóa!",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                // Delete all seats first
                for (Seat seat : seats) {
                    roomController.deleteSeat(seat.getSeatId());
                }
                
                // Then delete the room
                if (roomController.deleteRoom(roomId)) {
                    loadRoomData();
                    clearRoomForm();
                    JOptionPane.showMessageDialog(this, "Xóa phòng thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa phòng!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi xóa phòng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleAddSeat() {
        try {
            String selectedRoom = (String) roomComboBox.getSelectedItem();
            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng!");
                return;
            }
            
            int roomId = Integer.parseInt(selectedRoom.split(" - ")[0]);
            String seatNumber = seatNumberField.getText().trim();
            
            if (seatNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập vị trí ghế!");
                return;
            }
            
            Seat newSeat = new Seat(0, roomId, seatNumber);
            newSeat.setStatus("available"); // Set default status
            if (roomController.addSeat(newSeat)) {
                loadSeatData(roomId);
                clearSeatForm();
                JOptionPane.showMessageDialog(this, "Thêm ghế thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Vị trí ghế đã tồn tại trong phòng này!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi thêm ghế: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleUpdateSeat() {
        try {
            int selectedRow = seatTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ghế cần cập nhật!");
                return;
            }
            
            int seatId = (int) seatTable.getValueAt(selectedRow, 0);
            String selectedRoom = (String) roomComboBox.getSelectedItem();
            int roomId = Integer.parseInt(selectedRoom.split(" - ")[0]);
            String seatNumber = seatNumberField.getText().trim();
            String currentStatus = (String) seatTable.getValueAt(selectedRow, 4);
            
            if (seatNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập vị trí ghế!");
                return;
            }
            
            Seat updatedSeat = new Seat(seatId, roomId, seatNumber);
            updatedSeat.setStatus(currentStatus);
            if (roomController.updateSeat(updatedSeat)) {
                loadSeatData(roomId);
                clearSeatForm();
                JOptionPane.showMessageDialog(this, "Cập nhật ghế thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể cập nhật ghế!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi cập nhật ghế: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleDeleteSeat() {
        try {
            int selectedRow = seatTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn ghế cần xóa!");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa ghế này?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                int seatId = (int) seatTable.getValueAt(selectedRow, 0);
                if (roomController.deleteSeat(seatId)) {
                    String selectedRoom = (String) roomComboBox.getSelectedItem();
                    int roomId = Integer.parseInt(selectedRoom.split(" - ")[0]);
                    loadSeatData(roomId);
                    clearSeatForm();
                    JOptionPane.showMessageDialog(this, "Xóa ghế thành công!");
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa ghế!");
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi xóa ghế: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleGenerateSeats() {
        try {
            String selectedRoom = (String) roomComboBox.getSelectedItem();
            if (selectedRoom == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn phòng!");
                return;
            }
            
            int roomId = Integer.parseInt(selectedRoom.split(" - ")[0]);
            Room room = roomController.getRoomById(roomId);
            if (room == null) {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin phòng!");
                return;
            }
            
            String rowsInput = JOptionPane.showInputDialog(this, "Nhập số hàng ghế:");
            String seatsPerRowInput = JOptionPane.showInputDialog(this, "Nhập số ghế mỗi hàng:");
            
            if (rowsInput == null || seatsPerRowInput == null) return;
            
            int rows = Integer.parseInt(rowsInput);
            int seatsPerRow = Integer.parseInt(seatsPerRowInput);
            
            if (rows <= 0 || seatsPerRow <= 0) {
                JOptionPane.showMessageDialog(this, "Số hàng và số ghế phải lớn hơn 0!");
                return;
            }
            
            int totalSeats = rows * seatsPerRow;
            if (totalSeats > room.getTotalSeats()) {
                JOptionPane.showMessageDialog(this, 
                    String.format("Tổng số ghế (%d) không được vượt quá số ghế của phòng (%d)!", 
                    totalSeats, room.getTotalSeats()));
                return;
            }
            
            // Check if room already has seats
            List<Seat> existingSeats = roomController.getSeatsByRoomId(roomId);
            if (!existingSeats.isEmpty()) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Phòng này đã có ghế. Bạn có muốn tạo lại không?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
                // Delete existing seats
                for (Seat seat : existingSeats) {
                    roomController.deleteSeat(seat.getSeatId());
                }
            }
            
            // Generate seats with proper format (A1, A2, B1, B2, etc.)
            boolean success = true;
            for (int i = 0; i < rows; i++) {
                char rowChar = (char)('A' + i);
                for (int j = 1; j <= seatsPerRow; j++) {
                    String seatNumber = String.format("%c%d", rowChar, j);
                    Seat seat = new Seat(0, roomId, seatNumber);
                    seat.setStatus("available");
                    if (!roomController.addSeat(seat)) {
                        success = false;
                        break;
                    }
                }
                if (!success) break;
            }
            
            if (success) {
                loadSeatData(roomId);
                loadRoomData();
                JOptionPane.showMessageDialog(this, "Tạo ghế tự động thành công!");
            } else {
                JOptionPane.showMessageDialog(this, "Không thể tạo ghế tự động!");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số hợp lệ!");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tạo ghế tự động: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleRoomSelection() {
        try {
            int selectedRow = roomTable.getSelectedRow();
            if (selectedRow != -1) {
                roomNameField.setText((String) roomTable.getValueAt(selectedRow, 1));
                totalSeatsField.setText(roomTable.getValueAt(selectedRow, 2).toString());
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi chọn phòng: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void handleSeatSelection() {
        try {
            int selectedRow = seatTable.getSelectedRow();
            if (selectedRow != -1) {
                seatNumberField.setText((String) seatTable.getValueAt(selectedRow, 3));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi chọn ghế: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadSeatData(int roomId) {
        try {
            seatTableModel.setRowCount(0);
            List<Seat> seats = roomController.getSeatsByRoomId(roomId);
            for (Seat seat : seats) {
                Room room = roomController.getRoomById(seat.getRoomId());
                seatTableModel.addRow(new Object[]{
                    seat.getSeatId(),
                    seat.getRoomId(),
                    room.getRoomName(),
                    seat.getSeatNumber(),
                    seat.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Lỗi khi tải dữ liệu ghế: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void clearRoomForm() {
        roomNameField.setText("");
        totalSeatsField.setText("");
        roomTable.clearSelection();
    }
    
    private void clearSeatForm() {
        seatNumberField.setText("");
        seatTable.clearSelection();
    }
}