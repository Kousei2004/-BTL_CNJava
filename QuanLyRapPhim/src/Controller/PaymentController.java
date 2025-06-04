package Controller;

import Model.dbConnection;
import java.sql.*;
import java.util.Date;
import java.util.Vector;
import java.text.SimpleDateFormat;
import Model.Booking;

public class PaymentController {
    private Connection conn;
    
    public PaymentController() {
        try {
            conn = dbConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Connection getConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                conn = dbConnection.getConnection();
            }
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public boolean processPayment(String method, double amount, String description) {
        String sql = "INSERT INTO Payments (booking_id, amount, payment_method, payment_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, 1); // TODO: Get actual booking_id
            pstmt.setDouble(2, amount);
            pstmt.setString(3, method);
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean addPromotion(String code, String description, int discountPercent, double minTotal, Date validFrom, Date validTo) {
        System.out.println("[DEBUG] addPromotion called with: code=" + code + ", desc=" + description + ", percent=" + discountPercent + ", minTotal=" + minTotal + ", from=" + validFrom + ", to=" + validTo);
        String sql = "INSERT INTO Promotions (code, description, discount_percent, valid_from, valid_to, min_total) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, code);
            pstmt.setString(2, description);
            pstmt.setInt(3, discountPercent);
            pstmt.setDate(4, new java.sql.Date(validFrom.getTime()));
            pstmt.setDate(5, new java.sql.Date(validTo.getTime()));
            pstmt.setDouble(6, minTotal);
            int affected = pstmt.executeUpdate();
            System.out.println("[DEBUG] Rows affected: " + affected);
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            javax.swing.JOptionPane.showMessageDialog(null, e.getMessage(), "SQL Error", javax.swing.JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public Vector<Vector<Object>> generateReport(Date startDate, Date endDate) {
        Vector<Vector<Object>> reportData = new Vector<>();
        String sql = "SELECT DATE(payment_time) as payment_date, " +
                    "payment_method, " +
                    "COUNT(*) as count, " +
                    "SUM(amount) as total " +
                    "FROM Payments " +
                    "WHERE payment_time BETWEEN ? AND ? " +
                    "GROUP BY DATE(payment_time), payment_method " +
                    "ORDER BY payment_date, payment_method";
                    
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(startDate.getTime()));
            pstmt.setDate(2, new java.sql.Date(endDate.getTime()));
            
            ResultSet rs = pstmt.executeQuery();
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(dateFormat.format(rs.getDate("payment_date")));
                row.add(rs.getString("payment_method"));
                row.add(rs.getInt("count"));
                row.add(String.format("%,.0f VNĐ", rs.getDouble("total")));
                reportData.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reportData;
    }
    
    public double calculateDiscount(String promoCode, double totalAmount) {
        String sql = "SELECT discount_percent, min_total FROM Promotions " +
                    "WHERE code = ? AND valid_from <= CURRENT_DATE AND valid_to >= CURRENT_DATE";
                    
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, promoCode);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                double minTotal = rs.getDouble("min_total");
                if (totalAmount >= minTotal) {
                    int discountPercent = rs.getInt("discount_percent");
                    return totalAmount * discountPercent / 100;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return 0;
    }

    // Lấy danh sách booking chờ thanh toán (status = 'confirmed' và chưa có payment)
    public java.util.List<Booking> getPendingBookings() {
        java.util.List<Booking> bookings = new java.util.ArrayList<>();
        String sql = "SELECT b.* FROM bookings b WHERE b.status = 'confirmed' AND b.booking_id NOT IN (SELECT booking_id FROM payments)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setBookingTime(rs.getTimestamp("booking_time"));
                booking.setStatus(rs.getString("status"));
                bookings.add(booking);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bookings;
    }

    // Lấy tổng tiền của booking
    public double getBookingTotalAmount(int bookingId) {
        String sql = "SELECT COALESCE(SUM(unit_price),0) as total FROM bookingdetails WHERE booking_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Thực hiện thanh toán
    public boolean processPayment(int bookingId, double amount, String method) {
        String sql = "INSERT INTO Payments (booking_id, amount, payment_method, payment_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.setDouble(2, amount);
            pstmt.setString(3, method);
            pstmt.setTimestamp(4, new Timestamp(System.currentTimeMillis()));
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                // Cập nhật trạng thái booking nếu muốn
                String updateSql = "UPDATE bookings SET status = 'paid' WHERE booking_id = ?";
                try (PreparedStatement updatePstmt = getConnection().prepareStatement(updateSql)) {
                    updatePstmt.setInt(1, bookingId);
                    updatePstmt.executeUpdate();
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lưu mã khuyến mãi đã áp dụng cho booking
    public boolean savePromotionDetail(String promoCode, int bookingId) {
        String sql = "INSERT INTO PromotionDetails (promotion_id, booking_id) SELECT promotion_id, ? FROM Promotions WHERE code = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            pstmt.setString(2, promoCode);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy tất cả khuyến mãi
    public java.util.List<Object[]> getAllPromotions() {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        String sql = "SELECT * FROM Promotions ORDER BY promotion_id DESC";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getInt("promotion_id");
                row[1] = rs.getString("code");
                row[2] = rs.getInt("discount_percent");
                row[3] = rs.getDate("valid_from");
                row[4] = rs.getDate("valid_to");
                row[5] = rs.getDouble("min_total");
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Sửa khuyến mãi
    public boolean updatePromotion(String code, String description, int percent, double minTotal, Date validFrom, Date validTo) {
        String sql = "UPDATE Promotions SET description=?, discount_percent=?, valid_from=?, valid_to=?, min_total=? WHERE code=?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, description);
            pstmt.setInt(2, percent);
            pstmt.setDate(3, new java.sql.Date(validFrom.getTime()));
            pstmt.setDate(4, new java.sql.Date(validTo.getTime()));
            pstmt.setDouble(5, minTotal);
            pstmt.setString(6, code);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa khuyến mãi
    public boolean deletePromotion(String code) {
        String sql = "DELETE FROM Promotions WHERE code=?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, code);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Lấy danh sách hóa đơn đã thanh toán theo ngày và loại báo cáo
    public java.util.List<Object[]> getPaidInvoices(java.util.Date from, java.util.Date to, String reportType) {
        java.util.List<Object[]> list = new java.util.ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.payment_id, b.booking_id, c.full_name, p.payment_method, p.amount, p.payment_time ");
        sql.append("FROM Payments p ");
        sql.append("JOIN Bookings b ON p.booking_id = b.booking_id ");
        sql.append("JOIN Customers c ON b.customer_id = c.customer_id ");
        sql.append("WHERE 1=1 ");
        if (from != null) {
            sql.append("AND p.payment_time >= ? ");
        }
        if (to != null) {
            sql.append("AND p.payment_time <= ? ");
        }
        // Nếu lọc theo phương thức thanh toán
        if (reportType != null && !reportType.equals("Tất cả") && !reportType.equals("Theo ngày")) {
            // reportType sẽ là "Theo phương thức thanh toán" hoặc tên phương thức cụ thể
            // Nếu là "Theo phương thức thanh toán" thì không cần lọc, nếu là tên phương thức thì lọc
            if (!reportType.equals("Theo phương thức thanh toán")) {
                sql.append("AND p.payment_method = ? ");
            }
        }
        sql.append("ORDER BY p.payment_time DESC");
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql.toString())) {
            int idx = 1;
            if (from != null) {
                pstmt.setTimestamp(idx++, new java.sql.Timestamp(from.getTime()));
            }
            if (to != null) {
                // Để lấy hết ngày đến, cộng thêm 1 ngày trừ đi 1 mili giây
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(to);
                cal.add(java.util.Calendar.DATE, 1);
                cal.add(java.util.Calendar.MILLISECOND, -1);
                pstmt.setTimestamp(idx++, new java.sql.Timestamp(cal.getTimeInMillis()));
            }
            if (reportType != null && !reportType.equals("Tất cả") && !reportType.equals("Theo ngày")) {
                if (!reportType.equals("Theo phương thức thanh toán")) {
                    // Chuyển tên hiển thị sang ENUM trong DB nếu cần
                    String method = reportType;
                    switch (reportType) {
                        case "Tiền mặt": method = "CASH"; break;
                        case "Thẻ tín dụng": method = "CREDIT_CARD"; break;
                        case "MoMo": method = "MOMO"; break;
                        case "Chuyển khoản": method = "BANK_TRANSFER"; break;
                    }
                    pstmt.setString(idx++, method);
                }
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getInt("payment_id");
                row[1] = rs.getInt("booking_id");
                row[2] = rs.getString("full_name");
                row[3] = rs.getString("payment_method");
                row[4] = rs.getDouble("amount");
                row[5] = rs.getTimestamp("payment_time");
                list.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Lấy số vé bán hôm nay
    public int getTodayTicketCount() {
        int count = 0;
        String sql = "SELECT COUNT(*) " +
                     "FROM bookingdetails bd " +
                     "JOIN payments p ON bd.booking_id = p.booking_id " +
                     "WHERE DATE(p.payment_time) = CURDATE()";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    // Lấy tổng doanh thu hôm nay
    public double getTodayRevenue() {
        double total = 0;
        String sql = "SELECT SUM(amount) FROM payments WHERE DATE(payment_time) = CURDATE()";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return total;
    }
} 