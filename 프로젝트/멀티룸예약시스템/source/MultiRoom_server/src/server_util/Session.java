package server_util;

import java.util.HashMap;

//세션
public class Session {
	
	private HashMap<String, Object> data;
	private long creationTime; //세션 생성 시간
	private long expiryTime; //세션 만료 시간
	
	//세션 기본 설정
	public Session() {
		data = new HashMap<String, Object>();
		creationTime = System.currentTimeMillis();
		expiryTime = 1000*60*60; //기본 1시간
	}
	
	//만료 간격 설정(밀리초단위)
	public Session(long expiryTime) {
		data = new HashMap<String, Object>();
		this.creationTime = System.currentTimeMillis();
		this.expiryTime = expiryTime; 
	}
	
	//세션 만료 여부
	public boolean isExpired() {
		return (System.currentTimeMillis()-creationTime)>expiryTime?true:false;
	}
	
	//세션 시간 갱신
	public void renewSession(long creationTime) {
		this.creationTime = creationTime;
	}
	
	//세션 데이터 추가
	public void setAttribute(String key, Object value) {
		try {
			if(isExpired())
				throw new Exception("[에러] 세션이 만료됨");
			else if(data.containsKey(key))
				throw new Exception("[에러] 키가 중복됨");
			else
				data.put(key, value);
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	//세션에서 데이터 가져오기
	public Object getAttribute(String key) throws Exception {
		if(isExpired())
			throw new Exception("[에러] 세션이 만료됨");
		return data.get(key);
	}
	
	//세션 데이터 삭제
	public void removeAttribute(String key) {
		try {
			if(isExpired())
				throw new Exception("[에러] 세션이 만료됨");
			else
				data.remove(key);
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
}