package client_test;

import java.io.File;
import java.util.Scanner;
import org.json.simple.JSONObject;

import client_test.FileManager.PATH;

public class TestClientMain {
	
	//에코테스트
	public static void echoTest() throws Exception{
		ClientConnector client = new ClientConnector();
		Scanner sc = new Scanner(System.in);
		client.connect(); //연결 설정
		while(true) {
			//사용 예시
			System.out.print("전달할 데이터 입력 : ");
			String data=sc.nextLine();
			if(data.toLowerCase().trim().equals("q"))
				break;
			
			//예외처리 등록
			client.setExceptionHandler(e->{
				System.out.println(e.getMessage());
				client.disconnect();
			});
			
			//(명령어, 데이터) -> 서버로 보낼 데이터 ||response -> 서버에서 넘어온 데이터 처리 
			client.requestAndReponseSync("echo", data, response ->{
				JSONObject json = client.parse(response);
				String status = json.get("status").toString(); 
				//상태에 따른 이벤트 처리 방법
				switch(status) {
					case("success"):
						String encryptedData = json.get("data").toString();
						String originData=client.decryptReceivedData(encryptedData);
						System.out.println("넘어온 데이터 : "+originData);
						break;
					case("invalid"):
					case("fail"):
						String message = json.get("message").toString();
						System.out.println("넘어온 데이터 : "+message);
						break;
				}
			});
		}
		sc.close();
		client.disconnect();
	}
	
	//파일 요청 테스트
	public static void getRoomImg() throws Exception{
		ClientConnector client = new ClientConnector();
		client.connect();
		
    	client.setExceptionHandler(e->{
			client.disconnect();
		});
    	
    	//방id와 같은 데이터
		String data="임시데이터";
		
		//파일 전송 요청
    	client.requestAndReponseSync("getRoomImg", data, response -> {
			
    		JSONObject json = client.parse(response);
			String status = json.get("status").toString(); 
			
			switch(status) {
				case("success"):
					String encryptedData = json.get("data").toString(); //암호화된 데이터를 얻습니다. 
					String originData=client.decryptReceivedData(encryptedData); //복호화합니다.
					
					//메타 데이터 추출
					JSONObject jsonObj = (JSONObject)client.parse(originData);
					String metaDataString = (String)jsonObj.get("s2cFTP");
					JSONObject metaData = (JSONObject)client.parse(metaDataString);
					
					//FTP 수신 호출 -> 파일을 성공적으로 받았는지 여부를 반환
					boolean success=client.s2cFTP(metaData); 
					client.saveData("success", success);
					break;
				case("invalid"):
				case("fail"):
					String message = json.get("message").toString();
					System.out.println("넘어온 데이터 : "+message);
					client.saveData("success", false);
					break;
			}
		});
    	
    	//파일 전송 결과 확인
    	boolean success=(boolean)client.popData("success");
    	if(!success) {
    		client.disconnect();
    		return;
    	}
    	//전송에 성공한 경우 fileNames라는 이름으로 파일 이름 목록이 만들어짐
    	
    	//받은 파일 사용하기
    	String[] fileNames=(String[])client.popData("fileNames");
    	for(String fileName : fileNames) {
    		byte[] file=(byte[])client.popData(fileName);
    		//파일 쓰기
    		FileManager.writeFile(PATH.CLIENTIMAGE, "/"+fileName, file);
    		//파일 읽기
    		File f=FileManager.getFile(PATH.CLIENTIMAGE, "/"+fileName);
    		System.out.println("받은 파일 : "+f.getName());
    	}
    	//연결 종료
    	client.disconnect();
	}
	
	//파일 전송 테스트
	public static void setRoom() throws Exception {
		ClientConnector client = new ClientConnector();
		client.connect();
		
    	client.setExceptionHandler(e->{
    		e.printStackTrace();
		});
    	
    	String data = "서버에 저장할 방정보";
    	client.requestAndReponseSync("setRoomImg", data, response->{
    		
    		JSONObject json = client.parse(response);
			String status = json.get("status").toString(); 
			
			switch(status) {
				case("success"):
					String encryptedData = json.get("data").toString(); //암호화된 데이터를 얻습니다. 
					String originData=client.decryptReceivedData(encryptedData); //복호화합니다.
					client.saveData("fileTransfer", originData);
					break;
				case("invalid"):
				case("fail"):
					String message = json.get("message").toString();
					System.out.println("넘어온 데이터 : "+message);
					client.saveData("fileTransfer", null);
					break;
			}
    	});
    	
    	String ftp = (String)client.popData("fileTransfer");
    	if(ftp==null) {
    		client.disconnect();
    		return;
    	}
    	
    	//전송할 파일리스트 생성
    	File[] files = new File[1];
    	files[0] = FileManager.getFile(PATH.CLIENTIMAGE, "/888.png");
    	//전송 버퍼 크기 4KB
    	int bufferSize = 1024*4;
    	
    	//자동으로 파일의 메타 데이터를 생성한 뒤 서버로 보냅니다.
    	String success=client.c2sFTP(files,bufferSize);
    	
    	//결과값 확인
		if(success!=null)
			System.out.println("[응답] : "+success);
		else
			System.out.println("파일 전송 실패");
		
		client.disconnect();
	}
	
	public static void main(String[] args) throws Exception{
		//getRoomImg(); //파일 가져오기
		setRoom(); //파일 전송
	}
}

/* 비동기 처리 시 사용
client.requestAndReponseASync("search", "params", response -> 
    System.out.println("서버로부터 검색한 데이터 : " + response)
).thenRun(() ->{ 
	//비동기 데이터 처리 이후 코드 수행
	System.out.println("비동기 처리 완료됨");
});
//또는 하나의 쓰레드를 만들고 그 안에서 client 연결을 만들어 사용할 수도 있음 
*/
	