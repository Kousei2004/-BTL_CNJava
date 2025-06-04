package Model;

public class TicketView {
    private int ticketId;
    private String movieTitle;
    private String showtimeInfo;
    private String roomName;
    private String seatNumber;
    private String customerName;
    private String customerPhone;
    private double price;
    private String status;

    public TicketView() {}

    public TicketView(int ticketId, String movieTitle, String showtimeInfo, String roomName, String seatNumber, String customerName, String customerPhone, double price, String status) {
        this.ticketId = ticketId;
        this.movieTitle = movieTitle;
        this.showtimeInfo = showtimeInfo;
        this.roomName = roomName;
        this.seatNumber = seatNumber;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.price = price;
        this.status = status;
    }

    public int getTicketId() { return ticketId; }
    public void setTicketId(int ticketId) { this.ticketId = ticketId; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public String getShowtimeInfo() { return showtimeInfo; }
    public void setShowtimeInfo(String showtimeInfo) { this.showtimeInfo = showtimeInfo; }
    public String getRoomName() { return roomName; }
    public void setRoomName(String roomName) { this.roomName = roomName; }
    public String getSeatNumber() { return seatNumber; }
    public void setSeatNumber(String seatNumber) { this.seatNumber = seatNumber; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
} 