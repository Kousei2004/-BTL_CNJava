package Controller;

import Model.Showtime;
import Model.dbConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.math.BigDecimal;

public class ShowtimeController {
    private Connection connection;
    
    public ShowtimeController() {
        try {
            this.connection = dbConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = dbConnection.getConnection();
            }
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Add a new showtime
    public boolean addShowtime(Showtime showtime) {
        String sql = "INSERT INTO Showtimes (movie_id, room_id, show_date, start_time, end_time, base_price) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, showtime.getMovieId());
            pstmt.setInt(2, showtime.getRoomId());
            pstmt.setDate(3, new java.sql.Date(showtime.getShowDate().getTime()));
            pstmt.setTime(4, showtime.getStartTime());
            pstmt.setTime(5, showtime.getEndTime());
            pstmt.setBigDecimal(6, showtime.getBasePrice());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update an existing showtime
    public boolean updateShowtime(Showtime showtime) {
        String sql = "UPDATE Showtimes SET movie_id=?, room_id=?, show_date=?, start_time=?, end_time=?, base_price=? WHERE showtime_id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, showtime.getMovieId());
            pstmt.setInt(2, showtime.getRoomId());
            pstmt.setDate(3, new java.sql.Date(showtime.getShowDate().getTime()));
            pstmt.setTime(4, showtime.getStartTime());
            pstmt.setTime(5, showtime.getEndTime());
            pstmt.setBigDecimal(6, showtime.getBasePrice());
            pstmt.setInt(7, showtime.getShowtimeId());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete a showtime
    public boolean deleteShowtime(int showtimeId) {
        String sql = "DELETE FROM Showtimes WHERE showtime_id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, showtimeId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all showtimes
    public List<Showtime> getAllShowtimes() {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT * FROM Showtimes";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Showtime showtime = new Showtime(
                    rs.getInt("showtime_id"),
                    rs.getInt("movie_id"),
                    rs.getInt("room_id"),
                    rs.getDate("show_date"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time"),
                    rs.getBigDecimal("base_price")
                );
                showtimes.add(showtime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }
    
    // Get showtime by ID
    public Showtime getShowtimeById(int showtimeId) {
        String sql = "SELECT * FROM Showtimes WHERE showtime_id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, showtimeId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Showtime(
                    rs.getInt("showtime_id"),
                    rs.getInt("movie_id"),
                    rs.getInt("room_id"),
                    rs.getDate("show_date"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time"),
                    rs.getBigDecimal("base_price")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Search showtimes by movie
    public List<Showtime> searchShowtimesByMovie(int movieId) {
        List<Showtime> showtimes = new ArrayList<>();
        // Select showtimes for a movie that are currently in the future or today after the current time
        String sql = "SELECT * FROM Showtimes WHERE movie_id=? AND (show_date > CURRENT_DATE OR (show_date = CURRENT_DATE AND start_time > CURRENT_TIME)) ORDER BY show_date ASC, start_time ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, movieId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Showtime showtime = new Showtime(
                    rs.getInt("showtime_id"),
                    rs.getInt("movie_id"),
                    rs.getInt("room_id"),
                    rs.getDate("show_date"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time"),
                    rs.getBigDecimal("base_price")
                );
                showtimes.add(showtime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }
    
    // Search showtimes by room
    public List<Showtime> searchShowtimesByRoom(int roomId) {
        List<Showtime> showtimes = new ArrayList<>();
        // Select showtimes for a room that are currently in the future or today after the current time
        String sql = "SELECT * FROM Showtimes WHERE room_id=? AND (show_date > CURRENT_DATE OR (show_date = CURRENT_DATE AND start_time > CURRENT_TIME)) ORDER BY show_date ASC, start_time ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Showtime showtime = new Showtime(
                    rs.getInt("showtime_id"),
                    rs.getInt("movie_id"),
                    rs.getInt("room_id"),
                    rs.getDate("show_date"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time"),
                    rs.getBigDecimal("base_price")
                );
                showtimes.add(showtime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }
    
    // Search showtimes by date
    public List<Showtime> searchShowtimesByDate(Date date) {
        List<Showtime> showtimes = new ArrayList<>();
        // Select showtimes for a specific date that are currently in the future or today after the current time
        String sql = "SELECT * FROM Showtimes WHERE show_date=? AND (show_date > CURRENT_DATE OR (show_date = CURRENT_DATE AND start_time > CURRENT_TIME)) ORDER BY show_date ASC, start_time ASC";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDate(1, new java.sql.Date(date.getTime()));
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Showtime showtime = new Showtime(
                    rs.getInt("showtime_id"),
                    rs.getInt("movie_id"),
                    rs.getInt("room_id"),
                    rs.getDate("show_date"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time"),
                    rs.getBigDecimal("base_price")
                );
                showtimes.add(showtime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }
    
    // Get all movies for combobox
    public Map<Integer, String> getAllMovies() {
        Map<Integer, String> movies = new HashMap<>();
        String sql = "SELECT movie_id, title FROM Movies";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                movies.put(rs.getInt("movie_id"), rs.getString("title"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
    
    // Get all rooms for combobox
    public Map<Integer, String> getAllRooms() {
        Map<Integer, String> rooms = new HashMap<>();
        String sql = "SELECT room_id, room_name FROM Rooms";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                rooms.put(rs.getInt("room_id"), rs.getString("room_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
    
    // Get all current/future showtimes
    public List<Showtime> getAllCurrentShowtimes() {
        List<Showtime> showtimes = new ArrayList<>();
        // Select all showtimes that are currently in the future or today after the current time
        String sql = "SELECT * FROM Showtimes WHERE show_date > CURRENT_DATE OR (show_date = CURRENT_DATE AND start_time > CURRENT_TIME) ORDER BY show_date ASC, start_time ASC";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Showtime showtime = new Showtime(
                    rs.getInt("showtime_id"),
                    rs.getInt("movie_id"),
                    rs.getInt("room_id"),
                    rs.getDate("show_date"),
                    rs.getTime("start_time"),
                    rs.getTime("end_time"),
                    rs.getBigDecimal("base_price")
                );
                showtimes.add(showtime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }

    // Get movie name by ID
    public String getMovieName(int movieId) {
        String sql = "SELECT title FROM movies WHERE movie_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("title");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Movie";
    }
    
    // Get room name by ID
    public String getRoomName(int roomId) {
        String sql = "SELECT room_name FROM rooms WHERE room_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("room_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Room";
    }

    public List<Showtime> getShowtimesByRoomAndDate(int roomId, java.util.Date date) {
        List<Showtime> showtimes = new ArrayList<>();
        String sql = "SELECT * FROM showtimes WHERE room_id = ? AND DATE(show_date) = DATE(?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, roomId);
            stmt.setDate(2, new java.sql.Date(date.getTime()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Showtime showtime = new Showtime();
                showtime.setShowtimeId(rs.getInt("showtime_id"));
                showtime.setMovieId(rs.getInt("movie_id"));
                showtime.setRoomId(rs.getInt("room_id"));
                showtime.setShowDate(rs.getDate("show_date"));
                showtime.setStartTime(rs.getTime("start_time"));
                showtime.setEndTime(rs.getTime("end_time"));
                showtime.setBasePrice(rs.getBigDecimal("base_price"));
                showtimes.add(showtime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }

    public List<Showtime> searchShowtimesByDateRange(java.util.Date startDate, java.util.Date endDate) {
        List<Showtime> showtimes = new ArrayList<>();
        // Select showtimes within a date range that are currently in the future or today after the current time
        String sql = "SELECT * FROM showtimes WHERE DATE(show_date) BETWEEN DATE(?) AND DATE(?) AND (show_date > CURRENT_DATE OR (show_date = CURRENT_DATE AND start_time > CURRENT_TIME)) ORDER BY show_date ASC, start_time ASC";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDate(1, new java.sql.Date(startDate.getTime()));
            stmt.setDate(2, new java.sql.Date(endDate.getTime()));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Showtime showtime = new Showtime();
                showtime.setShowtimeId(rs.getInt("showtime_id"));
                showtime.setMovieId(rs.getInt("movie_id"));
                showtime.setRoomId(rs.getInt("room_id"));
                showtime.setShowDate(rs.getDate("show_date"));
                showtime.setStartTime(rs.getTime("start_time"));
                showtime.setEndTime(rs.getTime("end_time"));
                showtime.setBasePrice(rs.getBigDecimal("base_price"));
                showtimes.add(showtime);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return showtimes;
    }
} 