package MultiRoom;

import java.sql.Blob;

public class Rooms {
	private int room_id;
	private String room_pic;
	private String room_name;
	private int max_capacity;
	private String location;
	private String service;
	private String room_password;
	private int hourly_rate;
	private String details;
	
	public Rooms(int room_id,String room_pic,String room_name, int max_capacity,String location,String service, String room_password, int hourly_rate,String details) {
		this.room_id=room_id;
		this.room_pic=room_pic;
		this.room_name=room_name;
		this.max_capacity=max_capacity;
		this.location=location;
		this.service=service;
		this.room_password=room_password;
		this.hourly_rate=hourly_rate;
		this.details=details;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getRoom_pic() {
		return room_pic;
	}

	public void setRoom_pic(String room_pic) {
		this.room_pic = room_pic;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public int getRoom_id() {
		return room_id;
	}

	public void setRoom_id(int room_id) {
		this.room_id = room_id;
	}

	public String getRoom_name() {
		return room_name;
	}

	public void setRoom_name(String room_name) {
		this.room_name = room_name;
	}

	public int getMax_capacity() {
		return max_capacity;
	}

	public void setMax_capacity(int max_capacity) {
		this.max_capacity = max_capacity;
	}

	public String getRoom_password() {
		return room_password;
	}

	public void setRoom_password(String room_password) {
		this.room_password = room_password;
	}

	public int getHourly_rate() {
		return hourly_rate;
	}

	public void setHourly_rate(int hourly_rate) {
		this.hourly_rate = hourly_rate;
	}

	public String getLocation() {
		// TODO Auto-generated method stub
		return location;
	}
	
}
