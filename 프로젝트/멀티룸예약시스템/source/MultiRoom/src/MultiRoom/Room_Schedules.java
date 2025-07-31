package MultiRoom;

import java.sql.Date;

public class Room_Schedules {
	private int room_id;
	private Date date;
	private String time;
	private boolean  available;
	
	public Room_Schedules(int room_id, Date date,String time, boolean available) {
		this.room_id=room_id;
		this.date=date;
		this.time=time;
		this.available=available;
	}

	public int getRoom_id() {
		return room_id;
	}

	public void setRoom_id(int room_id) {
		this.room_id = room_id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}
	
}
