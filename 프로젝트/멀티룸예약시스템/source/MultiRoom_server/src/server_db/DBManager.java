package server_db;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import server_util.FileManager;
import server_util.FileManager.PATH;

public class DBManager {

	//연결 정보
	private static String db_hostname;
    private static int db_portnumber; //포트
    private static String db_database;
    private static String db_charset;
    private static String db_username;
    private static String db_password; //비번
	
    private static DBManager current = null; //DBManager
	private static HikariDataSource dataSource=null; //커넥션 풀  
	
	static {
		db_hostname = "localhost";
		db_portnumber = 3306;
		db_database = "MultiRoom";
		db_charset = "utf8";
		db_username = "name";
		db_password = "password";
		//없으면 파일 생성
		if(FileManager.createDir(PATH.DBCONFIG, "")) { 
			File file=FileManager.getFile(PATH.DBCONFIG, "db.conf");
			if(file==null) {
				JSONObject jsonObj = new JSONObject();
				jsonObj.put("hostName", db_hostname);
				jsonObj.put("portNumber", String.valueOf(db_portnumber));
			    jsonObj.put("databaseName", db_database);
			    jsonObj.put("dbCharset", db_charset);
			    jsonObj.put("dbUserName", db_username);
			    jsonObj.put("dbPassword", db_password);
			    String formattedJson=jsonObj.toString().replace(",", ",\n\t");
			    formattedJson = formattedJson.replace("{", "{\n\t");
			    formattedJson = formattedJson.replace("}", "\n}");
			    boolean tag=FileManager.writeFile(PATH.DBCONFIG, "db.conf", formattedJson);
			    //파일 쓰기 실패 시 디렉터리 삭제
			    if(!tag)
			    	FileManager.deleteDir(PATH.DBCONFIG, "", true);
			}
			else { //파일이 있으면 읽기
				try {
					BufferedReader br = new BufferedReader(new FileReader(file));
					StringBuffer sb = new StringBuffer();
					JSONParser parser = new JSONParser();
					String data;
					while((data = br.readLine())!=null)
						sb.append(data);
					data=sb.toString();
					data=data.replace("\t", ""); //복구
					JSONObject jsonObj = (JSONObject)parser.parse(data);
					db_hostname = jsonObj.get("hostName").toString();
					db_portnumber = Integer.parseInt(jsonObj.get("portNumber").toString());
					db_database = jsonObj.get("databaseName").toString();
					db_charset = jsonObj.get("dbCharset").toString();
					db_username = jsonObj.get("dbUserName").toString();
					db_password = jsonObj.get("dbPassword").toString();
					br.close();
				}catch(IOException e) {
					System.out.println("[에러] 파일을 읽을 수 없음 : "+e.getMessage());
				}catch(Exception e) {
					System.out.println("[에러] 파싱 오류 : "+e.getMessage());
				}
			}
		}
	}
	
	private DBManager() {
		super();
	}
	
	//DBManager Singleton
	public static DBManager getInstance() {
		return (current==null)?new DBManager():current;
	}
	
	//커넥션 가져오기
	public Connection getConnection() throws SQLException {
		if(dataSource!=null)
			return dataSource.getConnection();
		else
			return null;
	}
	//DB 연결
	public void openDBCP() {
		if(dataSource!=null)
			return;
		
		HikariConfig config = new HikariConfig();
		String urlFormat = "jdbc:mysql://%s:%d/%s?characterEncoding=%s&serverTimezone=Asia/Seoul";
		String url = String.format(urlFormat, db_hostname, db_portnumber, db_database,  db_charset);
        
		config.setJdbcUrl(url); //연결 설정
        config.setUsername(db_username);
        config.setPassword(db_password);
        
        //커넥션 풀 설정 계산 식에 의해 : 4*2+1 = 9
        config.setMinimumIdle(9); //최소 유휴 커넥션 수
        config.setMaximumPoolSize(9); //최대 커넥션 풀 사이즈
        
        config.setConnectionTimeout(30000); //30초 최대 커넥션 대기 시간(ms)
        config.setIdleTimeout(600000); //10분 커넥션 풀 유휴상태 최대 유지 시간(ms) 
        
        //보통 DB서버의 커넥션 최대 유지시간과 맞춤
        //MySQL 기준 기본 설정된 시간은 약 8시간 정도
        //확인 : SHOW VARIABLES LIKE 'wait_timeout'; 
        config.setMaxLifetime(25200000); //7시간 : 커넥션 최대 수명(ms)
        
        dataSource = new HikariDataSource(config);
        if(dataSource!=null)
        	System.out.println("[Info]커넥션 풀 열림");
        else
        	System.out.println("[Info]커넥션 풀 열기 실패");
	}
	
	//DB 연결 끊기
	public void closeDBCP() {
		if (dataSource != null) {
            dataSource.close();
            System.out.println("[Info]커넥션 풀 닫힘");
        }
	}
}