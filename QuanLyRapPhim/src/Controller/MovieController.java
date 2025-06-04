package Controller;

import Model.Movie;
import Model.dbConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class MovieController {
    
    // Test kết nối database
    public boolean testConnection() {
        try {
            Connection conn = dbConnection.getConnection();
            if (conn != null && !conn.isClosed()) {
                JOptionPane.showMessageDialog(null, "Kết nối database thành công!");
                return true;
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Lỗi kết nối database: " + e.getMessage(),
                "Lỗi",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return false;
    }
    
    // Thêm phim mới
    public boolean addMovie(Movie movie) {
        String sql = "INSERT INTO Movies (title, genre, duration, description, trailer_url, release_date) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getDuration());
            stmt.setString(4, movie.getDescription());
            stmt.setString(5, movie.getTrailerUrl());
            stmt.setDate(6, movie.getReleaseDate());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Cập nhật thông tin phim
    public boolean updateMovie(Movie movie) {
        String sql = "UPDATE Movies SET title = ?, genre = ?, duration = ?, description = ?, trailer_url = ?, release_date = ? WHERE movie_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, movie.getTitle());
            stmt.setString(2, movie.getGenre());
            stmt.setInt(3, movie.getDuration());
            stmt.setString(4, movie.getDescription());
            stmt.setString(5, movie.getTrailerUrl());
            stmt.setDate(6, movie.getReleaseDate());
            stmt.setInt(7, movie.getMovieId());
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Xóa phim
    public boolean deleteMovie(int movieId) {
        String sql = "DELETE FROM Movies WHERE movie_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, movieId);
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Lấy danh sách tất cả phim
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM Movies";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Movie movie = new Movie(
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getString("genre"),
                    rs.getInt("duration"),
                    rs.getString("description"),
                    rs.getString("trailer_url"),
                    rs.getDate("release_date")
                );
                movies.add(movie);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
    
    // Tìm kiếm phim theo tên
    public List<Movie> searchMovies(String keyword) {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM Movies WHERE title LIKE ? OR genre LIKE ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Movie movie = new Movie(
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getString("genre"),
                    rs.getInt("duration"),
                    rs.getString("description"),
                    rs.getString("trailer_url"),
                    rs.getDate("release_date")
                );
                movies.add(movie);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
    
    // Lấy thông tin phim theo ID
    public Movie getMovieById(int movieId) {
        String sql = "SELECT * FROM Movies WHERE movie_id = ?";
        
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, movieId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return new Movie(
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getString("genre"),
                    rs.getInt("duration"),
                    rs.getString("description"),
                    rs.getString("trailer_url"),
                    rs.getDate("release_date")
                );
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy danh sách phim đang chiếu
    public List<Movie> getNowShowingMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM Movies WHERE release_date <= CURRENT_DATE()";
        
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Movie movie = new Movie(
                    rs.getInt("movie_id"),
                    rs.getString("title"),
                    rs.getString("genre"),
                    rs.getInt("duration"),
                    rs.getString("description"),
                    rs.getString("trailer_url"),
                    rs.getDate("release_date")
                );
                movies.add(movie);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return movies;
    }
} 