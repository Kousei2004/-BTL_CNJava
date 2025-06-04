package Controller;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

import Model.User;
import Model.dbConnection;

public class UserController {

    // Hàm login kiểm tra username và mật khẩu đã mã hóa
	public static boolean login(String username, String password) {
	    String query = "SELECT * FROM Users WHERE username = ? AND password = ?";
	    String hashedPassword = hashPassword(password); // mã hóa mật khẩu nhập vào

	    try (Connection conn = dbConnection.getConnection();
	         PreparedStatement stmt = conn.prepareStatement(query)) {

	        stmt.setString(1, username);
	        stmt.setString(2, hashedPassword);

	        ResultSet rs = stmt.executeQuery();
	        return rs.next(); // nếu có dữ liệu trả về -> đăng nhập thành công

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

    // Hàm login và lấy role của user
    public static String loginAndGetRole(String username, String password) {
        String query = "SELECT role FROM Users WHERE username = ? AND password = ?";
        String hashedPassword = hashPassword(password);

        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("role");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                User user = new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("full_name"),
                    rs.getDate("created_at")
                );
                users.add(user);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy danh sách người dùng: " + e.getMessage());
        }
        return users;
    }
    
    public boolean addUser(User user) {
        String query = "INSERT INTO users (username, password, role, full_name, created_at) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, hashPassword(user.getPassword())); // Mã hóa mật khẩu trước khi lưu
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getFullName());
            pstmt.setDate(5, new java.sql.Date(user.getCreatedAt().getTime()));
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi thêm người dùng: " + e.getMessage());
            return false;
        }
    }
    
    public boolean updateUser(User user) {
        String query = "UPDATE users SET username=?, password=?, role=?, full_name=? WHERE user_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, user.getUsername());
            // Chỉ mã hóa mật khẩu nếu có thay đổi (không phải chuỗi rỗng)
            String password = user.getPassword().trim();
            if (!password.isEmpty()) {
                pstmt.setString(2, hashPassword(password));
            } else {
                // Nếu không có thay đổi mật khẩu, giữ nguyên mật khẩu cũ
                User oldUser = getUserById(user.getUserId());
                pstmt.setString(2, oldUser.getPassword());
            }
            pstmt.setString(3, user.getRole());
            pstmt.setString(4, user.getFullName());
            pstmt.setInt(5, user.getUserId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi cập nhật người dùng: " + e.getMessage());
            return false;
        }
    }
    
    public boolean deleteUser(int userId) {
        String query = "DELETE FROM users WHERE user_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi xóa người dùng: " + e.getMessage());
            return false;
        }
    }
    
    public User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE user_id=?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new User(
                    rs.getInt("user_id"),
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("role"),
                    rs.getString("full_name"),
                    rs.getDate("created_at")
                );
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Lỗi khi lấy thông tin người dùng: " + e.getMessage());
        }
        return null;
    }

    // ======================
    // ✅ Hàm mã hóa SHA-256
    // ======================
    private static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashed = md.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Không hỗ trợ SHA-256", e);
        }
    }
}
