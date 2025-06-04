package View;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.UIManager;
import javax.swing.SwingUtilities;

import Controller.UserController;

public class LoginForm extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private JCheckBox chkRememberMe;
    private JLabel lblTitle;
    private JLabel lblLogo;
    private JLabel lblUserIcon;
    private JLabel lblPasswordIcon;
    private JLabel lblForgotPassword;
    private JPanel mainPanel;
    private JPanel formPanel;
    private JPanel buttonPanel;
    private JPanel headerPanel;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }

    /**
     * Create the frame.
     */
    public LoginForm() {
        setTitle("Cinema Ticket Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 600);
        setLocationRelativeTo(null); // Center the window
        
        // Main content pane
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.setLayout(new BorderLayout(0, 0));
        contentPane.setBackground(new Color(25, 32, 65)); // Dark blue background
        setContentPane(contentPane);
        
        // Create panels
        createHeaderPanel();
        createMainPanel();
        
        // Add panels to content pane
        contentPane.add(headerPanel, BorderLayout.NORTH);
        contentPane.add(mainPanel, BorderLayout.CENTER);
    }
    
    private void createHeaderPanel() {
        headerPanel = new JPanel();
        headerPanel.setBackground(new Color(25, 32, 65)); // Dark blue background
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
     // Add logo (placeholder)
        lblLogo = new JLabel();
        // Load actual logo
        ImageIcon logoIcon = new ImageIcon(getClass().getResource("/resources/tv.png"));
        // If resource not found, create a placeholder
        if (logoIcon.getIconWidth() == -1) {
            lblLogo.setText("CINEMA");
            
            lblLogo.setForeground(new Color(255, 215, 0)); // Gold color
            lblLogo.setFont(new Font("Arial", Font.BOLD, 36));
        } else {
            // Scale logo to smaller size (e.g., 80x80)
            Image img = logoIcon.getImage().getScaledInstance(36, 36, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        }
        lblLogo.setAlignmentX(CENTER_ALIGNMENT);

        
        // Add title
        lblTitle = new JLabel("Cinema Ticket Management System");
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setAlignmentX(CENTER_ALIGNMENT);
        
        // Add subtitle
        JLabel lblSubtitle = new JLabel("Employee Login");
        lblSubtitle.setForeground(new Color(169, 169, 169)); // Light gray
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubtitle.setAlignmentX(CENTER_ALIGNMENT);
        
        // Add components to panel
        headerPanel.add(lblLogo);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        headerPanel.add(lblTitle);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(lblSubtitle);
    }
    
    private void createMainPanel() {
        mainPanel = new JPanel();
        mainPanel.setBackground(new Color(25, 32, 65)); // Dark blue background
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        
        // Create login form panel
        formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(30, 39, 73)); // Slightly lighter blue
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(53, 59, 93), 1),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)));
        formPanel.setMaximumSize(new Dimension(400, 300));
        formPanel.setPreferredSize(new Dimension(400, 300));
        
        // Username field with icon
        JPanel usernamePanel = new JPanel();
        usernamePanel.setLayout(new BorderLayout(10, 0));
        usernamePanel.setBackground(new Color(30, 39, 73));
        
        ImageIcon userIcon = new ImageIcon(getClass().getResource("/resources/user.png"));
        Image scaledUserIcon = userIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        lblUserIcon = new JLabel(new ImageIcon(scaledUserIcon));
        lblUserIcon.setForeground(Color.WHITE);
        lblUserIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        usernamePanel.add(lblUserIcon, BorderLayout.WEST);
        
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(53, 59, 93), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        txtUsername.setForeground(Color.BLACK);
        txtUsername.setPreferredSize(new Dimension(300, 40));
        usernamePanel.add(txtUsername, BorderLayout.CENTER);
        
        // Label Username
        GridBagConstraints gbcUsernameLabel = new GridBagConstraints();
        gbcUsernameLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcUsernameLabel.insets = new Insets(8, 5, 8, 5);
        gbcUsernameLabel.gridx = 0;
        gbcUsernameLabel.gridy = 0;
        gbcUsernameLabel.gridwidth = 2;
        
        JLabel lblUsername = new JLabel("Username");
        lblUsername.setForeground(Color.WHITE);
        lblUsername.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblUsername, gbcUsernameLabel);
        
        // Username panel
        GridBagConstraints gbcUsernamePanel = new GridBagConstraints();
        gbcUsernamePanel.fill = GridBagConstraints.HORIZONTAL;
        gbcUsernamePanel.insets = new Insets(8, 5, 8, 5);
        gbcUsernamePanel.gridx = 0;
        gbcUsernamePanel.gridy = 1;
        gbcUsernamePanel.gridwidth = 2;
        formPanel.add(usernamePanel, gbcUsernamePanel);
        
        // Password field with icon
        JPanel passwordPanel = new JPanel();
        passwordPanel.setLayout(new BorderLayout(10, 0));
        passwordPanel.setBackground(new Color(30, 39, 73));
        
        ImageIcon lockIcon = new ImageIcon(getClass().getResource("/resources/password.png"));
        Image scaledLock = lockIcon.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        lblPasswordIcon = new JLabel(new ImageIcon(scaledLock));

        lblPasswordIcon.setForeground(Color.WHITE);
        lblPasswordIcon.setFont(new Font("Arial", Font.PLAIN, 25));
        passwordPanel.add(lblPasswordIcon, BorderLayout.WEST);
        
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(53, 59, 93), 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        txtPassword.setForeground(Color.BLACK);
        txtPassword.setPreferredSize(new Dimension(300, 40));
        passwordPanel.add(txtPassword, BorderLayout.CENTER);
        
        // Label Password
        GridBagConstraints gbcPasswordLabel = new GridBagConstraints();
        gbcPasswordLabel.fill = GridBagConstraints.HORIZONTAL;
        gbcPasswordLabel.insets = new Insets(15, 5, 8, 5);
        gbcPasswordLabel.gridx = 0;
        gbcPasswordLabel.gridy = 2;
        gbcPasswordLabel.gridwidth = 2;
        
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setForeground(Color.WHITE);
        lblPassword.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblPassword, gbcPasswordLabel);
        
        // Password panel
        GridBagConstraints gbcPasswordPanel = new GridBagConstraints();
        gbcPasswordPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcPasswordPanel.insets = new Insets(8, 5, 8, 5);
        gbcPasswordPanel.gridx = 0;
        gbcPasswordPanel.gridy = 3;
        gbcPasswordPanel.gridwidth = 2;
        formPanel.add(passwordPanel, gbcPasswordPanel);
        
        // Remember me checkbox
        GridBagConstraints gbcRememberMe = new GridBagConstraints();
        gbcRememberMe.fill = GridBagConstraints.HORIZONTAL;
        gbcRememberMe.insets = new Insets(8, 5, 8, 5);
        gbcRememberMe.gridx = 0;
        gbcRememberMe.gridy = 4;
        gbcRememberMe.gridwidth = 1;
        
        chkRememberMe = new JCheckBox("Remember me");
        chkRememberMe.setForeground(Color.WHITE);
        chkRememberMe.setFont(new Font("Arial", Font.PLAIN, 14));
        chkRememberMe.setBackground(new Color(30, 39, 73));
        formPanel.add(chkRememberMe, gbcRememberMe);
        
        // Forgot password link
        GridBagConstraints gbcForgotPassword = new GridBagConstraints();
        gbcForgotPassword.fill = GridBagConstraints.HORIZONTAL;
        gbcForgotPassword.insets = new Insets(8, 30, 8, 5);
        gbcForgotPassword.gridx = 1;
        gbcForgotPassword.gridy = 4;
        gbcForgotPassword.anchor = GridBagConstraints.EAST;
        
        lblForgotPassword = new JLabel("Forgot password?");
        lblForgotPassword.setForeground(new Color(100, 149, 237)); // Cornflower blue
        lblForgotPassword.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblForgotPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(lblForgotPassword, gbcForgotPassword);
        
     // Button panel
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setBackground(new Color(30, 39, 73));

     // Login button
        btnLogin = new JButton("Login");
        btnLogin.setPreferredSize(new Dimension(220, 70)); // Tăng kích thước tổng thể
        btnLogin.setBackground(new Color(0, 122, 204));
        btnLogin.setForeground(Color.BLACK);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(new LineBorder(new Color(0, 90, 150), 2));
        btnLogin.setMargin(new Insets(10, 20, 10, 20)); // Padding bên trong nút

        GridBagConstraints gbcLogin = new GridBagConstraints();
        gbcLogin.insets = new Insets(10, 30, 10, 30);
        gbcLogin.gridx = 0;
        gbcLogin.gridy = 0;
        gbcLogin.fill = GridBagConstraints.NONE;
        buttonPanel.add(btnLogin, gbcLogin);

        // Cancel button
        btnCancel = new JButton("Cancel");
        btnCancel.setPreferredSize(new Dimension(220, 70));
        btnCancel.setBackground(new Color(100, 100, 100));
        btnCancel.setForeground(Color.BLACK);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 18));
        btnCancel.setFocusPainted(false);
        btnCancel.setBorder(new LineBorder(new Color(64, 64, 64), 2));
        btnCancel.setMargin(new Insets(10, 20, 10, 20)); // Padding bên trong nút

        GridBagConstraints gbcCancel = new GridBagConstraints();
        gbcCancel.insets = new Insets(10, 30, 10, 30);
        gbcCancel.gridx = 1;
        gbcCancel.gridy = 0;
        gbcCancel.fill = GridBagConstraints.NONE;
        buttonPanel.add(btnCancel, gbcCancel);



        
        // Add button panel to form
        GridBagConstraints gbcButtonPanel = new GridBagConstraints();
        gbcButtonPanel.fill = GridBagConstraints.HORIZONTAL;
        gbcButtonPanel.insets = new Insets(20, 5, 5, 5);
        gbcButtonPanel.gridx = 0;
        gbcButtonPanel.gridy = 5;
        gbcButtonPanel.gridwidth = 2;
        gbcButtonPanel.anchor = GridBagConstraints.CENTER;
        formPanel.add(buttonPanel, gbcButtonPanel);
        
        // Add form panel to main panel
        mainPanel.add(Box.createVerticalGlue());
        mainPanel.add(formPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());
                // Kiểm tra xem người dùng đã nhập thông tin chưa
                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(LoginForm.this, "Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.", 
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
                } else {
                    String role = UserController.loginAndGetRole(username, password);
                    if (role != null) {
                        JOptionPane.showMessageDialog(LoginForm.this, "Đăng nhập thành công!");
                        CinemaManagementSystem mainFrame = new CinemaManagementSystem(role);
                        mainFrame.setVisible(true);
                        dispose();
                    } else {
                        JOptionPane.showMessageDialog(LoginForm.this, "Tên đăng nhập hoặc mật khẩu không đúng.", 
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });


    }   
    
}