package Model;

public class Seat {
    public Seat() {
		super();
	}

	private int seatId;
    private int roomId;
    private String seatNumber;
    private String status;

    // Constructor
    public Seat(int seatId, int roomId, String seatNumber) {
        this.seatId = seatId;
        this.roomId = roomId;
        this.seatNumber = seatNumber;
        this.status = "available";
    }

    // Getters and Setters
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    
    // Phương thức để kiểm tra ghế có trống không
    public boolean isAvailable() {
        return "available".equalsIgnoreCase(this.status);
    }
    
    public int getSeatId() {
        return seatId;
    }

    public void setSeatId(int seatId) {
        this.seatId = seatId;
    }

    public int getRoomId() {
        return roomId;
    }

    public void setRoomId(int roomId) {
        this.roomId = roomId;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public boolean isBooked() {
        return "booked".equalsIgnoreCase(status);
    }
    
    public boolean isSold() {
        return "sold".equalsIgnoreCase(status);
    }
}
