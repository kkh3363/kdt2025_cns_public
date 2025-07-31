package server_db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReservationsDAO {
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	public ReservationsDAO(Connection conn) {
		this.conn = conn;
	}
	//필요한 쿼리문 작성
	public void Reservation(Object[] info) {
		try {
			
			String sql = ""+
					"INSERT INTO Reservations (room_id, user_id, reservation_date, reservation_time, total_price,dateOfReservation,people)"
					+ "	values(?,?,?,?,3*(SELECT hourly_rate FROM Rooms WHERE room_id = ?),now(),?);";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, (int)info[0]);
			pstmt.setString(2, (String)info[1]);
			pstmt.setString(3, (String)info[2]);
			pstmt.setString(4, (String)info[3]);
			pstmt.setInt(5, (int)info[0]);
			pstmt.setInt(6, (int)info[4]);
			pstmt.executeUpdate(); //쿼리 실행
			
			pstmt.close();
			pstmt = null;
			
			updateRoomAvailability((int) info[0], (String) info[2], (String) info[3], false);
			
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void updateRoomAvailability(int roomId, String reservationDate, String reservationTime, boolean available) {
		// TODO Auto-generated method stub
		 try {
		        String updateAvailabilitySQL = "UPDATE Room_Schedules SET available = ? WHERE room_id = ? AND date = ? AND time = ?";
		        pstmt = conn.prepareStatement(updateAvailabilitySQL);
		        pstmt.setBoolean(1, available);
		        pstmt.setInt(2, roomId);
		        pstmt.setString(3, reservationDate);
		        pstmt.setString(4, reservationTime);
		        pstmt.executeUpdate(); // 쿼리 실행
		        
		        pstmt.close();
		        pstmt = null;

		    } catch (Exception e) {
		        e.printStackTrace();
		    }
		
	}
	public void ReservationCancel(int reservation_id) {
		try {
			String sql = ""+
					"delete from Reservations where reservation_id=? ";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, reservation_id);
			pstmt.executeUpdate(); //쿼리 실행
			
			pstmt.close();
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	//방 예약 정보 가져오기
	public Reservations getReservation(int reservation_Id) {
		Reservations reservation = null;
		try {
			String sql = """
					select * from Reservations
					where reservation_id=?;
			""";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, reservation_Id);
			rs = pstmt.executeQuery(); //쿼리 실행
			if(rs.next()) {
				reservation=new Reservations(
					rs.getInt("reservation_id"),
					rs.getInt("room_id"),
					rs.getString("user_id"),
					rs.getDate("reservation_date"),
					rs.getString("reservation_time"),
					rs.getInt("total_price"),
					rs.getDate("dateOfReservation"),
					rs.getInt("people")
				);
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
		return reservation;
	}
	
	public List<Reservations> printREservations(String id) {
			//Reservations reservation = null;
			List<Reservations> reservation= new ArrayList<>();
			try {
				String sql = "select * from Reservations"
						+ " where user_id = ? and reservation_date >= date_format(Now(), '%y-%m-%d')";
				pstmt = conn.prepareStatement(sql);
				pstmt.setString(1, id);
				rs = pstmt.executeQuery(); //쿼리 실행
				
				while(rs.next()) {
					reservation.add(new Reservations(
							rs.getInt("reservation_id"),
							rs.getInt("room_id"),
							id,
							rs.getDate("reservation_date"),
							rs.getString("reservation_time"),
							rs.getInt("total_price"),
							rs.getDate("dateOfReservation"),
							rs.getInt("people")));
				}
				rs.close();
				pstmt.close();
				rs = null;
				pstmt = null;
			}catch(Exception e) {
				e.printStackTrace();
			}
			return reservation;
		}
	public List<Reservations> printPastREservations(String id) {
		//Reservations reservation = null;
		List<Reservations> reservation= new ArrayList<>();
		try {
			String sql = "select * from Reservations where user_id =? and reservation_date < date_format(Now(), '%y-%m-%d')";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery(); //쿼리 실행
			
			while(rs.next()) {
				reservation.add(new Reservations(
						rs.getInt("reservation_id"),
						rs.getInt("room_id"),
						id,
						rs.getDate("reservation_date"),
						rs.getString("reservation_time"),
						rs.getInt("total_price"),
						rs.getDate("dateOfReservation"),
						rs.getInt("people")));
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return reservation;
	}
	
	
	
	public Map<String, Integer> printSales(String room_id) {
		Map<String,Integer> sales = new HashMap<>();
		try {
			String sql = "select room_id,sum(total_price) as sales from  Reservations group by(?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, room_id);
			rs = pstmt.executeQuery(); //쿼리 실행
			
			while(rs.next()){
				sales.put(rs.getString("room_id"), rs.getInt("sales"));
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return sales;
	}

}
