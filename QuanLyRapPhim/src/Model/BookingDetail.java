package Model;

public class BookingDetail {
   
	private int bookingDetailId;
    private int bookingId;
    private int ticketId;
    private double unitPrice;
    
    public BookingDetail() {
		super();
	}


    // Constructor
    public BookingDetail(int bookingDetailId, int bookingId, int ticketId, double unitPrice) {
        this.bookingDetailId = bookingDetailId;
        this.bookingId = bookingId;
        this.ticketId = ticketId;
        this.unitPrice = unitPrice;
    }

    // Getters and Setters
    public int getBookingDetailId() {
        return bookingDetailId;
    }

    public void setBookingDetailId(int bookingDetailId) {
        this.bookingDetailId = bookingDetailId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getTicketId() {
        return ticketId;
    }

    public void setTicketId(int ticketId) {
        this.ticketId = ticketId;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
}
