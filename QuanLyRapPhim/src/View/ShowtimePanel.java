package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.github.lgooddatepicker.components.DatePicker;
import Controller.ShowtimeController;
import Model.Showtime;
import java.sql.Time;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.math.BigDecimal;

public class ShowtimePanel extends JPanel {
    // UI Components
    private JTable showtimeTable;
    private DefaultListModel<String> showtimeListModel;
    private JList<String> showtimeList;
    private JTextField searchField;
    private JComboBox<String> searchTypeComboBox;
    private JComboBox<String> movieComboBox;
    private JComboBox<String> roomComboBox;
    private DatePicker datePicker;
    private JSpinner startTimeSpinner;
    private JSpinner endTimeSpinner;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JTextField priceField;
    
    // Colors
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color accentColor = new Color(231, 76, 60);
    private Color bgColor = new Color(236, 240, 241);
    private Color darkColor = new Color(44, 62, 80);
    private Color lightTextColor = Color.WHITE;
    
    private ShowtimeController showtimeController;
    
    private Map<Integer, String> movies;
    private Map<Integer, String> rooms;
    private Map<Integer, Showtime> showtimeMap; // Map to store showtime objects by their ID
    
    public ShowtimePanel() {
        showtimeController = new ShowtimeController();
        setLayout(new BorderLayout());
        setBackground(bgColor);
        loadData();
        initComponents();
        loadCurrentShowtimes();
    }
    
    private void loadData() {
        // Load movies and rooms from database
        movies = showtimeController.getAllMovies();
        rooms = showtimeController.getAllRooms();
    }
    
    private void initComponents() {
        // Title Panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(bgColor);
        titlePanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Quản Lý Suất Chiếu");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(darkColor);
        titlePanel.add(titleLabel);
        
        // Main Split Panel
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(800);
        splitPane.setDividerSize(5);
        splitPane.setBorder(null);
        
        // Left Panel (Search & List)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(bgColor);
        leftPanel.setBorder(new EmptyBorder(10, 20, 20, 10));
        
        // Search Panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);

        // Tạo font cho title tìm kiếm
        Font searchTitleFont = new Font("Arial", Font.PLAIN, 16);
        TitledBorder searchBorder = BorderFactory.createTitledBorder("Tìm kiếm suất chiếu");
        searchBorder.setTitleFont(searchTitleFont);
        searchBorder.setTitleColor(new Color(44, 62, 80));

        searchPanel.setBorder(searchBorder);
        
        JPanel searchControlsPanel = new JPanel(new GridBagLayout());
        searchControlsPanel.setBackground(Color.WHITE);
        
        // Search type selection
        GridBagConstraints gbcLabel = new GridBagConstraints();
        gbcLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcLabel.insets = new Insets(5, 10, 5, 10);
        gbcLabel.gridx = 0;
        gbcLabel.gridy = 0;
        JLabel searchTypeLabel = new JLabel("Tìm theo:");
        searchTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchControlsPanel.add(searchTypeLabel, gbcLabel);
        
        GridBagConstraints gbcCombo = new GridBagConstraints();
        gbcCombo.fill = GridBagConstraints.HORIZONTAL;
        gbcCombo.insets = new Insets(5, 10, 5, 10);
        gbcCombo.gridx = 1;
        gbcCombo.gridy = 0;
        gbcCombo.weightx = 1.0;
        searchTypeComboBox = new JComboBox<>(new String[] {"Tất cả", "Phim", "Phòng chiếu", "Ngày chiếu"});
        searchTypeComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        searchControlsPanel.add(searchTypeComboBox, gbcCombo);
        
        // Search field
        GridBagConstraints gbcSearchLabel = new GridBagConstraints();
        gbcSearchLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcSearchLabel.insets = new Insets(5, 10, 5, 10);
        gbcSearchLabel.gridx = 0;
        gbcSearchLabel.gridy = 1;
        gbcSearchLabel.weightx = 0.0;
        JLabel searchKeywordLabel = new JLabel("Nhập từ khóa:");
        searchKeywordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        searchControlsPanel.add(searchKeywordLabel, gbcSearchLabel);
        
        GridBagConstraints gbcSearchField = new GridBagConstraints();
        gbcSearchField.fill = GridBagConstraints.HORIZONTAL;
        gbcSearchField.insets = new Insets(5, 10, 5, 10);
        gbcSearchField.gridx = 1;
        gbcSearchField.gridy = 1;
        gbcSearchField.weightx = 1.0;
        searchField = new JTextField();
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchControlsPanel.add(searchField, gbcSearchField);
        
        // Search button
        GridBagConstraints gbcButton = new GridBagConstraints();
        gbcButton.fill = GridBagConstraints.HORIZONTAL;
        gbcButton.insets = new Insets(10, 10, 5, 10);
        gbcButton.gridx = 0;
        gbcButton.gridy = 2;
        gbcButton.gridwidth = 2;
        gbcButton.weightx = 1.0;
        JButton searchButton = createStyledButton("Tìm kiếm", secondaryColor);
        searchButton.addActionListener(e -> performSearch());
        searchControlsPanel.add(searchButton, gbcButton);
        
        searchPanel.add(searchControlsPanel, BorderLayout.CENTER);
        
        // Showtime List Panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBackground(Color.WHITE);

        // Tạo font cho title danh sách
        Font listTitleFont = new Font("Arial", Font.PLAIN, 16);
        TitledBorder listBorder = BorderFactory.createTitledBorder("Danh sách suất chiếu");
        listBorder.setTitleFont(listTitleFont);
        listBorder.setTitleColor(new Color(44, 62, 80));

        listPanel.setBorder(listBorder);
        
        showtimeListModel = new DefaultListModel<>();
        showtimeList = new JList<>(showtimeListModel);
        showtimeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        showtimeList.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Add selection listener to load showtime data when selected
        showtimeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIndex = showtimeList.getSelectedIndex();
                if (selectedIndex != -1) {
                    int showtimeId = getSelectedShowtimeId();
                    if (showtimeId != -1) {
                        Showtime showtime = showtimeMap.get(showtimeId);
                        if (showtime != null) {
                            loadShowtimeData(showtime);
                        }
                    }
                }
            }
        });
        
        JScrollPane listScrollPane = new JScrollPane(showtimeList);
        listScrollPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        listPanel.add(listScrollPane, BorderLayout.CENTER);
        
        // Add components to left panel
        leftPanel.add(searchPanel, BorderLayout.NORTH);
        leftPanel.add(listPanel, BorderLayout.CENTER);
        
        // Right Panel (Form)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(bgColor);
        rightPanel.setBorder(new EmptyBorder(10, 10, 20, 20));
        
        // Form Panel
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(Color.WHITE);

        // Tạo font cho title thông tin suất chiếu
        Font formTitleFont = new Font("Arial", Font.PLAIN, 16);
        TitledBorder formBorder = BorderFactory.createTitledBorder("Thông tin suất chiếu");
        formBorder.setTitleFont(formTitleFont);
        formBorder.setTitleColor(new Color(44, 62, 80));

        formPanel.setBorder(formBorder);
        
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        formGbc.insets = new Insets(5, 10, 5, 10);
        
        // Movie selection
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.weightx = 0.3;
        JLabel movieLabel = new JLabel("Phim:");
        movieLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(movieLabel, formGbc);
        
        formGbc.gridx = 1;
        formGbc.gridy = 0;
        formGbc.weightx = 0.7;
        movieComboBox = new JComboBox<>(movies.values().toArray(new String[0]));
        movieComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(movieComboBox, formGbc);
        
        // Room selection
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formGbc.weightx = 0.3;
        JLabel roomLabel = new JLabel("Phòng chiếu:");
        roomLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(roomLabel, formGbc);
        
        formGbc.gridx = 1;
        formGbc.gridy = 1;
        formGbc.weightx = 0.7;
        roomComboBox = new JComboBox<>(rooms.values().toArray(new String[0]));
        roomComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(roomComboBox, formGbc);
        
        // Date selection
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        formGbc.weightx = 0.3;
        JLabel dateLabel = new JLabel("Ngày chiếu:");
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(dateLabel, formGbc);
        
        formGbc.gridx = 1;
        formGbc.gridy = 2;
        formGbc.weightx = 0.7;
        datePicker = new DatePicker();
        datePicker.setDateToToday();
        datePicker.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(datePicker, formGbc);
        
        // Start time
        formGbc.gridx = 0;
        formGbc.gridy = 3;
        formGbc.weightx = 0.3;
        JLabel startTimeLabel = new JLabel("Giờ bắt đầu:");
        startTimeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(startTimeLabel, formGbc);
        
        formGbc.gridx = 1;
        formGbc.gridy = 3;
        formGbc.weightx = 0.7;
        SpinnerDateModel startModel = new SpinnerDateModel();
        startTimeSpinner = new JSpinner(startModel);
        JSpinner.DateEditor startTimeEditor = new JSpinner.DateEditor(startTimeSpinner, "HH:mm");
        startTimeSpinner.setEditor(startTimeEditor);
        startTimeSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(startTimeSpinner, formGbc);
        
        // End time
        formGbc.gridx = 0;
        formGbc.gridy = 4;
        formGbc.weightx = 0.3;
        JLabel endTimeLabel = new JLabel("Giờ kết thúc:");
        endTimeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(endTimeLabel, formGbc);
        
        formGbc.gridx = 1;
        formGbc.gridy = 4;
        formGbc.weightx = 0.7;
        SpinnerDateModel endModel = new SpinnerDateModel();
        endTimeSpinner = new JSpinner(endModel);
        JSpinner.DateEditor endTimeEditor = new JSpinner.DateEditor(endTimeSpinner, "HH:mm");
        endTimeSpinner.setEditor(endTimeEditor);
        endTimeSpinner.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(endTimeSpinner, formGbc);

        // Base Price
        formGbc.gridx = 0;
        formGbc.gridy = 5;
        formGbc.weightx = 0.3;
        JLabel priceLabel = new JLabel("Giá suất chiếu:");
        priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        inputPanel.add(priceLabel, formGbc);
        
        formGbc.gridx = 1;
        formGbc.gridy = 5;
        formGbc.weightx = 0.7;
        priceField = new JTextField();
        priceField.setFont(new Font("Arial", Font.PLAIN, 14));
        inputPanel.add(priceField, formGbc);
        
        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 10));
        buttonPanel.setBackground(Color.WHITE);
        
        addButton = createStyledButton("Thêm", secondaryColor);
        updateButton = createStyledButton("Cập nhật", primaryColor);
        deleteButton = createStyledButton("Xóa", accentColor);
        clearButton = createStyledButton("Làm mới", darkColor);
        
        // Add action listeners
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Validate inputs
                    if (movieComboBox.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(ShowtimePanel.this, "Vui lòng chọn phim!");
                        return;
                    }
                    if (roomComboBox.getSelectedItem() == null) {
                        JOptionPane.showMessageDialog(ShowtimePanel.this, "Vui lòng chọn phòng chiếu!");
                        return;
                    }
                    if (datePicker.getDate() == null) {
                        JOptionPane.showMessageDialog(ShowtimePanel.this, "Vui lòng chọn ngày chiếu!");
                        return;
                    }
                    
                    // Validate price
                    String priceText = priceField.getText().trim();
                    if (priceText.isEmpty()) {
                        JOptionPane.showMessageDialog(ShowtimePanel.this, "Vui lòng nhập giá suất chiếu!");
                        return;
                    }
                    
                    BigDecimal basePrice;
                    try {
                        basePrice = new BigDecimal(priceText);
                        if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
                            JOptionPane.showMessageDialog(ShowtimePanel.this, "Giá suất chiếu phải lớn hơn 0!");
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(ShowtimePanel.this, "Giá suất chiếu không hợp lệ!");
                        return;
                    }
                    
                    // Get selected values
                    int movieId = getSelectedMovieId();
                    int roomId = getSelectedRoomId();
                    Date showDate = Date.from(datePicker.getDate().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
                    Time startTime = new Time(((Date)startTimeSpinner.getValue()).getTime());
                    Time endTime = new Time(((Date)endTimeSpinner.getValue()).getTime());
                    
                    // Validate times
                    if (startTime.after(endTime)) {
                        JOptionPane.showMessageDialog(ShowtimePanel.this, 
                            "Thời gian bắt đầu phải trước thời gian kết thúc!");
                        return;
                    }
                    
                    // Check if showtime is too late (after 23:00)
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(startTime);
                    if (calendar.get(Calendar.HOUR_OF_DAY) >= 23) {
                        JOptionPane.showMessageDialog(ShowtimePanel.this,
                            "Không thể tạo suất chiếu sau 23:00!");
                        return;
                    }
                    
                    // Check for time conflicts with existing showtimes
                    List<Showtime> existingShowtimes = showtimeController.getShowtimesByRoomAndDate(roomId, showDate);
                    for (Showtime existing : existingShowtimes) {
                        if ((startTime.after(existing.getStartTime()) && startTime.before(existing.getEndTime())) ||
                            (endTime.after(existing.getStartTime()) && endTime.before(existing.getEndTime())) ||
                            (startTime.before(existing.getStartTime()) && endTime.after(existing.getEndTime()))) {
                            JOptionPane.showMessageDialog(ShowtimePanel.this,
                                String.format("Xung đột thời gian với suất chiếu khác (%s - %s)!",
                                    existing.getStartTime(), existing.getEndTime()));
                            return;
                        }
                    }
                    
                    // Create and add showtime
                    Showtime showtime = new Showtime();
                    showtime.setMovieId(movieId);
                    showtime.setRoomId(roomId);
                    showtime.setShowDate(showDate);
                    showtime.setStartTime(startTime);
                    showtime.setEndTime(endTime);
                    showtime.setBasePrice(basePrice);
                    
                    if (showtimeController.addShowtime(showtime)) {
                        JOptionPane.showMessageDialog(ShowtimePanel.this, 
                            "Thêm suất chiếu thành công!", 
                            "Thông báo", 
                            JOptionPane.INFORMATION_MESSAGE);
                        loadCurrentShowtimes();
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(ShowtimePanel.this,
                            "Lỗi khi thêm suất chiếu!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ShowtimePanel.this,
                        "Vui lòng nhập đầy đủ thông tin!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = showtimeList.getSelectedIndex();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(ShowtimePanel.this,
                        "Vui lòng chọn suất chiếu cần cập nhật!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    // Validate price
                    String priceText = priceField.getText().trim();
                    if (priceText.isEmpty()) {
                        JOptionPane.showMessageDialog(ShowtimePanel.this, "Vui lòng nhập giá suất chiếu!");
                        return;
                    }
                    
                    BigDecimal basePrice;
                    try {
                        basePrice = new BigDecimal(priceText);
                        if (basePrice.compareTo(BigDecimal.ZERO) <= 0) {
                            JOptionPane.showMessageDialog(ShowtimePanel.this, "Giá suất chiếu phải lớn hơn 0!");
                            return;
                        }
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(ShowtimePanel.this, "Giá suất chiếu không hợp lệ!");
                        return;
                    }

                    Showtime showtime = new Showtime();
                    showtime.setShowtimeId(getSelectedShowtimeId());
                    showtime.setMovieId(getSelectedMovieId());
                    showtime.setRoomId(getSelectedRoomId());
                    showtime.setShowDate(Date.from(datePicker.getDate().atStartOfDay(java.time.ZoneId.systemDefault()).toInstant()));
                    showtime.setStartTime(new Time(((Date)startTimeSpinner.getValue()).getTime()));
                    showtime.setEndTime(new Time(((Date)endTimeSpinner.getValue()).getTime()));
                    showtime.setBasePrice(basePrice);
                    
                    if (showtimeController.updateShowtime(showtime)) {
                        JOptionPane.showMessageDialog(ShowtimePanel.this, 
                            "Cập nhật suất chiếu thành công!", 
                            "Thông báo", 
                            JOptionPane.INFORMATION_MESSAGE);
                        loadCurrentShowtimes();
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(ShowtimePanel.this,
                            "Lỗi khi cập nhật suất chiếu!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(ShowtimePanel.this,
                        "Vui lòng nhập đầy đủ thông tin!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = showtimeList.getSelectedIndex();
                if (selectedIndex == -1) {
                    JOptionPane.showMessageDialog(ShowtimePanel.this,
                        "Vui lòng chọn suất chiếu cần xóa!",
                        "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int result = JOptionPane.showConfirmDialog(ShowtimePanel.this,
                    "Bạn có chắc chắn muốn xóa suất chiếu này?",
                    "Xác nhận",
                    JOptionPane.YES_NO_OPTION);
                    
                if (result == JOptionPane.YES_OPTION) {
                    if (showtimeController.deleteShowtime(getSelectedShowtimeId())) {
                        JOptionPane.showMessageDialog(ShowtimePanel.this,
                            "Xóa suất chiếu thành công!",
                            "Thông báo",
                            JOptionPane.INFORMATION_MESSAGE);
                        loadCurrentShowtimes();
                        clearForm();
                    } else {
                        JOptionPane.showMessageDialog(ShowtimePanel.this,
                            "Lỗi khi xóa suất chiếu!",
                            "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        
        formPanel.add(inputPanel, BorderLayout.CENTER);
        formPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Help Panel
        JPanel helpPanel = new JPanel(new BorderLayout());
        helpPanel.setBackground(new Color(241, 196, 15, 30));
        helpPanel.setBorder(BorderFactory.createTitledBorder("Hướng dẫn"));
        
        JTextArea helpText = new JTextArea(
            "- Chọn phim và phòng chiếu từ danh sách\n" +
            "- Chọn ngày chiếu từ lịch\n" +
            "- Nhập giờ bắt đầu và giờ kết thúc\n" +
            "- Nhấn 'Thêm' để tạo suất chiếu mới\n" +
            "- Chọn suất chiếu từ danh sách để cập nhật hoặc xóa"
        );
        helpText.setFont(new Font("Arial", Font.PLAIN, 13));
        helpText.setEditable(false);
        helpText.setBackground(new Color(241, 196, 15, 30));
        helpText.setBorder(new EmptyBorder(5, 10, 5, 10));
        
        helpPanel.add(helpText, BorderLayout.CENTER);
        
        // Add components to right panel
        rightPanel.add(formPanel, BorderLayout.CENTER);
        rightPanel.add(helpPanel, BorderLayout.SOUTH);
        
        // Add panels to split pane
        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        
        // Add components to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }
    
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(5, 10, 5, 10));
        button.setPreferredSize(new Dimension(100, 35));
        
        // Add hover effect
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
    
    private void clearForm() {
        // Load lại dữ liệu từ database
        loadData();
        
        // Cập nhật lại combobox với dữ liệu mới
        movieComboBox.removeAllItems();
        for (String movieName : movies.values()) {
            movieComboBox.addItem(movieName);
        }
        
        roomComboBox.removeAllItems();
        for (String roomName : rooms.values()) {
            roomComboBox.addItem(roomName);
        }
        
        // Reset các trường khác
        datePicker.setDateToToday();
        Date now = new Date();
        startTimeSpinner.setValue(now);
        endTimeSpinner.setValue(now);
        priceField.setText("");
        showtimeList.clearSelection();
    }
    
    // Load only current/future showtimes
    private void loadCurrentShowtimes() {
        showtimeListModel.clear();
        showtimeMap = new HashMap<>(); // Clear the map as well

        // Call a new method in controller to get filtered showtimes
        List<Showtime> showtimes = showtimeController.getAllCurrentShowtimes();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

        for (Showtime showtime : showtimes) {
            String movieName = getMovieName(showtime.getMovieId());
            String roomName = getRoomName(showtime.getRoomId());
            String dateStr = dateFormat.format(showtime.getShowDate());
            String timeStr = timeFormat.format(showtime.getStartTime());

            String showtimeInfo = String.format("%s - %s %s - Phòng %s",
                movieName, dateStr, timeStr, roomName);

            showtimeListModel.addElement(showtimeInfo);
            showtimeMap.put(showtimeListModel.indexOf(showtimeInfo), showtime);
        }
    }
    
    private String getMovieName(int movieId) {
        return showtimeController.getMovieName(movieId);
    }
    
    private String getRoomName(int roomId) {
        return showtimeController.getRoomName(roomId);
    }
    
    private int getSelectedMovieId() {
        String selectedMovie = (String) movieComboBox.getSelectedItem();
        for (Map.Entry<Integer, String> entry : movies.entrySet()) {
            if (entry.getValue().equals(selectedMovie)) {
                return entry.getKey();
            }
        }
        return -1;
    }
    
    private int getSelectedRoomId() {
        String selectedRoom = (String) roomComboBox.getSelectedItem();
        for (Map.Entry<Integer, String> entry : rooms.entrySet()) {
            if (entry.getValue().equals(selectedRoom)) {
                return entry.getKey();
            }
        }
        return -1;
    }
    
    private int getSelectedShowtimeId() {
        int selectedIndex = showtimeList.getSelectedIndex();
        if (selectedIndex == -1) return -1;
        
        // Get the selected showtime string
        String selectedShowtime = showtimeList.getSelectedValue();
        
        // Find the matching showtime in our map
        for (Map.Entry<Integer, Showtime> entry : showtimeMap.entrySet()) {
            Showtime showtime = entry.getValue();
            String showtimeStr = String.format("%s - Phòng: %s - %s (%s-%s) - Giá: %s VNĐ",
                getMovieName(showtime.getMovieId()),
                getRoomName(showtime.getRoomId()),
                new SimpleDateFormat("dd/MM/yyyy HH:mm").format(showtime.getShowDate()),
                showtime.getStartTime(),
                showtime.getEndTime(),
                showtime.getBasePrice().toString()
            );
            if (showtimeStr.equals(selectedShowtime)) {
                return entry.getKey();
            }
        }
        return -1;
    }
    
    private void loadShowtimeData(Showtime showtime) {
        // Set movie
        String movieName = getMovieName(showtime.getMovieId());
        movieComboBox.setSelectedItem(movieName);
        
        // Set room
        String roomName = getRoomName(showtime.getRoomId());
        roomComboBox.setSelectedItem(roomName);
        
        // Set date
        java.util.Date utilDate = showtime.getShowDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(utilDate);
        LocalDate localDate = LocalDate.of(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePicker.setDate(localDate);
        
        // Set times
        startTimeSpinner.setValue(showtime.getStartTime());
        endTimeSpinner.setValue(showtime.getEndTime());
        
        // Set price
        priceField.setText(showtime.getBasePrice().toString());
    }
    
    private void performSearch() {
        String searchText = searchField.getText().trim();
        String searchType = (String) searchTypeComboBox.getSelectedItem();
        
        List<Showtime> searchResults;
        
        switch (searchType) {
            case "Phim":
                // Search by movie name
                searchResults = new ArrayList<>();
                for (Map.Entry<Integer, String> entry : movies.entrySet()) {
                    if (entry.getValue().toLowerCase().contains(searchText.toLowerCase())) {
                        searchResults.addAll(showtimeController.searchShowtimesByMovie(entry.getKey()));
                    }
                }
                break;
                
            case "Phòng chiếu":
                // Search by room name
                searchResults = new ArrayList<>();
                for (Map.Entry<Integer, String> entry : rooms.entrySet()) {
                    if (entry.getValue().toLowerCase().contains(searchText.toLowerCase())) {
                        searchResults.addAll(showtimeController.searchShowtimesByRoom(entry.getKey()));
                    }
                }
                break;
                
            case "Ngày chiếu":
                // Search by date or date range
                searchResults = new ArrayList<>();
                try {
                    if (searchText.contains("-")) {
                        // Date range search
                        String[] dates = searchText.split("-");
                        if (dates.length != 2) {
                            throw new IllegalArgumentException("Định dạng khoảng thời gian không hợp lệ");
                        }
                        
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date startDate = sdf.parse(dates[0].trim());
                        Date endDate = sdf.parse(dates[1].trim());
                        
                        if (startDate.after(endDate)) {
                            JOptionPane.showMessageDialog(this,
                                "Ngày bắt đầu phải trước ngày kết thúc!",
                                "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        searchResults = showtimeController.searchShowtimesByDateRange(
                            new java.sql.Date(startDate.getTime()),
                            new java.sql.Date(endDate.getTime())
                        );
                    } else {
                        // Single date search
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                        Date searchDate = sdf.parse(searchText);
                        searchResults = showtimeController.searchShowtimesByDate(
                            new java.sql.Date(searchDate.getTime())
                        );
                    }
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this,
                        "Định dạng ngày không hợp lệ. Vui lòng nhập theo định dạng dd/MM/yyyy hoặc dd/MM/yyyy - dd/MM/yyyy",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this,
                        "Lỗi khi tìm kiếm theo ngày: " + e.getMessage(),
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                break;
                
            default: // "Tất cả"
                searchResults = showtimeController.getAllCurrentShowtimes();
                break;
        }
        
        // Update the list with search results
        showtimeListModel.clear();
        showtimeMap.clear();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        
        for (Showtime showtime : searchResults) {
            String showtimeStr = String.format("%s - Phòng: %s - %s (%s-%s) - Giá: %s VNĐ",
                getMovieName(showtime.getMovieId()),
                getRoomName(showtime.getRoomId()),
                sdf.format(showtime.getShowDate()),
                showtime.getStartTime(),
                showtime.getEndTime(),
                showtime.getBasePrice().toString()
            );
            showtimeListModel.addElement(showtimeStr);
            showtimeMap.put(showtime.getShowtimeId(), showtime);
        }
        
        // Clear selection
        showtimeList.clearSelection();
        clearForm();
    }
}