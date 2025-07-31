package server_cmd;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import server_db.Reservations;
import server_db.ReservationsDAO;
import server_db.Room_SchedulesDAO;
import server_db.Rooms;
import server_db.RoomsDAO;
import server_db.Users;
import server_db.UsersDAO;
import server_manager.SocketBinder;
import server_util.Message;
import server_util.Session;

public class AuthCommand{
	/********************* 명령어에 해당하는 메서드 추가 ******************************/
	//로그인
	static String login(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		//세션 생성 (24시간동안 지속되는 세션)
		String sessionId = socket.createSession(60*60*24);
		try {
			if(conn==null)
				return Message.createFail("fail");
			JSONObject receiveData = new JSONObject();
			JSONObject data_id = (JSONObject)Command.parser.parse(data);
			String id = (String) data_id.get("id");
			String pw = (String) data_id.get("pw");
			
			UsersDAO dao = new UsersDAO(conn);
			Users user = dao.LoginByIDPWD(id, pw);
			conn.close();
			if(user==null) {
				return Message.createFail("해당 아이디 정보 없음!");
			}
			else {
				//세션에 사용자 정보를 넣습니다.
				Session session = socket.getSession(sessionId);
				session.setAttribute("id", id);
				session.setAttribute("user_type",user.getUser_type().toString());
				
				receiveData.put("sessionId", sessionId);
				receiveData.put("login_type",user.getUser_type().toString());
				receiveData.put("id", id);
				//이후 인증이 필요한 요청에 대해서는 id가 아닌 sessionId를 활용해서 요청해야 합니다.
				String sendData=socket.encryptSendData(receiveData.toJSONString());
				return Message.createSuccess(sendData);
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
			socket.removeSession(sessionId);
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	//로그아웃
	static String logout(String data, SocketBinder socket) {
		try {
			JSONObject receiveData = (JSONObject)Command.parser.parse(data);
			String sessionId = (String) receiveData.get("sessionId");
			Session session=socket.getSession(sessionId);
			if(session==null)
				return Message.createFail("expired_session");
			else {
				socket.removeSession(sessionId);
				return Message.createSuccess(socket.encryptSendData("session_is_deleted"));
			}
		}catch(Exception e) {
			return Message.createFail("요청 처리 실패");
		}
	}
	//id 중복검사
	static String check(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_id = (JSONObject)Command.parser.parse(data);

			if(conn==null) {
				return Message.createFail("fail");
			}
			
			UsersDAO dao=new UsersDAO(conn);
			String id = (String) data_id.get("id");
			Users user = dao.checkID(id);
			
			conn.close();
			if(user==null) {
				//중복없음
				receiveData.put("check", "check");
				String sendData=socket.encryptSendData(receiveData.toJSONString());
				return Message.createSuccess(sendData);
			}
			else {
				System.out.println("실패");
				return Message.createFail("duplicate");
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	//회원가입
	static String signUp(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_id = (JSONObject)Command.parser.parse(data);

			if(conn==null) {
				return Message.createFail("fail");
			}

			UsersDAO dao=new UsersDAO(conn);
			
			String[] info = new String[5];
			info[0]  = (String) data_id.get("id").toString();
			info[1]  = (String) data_id.get("password");
			info[2]  = (String) data_id.get("email");
			info[3]  = (String) data_id.get("name");
			info[4]  = (String) data_id.get("user_type");

			dao.insertDB(info);
			conn.close();
			receiveData.put("signUp", "check");
			String sendData=socket.encryptSendData(receiveData.toJSONString());
			return Message.createSuccess(sendData);
		}			
		catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	//아이디 찾기
	static String findID(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_id = (JSONObject)Command.parser.parse(data);

			if(conn==null) {
				return Message.createFail("fail");
			}

			UsersDAO dao=new UsersDAO(conn);
			
			String id = dao.findId(data_id.get("name").toString(),data_id.get("email").toString());
			conn.close();
			
			receiveData.put("findID", id);
			String sendData=socket.encryptSendData(receiveData.toJSONString());
			return Message.createSuccess(sendData);
		}			
		catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	//비밀번호 찾기
	static String findPWD(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_id = (JSONObject)Command.parser.parse(data);

			if(conn==null) {
				return Message.createFail("fail");
			}

			UsersDAO dao=new UsersDAO(conn);
			String result = dao.findPwd(data_id.get("name").toString(),data_id.get("id").toString(),data_id.get("email").toString());
			
			conn.close();
			receiveData.put("findPWD", result);
			String sendData=socket.encryptSendData(receiveData.toJSONString());
			return Message.createSuccess(sendData);
		}			
		catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	
	//비밀번호 변경
	static String UpdatePWD(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_id = (JSONObject)Command.parser.parse(data);

			if(conn==null) {
				return Message.createFail("fail");
			}

			UsersDAO dao=new UsersDAO(conn);
			dao.updatePWD(data_id.get("id").toString(),data_id.get("pwd").toString());
			conn.close();
			
			receiveData.put("UpdatePWD", "완료");
			String sendData=socket.encryptSendData(receiveData.toJSONString());
			return Message.createSuccess(sendData);
		}			
		catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	
	//예약정보 출력
	static String my_reservation_print(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_idpw = (JSONObject)Command.parser.parse(data);
			
			if(conn==null) {
				return Message.createFail("fail");
			}
			RoomsDAO room_dao = new RoomsDAO(conn);
			ReservationsDAO dao = new ReservationsDAO(conn);
			String id = (String) data_idpw.get("id");

			List<Reservations> reservations= dao.printREservations(id);
			JSONArray arr = new JSONArray();
			
    		if(reservations==null) {
    			conn.close();
    			return Message.createFail("현재 예약이 없습니다.");
    		}
    		else {
    			for(Reservations reservation : reservations) {
    				String pwd = room_dao.RoomPWD(reservation.getRoom_id());
    				String room_name = room_dao.RoomName(reservation.getRoom_id());
    				String str = reservation.getReservation_id()+","+
    						reservation.getRoom_id()+","+reservation.getReservation_date()+","+
    						reservation.getReservation_time()+","+reservation.getTotal_price()+","+pwd+","+room_name
    						+","+reservation.getDateOfReservation()+","+reservation.getPeople();
    				System.out.println(str);
    				arr.add(str);
    			}
    			conn.close();
    		}
    		receiveData.put("my_reservation_print", arr);
    		String sendData=socket.encryptSendData(receiveData.toJSONString());
			return Message.createSuccess(sendData);
		}catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	
	//예약했던 목록
	static String my_past_reservation_print(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_idpw = (JSONObject)Command.parser.parse(data);
			if(conn==null) {
				return Message.createFail("fail");
			}
			RoomsDAO room_dao = new RoomsDAO(conn);
			ReservationsDAO dao = new ReservationsDAO(conn);
			String id = (String) data_idpw.get("id");

			List<Reservations> reservations= dao.printPastREservations(id);
			JSONArray arr = new JSONArray();
			
    		if(reservations==null) {
    			conn.close();
    			return Message.createFail("현재 예약이 없습니다.");
    		}
    		else {
    			for(Reservations reservation : reservations) {
    				String pwd = room_dao.RoomPWD(reservation.getRoom_id());
    				String room_name = room_dao.RoomName(reservation.getRoom_id());
    				String str = reservation.getReservation_id()+","+
    						reservation.getRoom_id()+","+reservation.getReservation_date()+","+
    						reservation.getReservation_time()+","+reservation.getTotal_price()+","+pwd+","+room_name
    						+","+reservation.getDateOfReservation()+","+reservation.getPeople();
    				System.out.println(str);
    				arr.add(str);
    			}
    			conn.close();
    		}
    		receiveData.put("my_past_reservation_print", arr);
    		String sendData=socket.encryptSendData(receiveData.toJSONString());
			return Message.createSuccess(sendData);
		}catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	
	//방찾기
	static String findRoom(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_info = (JSONObject)Command.parser.parse(data);

			if(conn==null) {
				return Message.createFail("fail");
			}
			RoomsDAO dao =  new RoomsDAO(conn);
			//String[] info = {"hourly_rate>=60000"};
			ArrayList<String> info = new ArrayList<>();
			if(!(data_info.get("max_capacity").toString().equals(""))) {
				info.add( "r.max_capacity >="+data_info.get("max_capacity").toString());
			}
			if(!(data_info.get("date").toString().equals(""))) {
				info.add( "rs.date = '"+data_info.get("date").toString()+"'");
			}
			if(!(data_info.get("time").toString().equals(""))) {
				info.add( "rs.time = '"+data_info.get("time").toString()+"'");
			}
			if(!(data_info.get("location").toString().equals(""))) {
				info.add( "r.location like '%"+data_info.get("location").toString()+"%'");
			}
			String sql = "SELECT distinct r.* FROM Rooms r JOIN Room_Schedules rs ON r.room_id = rs.room_id  WHERE " + String.join(" AND ", info);

			if (info.isEmpty()) {
			    sql = "SELECT distinct r.* FROM Rooms r JOIN Room_Schedules rs ON r.room_id = rs.room_id "; // 또는 원하는 대로 처리
			}
			System.out.println(sql);
			List<Rooms> rooms = dao.search(sql);
			
			JSONArray arr = new JSONArray();
			int i =0;
			conn.close();
    		if(rooms==null)
    			return Message.createFail("현재 예약가능한 방이 없습니다.");
    		else {
    			for(Rooms room : rooms) {
    				String str = room.getRoom_name()+","+room.getRoom_pic()+","+
    							room.getMax_capacity()+","+room.getLocation()+","+room.getService()+","+room.getHourly_rate()+","
    							+room.getDetails()+","+room.getRoom_id();
    				arr.add(str);
    			}
    		}
    		receiveData.put("findRoom", arr);
    		String sendData=socket.encryptSendData(receiveData.toJSONString());
			return Message.createSuccess(sendData);
		}			
		catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	
	//예약 삭제
    static String deleteReservation(String data, SocketBinder socket) {
    	 Connection conn = socket.getConnection();
         try {
             // 데이터 파싱
             JSONObject receiveData = (JSONObject) new org.json.simple.parser.JSONParser().parse(data);
             int reservation_id = Integer.parseInt(receiveData.get("reservation_id").toString());

             if (conn == null)
                 return Message.createFail("데이터베이스 연결 실패");

             ReservationsDAO reservation = new ReservationsDAO(conn);
             Reservations reserv = reservation.getReservation(reservation_id); 
             if(reserv == null) {
            	 conn.close();
            	 return Message.createFail("방 정보 인식 불가");
             }
             
             Room_SchedulesDAO roomSchedule = new Room_SchedulesDAO(conn);
             
             //결제 취소
             reservation.ReservationCancel(reservation_id);
             roomSchedule.availableUpdate(
                 reserv.getRoom_id(), reserv.getReservation_date(), 
                 reserv.getReservation_time(), true);
             
             conn.close();
             String sendData=socket.encryptSendData("방 삭제 처리 성공");
             return Message.createSuccess(sendData);
         } catch (Exception e) {
             System.out.println("[서버] 방 삭제 처리 실패: " + e.getMessage());
 			 try { if(conn!=null) conn.close(); }catch(Exception ex) {}
             return Message.createFail("방 삭제 처리 실패");
         }
    }
    
    //고객 정보 찾기
	static String findUser(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_id = (JSONObject)Command.parser.parse(data);

			if(conn==null)
				return Message.createFail("fail");
			
			UsersDAO dao=new UsersDAO(conn);
			String id = (String) data_id.get("id");
			Users user = dao.checkID(id);
			conn.close();
			receiveData.put("check",user.getName()+","+user.getEmail());
			String sendData=socket.encryptSendData(receiveData.toJSONString());
			return Message.createSuccess(sendData);
		}catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	
	//정보 변경
	static String changeInfo(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_id = (JSONObject)Command.parser.parse(data);
			
			if(conn==null)
				return Message.createFail("fail");

			UsersDAO dao=new UsersDAO(conn);
			dao.changeInfo(data_id.get("id").toString(),data_id.get("pwd").toString(),data_id.get("email").toString());
			conn.close();
			receiveData.put("UpdatePWD", "완료");
			String sendData=socket.encryptSendData(receiveData.toJSONString());
			return Message.createSuccess(sendData);
			}			
		catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	
	//방정보 찾기
	static String findRoomByid(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_info = (JSONObject)Command.parser.parse(data);

			if(conn==null) {
				return Message.createFail("fail");
			}
			RoomsDAO dao =  new RoomsDAO(conn);
			List<Rooms> rooms= dao.findRoomBYID(data_info.get("room_id").toString());
			JSONArray arr = new JSONArray();
			
			conn.close();
    		if(rooms==null)
    			return Message.createFail("현재 예약가능한 방이 없습니다.");
    		else {
    			for(Rooms room : rooms) {
    				String str = room.getRoom_name()+","+room.getRoom_pic()+","+
    							room.getMax_capacity()+","+room.getLocation()+","+room.getService()+","+room.getHourly_rate()+","
    							+room.getDetails()+","+room.getRoom_id();
    				arr.add(str);
    			}
    		}
    		receiveData.put("findRoom", arr);
    		String sendData=socket.encryptSendData(receiveData.toJSONString());
			return Message.createSuccess(sendData);
		}			
		catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	
	//예약
	static String reservation(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_info = (JSONObject)Command.parser.parse(data);

			if(conn==null)
				return Message.createFail("fail");
			
			Object[] info = {Integer.parseInt(data_info.get("room_id").toString()),
					data_info.get("user_id").toString(),
					data_info.get("reservation_date").toString(),
					data_info.get("reservation_time").toString(),
					Integer.parseInt(data_info.get("people").toString())
					};
			
			ReservationsDAO reservation_dao = new ReservationsDAO(conn);
    		reservation_dao.Reservation(info);

			conn.close();
			
			receiveData.put("reservation", "완료");
			String sendData=socket.encryptSendData(receiveData.toJSONString());
			return Message.createSuccess(sendData);
			}			
		catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
	
	//이용가능 시간 반환
	static String available_time(String data, SocketBinder socket) {
		Connection conn=socket.getConnection();
		try {
			JSONObject receiveData = new JSONObject();			
			JSONObject data_info = (JSONObject)Command.parser.parse(data);

			if(conn==null)
				return Message.createFail("fail");
			
			Room_SchedulesDAO dao =  new Room_SchedulesDAO(conn);
			String[] result_time = dao.result_time(Integer.parseInt(data_info.get("room_id").toString()),data_info.get("reservation_date").toString());
			String result=String.join(",", result_time);
			result = result.replace("null,", "");
			result = result.replace(",null", "");
			result = result.replace("null", "");
			conn.close();
    		receiveData.put("result", result);
    		String sendData=socket.encryptSendData(receiveData.toJSONString());
			return Message.createSuccess(sendData);
		}			
		catch(Exception e) {
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			return Message.createFail("요청 처리 실패");
		}
	}
}