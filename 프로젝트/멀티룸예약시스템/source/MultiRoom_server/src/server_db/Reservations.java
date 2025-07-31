package server_db;

import java.sql.Date;

public class Reservations {
	private int reservation_id;
	private int room_id;
	private String user_id;
	private Date reservation_date;
	private String reservation_time;
	private int total_price;
	private Date dateOfReservation;
	private int people;
	
	
	public Reservations(int reservation_id,int room_id,String user_id,Date reservation_date, String reservation_time,int total_price,Date dateOfReservation, int people) {
		this.reservation_id=reservation_id;
		this.room_id=room_id;
		this.user_id=user_id;
		this.reservation_date=reservation_date;
		this.reservation_time=reservation_time;
		this.total_price=total_price;
		this.dateOfReservation=dateOfReservation;
		this.people=people;
	}

	public Date getDateOfReservation() {
		return dateOfReservation;
	}

	public void setDateOfReservation(Date dateOfReservation) {
		this.dateOfReservation = dateOfReservation;
	}

	public int getPeople() {
		return people;
	}

	public void setPeople(int people) {
		this.people = people;
	}

	public int getReservation_id() {
		return reservation_id;
	}

	public void setReservation_id(int reservation_id) {
		this.reservation_id = reservation_id;
	}

	public int getRoom_id() {
		return room_id;
	}

	public void setRoom_id(int room_id) {
		this.room_id = room_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public Date getReservation_date() {
		return reservation_date;
	}

	public void setReservation_date(Date reservation_date) {
		this.reservation_date = reservation_date;
	}

	public String getReservation_time() {
		return reservation_time;
	}

	public void setReservation_time(String reservation_time) {
		this.reservation_time = reservation_time;
	}

	public int getTotal_price() {
		return total_price;
	}

	public void setTotal_price(int total_price) {
		this.total_price = total_price;
	}
	
}
