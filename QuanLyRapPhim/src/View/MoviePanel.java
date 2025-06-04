package View;

import Controller.MovieController;
import Model.Movie;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.github.lgooddatepicker.components.DatePicker;

import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.util.List;
import java.text.SimpleDateFormat;

import java.time.LocalDate;

public class MoviePanel extends JPanel {
    // Colors
    private Color primaryColor = new Color(41, 128, 185);
    private Color secondaryColor = new Color(52, 152, 219);
    private Color accentColor = new Color(231, 76, 60);
    private Color bgColor = new Color(236, 240, 241);
    private Color darkColor = new Color(44, 62, 80);
    private Color lightTextColor = Color.WHITE;
    private Color menuBgColor = new Color(52, 73, 94);
    
    // Components
    private JTable movieTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch, txtTitle, txtGenre, txtDuration, txtTrailerUrl;
    private JTextArea txtDescription;
    private DatePicker datePickerRelease;
    private JButton btnAdd, btnUpdate, btnDelete, btnClear, btnSearch, btnViewDetails;
    private JScrollPane tableScrollPane;
    private MovieController movieController;
    
    public MoviePanel() {
        movieController = new MovieController();
        setLayout(new BorderLayout());
        initializeComponents();
        loadMovieData();
    }
    
    private void initializeComponents() {
        // Panel tiêu đề
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(bgColor);
        titlePanel.setBorder(new EmptyBorder(20, 20, 10, 20));
        
        JLabel titleLabel = new JLabel("Quản lý phim");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(darkColor);
             
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createHorizontalStrut(20));
        
        add(titlePanel, BorderLayout.NORTH);
        
        // Panel chính
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(0, 20, 20, 20));
        
        // Panel tìm kiếm
        JPanel searchPanel = createSearchPanel();
        
        // Panel bảng
        JPanel tablePanel = createTablePanel();
        
        // Panel form
        JPanel formPanel = createFormPanel();
        
        // Thêm các panel vào panel chính
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(bgColor);
        topPanel.add(searchPanel, BorderLayout.NORTH);
        topPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(formPanel, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
        
        // Thêm các sự kiện
        btnAdd.addActionListener(e -> addMovie());
        btnUpdate.addActionListener(e -> updateMovie());
        btnDelete.addActionListener(e -> deleteMovie());
        btnSearch.addActionListener(e -> searchMovies());
        btnClear.addActionListener(e -> clearForm());
        btnViewDetails.addActionListener(e -> showMovieDetails());
        
        movieTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedRow = movieTable.getSelectedRow();
                if (selectedRow != -1) {
                    loadMovieToForm(selectedRow);
                }
            }
        });
    }
    
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel searchLabel = new JLabel("Tìm kiếm phim:");
        searchLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        txtSearch = new JTextField();
        txtSearch.setFont(new Font("Arial", Font.PLAIN, 14));
        
        btnSearch = createStyledButton("Tìm kiếm", primaryColor);
        
        JPanel labelAndFieldPanel = new JPanel(new BorderLayout(10, 0));
        labelAndFieldPanel.setBackground(Color.WHITE);
        labelAndFieldPanel.add(searchLabel, BorderLayout.WEST);
        labelAndFieldPanel.add(txtSearch, BorderLayout.CENTER);
        
        panel.add(labelAndFieldPanel, BorderLayout.CENTER);
        panel.add(btnSearch, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        
        // Khởi tạo bảng
        String[] columnNames = {"ID", "Tên phim", "Thể loại", "Thời lượng (phút)", "Ngày khởi chiếu"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        movieTable = new JTable(tableModel);
        movieTable.setFont(new Font("Arial", Font.PLAIN, 14));
        movieTable.setRowHeight(30);
        movieTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        movieTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        movieTable.getTableHeader().setBackground(new Color(240, 240, 240));
        
        tableScrollPane = new JScrollPane(movieTable);
        
        // Panel chứa nút tác vụ cho bảng
        JPanel tableButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        tableButtonPanel.setBackground(Color.WHITE);
        
        btnViewDetails = createStyledButton("Xem chi tiết", secondaryColor);
        tableButtonPanel.add(btnViewDetails);
        
        panel.add(tableScrollPane, BorderLayout.CENTER);
        panel.add(tableButtonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(350, 600));
        
        // Tiêu đề form
        JLabel formTitle = new JLabel("Thông tin phim");
        formTitle.setFont(new Font("Arial", Font.BOLD, 18));
        formTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(formTitle);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Tên phim
        panel.add(createFormLabel("Tên phim:"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        txtTitle = new JTextField();
        setupTextField(txtTitle, panel);
        
        // Thể loại
        panel.add(createFormLabel("Thể loại:"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        txtGenre = new JTextField();
        setupTextField(txtGenre, panel);
        
        // Thời lượng
        panel.add(createFormLabel("Thời lượng (phút):"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        txtDuration = new JTextField();
        setupTextField(txtDuration, panel);
        
        // Mô tả
        panel.add(createFormLabel("Mô tả:"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        txtDescription = new JTextArea(5, 20);
        txtDescription.setFont(new Font("Arial", Font.PLAIN, 14));
        txtDescription.setLineWrap(true);
        txtDescription.setWrapStyleWord(true);
        
        JScrollPane scrollPane = new JScrollPane(txtDescription);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        scrollPane.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        panel.add(scrollPane);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // URL Trailer
        panel.add(createFormLabel("URL Trailer:"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        txtTrailerUrl = new JTextField();
        setupTextField(txtTrailerUrl, panel);
        
        // Ngày khởi chiếu
        panel.add(createFormLabel("Ngày khởi chiếu (dd/MM/yyyy):"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        datePickerRelease = new DatePicker();
        datePickerRelease.setFont(new Font("Arial", Font.PLAIN, 14));
        datePickerRelease.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(datePickerRelease);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Buttons
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        btnAdd = createStyledButton("Thêm phim", primaryColor);
        btnUpdate = createStyledButton("Cập nhật", secondaryColor);
        btnDelete = createStyledButton("Xóa phim", accentColor);
        btnClear = createStyledButton("Làm mới", new Color(149, 165, 166));
        
        buttonPanel.add(btnAdd);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);
        
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JLabel createFormLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }
    
    private void setupTextField(JTextField textField, JPanel panel) {
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        textField.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(textField);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(new EmptyBorder(8, 15, 8, 15));
        
        // Hiệu ứng hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor.darker());
            }
            
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private void loadMovieData() {
        tableModel.setRowCount(0);
        List<Movie> movies = movieController.getAllMovies();
        for (Movie movie : movies) {
            Object[] row = {
                movie.getMovieId(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getDuration(),
                movie.getReleaseDate()
            };
            tableModel.addRow(row);
        }
    }
    
    private void addMovie() {
        try {
            Movie movie = new Movie();
            movie.setTitle(txtTitle.getText());
            movie.setGenre(txtGenre.getText());
            movie.setDuration(Integer.parseInt(txtDuration.getText()));
            movie.setDescription(txtDescription.getText());
            movie.setTrailerUrl(txtTrailerUrl.getText());
            LocalDate localDate = datePickerRelease.getDate();
            if (localDate != null) {
                java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
                movie.setReleaseDate(sqlDate);
            } else {
                movie.setReleaseDate(null);
            }
            if (movieController.addMovie(movie)) {
                JOptionPane.showMessageDialog(this, "Thêm phim thành công!");
                clearForm();
                loadMovieData();
            } else {
                JOptionPane.showMessageDialog(this, "Thêm phim thất bại!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin và chọn ngày khởi chiếu!");
        }
    }
    
    private void updateMovie() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phim cần cập nhật!");
            return;
        }
        try {
            Movie movie = new Movie();
            movie.setMovieId((int) tableModel.getValueAt(selectedRow, 0));
            movie.setTitle(txtTitle.getText());
            movie.setGenre(txtGenre.getText());
            movie.setDuration(Integer.parseInt(txtDuration.getText()));
            movie.setDescription(txtDescription.getText());
            movie.setTrailerUrl(txtTrailerUrl.getText());
            LocalDate localDate = datePickerRelease.getDate();
            if (localDate != null) {
                java.sql.Date sqlDate = java.sql.Date.valueOf(localDate);
                movie.setReleaseDate(sqlDate);
            } else {
                movie.setReleaseDate(null);
            }
            if (movieController.updateMovie(movie)) {
                JOptionPane.showMessageDialog(this, "Cập nhật phim thành công!");
                loadMovieData();
            } else {
                JOptionPane.showMessageDialog(this, "Cập nhật phim thất bại!");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin và chọn ngày khởi chiếu!");
        }
    }
    
    private void deleteMovie() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phim cần xóa!");
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn xóa phim này?");
        if (confirm == JOptionPane.YES_OPTION) {
            int movieId = (int) tableModel.getValueAt(selectedRow, 0);
            if (movieController.deleteMovie(movieId)) {
                JOptionPane.showMessageDialog(this, "Xóa phim thành công!");
                clearForm();
                loadMovieData();
            } else {
                JOptionPane.showMessageDialog(this, "Xóa phim thất bại!");
            }
        }
    }
    
    private void searchMovies() {
        String keyword = txtSearch.getText().trim();
        if (keyword.isEmpty()) {
            loadMovieData();
            return;
        }
        
        tableModel.setRowCount(0);
        List<Movie> movies = movieController.searchMovies(keyword);
        for (Movie movie : movies) {
            Object[] row = {
                movie.getMovieId(),
                movie.getTitle(),
                movie.getGenre(),
                movie.getDuration(),
                movie.getReleaseDate()
            };
            tableModel.addRow(row);
        }
    }
    
    private void clearForm() {
        txtTitle.setText("");
        txtGenre.setText("");
        txtDuration.setText("");
        txtDescription.setText("");
        txtTrailerUrl.setText("");
        datePickerRelease.setDate(null);
        movieTable.clearSelection();
    }
    
    private void loadMovieToForm(int row) {
        txtTitle.setText((String) tableModel.getValueAt(row, 1));
        txtGenre.setText((String) tableModel.getValueAt(row, 2));
        txtDuration.setText(tableModel.getValueAt(row, 3).toString());
        java.sql.Date sqlDate = (java.sql.Date) tableModel.getValueAt(row, 4);
        if (sqlDate != null) {
            datePickerRelease.setDate(sqlDate.toLocalDate());
        } else {
            datePickerRelease.setDate(null);
        }
        
        // Lấy thông tin chi tiết từ database
        int movieId = (int) tableModel.getValueAt(row, 0);
        Movie movie = movieController.getMovieById(movieId);
        if (movie != null) {
            txtDescription.setText(movie.getDescription());
            txtTrailerUrl.setText(movie.getTrailerUrl());
        }
    }
    
    private void showMovieDetails() {
        int selectedRow = movieTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn phim để xem chi tiết!");
            return;
        }
        int movieId = (int) tableModel.getValueAt(selectedRow, 0);
        Movie movie = movieController.getMovieById(movieId);
        if (movie == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin phim!");
            return;
        }
        StringBuilder details = new StringBuilder();
        details.append("Tên phim: ").append(movie.getTitle()).append("\n");
        details.append("Thể loại: ").append(movie.getGenre()).append("\n");
        details.append("Thời lượng: ").append(movie.getDuration()).append(" phút\n");
        details.append("Mô tả: ").append(movie.getDescription()).append("\n");
        details.append("Trailer URL: ").append(movie.getTrailerUrl()).append("\n");
        if (movie.getReleaseDate() != null) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            details.append("Ngày khởi chiếu: ").append(sdf.format(movie.getReleaseDate())).append("\n");
        }
        JOptionPane.showMessageDialog(this, details.toString(), "Chi tiết phim", JOptionPane.INFORMATION_MESSAGE);
    }
    
    
}