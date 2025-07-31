package server_db;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import server_util.PasswordHash;


public class TestConnection {
	
	public static void main(String[] args) {
		DBManager.getInstance().openDBCP(); //커넥션 풀 생성
		Connection conn = null;
        try{
        	conn = DBManager.getInstance().getConnection();
        	System.out.println("연결 성공");
        	UsersDAO user_dao = new UsersDAO(conn);
        	RoomsDAO room_dao = new RoomsDAO(conn);
    		
        	//회원가입
        	//String[] user_info = {"test","test","TEST@naver.com","박테스","유저"};
        	//user_dao.insertDB(user_info);
        	
        	//로그인
        	Users user = user_dao.LoginByIDPWD("manager","manager");
    		if(user==null)
    			System.out.println("결과없음");
    		else {
    			System.out.println(user);
    		}
    		
        	//비밀번호 변경
        	user_dao.updatePWD("user","user");
        	
        	
        	//변경된게 맞는지 확인
    		Users user2 = user_dao.LoginByIDPWD("user","user");
    		if(user2==null)
    			System.out.println("결과없음");
    		else {
    			System.out.println(user2);
    		}
    		//관리자면 방등록 테스트
    		if((user.getUser_type()).equals("관리자")){
    	        Object[] info = {"천안9호","test.png",5,"1235",50000,"접근성좋음, 가격 쌉니다. 회의실로 가능"};
    			room_dao.insertDB(info);
    		}
    		
    		//조건 검색
    		String[] info = {"hourly_rate>=60000"}; // 시간당 6만원 이상인 회의실
    		//List<Rooms> rooms = room_dao.search(info);
    		//if(rooms==null)
    		//	System.out.println("결과없음");
    		//else {
    		//	for(Rooms room : rooms) {
    		//		System.out.println(room.getRoom_name()+ " / " +room.getDetails());
    		//	}
    		//}
    		
        	//예약
    		ReservationsDAO reservation_dao = new ReservationsDAO(conn);
    		//Object[] info2 = {3,"hankyul","2024-08-29","오후"};
    		//reservation_dao.Reservation(info2);
    		//Room_Sechedules에서도 바꿔야댐
    		
    		//예약 취소
    		//reservation_dao.ReservationCancel(3,"hankyul");
    		//Room_Sechedules에서도 바꿔야댐
    		
    		//사용자의 예약정보 출력

    		List<Reservations> resrvations= reservation_dao.printREservations("hankyul");
    		//if(rooms==null)
    		//	System.out.println("결과없음");
    		//else {
    		//	for(Reservations resrvation : resrvations) {
    		//		System.out.println(resrvation.getRoom_id()+ " / " +resrvation.getTotal_price());
    		//	}
    		//}
        	conn.close(); //커넥션 풀에 반환
        }catch (SQLException e) {
        	System.out.println("연결 실패");
        }finally {
        	//애플리케이션 종료 시 커넥션풀 닫기 수행
        	DBManager.getInstance().closeDBCP();
        }
	}
}
