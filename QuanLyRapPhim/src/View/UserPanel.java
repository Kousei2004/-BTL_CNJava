package View;

import Controller.UserController;
import Model.User;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.List;

public class UserPanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private UserController userController;
	private JTable userTable;
	private DefaultTableModel tableModel;
	private JTextField usernameField, passwordField, fullNameField;
	private JComboBox<String> roleComboBox;
	private JButton addButton, updateButton, deleteButton, clearButton;
	private int selectedUserId = -1;

	public UserPanel() {
		userController = new UserController();
		setLayout(new BorderLayout());
		setBackground(new Color(236, 240, 241));
		
		// Create components
		createComponents();
		
		// Load initial data
		loadUserData();
	}

	private void createComponents() {
		// Title Panel
		JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		titlePanel.setBackground(new Color(236, 240, 241));
		titlePanel.setBorder(new EmptyBorder(20, 20, 10, 20));
		
		JLabel titleLabel = new JLabel("Quản lý nhân viên");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		titleLabel.setForeground(new Color(44, 62, 80));
		titlePanel.add(titleLabel);
		
		// Form Panel
		JPanel formPanel = createFormPanel();
		
		// Table Panel
		JPanel tablePanel = createTablePanel();
		
		// Add panels to main panel
		add(titlePanel, BorderLayout.NORTH);
		add(formPanel, BorderLayout.WEST);
		add(tablePanel, BorderLayout.CENTER);
	}

	private JPanel createFormPanel() {
		JPanel formPanel = new JPanel(new GridBagLayout());
		formPanel.setBackground(Color.WHITE);
		formPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(230, 230, 230)),
			new EmptyBorder(15, 15, 15, 15)
		));
		formPanel.setPreferredSize(new Dimension(340, 0));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 4, 8, 4);
		gbc.anchor = GridBagConstraints.WEST;

		// Khởi tạo các component
		usernameField = new JTextField();
		passwordField = new JPasswordField();
		fullNameField = new JTextField();
		String[] roles = {"admin", "staff"};
		roleComboBox = new JComboBox<>(roles);

		// Style input
		Font inputFont = new Font("Arial", Font.PLAIN, 15);
		Dimension inputSize = new Dimension(180, 32);
		for (JComponent comp : new JComponent[]{usernameField, passwordField, fullNameField, roleComboBox}) {
			comp.setFont(inputFont);
			comp.setPreferredSize(inputSize);
			comp.setMinimumSize(inputSize);
			comp.setMaximumSize(inputSize);
			comp.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200)),
				new EmptyBorder(4, 10, 4, 10)
			));
		}

		// Label style
		Font labelFont = new Font("Arial", Font.BOLD, 15);

		// Tên đăng nhập
		gbc.gridx = 0; gbc.gridy = 0; gbc.anchor = GridBagConstraints.EAST;
		formPanel.add(new JLabel("Tên đăng nhập:") {{ setFont(labelFont); setForeground(new Color(44,62,80)); setPreferredSize(new Dimension(110, 32)); }}, gbc);
		gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
		formPanel.add(usernameField, gbc);

		// Mật khẩu
		gbc.gridx = 0; gbc.gridy = 1; gbc.anchor = GridBagConstraints.EAST;
		formPanel.add(new JLabel("Mật khẩu:") {{ setFont(labelFont); setForeground(new Color(44,62,80)); setPreferredSize(new Dimension(110, 32)); }}, gbc);
		gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
		formPanel.add(passwordField, gbc);

		// Họ và tên
		gbc.gridx = 0; gbc.gridy = 2; gbc.anchor = GridBagConstraints.EAST;
		formPanel.add(new JLabel("Họ và tên:") {{ setFont(labelFont); setForeground(new Color(44,62,80)); setPreferredSize(new Dimension(110, 32)); }}, gbc);
		gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
		formPanel.add(fullNameField, gbc);

		// Vai trò
		gbc.gridx = 0; gbc.gridy = 3; gbc.anchor = GridBagConstraints.EAST;
		formPanel.add(new JLabel("Vai trò:") {{ setFont(labelFont); setForeground(new Color(44,62,80)); setPreferredSize(new Dimension(110, 32)); }}, gbc);
		gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
		formPanel.add(roleComboBox, gbc);

		// Nút
		gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2; gbc.anchor = GridBagConstraints.CENTER;
		JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 6, 6));
		buttonPanel.setBackground(Color.WHITE);
		// Khởi tạo các nút trước khi style
		addButton = new JButton("Thêm mới");
		updateButton = new JButton("Cập nhật");
		deleteButton = new JButton("Xóa");
		clearButton = new JButton("Xóa form");

		// Style các nút
		styleButton(addButton, new Color(46, 204, 113));
		styleButton(updateButton, new Color(52, 152, 219));
		styleButton(deleteButton, new Color(231, 76, 60));
		styleButton(clearButton, new Color(149, 165, 166));
		buttonPanel.add(addButton);
		buttonPanel.add(updateButton);
		buttonPanel.add(deleteButton);
		buttonPanel.add(clearButton);
		formPanel.add(buttonPanel, gbc);

		// Add action listeners
		addButton.addActionListener(e -> addUser());
		updateButton.addActionListener(e -> updateUser());
		deleteButton.addActionListener(e -> deleteUser());
		clearButton.addActionListener(e -> clearForm());

		return formPanel;
	}

	private JPanel createTablePanel() {
		JPanel tablePanel = new JPanel(new BorderLayout());
		tablePanel.setBackground(Color.WHITE);
		tablePanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(new Color(230, 230, 230)),
			new EmptyBorder(15, 15, 15, 15)
		));

		// Create table model
		String[] columns = {"ID", "Tên đăng nhập", "Họ và tên", "Vai trò", "Ngày tạo"};
		tableModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};
		
		userTable = new JTable(tableModel);
		userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				int row = userTable.getSelectedRow();
				if (row != -1) {
					selectedUserId = (int) tableModel.getValueAt(row, 0);
					loadUserToForm(selectedUserId);
				}
			}
		});
		
		// Style table
		userTable.setRowHeight(32);
		userTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
		userTable.setFont(new Font("Arial", Font.PLAIN, 14));
		userTable.setGridColor(new Color(230, 230, 230));
		userTable.setShowGrid(true);
		userTable.setShowHorizontalLines(true);
		userTable.setShowVerticalLines(true);
		
		// Style header
	
		
		JScrollPane scrollPane = new JScrollPane(userTable);
		scrollPane.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
		tablePanel.add(scrollPane, BorderLayout.CENTER);
		
		return tablePanel;
	}

	private void styleFormComponents(JComponent... components) {
		for (JComponent component : components) {
			if (component instanceof JLabel) {
				component.setFont(new Font("Arial", Font.BOLD, 14));
			} else if (component instanceof JTextField) {
				component.setFont(new Font("Arial", Font.PLAIN, 14));
				component.setPreferredSize(new Dimension(250, 30));
			} else if (component instanceof JComboBox) {
				component.setFont(new Font("Arial", Font.PLAIN, 14));
				component.setPreferredSize(new Dimension(250, 30));
			}
		}
	}

	private void styleButton(JButton button, Color color) {
		button.setBackground(color);
		button.setForeground(Color.WHITE);
		button.setFocusPainted(false);
		button.setBorderPainted(false);
		button.setFont(new Font("Arial", Font.BOLD, 11));
		button.setCursor(new Cursor(Cursor.HAND_CURSOR));
		button.setBorder(new EmptyBorder(4, 8, 4, 8));
		button.setPreferredSize(new Dimension(80, 24));
		
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

	private void loadUserData() {
		tableModel.setRowCount(0);
		List<User> users = userController.getAllUsers();
		for (User user : users) {
			Object[] row = {
				user.getUserId(),
				user.getUsername(),
				user.getFullName(),
				user.getRole(),
				user.getCreatedAt()
			};
			tableModel.addRow(row);
		}
	}

	private void loadUserToForm(int userId) {
		User user = userController.getUserById(userId);
		if (user != null) {
			usernameField.setText(user.getUsername());
			passwordField.setText(user.getPassword());
			fullNameField.setText(user.getFullName());
			roleComboBox.setSelectedItem(user.getRole());
		}
	}

	private void addUser() {
		String username = usernameField.getText().trim();
		String password = passwordField.getText().trim();
		String fullName = fullNameField.getText().trim();
		String role = (String) roleComboBox.getSelectedItem();

		if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
			return;
		}

		User newUser = new User();
		newUser.setUsername(username);
		newUser.setPassword(password);
		newUser.setFullName(fullName);
		newUser.setRole(role);
		newUser.setCreatedAt(new Date());

		if (userController.addUser(newUser)) {
			JOptionPane.showMessageDialog(this, "Thêm người dùng thành công!");
			clearForm();
			loadUserData();
		}
	}

	private void updateUser() {
		if (selectedUserId == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần cập nhật!");
			return;
		}

		String username = usernameField.getText().trim();
		String password = passwordField.getText().trim();
		String fullName = fullNameField.getText().trim();
		String role = (String) roleComboBox.getSelectedItem();

		if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng điền đầy đủ thông tin!");
			return;
		}

		User user = new User();
		user.setUserId(selectedUserId);
		user.setUsername(username);
		user.setPassword(password);
		user.setFullName(fullName);
		user.setRole(role);
		user.setCreatedAt(new Date());

		if (userController.updateUser(user)) {
			JOptionPane.showMessageDialog(this, "Cập nhật người dùng thành công!");
			clearForm();
			loadUserData();
		}
	}

	private void deleteUser() {
		if (selectedUserId == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn người dùng cần xóa!");
			return;
		}

		int confirm = JOptionPane.showConfirmDialog(this,
			"Bạn có chắc chắn muốn xóa người dùng này?",
			"Xác nhận xóa",
			JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			if (userController.deleteUser(selectedUserId)) {
				JOptionPane.showMessageDialog(this, "Xóa người dùng thành công!");
				clearForm();
				loadUserData();
			}
		}
	}

	private void clearForm() {
		usernameField.setText("");
		passwordField.setText("");
		fullNameField.setText("");
		roleComboBox.setSelectedIndex(0);
		selectedUserId = -1;
		userTable.clearSelection();
	}
}
