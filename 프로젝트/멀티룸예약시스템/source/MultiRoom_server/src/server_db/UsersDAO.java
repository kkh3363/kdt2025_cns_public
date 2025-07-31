package server_db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import server_util.PasswordHash;

public class UsersDAO {
	private Connection conn = null;
	private PreparedStatement pstmt = null;
	private ResultSet rs = null;
	
	public UsersDAO(Connection conn) {
		this.conn = conn;
	}
	//회원가입에 필요 (정보 insert)
	public void insertDB(String[] info) {
		try {
			String sql = ""+
					"insert into Users"
					+ "	values(?,?,?,?,?);";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, info[0]);
			pstmt.setString(2, PasswordHash.getHashText(info[1]));
			pstmt.setString(3, info[2]);
			pstmt.setString(4, info[3]);
			pstmt.setString(5, info[4]);
			pstmt.executeUpdate(); //쿼리 실행
			pstmt.close();
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public Users checkID(String id) { 
		Users user = null;
		try {
			String sql = """
				select * from Users 
				where id=?;
						""";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery(); //쿼리 실행
			
			if(rs.next()){
				user = new Users(
					rs.getString("id"),
					rs.getString("password"),
					rs.getString("email"),
					rs.getString("name"),
					rs.getString("user_type")
				);
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	public void updatePWD(String id, String pwd) {
		try {
			String sql = ""+
					"update Users set"
					+ "	password=? where id = ?;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, PasswordHash.getHashText(pwd));
			pstmt.setString(2, id);

			pstmt.executeUpdate(); //쿼리 실행
			
			pstmt.close();
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public void changeInfo(String id, String pwd,String email) {
		try {
			String sql = ""+
					"update Users set"
					+ "	password=? , email =? where id = ?;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, PasswordHash.getHashText(pwd));
			pstmt.setString(2, email);
			pstmt.setString(3, id);

			pstmt.executeUpdate(); //쿼리 실행
			
			pstmt.close();
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	//필요한 쿼리문 작성, 예시.
	public Users LoginByIDPWD(String id,String password) { 
		Users user = null;
		try {
			String sql = """
				select * from Users 
				where id=? and password=?;
						""";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);
			pstmt.setString(2, PasswordHash.getHashText(password));
			rs = pstmt.executeQuery(); //쿼리 실행
			
			if(rs.next()){
				user = new Users(
					rs.getString("id"),
					rs.getString("password"),
					rs.getString("email"),
					rs.getString("name"),
					rs.getString("user_type")
				);
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public String findId(String name, String email) {
		String id="";
		try {
			String sql = "select id from Users where name = ? and email = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, email);
			rs = pstmt.executeQuery(); //쿼리 실행
			
			if(rs.next()){
				id = rs.getString("id");
			}
			else {
				id = "없습니다.";
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return id;
	}
	
	public String findPwd(String name, String id , String email) {
		String result="";
		try {
			String sql = "select id, password from Users where name = ? and email = ? and id = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, name);
			pstmt.setString(2, email);
			pstmt.setString(3, id);
			rs = pstmt.executeQuery(); //쿼리 실행
			
			if(rs.next()){
				result = id+" 변경가능";
			}
			else {
				result = "없습니다.";
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	public Users printUserInfo() { 
		Users user = null;
		try {
			String sql = """
				select id, from Users ;
						+""";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery(); //쿼리 실행
			
			if(rs.next()){
				user = new Users(
					rs.getString("id"),
					rs.getString("password"),
					rs.getString("email"),
					rs.getString("name"),
					rs.getString("user_type")
				);
			}
			rs.close();
			pstmt.close();
			rs = null;
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return user;
	}
	
	public void deleteUser(String id) {
		try {
			String sql = ""+
					"delete from Users where id = ?;";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, id);

			pstmt.executeUpdate(); //쿼리 실행
			
			pstmt.close();
			pstmt = null;
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public List<Users> getUsers() {
        List<Users> userList = new ArrayList<>();
        String sql = "SELECT * FROM Users ORDER BY id"; // 사용자 정보를 ID 순으로 정렬하여 가져옴

        try {
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Users user = new Users(
                    rs.getString("id"),
                    rs.getString("password"),
                    rs.getString("email"),
                    rs.getString("name"),
                    rs.getString("user_type")
                );
                userList.add(user);
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

        return userList;
    }
	
	 // 사용자 추가 메서드
    public boolean insertUser(Users user) {
        String sql = "INSERT INTO Users (id, password, email, name, user_type) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getId());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getEmail());
            pstmt.setString(4, user.getName());
            pstmt.setString(5, user.getUser_type());

            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;  // 삽입된 행이 1개 이상이면 성공
        } catch (SQLException e) {
            System.out.println("[서버] 사용자 추가 SQL 오류: " + e.getMessage());
            return false;
        }
    }
    
 // UsersDAO 클래스에 추가
    public boolean managerdeleteUser(String id) {
        String sql = "DELETE FROM Users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(id);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 사용자 삭제 실패: " + e.getMessage());
            return false;
        }
    }
    
}
