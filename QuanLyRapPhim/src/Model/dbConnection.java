package Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class dbConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/quanlyxemphim";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static Connection connection = null;
    
    static {
        try {
           
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, 
                "Không tìm thấy MySQL JDBC Driver. Vui lòng kiểm tra lại thư viện.",
                "Lỗi kết nối database",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
             
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null,
                    "Không thể kết nối đến database. Vui lòng kiểm tra:\n" +
                    "1. MySQL Server đã được khởi động\n" +
                    "2. Database 'quanlyxemphim' đã được tạo\n" +
                    "3. Username và password chính xác",
                    "Lỗi kết nối database",
                    JOptionPane.ERROR_MESSAGE);
                throw e;
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Đóng kết nối database thành công!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
