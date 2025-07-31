package server_cmd;

import java.sql.Connection;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import server_db.Room_Schedules;
import server_db.Room_SchedulesDAO;
import server_db.Rooms;
import server_db.RoomsDAO;
import server_db.Users;
import server_db.UsersDAO;
import server_manager.SocketBinder;
import server_util.Message;

public class ManagerCommand {
	//getUsers 명령어 처리 메서드
    static String getUsers(String data, SocketBinder socket) {
        Connection conn = null;
        try {
            conn = socket.getConnection();
            if (conn == null) {
                return Message.createFail("데이터베이스 연결 실패");
            }
            UsersDAO usersDAO = new UsersDAO(conn);
            List<Users> userList = usersDAO.getUsers();
            JSONArray jsonArray = new JSONArray();
            for (Users user : userList) {
                JSONObject jsonUser = new JSONObject();
                jsonUser.put("id", user.getId());
                jsonUser.put("password", user.getPassword());
                jsonUser.put("email", user.getEmail());
                jsonUser.put("name", user.getName());
                jsonUser.put("user_type", user.getUser_type());
                jsonArray.add(jsonUser);
            }
            conn.close();
            JSONObject receiveData = new JSONObject();
            receiveData.put("users", jsonArray);
            String sendData = socket.encryptSendData(receiveData.toJSONString());
            return Message.createSuccess(sendData);
        } catch (Exception e) {
            System.out.println("[서버] 사용자 목록 가져오기 실패: " + e.getMessage());
            try { if(conn!=null) conn.close(); }catch(Exception ex) {}
            return Message.createFail("사용자 목록 가져오기 실패");
        }
    }
    
    //사용자 추가 (insertUser) 명령어 처리 메서드
    static String insertUser(String data, SocketBinder socket) {
        Connection conn = null;
        try {
            // 데이터 파싱
            JSONObject receiveData = (JSONObject)Command.parser.parse(data);
            String id = (String) receiveData.get("id");
            String password = (String) receiveData.get("password");
            String email = (String) receiveData.get("email");
            String name = (String) receiveData.get("name");
            String user_type = (String) receiveData.get("user_type");

            // 데이터베이스 연결
            conn = socket.getConnection();
            if (conn == null) {
                return Message.createFail("데이터베이스 연결 실패");
            }

            // UsersDAO를 이용하여 사용자 추가
            UsersDAO usersDAO = new UsersDAO(conn);
            Users newUser = new Users(id, password, email, name, user_type);
            boolean isInserted = usersDAO.insertUser(newUser);  // insertUser 메서드가 UsersDAO에 있어야 함

            conn.close();
            if (isInserted) {
                // 성공적으로 추가된 경우
                JSONObject response = new JSONObject();
                response.put("status", "success");
                String sendData = socket.encryptSendData(response.toJSONString());
                return Message.createSuccess(sendData);
            } else {
                // 사용자 추가 실패
                return Message.createFail("사용자 추가 실패");
            }

        } catch (Exception e) {
            System.out.println("[서버] 사용자 추가 처리 실패: " + e.getMessage());
            try { if(conn!=null) conn.close(); }catch(Exception ex) {}
            return Message.createFail("사용자 추가 처리 실패");
        }
    }
    
    //사용자 삭제 (deleteUser) 명령어 처리 메서드
    static String deleteUser(String data, SocketBinder socket) {
        Connection conn = null;
        try {
            // 데이터 파싱
            JSONObject receiveData = (JSONObject)Command.parser.parse(data);
            String id = (String) receiveData.get("id");
            // 데이터베이스 연결
            conn = socket.getConnection();
            if (conn == null) {
                return Message.createFail("데이터베이스 연결 실패");
            }
            
            // UsersDAO를 이용하여 사용자 삭제
            UsersDAO usersDAO = new UsersDAO(conn);
            boolean isDeleted = usersDAO.managerdeleteUser(id);  // deleteUser 메서드가 UsersDAO에 있어야 함

            conn.close();
            if (isDeleted) {
                // 성공적으로 삭제된 경우
                JSONObject response = new JSONObject();
                response.put("status", "success");
                String sendData = socket.encryptSendData(response.toJSONString());
                return Message.createSuccess(sendData);
            } else {
                // 사용자 삭제 실패
                return Message.createFail("사용자 삭제 실패");
            }

        } catch (Exception e) {
            System.out.println("[서버] 사용자 삭제 처리 실패: " + e.getMessage());
            try { if(conn!=null) conn.close(); }catch(Exception ex) {}
            return Message.createFail("사용자 삭제 처리 실패");
        }
    }
    
    // getUsers 명령어 처리 메서드
    static String showRoom(String data, SocketBinder socket) {
        Connection conn = null;
        try {
            conn = socket.getConnection();
            if (conn == null) {
                return Message.createFail("데이터베이스 연결 실패");
            }
            RoomsDAO roomsDAO = new RoomsDAO(conn);
            List<Rooms> roomList = roomsDAO.getRooms();
            JSONArray jsonArray = new JSONArray();
            for (Rooms room : roomList) {
                JSONObject jsonRoom = new JSONObject();
                jsonRoom.put("room_id", room.getRoom_id());
                jsonRoom.put("room_pic", room.getRoom_pic());
                jsonRoom.put("room_name", room.getRoom_name());
                jsonRoom.put("max_capacity", room.getMax_capacity());
                jsonRoom.put("location", room.getLocation());
                jsonRoom.put("service", room.getService());
                jsonRoom.put("room_password", room.getRoom_password());
                jsonRoom.put("hourly_rate", room.getHourly_rate());
                jsonRoom.put("details", room.getDetails());
                jsonArray.add(jsonRoom);
            }
            conn.close();
            JSONObject receiveData = new JSONObject();
            receiveData.put("Rooms", jsonArray);
            String sendData = socket.encryptSendData(receiveData.toJSONString());
            return Message.createSuccess(sendData);
        } catch (Exception e) {
            System.out.println("[서버] 방 목록 가져오기 실패: " + e.getMessage());
            try { if(conn!=null) conn.close(); }catch(Exception ex) {}
            return Message.createFail("방 목록 가져오기 실패");
        }
    }
    
    // 사용자 삭제 (deleteUser) 명령어 처리 메서드
    static String deleteRoom(String data, SocketBinder socket) {
        Connection conn = null;
        try {
            // 데이터 파싱
            JSONObject receiveData = (JSONObject)Command.parser.parse(data);
            long room_id = (long) receiveData.get("room_id");

            // 데이터베이스 연결
            conn = socket.getConnection();
            if (conn == null) {
                return Message.createFail("데이터베이스 연결 실패");
            }

            // UsersDAO를 이용하여 사용자 삭제
            RoomsDAO roomsDAO = new RoomsDAO(conn);
            boolean isDeleted = roomsDAO.deleteroom(room_id);  // deleteroom 메서드가 RoomsDAO에 있어야 함

            conn.close();
            if (isDeleted) {
                // 성공적으로 삭제된 경우
                JSONObject response = new JSONObject();
                response.put("status", "success");
                String sendData = socket.encryptSendData(response.toJSONString());
                return Message.createSuccess(sendData);
            } else {
                // 방 삭제 실패
                return Message.createFail("방 삭제 실패");
            }

        } catch (Exception e) {
            System.out.println("[서버] 방 삭제 처리 실패: " + e.getMessage());
            try { if(conn!=null) conn.close(); }catch(Exception ex) {}
            return Message.createFail("방 삭제 처리 실패");
        }
    }
    
    //방정보 변경
    static String updateRoom(String data, SocketBinder socket) {
        Connection conn = null;
        try {
            // 데이터 파싱
            JSONObject receiveData = (JSONObject)Command.parser.parse(data);

            // JSON 데이터에서 값을 안전하게 가져오기
            Long room_id = null;
            Object roomIdObj = receiveData.get("room_id");
            if (roomIdObj instanceof String) {
                room_id = Long.parseLong((String) roomIdObj);
            } else if (roomIdObj instanceof Number) {
                room_id = ((Number) roomIdObj).longValue();
            }

            String room_pic = (String) receiveData.get("room_pic");
            String room_name = (String) receiveData.get("room_name");

            Long max_capacity = null;
            Object maxCapacityObj = receiveData.get("max_capacity");
            if (maxCapacityObj instanceof String) {
                max_capacity = Long.parseLong((String) maxCapacityObj);
            } else if (maxCapacityObj instanceof Number) {
                max_capacity = ((Number) maxCapacityObj).longValue();
            }
            String location = (String) receiveData.get("location");
            String service = (String) receiveData.get("service");
            String room_password = (String) receiveData.get("room_password");

            Long hourly_rate = null;
            Object hourlyRateObj = receiveData.get("hourly_rate");
            if (hourlyRateObj instanceof String) {
                hourly_rate = Long.parseLong((String) hourlyRateObj);
            } else if (hourlyRateObj instanceof Number) {
                hourly_rate = ((Number) hourlyRateObj).longValue();
            }

            String details = (String) receiveData.get("details");

            // 데이터베이스 연결
            conn = socket.getConnection();
            if (conn == null) {
                return Message.createFail("데이터베이스 연결 실패");
            }

            // RoomsDAO를 이용하여 방 정보 수정
            RoomsDAO roomsDAO = new RoomsDAO(conn);
            Rooms updatedRoom = new Rooms(room_id.intValue(), room_pic, room_name, max_capacity.intValue(),location,service, room_password, hourly_rate.intValue(), details);
            boolean isUpdated = roomsDAO.updateRoom(updatedRoom);

            conn.close();
            if (isUpdated) {
                // 성공적으로 수정된 경우
                JSONObject response = new JSONObject();
                response.put("status", "success");
                String sendData = socket.encryptSendData(response.toJSONString());
                return Message.createSuccess(sendData);
            } else {
                // 방 수정 실패
                return Message.createFail("방 수정 실패");
            }
        } catch (Exception e) {
            System.out.println("[서버] 방 수정 처리 실패: " + e.getMessage());
            try { if(conn!=null) conn.close(); }catch(Exception ex) {}
            return Message.createFail("방 수정 처리 실패");
        }
    }
    
    //방 스케줄 조회 (getRoomSchedules) 명령어 처리 메서드
    static String getRoomSchedules(String data, SocketBinder socket) {
        Connection conn = null;
        try {
            // Parse input data
            JSONObject receiveData = (JSONObject)Command.parser.parse(data);
            int room_id = ((Long) receiveData.get("room_id")).intValue(); // Cast to Long first, then to int

            // Connect to database
            conn = socket.getConnection();
            if (conn == null) {
                return Message.createFail("데이터베이스 연결 실패");
            }

            // Retrieve room schedules using Room_SchedulesDAO
            Room_SchedulesDAO roomSchedulesDAO = new Room_SchedulesDAO(conn);
            List<Room_Schedules> schedulesList = roomSchedulesDAO.getRoomSchedules(room_id);

            // Convert schedules to JSON
            JSONArray jsonArray = new JSONArray();
            for (Room_Schedules schedule : schedulesList) {
                JSONObject jsonSchedule = new JSONObject();
                jsonSchedule.put("room_id", schedule.getRoom_id());
                jsonSchedule.put("date", schedule.getDate().toString());  // Assuming date is of type java.sql.Date
                jsonSchedule.put("time", schedule.getTime());
                jsonSchedule.put("available", schedule.isAvailable());
                jsonArray.add(jsonSchedule);
            }

            conn.close();
            // Prepare response data
            JSONObject responseData = new JSONObject();  // Renamed to avoid conflict
            responseData.put("schedules", jsonArray);
            String sendData = socket.encryptSendData(responseData.toJSONString());
            return Message.createSuccess(sendData);

        } catch (Exception e) {
            System.out.println("[서버] 방 스케줄 조회 실패: " + e.getMessage());
            try { if(conn!=null) conn.close(); }catch(Exception ex) {}
            return Message.createFail("방 스케줄 조회 실패");
        }
    }
}