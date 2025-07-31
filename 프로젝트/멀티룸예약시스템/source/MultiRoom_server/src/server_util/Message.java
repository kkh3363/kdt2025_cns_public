package server_util;

import org.json.simple.JSONObject;

public class Message {
	
	//처리 성공
	public static String createSuccess(String msg) {
		JSONObject sendData = new JSONObject();
		sendData.put("status", "success");
		sendData.put("data", msg);
		return sendData.toJSONString();
	}
	
	//검증 에러
	public static String createInvalid(String msg) {
		JSONObject sendData = new JSONObject();
		sendData.put("status", "invalid");
		sendData.put("message", msg);
		return sendData.toJSONString();
	}
	
	//처리 실패
	public static String createFail(String msg) {
		JSONObject sendData = new JSONObject();
		sendData.put("status", "fail");
		sendData.put("message", msg);
		return sendData.toJSONString();
	}
	
}