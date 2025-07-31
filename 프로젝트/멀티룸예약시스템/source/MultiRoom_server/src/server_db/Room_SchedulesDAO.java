package server_db;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Room_SchedulesDAO {
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	public Room_SchedulesDAO(Connection conn) {
		this.conn = conn;
	}
	//필요한 쿼리문 작성
	public void availableUpdate(int room_id, Date date, String time, boolean available) {
		try {
			String sql = "update Room_Schedules set available = ? where room_id=? and date = ? and time = ? ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setBoolean(1, available);
			pstmt.setInt(2, room_id);
			pstmt.setDate(3, date);
			pstmt.setString(4, time);

			pstmt.executeUpdate(); //쿼리 실행
			
			pstmt.close();
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void insertDB(int room_id, Date date, String time, boolean available) {
		try {
			String sql = "INSERT INTO Room_Schedules (room_id, date, time, available) VALUES (?, ?,?,?)";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, room_id);
			pstmt.setDate(2, date);
			pstmt.setString(3, time);
			pstmt.setBoolean(4, available);

			pstmt.executeUpdate(); //쿼리 실행
			
			pstmt.close();
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	public String[] result_time(int room_id, String date) {
		// TODO Auto-generated method stub
		String[] time = new String[3];
		try {
			String sql ="select time from Room_Schedules where room_id = ? and date = ? and available = true";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, room_id);
			pstmt.setString(2, date);
			rs = pstmt.executeQuery(); //쿼리 실행
			int i = 0 ;
			while(rs.next()){
				time[i]=rs.getString("time");
				i++;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return time;
	}
	
	 // 방 스케줄 조회
	public List<Room_Schedules> getRoomSchedules(int roomId) throws SQLException {
	    List<Room_Schedules> schedulesList = new ArrayList<>();
	    String query = "SELECT room_id, date, time, available FROM Room_Schedules WHERE room_id = ?"; // Corrected table name

	    try (PreparedStatement stmt = conn.prepareStatement(query)) {
	        stmt.setInt(1, roomId);
	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                int id = rs.getInt("room_id");
	                Date date = rs.getDate("date");
	                String time = rs.getString("time");
	                boolean available = rs.getBoolean("available");

	                Room_Schedules schedule = new Room_Schedules(id, date, time, available);
	                schedulesList.add(schedule);
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("[Database] Error retrieving Room Schedules: " + e.getMessage());
	        throw e;  // Rethrow the exception after logging
	    }

	    return schedulesList;
	}
	
}
