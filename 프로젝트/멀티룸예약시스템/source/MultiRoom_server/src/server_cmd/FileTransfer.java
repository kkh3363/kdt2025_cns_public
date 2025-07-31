package server_cmd;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import server_db.Users;
import server_db.UsersDAO;
import server_manager.SocketBinder;
import server_util.FileManager;
import server_util.FileManager.PATH;
import server_util.Message;
import server_util.STimer;

public class FileTransfer {
	/********** 해당 프로토콜 구현체를 외부에서 호출 ******************/
	
	//1. 서버 -> 클라이언트 : 파일전송(메타 데이터 전송 및 파일 전송)
	static String s2cFTP(PATH path, String[] fileSubPaths, SocketBinder socket) {
		if(fileSubPaths == null || socket == null)
			return Message.createFail("no_files");
		
		//파일 가져오기
		File[] files = new File[fileSubPaths.length];
		
		//실제로 파일을 가져올 수 있는지 검토
		int fileCnt = 0;
		for(int i=0;i<fileSubPaths.length;++i) {
			files[i] = FileManager.getFile(path, fileSubPaths[i]);
			if(files[i]!=null)
				fileCnt++;
		}
		
		if(fileCnt==0)
			return Message.createFail("no_files");
		File[] checkedFiles = new File[fileCnt];	
		int checkCnt=0;
		
		//유효한 파일만 생성
		for(int i=0;i<files.length;++i) {
			if(files[i] == null)
				continue;
			checkedFiles[checkCnt++] = files[i];
		}
		
		//버퍼 사이즈 지정
		int bufferSize = 1024*4; //4KB
		
		//보낼 파일의 메타 데이터 생성
		String metaData = socket.createMetaFromFile(checkedFiles, bufferSize);
		JSONObject sendData = new JSONObject();
		sendData.put("s2cFTP", metaData);
		String success=null;
		try {
			//메타 데이터 전송
			metaData=socket.encryptSendData(sendData.toJSONString());
			socket.sendToClient(Message.createSuccess(metaData));
			//파일 전송
			socket.sendFiles(checkedFiles, bufferSize);
			//파일 전송 성공 시 데이터 
			success=socket.encryptSendData("file_transferred_successfully");
		}catch(Exception e) {
			System.out.println("[예외] 전송 실패 : "+e.getMessage());
			socket.close();
		}
		//마지막으로 파일 전송 성공 알림
		return Message.createSuccess(success);
	}
	
	//2. 클라이언트 -> 서버 : 파일전송(메타 데이터 수신 및 파일 수신)
	static Queue<byte[]> c2sFTP(SocketBinder socket) {
		String response = null;
		try {
			//메타 데이터 가져오기
			String metaDataString = socket.receiveMetaData();
			
			JSONObject metaData = (JSONObject)Command.parser.parse(metaDataString);
			//파일 수(파일 수가 너무 많으면 제한 가능)
			int fileCount = ((Number)metaData.get("fileCount")).intValue();
			//파일 이름 : 파일이름은 서버에서 임의로 정함
			//파일 크기
			JSONArray array = (JSONArray)metaData.get("fileSizes");
			long[] fileSizes = new long[array.size()];
			for(int i=0;i<fileSizes.length;++i)
				fileSizes[i]=((Number)array.get(i)).longValue();
			//버퍼 크기
			int bufferSize = ((Number)metaData.get("bufferSize")).intValue();
			
			//전송받은 파일 메모리 저장
			Queue<byte[]> files = new LinkedList<>();
			
			//파일 리스트 생성
			for(int i=0;i<fileCount;++i) {
				byte[] file=socket.receiveFilesFromClient(fileSizes[i], bufferSize);
				files.add(file);
			}
			return files;
		}catch(Exception e) { //도중 오류 시
			System.out.println(e.getMessage());
			return null;
		}
	}

}