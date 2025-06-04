package Controller;

import Model.Room;
import Model.Seat;
import Model.dbConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomController {
    private Connection connection;
    
    public RoomController() {
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
    
    // Room management methods
    public boolean addRoom(Room room) {
        String sql = "INSERT INTO Rooms (room_name, total_seats) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getRoomName());
            pstmt.setInt(2, room.getTotalSeats());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateRoom(Room room) {
        String sql = "UPDATE Rooms SET room_name=?, total_seats=? WHERE room_id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, room.getRoomName());
            pstmt.setInt(2, room.getTotalSeats());
            pstmt.setInt(3, room.getRoomId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteRoom(int roomId) {
        String sql = "DELETE FROM Rooms WHERE room_id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String sql = "SELECT * FROM Rooms";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Room room = new Room(
                    rs.getInt("room_id"),
                    rs.getString("room_name"),
                    rs.getInt("total_seats")
                );
                rooms.add(room);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rooms;
    }
    
    public Room getRoomById(int roomId) {
        String sql = "SELECT * FROM Rooms WHERE room_id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Room(
                    rs.getInt("room_id"),
                    rs.getString("room_name"),
                    rs.getInt("total_seats")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private boolean isRoomNameExists(String roomName) {
        String sql = "SELECT COUNT(*) FROM Rooms WHERE room_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, roomName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Seat management methods
    public boolean addSeat(Seat seat) {
        String sql = "INSERT INTO Seats (room_id, seat_number, status) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, seat.getRoomId());
            pstmt.setString(2, seat.getSeatNumber());
            pstmt.setString(3, seat.getStatus());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean updateSeat(Seat seat) {
        String sql = "UPDATE Seats SET room_id=?, seat_number=?, status=? WHERE seat_id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, seat.getRoomId());
            pstmt.setString(2, seat.getSeatNumber());
            pstmt.setString(3, seat.getStatus());
            pstmt.setInt(4, seat.getSeatId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean deleteSeat(int seatId) {
        String sql = "DELETE FROM Seats WHERE seat_id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, seatId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Seat> getSeatsByRoomId(int roomId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM Seats WHERE room_id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Seat seat = new Seat(
                    rs.getInt("seat_id"),
                    rs.getInt("room_id"),
                    rs.getString("seat_number")
                );
                seat.setStatus(rs.getString("status"));
                seats.add(seat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seats;
    }
    
    private boolean isSeatNumberExists(int roomId, String seatNumber) {
        String sql = "SELECT COUNT(*) FROM Seats WHERE room_id = ? AND seat_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.setString(2, seatNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // Generate seats for a room
    public boolean generateSeatsForRoom(int roomId, int rows, int seatsPerRow) {
        try {
            connection.setAutoCommit(false);
            
            // Delete existing seats for this room
            String deleteSql = "DELETE FROM Seats WHERE room_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSql)) {
                pstmt.setInt(1, roomId);
                pstmt.executeUpdate();
            }
            
            // Insert new seats
            String insertSql = "INSERT INTO Seats (room_id, seat_number, status) VALUES (?, ?, 'available')";
            try (PreparedStatement pstmt = connection.prepareStatement(insertSql)) {
                for (int row = 0; row < rows; row++) {
                    char rowChar = (char)('A' + row);
                    for (int seat = 1; seat <= seatsPerRow; seat++) {
                        String seatNumber = rowChar + String.valueOf(seat);
                        pstmt.setInt(1, roomId);
                        pstmt.setString(2, seatNumber);
                        pstmt.addBatch();
                    }
                }
                pstmt.executeBatch();
            }
            
            // Update room's total seats
            String updateSql = "UPDATE Rooms SET total_seats = ? WHERE room_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(updateSql)) {
                pstmt.setInt(1, rows * seatsPerRow);
                pstmt.setInt(2, roomId);
                pstmt.executeUpdate();
            }
            
            connection.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    
    public boolean hasShowtimes(int roomId) {
        String sql = "SELECT COUNT(*) FROM Showtimes WHERE room_id=?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
} 