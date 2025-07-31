package server_db;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RoomsDAO {
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	public RoomsDAO(Connection conn) {
		this.conn = conn;
	}
	//필요한 쿼리문 작성
	public List<Rooms> search(String sql) {
	    List<Rooms> room = new ArrayList<>();
	    try {
	    	
	        pstmt = conn.prepareStatement(sql.toString());
	        rs = pstmt.executeQuery(); // 쿼리 실행
	        while (rs.next()) {
	            room.add(new Rooms(
	                rs.getInt("room_id"),
	                rs.getString("room_pic"),
	                rs.getString("room_name"),
	                rs.getInt("max_capacity"),
	                rs.getString("location"),
	                rs.getString("service"),
	                rs.getString("room_password"),
	                rs.getInt("hourly_rate"),
	                rs.getString("details")
	            ));
	        }
	        rs.close();
	        pstmt.close();
	        rs = null;
	        pstmt = null;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return room;
	}
	public int insertDB(Object[] info) {
		try {
			int room_id;
			String sql = ""+
					"INSERT INTO Rooms (room_name, room_pic, max_capacity, location, service, room_password, hourly_rate,details) VALUES "
					+ "(?,?,?,?,?,?,?,?);";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, (String)info[0]);//room_name
			pstmt.setString(2, (String)info[1]);//room_pic
			pstmt.setInt(3, (int)info[2]);      //max_capacity
			pstmt.setString(4, (String)info[3]);//location
			pstmt.setString(5, (String)info[4]);//service
			pstmt.setString(6, (String)info[5]);//room_password
			pstmt.setInt(7, (int)info[6]);//hourly_rate
			pstmt.setString(8, (String)info[7]);//details
			
			pstmt.executeUpdate(); //쿼리 실행
			
			pstmt.close();
			pstmt = null;
			
			String sql2 = "SELECT room_id FROM Rooms ORDER BY room_id DESC LIMIT 1";
			pstmt = conn.prepareStatement(sql2);
			rs = pstmt.executeQuery();
			if(rs.next()) {
				room_id=rs.getInt("room_id");
				pstmt.close();
				pstmt = null;
				
				String sql3 = "UPDATE Rooms SET room_pic = CONCAT('/', room_id, '/')";
				pstmt = conn.prepareStatement(sql3);
				pstmt.executeUpdate(); //쿼리 실행
				return room_id;
			}

		}catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	public void deleteRoom(String id) {
		try {
			String sql = ""+
					"delete from Rooms where room_id = ?;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);

			pstmt.executeUpdate(); //쿼리 실행
			
			pstmt.close();
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void UpdateRoom(Object[] info,int room_id) {
		try {
			String sql = "update Rooms set ";
			for(int i = 0;i<info.length;i++) {
				if(i==info.length-1) {
					sql +=info[i] +"where room_id=?";
				}
				else {
					sql +=info[i]+" and ";
				}
			}
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, room_id);
			pstmt.executeUpdate(); //쿼리 실행
			
			pstmt.close();
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public List<Rooms> findRoom(String max_capacity, String date, String time, String location) {
		List<Rooms> room= new ArrayList<>();
		try {
			String sql = "SELECT * "
					+ "FROM Rooms r "
					+ "JOIN Room_Schedules rs "
					+ "ON r.room_id = rs.room_id "
					+ "WHERE r.max_capacity >= ? "
					+ "AND rs.date = ? "
					+ "AND r.location like ? "
					+ "AND rs.time = ? AND rs.available = true;";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, Integer.parseInt(max_capacity));
			pstmt.setString(2, date);
			pstmt.setString(3, "%" + location + "%"); 
			pstmt.setString(4, time);
			rs = pstmt.executeQuery(); //쿼리 실행
			while(rs.next()){
				room.add( new Rooms( 
						rs.getInt("room_id"),
						rs.getString("room_pic"),
						rs.getString("room_name"),
						rs.getInt("max_capacity"),
						rs.getString("location"),
						rs.getString("service"),
						rs.getString("room_password"),
						rs.getInt("hourly_rate"),
						rs.getString("details")
				));
				
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return room;
	}
				
				public List<Rooms> getRooms() {
			        List<Rooms> roomList = new ArrayList<>();
			        String sql = "SELECT * FROM Rooms ORDER BY room_id"; // 사용자 정보를 ID 순으로 정렬하여 가져옴

			        try {
			            pstmt = conn.prepareStatement(sql);
			            rs = pstmt.executeQuery();

			            while (rs.next()) {
			                Rooms room = new Rooms( 
									rs.getInt("room_id"),
									rs.getString("room_pic"),
									rs.getString("room_name"),
									rs.getInt("max_capacity"),
									rs.getString("location"),
									rs.getString("service"),
									rs.getString("room_password"),
									rs.getInt("hourly_rate"),
									rs.getString("details")
			                    
			                );
			                roomList.add(room);
			            }
			        } catch (SQLException e) {
			            e.printStackTrace();
			        } finally {
			            try {
			                if (rs != null) rs.close();
			                if (pstmt != null) pstmt.close();
			            } catch (SQLException e) {
			                e.printStackTrace();
			            }
			        }

			        return roomList;
			    }
				
				//방 삭제 메소드
			    public boolean deleteroom(long room_id) {
			        String sql = "DELETE FROM Rooms WHERE room_id = ?";
			        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			            pstmt.setLong(1, room_id);
			            int rowsAffected = pstmt.executeUpdate();
			            return rowsAffected > 0;
			        } catch (SQLException e) {
			            System.out.println("[DB 오류] 사용자 삭제 실패: " + e.getMessage());
			            return false;
			        }
			    }
			    //방 수정 메소드
			    public boolean updateRoom(Rooms room) {
			        String sql = "UPDATE Rooms SET room_pic = ?, room_name = ?, max_capacity = ?, location = ? , service = ?, room_password = ?, hourly_rate = ?, details = ? WHERE room_id = ?";
			        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
			            pstmt.setString(1, room.getRoom_pic());
			            pstmt.setString(2, room.getRoom_name());
			            pstmt.setInt(3, room.getMax_capacity());
			            pstmt.setString(4, room.getLocation());
			            pstmt.setString(5, room.getService());
			            pstmt.setString(6, room.getRoom_password());
			            pstmt.setInt(7, room.getHourly_rate());
			            pstmt.setString(8, room.getDetails());
			            pstmt.setInt(9, room.getRoom_id());
			            
			            int rowsAffected = pstmt.executeUpdate();
			            return rowsAffected > 0;
			        } catch (SQLException e) {
			            System.out.println("[DB 오류] 방 수정 실패: " + e.getMessage());
			            return false;
			        }
			    }
			    public String RoomPWD(int room_id) {
			    	String PWD="";
			    	String sql = "select room_password from Rooms where room_id=?;";
			        try {
						pstmt = conn.prepareStatement(sql);
						pstmt.setInt(1, room_id);
						rs = pstmt.executeQuery(); //쿼리 실행
						if(rs.next()) {
							PWD=rs.getString("room_password");
							return PWD;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	return PWD;
			    }
			    public String RoomName(int room_id) {
			    	String name="";
			    	String sql = "select room_name from Rooms where room_id=?;";
			        try {
						pstmt = conn.prepareStatement(sql);
						pstmt.setInt(1, room_id);
						rs = pstmt.executeQuery(); //쿼리 실행
						if(rs.next()) {
							name=rs.getString("room_name");
							return name;
						}
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			    	return name;
			    }
			    public List<Rooms> findRoomBYID(String room_id) {
			        List<Rooms> roomList = new ArrayList<>();
			        String sql = "SELECT * from Rooms where room_id = ? "; // 사용자 정보를 ID 순으로 정렬하여 가져옴

			        try {
			        	pstmt = conn.prepareStatement(sql);
						pstmt.setInt(1, Integer.parseInt(room_id));
						rs = pstmt.executeQuery(); //쿼리 실행
						if (rs.next()) {
			                Rooms room = new Rooms( 
									rs.getInt("room_id"),
									rs.getString("room_pic"),
									rs.getString("room_name"),
									rs.getInt("max_capacity"),
									rs.getString("location"),
									rs.getString("service"),
									rs.getString("room_password"),
									rs.getInt("hourly_rate"),
									rs.getString("details")
			                    
			                );
			                roomList.add(room);
			            }
			        } catch (SQLException e) {
			            e.printStackTrace();
			        } finally {
			            try {
			                if (rs != null) rs.close();
			                if (pstmt != null) pstmt.close();
			            } catch (SQLException e) {
			                e.printStackTrace();
			            }
			        }

			        return roomList;
			    }
}