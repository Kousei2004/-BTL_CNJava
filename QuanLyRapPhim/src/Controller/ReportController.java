package Controller;

import java.sql.*;
import java.util.*;
import Model.*;
import java.time.LocalDate;

public class ReportController {
    // Lấy doanh thu theo khoảng thời gian
    public List<Object[]> getRevenueByDateRange(LocalDate from, LocalDate to) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT DATE(b.booking_time) as date, " +
                    "SUM(bd.unit_price) as revenue, " +
                    "COUNT(*) as ticket_count, " +
                    "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM seats), 2) as occupancy_rate " +
                    "FROM bookings b " +
                    "JOIN bookingdetails bd ON b.booking_id = bd.booking_id " +
                    "WHERE b.booking_time BETWEEN ? AND ? " +
                    "GROUP BY DATE(b.booking_time) " +
                    "ORDER BY date";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(to));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getDate("date"),
                    rs.getDouble("revenue"),
                    rs.getInt("ticket_count"),
                    rs.getDouble("occupancy_rate")
                };
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Lấy doanh thu theo phim
    public List<Object[]> getRevenueByMovie(LocalDate from, LocalDate to) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT m.title, " +
                    "SUM(bd.unit_price) as revenue, " +
                    "COUNT(*) as ticket_count, " +
                    "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM seats), 2) as occupancy_rate " +
                    "FROM bookings b " +
                    "JOIN bookingdetails bd ON b.booking_id = bd.booking_id " +
                    "JOIN tickets t ON bd.ticket_id = t.ticket_id " +
                    "JOIN showtimes s ON t.showtime_id = s.showtime_id " +
                    "JOIN movies m ON s.movie_id = m.movie_id " +
                    "WHERE b.booking_time BETWEEN ? AND ? " +
                    "GROUP BY m.movie_id, m.title " +
                    "ORDER BY revenue DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(to));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("title"),
                    rs.getDouble("revenue"),
                    rs.getInt("ticket_count"),
                    rs.getDouble("occupancy_rate")
                };
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Lấy doanh thu theo suất chiếu
    public List<Object[]> getRevenueByShowtime(LocalDate from, LocalDate to) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT TIME(s.start_time) as time, " +
                    "SUM(bd.unit_price) as revenue, " +
                    "COUNT(*) as ticket_count, " +
                    "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM seats), 2) as occupancy_rate " +
                    "FROM bookings b " +
                    "JOIN bookingdetails bd ON b.booking_id = bd.booking_id " +
                    "JOIN tickets t ON bd.ticket_id = t.ticket_id " +
                    "JOIN showtimes s ON t.showtime_id = s.showtime_id " +
                    "WHERE b.booking_time BETWEEN ? AND ? " +
                    "GROUP BY TIME(s.start_time) " +
                    "ORDER BY revenue DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(to));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getTime("time").toString(),
                    rs.getDouble("revenue"),
                    rs.getInt("ticket_count"),
                    rs.getDouble("occupancy_rate")
                };
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Lấy doanh thu theo phòng chiếu
    public List<Object[]> getRevenueByRoom(LocalDate from, LocalDate to) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT r.room_name, " +
                    "SUM(bd.unit_price) as revenue, " +
                    "COUNT(*) as ticket_count, " +
                    "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM seats), 2) as occupancy_rate " +
                    "FROM bookings b " +
                    "JOIN bookingdetails bd ON b.booking_id = bd.booking_id " +
                    "JOIN tickets t ON bd.ticket_id = t.ticket_id " +
                    "JOIN showtimes s ON t.showtime_id = s.showtime_id " +
                    "JOIN rooms r ON s.room_id = r.room_id " +
                    "WHERE b.booking_time BETWEEN ? AND ? " +
                    "GROUP BY r.room_id, r.room_name " +
                    "ORDER BY revenue DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(to));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("room_name"),
                    rs.getDouble("revenue"),
                    rs.getInt("ticket_count"),
                    rs.getDouble("occupancy_rate")
                };
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Lấy doanh thu theo loại vé
    public List<Object[]> getRevenueByTicketType(LocalDate from, LocalDate to) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT " +
                    "CASE " +
                    "    WHEN t.price <= 50000 THEN 'Vé thường' " +
                    "    WHEN t.price <= 100000 THEN 'Vé VIP' " +
                    "    ELSE 'Vé đặc biệt' " +
                    "END as ticket_type, " +
                    "SUM(bd.unit_price) as revenue, " +
                    "COUNT(*) as ticket_count, " +
                    "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM seats), 2) as occupancy_rate " +
                    "FROM bookings b " +
                    "JOIN bookingdetails bd ON b.booking_id = bd.booking_id " +
                    "JOIN tickets t ON bd.ticket_id = t.ticket_id " +
                    "WHERE b.booking_time BETWEEN ? AND ? " +
                    "GROUP BY " +
                    "    CASE " +
                    "        WHEN t.price <= 50000 THEN 'Vé thường' " +
                    "        WHEN t.price <= 100000 THEN 'Vé VIP' " +
                    "        ELSE 'Vé đặc biệt' " +
                    "    END " +
                    "ORDER BY revenue DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(to));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("ticket_type"),
                    rs.getDouble("revenue"),
                    rs.getInt("ticket_count"),
                    rs.getDouble("occupancy_rate")
                };
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Lấy thống kê khách hàng
    public List<Object[]> getCustomerStatistics(LocalDate from, LocalDate to) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT c.full_name, " +
                    "COUNT(DISTINCT b.booking_id) as booking_count, " +
                    "SUM(bd.unit_price) as total_spent, " +
                    "COUNT(t.ticket_id) as ticket_count " +
                    "FROM customers c " +
                    "JOIN bookings b ON c.customer_id = b.customer_id " +
                    "JOIN bookingdetails bd ON b.booking_id = bd.booking_id " +
                    "JOIN tickets t ON bd.ticket_id = t.ticket_id " +
                    "WHERE b.booking_time BETWEEN ? AND ? " +
                    "GROUP BY c.customer_id, c.full_name " +
                    "ORDER BY total_spent DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(to));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getString("full_name"),
                    rs.getDouble("total_spent"),
                    rs.getInt("ticket_count"),
                    rs.getInt("booking_count")
                };
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    
    // Lấy tất cả thống kê
    public List<Object[]> getAllStatistics(LocalDate from, LocalDate to) {
        List<Object[]> result = new ArrayList<>();
        String sql = "SELECT " +
                    "DATE(b.booking_time) as date, " +
                    "m.title as movie_title, " +
                    "r.room_name, " +
                    "TIME(s.start_time) as showtime, " +
                    "SUM(bd.unit_price) as revenue, " +
                    "COUNT(t.ticket_id) as ticket_count, " +
                    "ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM seats), 2) as occupancy_rate " +
                    "FROM bookings b " +
                    "JOIN bookingdetails bd ON b.booking_id = bd.booking_id " +
                    "JOIN tickets t ON bd.ticket_id = t.ticket_id " +
                    "JOIN showtimes s ON t.showtime_id = s.showtime_id " +
                    "JOIN movies m ON s.movie_id = m.movie_id " +
                    "JOIN rooms r ON s.room_id = r.room_id " +
                    "WHERE b.booking_time BETWEEN ? AND ? " +
                    "GROUP BY DATE(b.booking_time), m.movie_id, r.room_id, s.showtime_id " +
                    "ORDER BY date, revenue DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setDate(1, java.sql.Date.valueOf(from));
            stmt.setDate(2, java.sql.Date.valueOf(to));
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Object[] row = {
                    rs.getDate("date"),
                    rs.getString("movie_title"),
                    rs.getString("room_name"),
                    rs.getTime("showtime").toString(),
                    rs.getDouble("revenue"),
                    rs.getInt("ticket_count"),
                    rs.getDouble("occupancy_rate")
                };
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public List<Object[]> getRevenueByWeek() {
        List<Object[]> result = new ArrayList<>();
        String query = "SELECT " +
            "DATE_FORMAT(b.booking_time, '%Y-%m-%d') as week, " +
            "SUM(bd.unit_price) as total_revenue, " +
            "COUNT(*) as total_tickets, " +
            "ROUND(COUNT(*) * 100.0 / ( " +
            "    SELECT COUNT(*) " +
            "    FROM bookingdetails bd2 " +
            "    JOIN bookings b2 ON bd2.booking_id = b2.booking_id " +
            "    WHERE DATE(b2.booking_time) BETWEEN DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND CURDATE() " +
            "), 2) as percentage " +
            "FROM bookings b " +
            "JOIN bookingdetails bd ON b.booking_id = bd.booking_id " +
            "WHERE b.booking_time BETWEEN DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND CURDATE() " +
            "GROUP BY DATE_FORMAT(b.booking_time, '%Y-%m-%d') " +
            "ORDER BY week DESC";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getDate("week");
                row[1] = rs.getDouble("total_revenue");
                row[2] = rs.getInt("total_tickets");
                row[3] = rs.getDouble("percentage");
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }

    public List<Object[]> getTopMoviesInWeek() {
        List<Object[]> result = new ArrayList<>();
        String query = "SELECT " +
            "m.title, " +
            "COUNT(t.ticket_id) as ticket_count, " +
            "SUM(bd.unit_price) as revenue, " +
            "ROUND(COUNT(t.ticket_id) * 100.0 / ( " +
            "    SELECT COUNT(*) FROM tickets t2 " +
            "    JOIN bookingdetails bd2 ON t2.ticket_id = bd2.ticket_id " +
            "    JOIN bookings b2 ON bd2.booking_id = b2.booking_id " +
            "    WHERE DATE(b2.booking_time) BETWEEN DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND CURDATE() " +
            "), 2) as percentage " +
            "FROM movies m " +
            "JOIN showtimes s ON m.movie_id = s.movie_id " +
            "JOIN tickets t ON s.showtime_id = t.showtime_id " +
            "JOIN bookingdetails bd ON t.ticket_id = bd.ticket_id " +
            "JOIN bookings b ON bd.booking_id = b.booking_id " +
            "WHERE DATE(b.booking_time) BETWEEN DATE_SUB(CURDATE(), INTERVAL 7 DAY) AND CURDATE() " +
            "GROUP BY m.movie_id, m.title " +
            "ORDER BY ticket_count DESC " +
            "LIMIT 5";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Object[] row = new Object[4];
                row[0] = rs.getString("title");
                row[1] = rs.getInt("ticket_count");
                row[2] = rs.getDouble("revenue");
                row[3] = rs.getDouble("percentage");
                result.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return result;
    }
} 