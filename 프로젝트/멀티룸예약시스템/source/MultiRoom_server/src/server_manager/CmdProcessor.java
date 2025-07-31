package server_manager;


import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import server_util.*;
import crypto.*;
//import server_cmd.*;
import server_cmd.Command;

//명령어 처리기
public class CmdProcessor {

	private String cmd;
	private String data;
	private SocketBinder socket;
	
	//함수형 인터페이스로 메서드 등록 -> origin data를 받아서 처리 후 응답 반환
	private static final ConcurrentHashMap<String, BiFunction<String, SocketBinder, String>> commandMapper;
	
	//명령어 확장
	static {
		commandMapper = Command.getCommandMapper();
	}
	
	private CmdProcessor() { super(); }
	
	//생성 시 명령어와 데이터를 분리함
	public CmdProcessor(String encryptedData, SocketBinder socket) {
		seperateCmdData(encryptedData,socket);
	}
	
	//명령어, 데이터 분리 메서드
	private void seperateCmdData(String encryptedData,SocketBinder socket) {
		String originData=null;
		try {
			this.socket = socket;
			originData=socket.decryptReceivedData(encryptedData); //암호화된 데이터 복호화
			JSONParser parser = new JSONParser();
			JSONObject jsonObj = (JSONObject)parser.parse(originData);
			this.cmd = jsonObj.get("command").toString();
			this.data = jsonObj.get("data").toString();
		}catch(Exception e) {
			System.out.println("[예외] : "+e.getMessage());
			this.cmd = null;
			this.data = null;
			this.socket = null;
		}
	}
	
	//명령 실행기
	public String executor() throws Exception{
		String response;
		if(cmd==null) { //복호화가 안되는 경우
			response = Message.createFail("처리 불가능한 형식");
		}
		else {
			response = commandMapper.getOrDefault(cmd, (data,socket)->
				Message.createInvalid("유효하지 않은 명령어")
			).apply(data,socket);
		}
		return response;
	}
}