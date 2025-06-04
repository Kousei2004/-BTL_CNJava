package Model;

import java.util.Date;

public class Room {
	public Room() {
		super();
	}
	
	private int roomId;
	private String roomName;
    private int totalSeats;
    
    
	public Room(int roomId, String roomName, int totalSeats) {
		super();
		this.roomId = roomId;
		this.roomName = roomName;
		this.totalSeats = totalSeats;
	}
	
    
    public int getRoomId() {
		return roomId;
	}
	public void setRoomId(int roomId) {
		this.roomId = roomId;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public int getTotalSeats() {
		return totalSeats;
	}
	public void setTotalSeats(int totalSeats) {
		this.totalSeats = totalSeats;
	}
	

}
