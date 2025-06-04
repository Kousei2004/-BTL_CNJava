package View;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.github.lgooddatepicker.components.DatePicker;
import Controller.MovieController;
import Controller.ShowtimeController;
import Controller.PaymentController;
import Controller.ReportController;

public class CinemaManagementSystem extends JFrame {
    private JPanel mainPanel;
    private JLabel statusLabel;
    private JLabel timeLabel;
    private JLabel userLabel;
    private JPanel contentPanel;
    private Timer timer;
    
    // Khai báo các panel chức năng
    private JPanel dashboardPanel;
    private MoviePanel moviesPanel;
    private RoomPanel roomsPanel;
    private ShowtimePanel showtimePanel;
    private JPanel ticketsPanel;
    private JPanel customersPanel;
    private UserPanel usersPanel;
    private JPanel reportsPanel;
    private PaymentPanel paymentPanel;
    
    // Màu sắc sử dụng trong hệ thống
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color accentColor = new Color(231, 76, 60);
    private Color bgColor = new Color(236, 240, 241);
    private Color darkColor = new Color(44, 62, 80);
    private Color lightTextColor = Color.WHITE;
    private Color menuBgColor = new Color(52, 73, 94);
    
    private String userRole = "admin";
    
    // Khai báo các controller để lấy dữ liệu thống kê
    private MovieController movieController = new MovieController();
    private ShowtimeController showtimeController = new ShowtimeController();
    private PaymentController paymentController = new PaymentController();
    private ReportController reportController = new ReportController();
    
    public CinemaManagementSystem() {
        this("admin");
    }

    public CinemaManagementSystem(String role) {
        this.userRole = role;
        setTitle("Hệ Thống Quản Lý Phòng Vé");
        setSize(1500, 1088);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Thiết lập layout chính
        getContentPane().setLayout(new BorderLayout());
        
        // Khởi tạo các panel
        createUIComponents();
       
        // Hiển thị panel mặc định
        showPanel("dashboard");
        
        setVisible(true);
    }
    
    private void createUIComponents() {
        // HEADER PANEL
        JPanel headerPanel = createHeaderPanel();
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        
        // SIDEBAR PANEL
        JPanel sidebarPanel = createSidebarPanel();
        getContentPane().add(sidebarPanel, BorderLayout.WEST);
        
        // CONTENT PANEL
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(bgColor);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        
        // FOOTER PANEL
        JPanel footerPanel = createFooterPanel();
        getContentPane().add(footerPanel, BorderLayout.SOUTH);
        
        // Khởi tạo các panel chức năng
        initFunctionPanels();
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(primaryColor);
        headerPanel.setPreferredSize(new Dimension(getWidth(), 50));
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(39, 55, 70)));

        // Logo và tên hệ thống
        JPanel logoPanel = new JPanel();
        logoPanel.setBackground(primaryColor);
        
        // Thêm EmptyBorder để tạo khoảng cách từ viền trên
        logoPanel.setBorder(new EmptyBorder(5, 15, 0, 0));  
        JLabel logoLabel = new JLabel("CINEMA MANAGEMENT SYSTEM");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 18));
        logoLabel.setForeground(lightTextColor);
        logoPanel.add(logoLabel);

        // Hàng chứa Xin chào, Admin và Đăng xuất
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userInfoPanel.setBackground(primaryColor);
        userInfoPanel.setBorder(new EmptyBorder(0, 0, 0, 15)); 

        userLabel = new JLabel("Xin chào, Admin");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(lightTextColor);
        userLabel.setBorder(new EmptyBorder(0, 0, 0, 20));

        JButton logoutButton = new JButton("Đăng xuất");
        logoutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            }
        });
        stylizeButton(logoutButton, new Color(231, 76, 60));
        logoutButton.setBorder(new EmptyBorder(8, 15, 8, 15)); 
        logoutButton.putClientProperty("JButton.buttonType", "roundRect");

        userInfoPanel.add(userLabel);
        userInfoPanel.add(logoutButton);

        headerPanel.add(logoPanel, BorderLayout.WEST);
        headerPanel.add(userInfoPanel, BorderLayout.EAST);

        return headerPanel;
    }


    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(menuBgColor);
        
        // Tạo các nút menu
        JButton dashboardBtn = createMenuButton("Dashboard", "dashboard", "/icons/dashboard.png");
        JButton moviesBtn = createMenuButton("Quản lý phim", "movies", "/icons/movie.png");
        JButton roomsBtn = createMenuButton("Phòng chiếu & Ghế", "rooms", "/icons/room.png");
        JButton showtimesBtn = createMenuButton("Suất chiếu", "showtimes", "/icons/showtime.png");
        JButton ticketsBtn = createMenuButton("Đặt vé & Quản lý vé", "tickets", "/icons/ticket.png");
        JButton customersBtn = createMenuButton("Khách hàng", "customers", "/icons/customer.png");
        JButton usersBtn = createMenuButton("Nhân viên", "users", "/icons/user.png");
        JButton paymentsBtn = createMenuButton("Thanh toán & KM", "payments", "/icons/payment.png");
        JButton reportsBtn = createMenuButton("Báo cáo & Thống kê", "reports", "/icons/report.png");
        
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        if ("admin".equalsIgnoreCase(userRole)) {
            sidebarPanel.add(dashboardBtn);
            sidebarPanel.add(moviesBtn);
            sidebarPanel.add(roomsBtn);
            sidebarPanel.add(showtimesBtn);
            sidebarPanel.add(ticketsBtn);
            sidebarPanel.add(customersBtn);
            sidebarPanel.add(usersBtn);
            sidebarPanel.add(paymentsBtn);
            sidebarPanel.add(reportsBtn);
        } else if ("staff".equalsIgnoreCase(userRole)) {
            sidebarPanel.add(ticketsBtn);
            sidebarPanel.add(customersBtn);
            sidebarPanel.add(paymentsBtn);
        } else {
            sidebarPanel.add(customersBtn);
            sidebarPanel.add(paymentsBtn);
        }
        sidebarPanel.add(Box.createVerticalGlue());
        
        return sidebarPanel;
    }
    
    private JButton createMenuButton(String text, String panelName, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 13)); // Giảm kích thước font
        button.setForeground(Color.WHITE);
        button.setBackground(menuBgColor);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 20, 8, 10)); // Giảm padding
        button.setMaximumSize(new Dimension(200, 40)); // Giảm kích thước tối đa
        
        // Xử lý sự kiện khi nhấn nút
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPanel(panelName);
            }
        });
        
        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(75, 101, 132));
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(menuBgColor);
            }
        });
        
        return button;
    }
    
    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(darkColor);
        footerPanel.setPreferredSize(new Dimension(getWidth(), 30));
        
        statusLabel = new JLabel(" Hệ thống đang hoạt động");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        JLabel versionLabel = new JLabel("Phiên bản 1.0.0 ");
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        versionLabel.setForeground(Color.WHITE);
        versionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        versionLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
        
        footerPanel.add(statusLabel, BorderLayout.WEST);
        footerPanel.add(versionLabel, BorderLayout.EAST);
        
        return footerPanel;
    }
    
    private void initFunctionPanels() {
        // Dashboard Panel
        dashboardPanel = createDashboardPanel();
        contentPanel.add(dashboardPanel, "dashboard");
        
        // Movies Panel
        moviesPanel = new MoviePanel();
        contentPanel.add(moviesPanel, "movies");
        
        // Rooms Panel
        roomsPanel = new RoomPanel();
        contentPanel.add(roomsPanel, "rooms");
        
        // Showtimes Panel
        showtimePanel = new ShowtimePanel();
        contentPanel.add(showtimePanel, "showtimes");
        
        // Tickets Panel
        ticketsPanel = new TicketPanel();
        contentPanel.add(ticketsPanel, "tickets");
        
        // Customers Panel
        customersPanel = new CustomerPanel();
        contentPanel.add(customersPanel, "customers");
        
        // Users Panel
        usersPanel = new UserPanel();
        contentPanel.add(usersPanel, "users");
        
        // Reports Panel
        reportsPanel = new ReportPanel();
        contentPanel.add(reportsPanel, "reports");
        
        // Payments Panel
        paymentPanel = new PaymentPanel();
        contentPanel.add(paymentPanel, "payments");
    }
    
    private JPanel createDashboardPanel() {
        JPanel dashboardPanel = new JPanel();
        dashboardPanel.setLayout(new BorderLayout());
        dashboardPanel.setBackground(bgColor);
        
        // Panel tiêu đề
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(bgColor);
        titlePanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Dashboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(darkColor);
        
        titlePanel.add(titleLabel);
        
        // Panel chính chứa các thẻ thống kê
        JPanel mainDashboard = new JPanel();
        mainDashboard.setLayout(new GridBagLayout());
        mainDashboard.setBackground(bgColor);
        mainDashboard.setBorder(new EmptyBorder(10, 20, 20, 20));
        
        // Thiết lập chung cho tất cả các constraints
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Lấy dữ liệu thực tế
        int movieCount = movieController.getAllMovies().size();
        int showtimeCount = showtimeController.searchShowtimesByDateRange(new java.util.Date(), new java.util.Date()).size();
        int ticketCount = paymentController.getTodayTicketCount();
        double todayRevenue = paymentController.getTodayRevenue();
        String revenueStr = String.format("%,.0fđ", todayRevenue);
        
        // Hàng đầu tiên - Các thẻ thống kê
        // Thẻ 1
        GridBagConstraints gbc1 = (GridBagConstraints) gbc.clone();
        gbc1.gridx = 0;
        gbc1.gridy = 0;
        gbc1.gridwidth = 1;
        gbc1.weightx = 1.0;
        gbc1.weighty = 0.0;
        JPanel movieStatsCard = createStatsCard("Phim đang chiếu", String.valueOf(movieCount), new Color(52, 152, 219));
        mainDashboard.add(movieStatsCard, gbc1);
        
        // Thẻ 2
        GridBagConstraints gbc2 = (GridBagConstraints) gbc.clone();
        gbc2.gridx = 1;
        gbc2.gridy = 0;
        gbc2.gridwidth = 1;
        gbc2.weightx = 1.0;
        gbc2.weighty = 0.0;
        JPanel showtimeStatsCard = createStatsCard("Suất chiếu hôm nay", String.valueOf(showtimeCount), new Color(46, 204, 113));
        mainDashboard.add(showtimeStatsCard, gbc2);
        
        // Thẻ 3
        GridBagConstraints gbc3 = (GridBagConstraints) gbc.clone();
        gbc3.gridx = 2;
        gbc3.gridy = 0;
        gbc3.gridwidth = 1;
        gbc3.weightx = 1.0;
        gbc3.weighty = 0.0;
        JPanel ticketStatsCard = createStatsCard("Vé bán hôm nay", String.valueOf(ticketCount), new Color(155, 89, 182));
        mainDashboard.add(ticketStatsCard, gbc3);
        
        // Thẻ 4
        GridBagConstraints gbc4 = (GridBagConstraints) gbc.clone();
        gbc4.gridx = 3;
        gbc4.gridy = 0;
        gbc4.gridwidth = 1;
        gbc4.weightx = 1.0;
        gbc4.weighty = 0.0;
        JPanel revenueStatsCard = createStatsCard("Doanh thu hôm nay", revenueStr, new Color(231, 76, 60));
        mainDashboard.add(revenueStatsCard, gbc4);
        
        // Hàng thứ hai - Biểu đồ và báo cáo
        GridBagConstraints gbc5 = (GridBagConstraints) gbc.clone();
        gbc5.gridx = 0;
        gbc5.gridy = 1;
        gbc5.gridwidth = 2;
        gbc5.weightx = 2.0;
        gbc5.weighty = 1.0;
        JPanel revenueChartPanel = createWeeklyRevenueChartPanel();
        mainDashboard.add(revenueChartPanel, gbc5);
        
        GridBagConstraints gbc6 = (GridBagConstraints) gbc.clone();
        gbc6.gridx = 2;
        gbc6.gridy = 1;
        gbc6.gridwidth = 2;
        gbc6.weightx = 2.0;
        gbc6.weighty = 1.0;
        JPanel topMoviesPanel = createTopMoviesPanel();
        mainDashboard.add(topMoviesPanel, gbc6);
        
        // Hàng thứ ba - Các thao tác nhanh
        GridBagConstraints gbc7 = (GridBagConstraints) gbc.clone();
        gbc7.gridx = 0;
        gbc7.gridy = 2;
        gbc7.gridwidth = 4;
        gbc7.weightx = 4.0;
        gbc7.weighty = 0.3;
        JPanel quickActionsPanel = createQuickActionsPanel();
        mainDashboard.add(quickActionsPanel, gbc7);
        
        dashboardPanel.add(titlePanel, BorderLayout.NORTH);
        dashboardPanel.add(mainDashboard, BorderLayout.CENTER);
        
        return dashboardPanel;
    }
    
    private JPanel createStatsCard(String title, String value, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        // Thanh màu phía trên
        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 5));
        
        // Nội dung thẻ
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Color.WHITE);
        content.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(new Color(100, 100, 100));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(darkColor);
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        content.add(titleLabel);
        content.add(Box.createRigidArea(new Dimension(0, 10)));
        content.add(valueLabel);
        
        card.add(colorBar, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createCardPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        
        if (title.equals("Doanh thu trong tuần")) {
            contentPanel.setLayout(new BorderLayout());
            JLabel chartPlaceholder = new JLabel("Biểu đồ doanh thu sẽ hiển thị ở đây", SwingConstants.CENTER);
            chartPlaceholder.setFont(new Font("Arial", Font.ITALIC, 14));
            chartPlaceholder.setForeground(new Color(150, 150, 150));
            contentPanel.add(chartPlaceholder, BorderLayout.CENTER);
        } else if (title.equals("Top phim phổ biến")) {
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(new EmptyBorder(0, 15, 15, 15));
            
            String[] movies = {"1. Avengers: Endgame - 245 vé", 
                              "2. Spider-Man: No Way Home - 198 vé", 
                              "3. Fast & Furious 10 - 156 vé", 
                              "4. The Batman - 134 vé", 
                              "5. Doctor Strange 2 - 129 vé"};
            
            for (String movie : movies) {
                JLabel movieLabel = new JLabel(movie);
                movieLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                movieLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
                contentPanel.add(movieLabel);
            }
        }
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createQuickActionsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        
        JLabel titleLabel = new JLabel("Thao tác nhanh");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        buttonsPanel.setBackground(Color.WHITE);
        buttonsPanel.setBorder(new EmptyBorder(0, 15, 15, 15));
        
        // Tạo các nút thao tác nhanh
        JButton newTicketBtn = createActionButton("Đặt vé mới", new Color(52, 152, 219));
        JButton newMovieBtn = createActionButton("Thêm phim mới", new Color(46, 204, 113));
        JButton newShowtimeBtn = createActionButton("Thêm suất chiếu", new Color(155, 89, 182));
        JButton reportBtn = createActionButton("Báo cáo doanh thu", new Color(231, 76, 60));
        
        // Thêm action listeners cho các nút
        newTicketBtn.addActionListener(e -> showPanel("tickets"));
        newMovieBtn.addActionListener(e -> showPanel("movies"));
        newShowtimeBtn.addActionListener(e -> showPanel("showtimes"));
        reportBtn.addActionListener(e -> showPanel("reports"));
        
        // Thêm các nút vào panel
        buttonsPanel.add(newTicketBtn);
        buttonsPanel.add(newMovieBtn);
        buttonsPanel.add(newShowtimeBtn);
        buttonsPanel.add(reportBtn);
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        stylizeButton(button, color);
        return button;
    }
    
    private void stylizeButton(JButton button, Color color) {
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Tạo góc tròn cho nút (hoạt động trên các look and feel hỗ trợ)
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
    }
    
    private void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);
        statusLabel.setText(" Đang xem: " + panelName);
    }
    
    // Thêm hàm tạo biểu đồ doanh thu tuần
    private JPanel createWeeklyRevenueChartPanel() {
        java.util.List<Object[]> data = reportController.getRevenueByWeek();
        org.jfree.data.category.DefaultCategoryDataset dataset = new org.jfree.data.category.DefaultCategoryDataset();
        for (Object[] row : data) {
            java.sql.Date day = (java.sql.Date) row[0];
            double total = (double) row[1];
            dataset.addValue(total, "Doanh thu", day.toString());
        }
        org.jfree.chart.JFreeChart chart = org.jfree.chart.ChartFactory.createLineChart(
            "Doanh thu 7 ngày gần nhất",
            "Ngày",
            "Doanh thu (VNĐ)",
            dataset,
            org.jfree.chart.plot.PlotOrientation.VERTICAL,
            false,
            true,
            false
        );
        chart.setBackgroundPaint(Color.WHITE);
        org.jfree.chart.plot.CategoryPlot plot = (org.jfree.chart.plot.CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);
        plot.setOutlinePaint(null);
        
        // Thay đổi renderer từ BarRenderer sang LineAndShapeRenderer
        org.jfree.chart.renderer.category.LineAndShapeRenderer renderer = new org.jfree.chart.renderer.category.LineAndShapeRenderer();
        renderer.setSeriesPaint(0, new Color(41, 128, 185));
        renderer.setSeriesStroke(0, new java.awt.BasicStroke(3.0f));
        renderer.setSeriesShapesVisible(0, true);
        plot.setRenderer(renderer);
        
        org.jfree.chart.ChartPanel chartPanel = new org.jfree.chart.ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(500, 300));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.add(chartPanel, BorderLayout.CENTER);
        return panel;
    }
    
    // Thêm hàm tạo panel top phim phổ biến
    private JPanel createTopMoviesPanel() {
        java.util.List<Object[]> data = reportController.getTopMoviesInWeek();
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        JLabel titleLabel = new JLabel("Top phim phổ biến (7 ngày gần nhất)");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(new EmptyBorder(15, 15, 15, 15));
        panel.add(titleLabel, BorderLayout.NORTH);
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(new EmptyBorder(0, 15, 15, 15));
        if (data.isEmpty()) {
            JLabel emptyLabel = new JLabel("Không có dữ liệu");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyLabel.setForeground(new Color(150, 150, 150));
            contentPanel.add(emptyLabel);
        } else {
            int rank = 1;
            for (Object[] row : data) {
                String movie = (String) row[0];
                int count = (int) row[1];
                JLabel movieLabel = new JLabel(rank + ". " + movie + " - " + count + " vé");
                movieLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                movieLabel.setBorder(new EmptyBorder(5, 0, 5, 0));
                contentPanel.add(movieLabel);
                rank++;
            }
        }
        panel.add(contentPanel, BorderLayout.CENTER);
        return panel;
    }
    
    public static void main(String[] args) {
        try {
            // Thiết lập look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new CinemaManagementSystem();
            }
        });
    }
}