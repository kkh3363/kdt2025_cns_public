package server_cmd;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Queue;

import org.json.simple.JSONObject;

import server_db.Room_SchedulesDAO;
import server_db.RoomsDAO;
import server_manager.SocketBinder;
import server_util.FileManager;
import server_util.Message;
import server_util.FileManager.PATH;

public class FileTransferCommand {
	
	//방 이미지 전송 예제
	static String getRoomImg(String data, SocketBinder socket) {
		
		//1. data로 받은 파라미터로부터 방 이름이나 ID를 얻어냄
		
		//2. DB에서 방id를 조회해서 이미지들이 저장된 주소를 가져옴
		
		//3. 주소를 아래 메서드에 넣어서 파일 이름을 취득했다고 가정함
		String[] fileSubPaths = FileManager.getFileNames(PATH.IMAGE, data);
		
		//파일이 존재하는 경우에만 FTP 수행
		if(fileSubPaths==null)
			return Message.createFail("no_file");
		else
			return FileTransfer.s2cFTP(PATH.IMAGE, fileSubPaths, socket);
	}

	//방 이미지 설정 예제
	static String setRoom(String data, SocketBinder socket) {
		String response = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Calendar calendar = Calendar.getInstance();
		
		Connection conn=socket.getConnection();
		try {
			JSONObject data_info = (JSONObject)Command.parser.parse(data);
			
			if(conn==null)
				return Message.createFail("fail");
			
			RoomsDAO dao = new RoomsDAO(conn);
			Object[] info= {data_info.get("room_name"),
					null,
					Integer.parseInt(data_info.get("max_capacity").toString()),
					data_info.get("location"),
					data_info.get("service"),
					data_info.get("room_password"),
					Integer.parseInt(data_info.get("hourly_rate").toString()),
					data_info.get("details")};
			int room_id = dao.insertDB(info);
			

            // 시작 날짜와 종료 날짜 설정
            String startDate = data_info.get("date1").toString();
            String endDate = data_info.get("date2").toString();

            // 날짜 문자열을 Date 객체로 변환
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);

            calendar.setTime(start);
            System.out.println(data_info.get("time").toString());
            String[] time_array = data_info.get("time").toString().split(" ");
            // 데이터 삽입
            Room_SchedulesDAO roomSchedule = new Room_SchedulesDAO(conn);
            
            while (!calendar.getTime().after(end)) {
                //roomSchedule.insertDB(room_id, new java.sql.Date(calendar.getTime().getTime()), "오전", true);
                //int room_id, Date date, String time, boolean available
                for (String s : time_array) {
                    roomSchedule.insertDB(room_id, new java.sql.Date(calendar.getTime().getTime()), s, true);
                }
                //날짜를 하루 증가
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }
            conn.close();
            
			//클라이언트 파일 전송 수락
			String encryptedData=socket.encryptSendData("c2sFTP");
			String sendDataString=Message.createSuccess(encryptedData);
			socket.sendToClient(sendDataString);
			
			//파일 수신 호출
			Queue<byte[]> files = FileTransfer.c2sFTP(socket);
			
			if(files==null)
				throw new Exception("[예외] 파일 수신 실패");
			
			//파일 저장공간 생성
			//여기에 room을 방ID로 대체하는 방안을 고려해볼 수 있음
			FileManager.createDir(PATH.IMAGE, "/"+Integer.toString(room_id));
			//파일 개수
			int fileCount=files.size();
			//파일 쓰기
			for(int i=0;i<fileCount;++i) {
				byte[] file = files.poll();
				if(!FileManager.writeFile(PATH.IMAGE, "/"+Integer.toString(room_id)+"/"+(i+1)+".png", file)) {
					//파일 쓰기 롤백
					FileManager.deleteDir(PATH.IMAGE, "/"+Integer.toString(room_id), true);
					throw new Exception("[예외] 파일을 쓸 수 없음");
				}
			}
			response = socket.encryptSendData("file_received_successfully");
		}catch(Exception e) { //파일 수신 또는 쓰기 오류 시 롤백 처리
			//기존 DB 작업 롤백
			System.out.println(e.getMessage());
			try { if(conn!=null) conn.close(); }catch(Exception ex) {}
			socket.close(); //소켓 연결 끊기
		}
		return Message.createSuccess(response);
	}
}