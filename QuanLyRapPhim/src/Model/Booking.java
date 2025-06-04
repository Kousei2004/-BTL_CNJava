package Model;

import java.util.Date;

public class Booking {
    private int bookingId;
	private int customerId;
    private int userId;
    private Date bookingTime;
    private String status;

    public Booking() {
		super();
	}

    // Constructor
    public Booking(int bookingId, int customerId, int userId, Date bookingTime, String status) {
        this.bookingId = bookingId;
        this.customerId = customerId;
        this.userId = userId;
        this.bookingTime = bookingTime;
        this.status = status;
    }

    // Getters and Setters
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public Date getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(Date bookingTime) {
        this.bookingTime = bookingTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public Object getMovieId() {
		// TODO Auto-generated method stub
		return null;
	}
}
