package Controller;

import Model.*;
import Model.dbConnection;
import Model.Booking;
import Model.BookingDetail;
import Model.Customer;
import Model.Movie;
import Model.Room;
import Model.Seat;
import Model.Showtime;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class BookingController {
    private MovieController movieController;
    private ShowtimeController showtimeController;
    private CustomerController customerController;
    private RoomController roomController;
    private Connection connection;
    
    public BookingController() {
        this.movieController = new MovieController();
        this.showtimeController = new ShowtimeController();
        this.customerController = new CustomerController();
        this.roomController = new RoomController();
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
    
    // Load danh sách phim
    public List<String> loadMovies() {
        List<Movie> movies = movieController.getAllMovies();
        List<String> movieTitles = new ArrayList<>();
        for (Movie movie : movies) {
            movieTitles.add(movie.getTitle());
        }
        return movieTitles;
    }
    
    // Load suất chiếu theo phim
    public List<String> loadShowtimes(String movieTitle) {
        List<String> showtimeInfos = new ArrayList<>();
        List<Movie> movies = movieController.searchMovies(movieTitle);
        
        if (!movies.isEmpty()) {
            int movieId = movies.get(0).getMovieId();
            List<Showtime> showtimes = showtimeController.searchShowtimesByMovie(movieId);
            
            for (Showtime showtime : showtimes) {
                // Lấy thông tin phòng chiếu
                Room room = roomController.getRoomById(showtime.getRoomId());
                String roomName = room != null ? room.getRoomName() : "Phòng không xác định";
                
                String showtimeInfo = String.format("%s - %s - Phòng %s", 
                    showtime.getShowDate().toString(),
                    showtime.getStartTime().toString(),
                    roomName);
                showtimeInfos.add(showtimeInfo);
            }
        }
        return showtimeInfos;
    }
    
    // Load danh sách khách hàng
    public List<String> loadCustomers() {
        List<Customer> customers = customerController.getAllCustomers();
        List<String> customerNames = new ArrayList<>();
        for (Customer customer : customers) {
            customerNames.add(customer.getFullName());
        }
        return customerNames;
    }
    
    // Thêm khách hàng mới
    public boolean addCustomer(String fullName, String phone, String email) {
        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setPhone(phone);
        customer.setEmail(email);
        return customerController.addCustomer(customer);
    }
    
    // Lấy thông tin phim theo tên
    public Movie getMovieByTitle(String title) {
        List<Movie> movies = movieController.searchMovies(title);
        if (!movies.isEmpty()) {
            return movies.get(0);
        }
        return null;
    }
    
    // Lấy thông tin suất chiếu theo phim và thời gian
    public Showtime getShowtimeByMovieAndTime(int movieId, String showtimeInfo) {
        List<Showtime> showtimes = showtimeController.searchShowtimesByMovie(movieId);
        for (Showtime showtime : showtimes) {
            Room room = roomController.getRoomById(showtime.getRoomId());
            String roomName = room != null ? room.getRoomName() : "Phòng không xác định";
            
            String currentShowtimeInfo = String.format("%s - %s - Phòng %s", 
                showtime.getShowDate().toString(),
                showtime.getStartTime().toString(),
                roomName);
            if (currentShowtimeInfo.equals(showtimeInfo)) {
                return showtime;
            }
        }
        return null;
    }
    
    // Lấy thông tin khách hàng theo tên
    public Customer getCustomerByName(String name) {
        String sql = "SELECT * FROM Customers WHERE full_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFullName(rs.getString("full_name"));
                customer.setPhone(rs.getString("phone"));
                customer.setEmail(rs.getString("email"));
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    // Lấy thông tin phòng theo ID
    public Room getRoomById(int roomId) {
        return roomController.getRoomById(roomId);
    }
    
    // Lấy danh sách ghế của phòng
    public List<Seat> getSeatsByRoomId(int roomId) {
        return roomController.getSeatsByRoomId(roomId);
    }
    
    // Lấy trạng thái ghế theo phòng và suất chiếu
    public Map<String, String> getSeatStatus(int roomId, int showtimeId) {
        Map<String, String> seatStatus = new HashMap<>();
        List<Seat> seats = roomController.getSeatsByRoomId(roomId);
        
        // Đầu tiên, set tất cả là available
        for (Seat seat : seats) {
            seatStatus.put(seat.getSeatNumber(), "available");
        }

        // Lấy trạng thái thực từ bảng tickets
        String sql = "SELECT s.seat_number, t.status FROM tickets t " +
                     "JOIN seats s ON t.seat_id = s.seat_id " +
                     "WHERE t.showtime_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, showtimeId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String seatNumber = rs.getString("seat_number");
                String status = rs.getString("status"); // 'booked', 'sold'
                seatStatus.put(seatNumber, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return seatStatus;
    }
    
    // Kiểm tra ghế có thể đặt không
    public boolean isSeatAvailable(int roomId, int showtimeId, String seatNumber) {
        Map<String, String> seatStatus = getSeatStatus(roomId, showtimeId);
        return "available".equals(seatStatus.get(seatNumber));
    }
    
    // Đặt ghế
    public boolean bookSeat(int roomId, int showtimeId, String seatNumber) {
        if (isSeatAvailable(roomId, showtimeId, seatNumber)) {
            // TODO: Cập nhật trạng thái ghế trong database
            return true;
        }
        return false;
    }
    
    // Hủy đặt ghế
    public boolean cancelSeat(int roomId, int showtimeId, String seatNumber) {
        // TODO: Cập nhật trạng thái ghế trong database
        return true;
    }

    public List<Object[]> getAllTickets() {
        List<Object[]> tickets = new ArrayList<>();
        try {
            String query = "SELECT t.ticket_id, m.title, " +
                          "CONCAT(s.show_date, ' ', s.start_time) AS showtime, " +
                          "CONCAT(r.room_name, '/', st.seat_number) AS room_seat, " +
                          "c.full_name, c.phone, t.price, t.status " +
                          "FROM tickets t " +
                          "LEFT JOIN showtimes s ON t.showtime_id = s.showtime_id " +
                          "LEFT JOIN movies m ON s.movie_id = m.movie_id " +
                          "LEFT JOIN rooms r ON s.room_id = r.room_id " +
                          "LEFT JOIN seats st ON t.seat_id = st.seat_id " +
                          "LEFT JOIN bookingdetails bd ON t.ticket_id = bd.ticket_id " +
                          "LEFT JOIN bookings b ON bd.booking_id = b.booking_id " +
                          "LEFT JOIN customers c ON b.customer_id = c.customer_id " +
                          "ORDER BY t.ticket_id DESC";
            
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("ticket_id");
                row[1] = rs.getString("title");
                row[2] = rs.getString("showtime");
                row[3] = rs.getString("room_seat");
                row[4] = rs.getString("full_name");
                row[5] = rs.getString("phone");
                row[6] = rs.getDouble("price");
                row[7] = rs.getString("status");
                tickets.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public List<Object[]> getBookingHistory() {
        List<Object[]> history = new ArrayList<>();
        try {
            String query = "SELECT b.booking_id, b.booking_time, c.full_name, " +
                          "COUNT(t.ticket_id) as ticket_count, COALESCE(SUM(t.price), 0) as total_price, " +
                          "b.status " +
                          "FROM bookings b " +
                          "LEFT JOIN customers c ON b.customer_id = c.customer_id " +
                          "LEFT JOIN bookingdetails bd ON b.booking_id = bd.booking_id " +
                          "LEFT JOIN tickets t ON bd.ticket_id = t.ticket_id " +
                          "GROUP BY b.booking_id, b.booking_time, c.full_name, b.status " +
                          "HAVING COUNT(t.ticket_id) > 0 " +
                          "ORDER BY b.booking_time DESC";
            
            Statement stmt = getConnection().createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getString("booking_id");
                row[1] = rs.getString("booking_time");
                row[2] = rs.getString("full_name");
                row[3] = rs.getInt("ticket_count");
                row[4] = rs.getDouble("total_price");
                row[5] = rs.getString("status");
                history.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    public int createBooking(Booking booking) {
        String sql = "INSERT INTO Bookings (customer_id, user_id, booking_time, status) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, booking.getCustomerId());
            pstmt.setInt(2, booking.getUserId());
            pstmt.setTimestamp(3, new java.sql.Timestamp(booking.getBookingTime().getTime()));
            pstmt.setString(4, booking.getStatus());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int getSeatIdByNumber(int roomId, String seatNumber) {
        String sql = "SELECT seat_id FROM Seats WHERE room_id = ? AND seat_number = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, roomId);
            pstmt.setString(2, seatNumber);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("seat_id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public int createTicket(int showtimeId, int seatId, double price) {
        String sql = "INSERT INTO Tickets (showtime_id, seat_id, price, status) VALUES (?, ?, ?, 'sold')";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, showtimeId);
            pstmt.setInt(2, seatId);
            pstmt.setDouble(3, price);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean createBookingDetail(BookingDetail detail) {
        String sql = "INSERT INTO bookingdetails (booking_id, ticket_id, unit_price) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, detail.getBookingId());
            pstmt.setInt(2, detail.getTicketId());
            pstmt.setDouble(3, detail.getUnitPrice());
            
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteBooking(int bookingId) {
        String selectTickets = "SELECT ticket_id FROM bookingdetails WHERE booking_id = ?";
        String deleteDetails = "DELETE FROM bookingdetails WHERE booking_id = ?";
        String deleteTicket = "DELETE FROM tickets WHERE ticket_id = ?";
        String deletePayment = "DELETE FROM payments WHERE booking_id = ?";
        String deleteBooking = "DELETE FROM bookings WHERE booking_id = ?";
        try (Connection conn = getConnection()) {
            // 1. Lấy danh sách ticket_id liên quan
            List<Integer> ticketIds = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement(selectTickets)) {
                pstmt.setInt(1, bookingId);
                ResultSet rs = pstmt.executeQuery();
                while (rs.next()) {
                    ticketIds.add(rs.getInt("ticket_id"));
                }
            }
            // 2. Xóa bookingdetails trước
            try (PreparedStatement pstmt1 = conn.prepareStatement(deleteDetails)) {
                pstmt1.setInt(1, bookingId);
                pstmt1.executeUpdate();
            }
            // 3. Xóa các ticket liên quan
            for (Integer ticketId : ticketIds) {
                try (PreparedStatement pstmt2 = conn.prepareStatement(deleteTicket)) {
                    pstmt2.setInt(1, ticketId);
                    pstmt2.executeUpdate();
                }
            }
            // 4. Xóa payment liên quan
            try (PreparedStatement pstmtPay = conn.prepareStatement(deletePayment)) {
                pstmtPay.setInt(1, bookingId);
                pstmtPay.executeUpdate();
            }
            // 5. Xóa booking
            try (PreparedStatement pstmt3 = conn.prepareStatement(deleteBooking)) {
                pstmt3.setInt(1, bookingId);
                return pstmt3.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getCurrentUserId() {
        // TODO: Lấy user_id từ session hoặc authentication system
        // Tạm thời trả về 1 cho user mặc định
        return 1;
    }

    public List<Object[]> searchTickets(String ticketId, String customerName, String phone) {
        List<Object[]> tickets = new ArrayList<>();
        try {
            StringBuilder query = new StringBuilder("SELECT t.ticket_id, m.title, CONCAT(s.show_date, ' ', s.start_time), " +
                "CONCAT(r.room_name, '/', st.seat_number), " +
                "c.full_name, c.phone, t.price, t.status " +
                "FROM tickets t " +
                "LEFT JOIN showtimes s ON t.showtime_id = s.showtime_id " +
                "LEFT JOIN movies m ON s.movie_id = m.movie_id " +
                "LEFT JOIN rooms r ON s.room_id = r.room_id " +
                "LEFT JOIN seats st ON t.seat_id = st.seat_id " +
                "LEFT JOIN bookingdetails bd ON t.ticket_id = bd.ticket_id " +
                "LEFT JOIN bookings b ON bd.booking_id = b.booking_id " +
                "LEFT JOIN customers c ON b.customer_id = c.customer_id " +
                "WHERE 1=1 ");
            if (!ticketId.isEmpty()) query.append("AND t.ticket_id LIKE ? ");
            if (!customerName.isEmpty()) query.append("AND c.full_name LIKE ? ");
            if (!phone.isEmpty()) query.append("AND c.phone LIKE ? ");
            query.append("ORDER BY t.ticket_id DESC");
            PreparedStatement pstmt = getConnection().prepareStatement(query.toString());
            int idx = 1;
            if (!ticketId.isEmpty()) pstmt.setString(idx++, "%" + ticketId + "%");
            if (!customerName.isEmpty()) pstmt.setString(idx++, "%" + customerName + "%");
            if (!phone.isEmpty()) pstmt.setString(idx++, "%" + phone + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[8];
                row[0] = rs.getString("ticket_id");
                row[1] = rs.getString("title");
                row[2] = rs.getString("CONCAT(s.show_date, ' ', s.start_time)");
                row[3] = rs.getString("CONCAT(r.room_name, '/', st.seat_number)");
                row[4] = rs.getString("full_name");
                row[5] = rs.getString("phone");
                row[6] = rs.getDouble("price");
                row[7] = rs.getString("status");
                tickets.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tickets;
    }

    public boolean updateTicketStatus(int ticketId, String newStatus) {
        String sql = "UPDATE tickets SET status = ? WHERE ticket_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, ticketId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteTicket(int ticketId) {
        String deleteDetails = "DELETE FROM bookingdetails WHERE ticket_id = ?";
        String deleteTicket = "DELETE FROM tickets WHERE ticket_id = ?";
        try (Connection conn = getConnection()) {
            // Xóa bookingdetails trước
            try (PreparedStatement pstmt1 = conn.prepareStatement(deleteDetails)) {
                pstmt1.setInt(1, ticketId);
                pstmt1.executeUpdate();
            }
            // Sau đó xóa ticket
            try (PreparedStatement pstmt2 = conn.prepareStatement(deleteTicket)) {
                pstmt2.setInt(1, ticketId);
                return pstmt2.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBookingStatus(int bookingId, String newStatus) {
        String sql = "UPDATE bookings SET status = ? WHERE booking_id = ?";
        try (PreparedStatement pstmt = getConnection().prepareStatement(sql)) {
            pstmt.setString(1, newStatus);
            pstmt.setInt(2, bookingId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getSeatNumbersByBookingId(int bookingId) {
        List<String> seatNumbers = new ArrayList<>();
        String sql = "SELECT st.seat_number " +
                     "FROM bookingdetails bd " +
                     "JOIN tickets t ON bd.ticket_id = t.ticket_id " +
                     "JOIN seats st ON t.seat_id = st.seat_id " +
                     "WHERE bd.booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                seatNumbers.add(rs.getString("seat_number"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return seatNumbers;
    }

    public List<Object[]> searchBookingHistory(String date, String customer, String status) {
        List<Object[]> history = new ArrayList<>();
        try {
            StringBuilder query = new StringBuilder("SELECT b.booking_id, b.booking_time, c.full_name, " +
                "COUNT(t.ticket_id) as ticket_count, COALESCE(SUM(t.price), 0) as total_price, " +
                "b.status " +
                "FROM bookings b " +
                "LEFT JOIN customers c ON b.customer_id = c.customer_id " +
                "LEFT JOIN bookingdetails bd ON b.booking_id = bd.booking_id " +
                "LEFT JOIN tickets t ON bd.ticket_id = t.ticket_id " +
                "WHERE 1=1 ");
            List<Object> params = new ArrayList<>();
            if (date != null && !date.isEmpty()) {
                query.append("AND DATE(b.booking_time) = ? ");
                params.add(date);
            }
            if (customer != null && !customer.isEmpty() && !customer.equals("Tất cả khách hàng")) {
                query.append("AND c.full_name LIKE ? ");
                params.add("%" + customer + "%");
            }
            if (status != null && !status.isEmpty() && !status.equals("Tất cả trạng thái")) {
                // Chuyển trạng thái tiếng Việt về tiếng Anh
                String dbStatus = status;
                if (status.equals("Chờ xác nhận")) dbStatus = "pending";
                else if (status.equals("Đã xác nhận")) dbStatus = "confirmed";
                else if (status.equals("Đã hủy")) dbStatus = "cancelled";
                query.append("AND b.status = ? ");
                params.add(dbStatus);
            }
            query.append("GROUP BY b.booking_id, b.booking_time, c.full_name, b.status ");
            query.append("HAVING COUNT(t.ticket_id) > 0 ");
            query.append("ORDER BY b.booking_time DESC");
            PreparedStatement pstmt = getConnection().prepareStatement(query.toString());
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Object[] row = new Object[6];
                row[0] = rs.getString("booking_id");
                row[1] = rs.getString("booking_time");
                row[2] = rs.getString("full_name");
                row[3] = rs.getInt("ticket_count");
                row[4] = rs.getDouble("total_price");
                row[5] = rs.getString("status");
                history.add(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return history;
    }

    public Booking getBookingById(int bookingId) {
        String sql = "SELECT * FROM bookings WHERE booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setBookingTime(rs.getTimestamp("booking_time"));
                booking.setStatus(rs.getString("status"));
                return booking;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Booking getLatestBookingByPhone(String phone) {
        String sql = "SELECT b.* FROM bookings b " +
                    "JOIN customers c ON b.customer_id = c.customer_id " +
                    "WHERE c.phone = ? " +
                    "ORDER BY b.booking_time DESC LIMIT 1";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, phone);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Booking booking = new Booking();
                booking.setBookingId(rs.getInt("booking_id"));
                booking.setCustomerId(rs.getInt("customer_id"));
                booking.setUserId(rs.getInt("user_id"));
                booking.setBookingTime(rs.getTimestamp("booking_time"));
                booking.setStatus(rs.getString("status"));
                return booking;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Customer getCustomerById(int customerId) {
        String sql = "SELECT * FROM customers WHERE customer_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Customer customer = new Customer();
                customer.setCustomerId(rs.getInt("customer_id"));
                customer.setFullName(rs.getString("full_name"));
                customer.setPhone(rs.getString("phone"));
                customer.setEmail(rs.getString("email"));
                return customer;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<BookingDetail> getBookingDetailsByBookingId(int bookingId) {
        List<BookingDetail> details = new ArrayList<>();
        String sql = "SELECT * FROM bookingdetails WHERE booking_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, bookingId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                BookingDetail detail = new BookingDetail();
                detail.setBookingDetailId(rs.getInt("booking_detail_id"));
                detail.setBookingId(rs.getInt("booking_id"));
                detail.setTicketId(rs.getInt("ticket_id"));
                detail.setUnitPrice(rs.getDouble("unit_price"));
                details.add(detail);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return details;
    }

    public Ticket getTicketById(int ticketId) {
        String sql = "SELECT * FROM tickets WHERE ticket_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, ticketId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Ticket ticket = new Ticket();
                ticket.setTicketId(rs.getInt("ticket_id"));
                ticket.setShowtimeId(rs.getInt("showtime_id"));
                ticket.setSeatId(rs.getInt("seat_id"));
                ticket.setPrice(rs.getDouble("price"));
                ticket.setStatus(rs.getString("status"));
                return ticket;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getSeatNumberById(int seatId) {
        String sql = "SELECT seat_number FROM seats WHERE seat_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, seatId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("seat_number");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getMovieTitleByShowtimeId(int showtimeId) {
        String sql = "SELECT m.title FROM movies m " +
                    "JOIN showtimes s ON m.movie_id = s.movie_id " +
                    "WHERE s.showtime_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, showtimeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("title");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String getShowtimeInfo(int showtimeId) {
        String sql = "SELECT s.show_date, s.start_time, r.room_name " +
                    "FROM showtimes s " +
                    "JOIN rooms r ON s.room_id = r.room_id " +
                    "WHERE s.showtime_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, showtimeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return String.format("%s %s - Phòng %s",
                    rs.getDate("show_date"),
                    rs.getTime("start_time"),
                    rs.getString("room_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    public Showtime getShowtimeById(int showtimeId) {
        String sql = "SELECT * FROM showtimes WHERE showtime_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, showtimeId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Showtime showtime = new Showtime();
                showtime.setShowtimeId(rs.getInt("showtime_id"));
                showtime.setMovieId(rs.getInt("movie_id"));
                showtime.setRoomId(rs.getInt("room_id"));
                showtime.setShowDate(rs.getDate("show_date"));
                showtime.setStartTime(rs.getTime("start_time"));
                showtime.setEndTime(rs.getTime("end_time"));
                showtime.setBasePrice(rs.getBigDecimal("base_price"));
                return showtime;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Object[] searchBookingForPreview(String bookingId, String phone) {
        String sql = "SELECT b.booking_id, m.title, " +
                    "CONCAT(s.show_date, ' ', s.start_time) as showtime, " +
                    "r.room_name, GROUP_CONCAT(st.seat_number) as seats, " +
                    "c.full_name, c.email, SUM(t.price) as total_price " +
                    "FROM bookings b " +
                    "JOIN customers c ON b.customer_id = c.customer_id " +
                    "JOIN bookingdetails bd ON b.booking_id = bd.booking_id " +
                    "JOIN tickets t ON bd.ticket_id = t.ticket_id " +
                    "JOIN showtimes s ON t.showtime_id = s.showtime_id " +
                    "JOIN movies m ON s.movie_id = m.movie_id " +
                    "JOIN rooms r ON s.room_id = r.room_id " +
                    "JOIN seats st ON t.seat_id = st.seat_id " +
                    "WHERE 1=1 ";
        
        if (!bookingId.isEmpty()) {
            sql += "AND b.booking_id = ? ";
        }
        if (!phone.isEmpty()) {
            sql += "AND c.phone = ? ";
        }
        
        sql += "GROUP BY b.booking_id, m.title, s.show_date, s.start_time, r.room_name, c.full_name, c.email";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            if (!bookingId.isEmpty()) {
                pstmt.setString(paramIndex++, bookingId);
            }
            if (!phone.isEmpty()) {
                pstmt.setString(paramIndex, phone);
            }
            
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Object[] bookingInfo = new Object[8];
                bookingInfo[0] = rs.getString("booking_id");
                bookingInfo[1] = rs.getString("title");
                bookingInfo[2] = rs.getString("showtime");
                bookingInfo[3] = rs.getString("room_name");
                bookingInfo[4] = rs.getString("seats");
                bookingInfo[5] = rs.getString("full_name");
                bookingInfo[6] = rs.getString("email");
                bookingInfo[7] = String.format("%.0fđ", rs.getDouble("total_price"));
                return bookingInfo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getTotalTicketCount() {
        String sql = "SELECT COUNT(*) FROM tickets";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getAvailableTicketCount() {
        String sql = "SELECT COUNT(*) FROM tickets WHERE status = 'available'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getBookedTicketCount() {
        String sql = "SELECT COUNT(*) FROM tickets WHERE status = 'booked'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getSoldTicketCount() {
        String sql = "SELECT COUNT(*) FROM tickets WHERE status = 'sold'";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
} 