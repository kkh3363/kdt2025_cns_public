package server_util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import crypto.PasswordHash;

public class SessionManager {

	//ConcurrentHashMap -> 멀티쓰레드 환경 적용
	private static ConcurrentHashMap<String, Session> sessions = null;
	private static SessionManager current = null;
	
	private SessionManager() {
		super();
	}
	
	//생성 시 세션 만료 검사를 몇 초마다 수행할지 지정함 
	public static SessionManager getInstance(int sec) {
		if(current == null) {
			current=new SessionManager();
			sessions=new ConcurrentHashMap<String, Session>();
			
			//세션 만료 검사 이벤트 쓰레드 수행
			startRemoverEvent((long)sec*1000); 
		}
		return current;
	}
	
	//현재 시간으로 단방향 해싱 -> SessionId 생성
	public String generateSId() {
		long time=System.currentTimeMillis();
		String now=String.valueOf(time) + "kdt2024";
		return PasswordHash.getHashText(now);
	}
	
	//세션 추가
	public void addSession(String sessionId, Session session) {
		try {
			if(sessions.containsKey(sessionId))
				throw new Exception("[에러] 세션 키가 이미 존재함");
			else {
				sessions.put(sessionId, session);
			}
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	//세션 가져오기
	public Session getSession(String sessionId) {
		Session session = sessions.get(sessionId);
		if(session.isExpired()) {
			removeSession(sessionId);
			return null;
		}
		else
			return session;
	}
	
	//세션 제거
	public void removeSession(String sessionId) {
		sessions.remove(sessionId);
	}
	
	//iterator로 순회하면서 만료된 세션 제거
	private static void removeExpirySession() {
		Iterator<Map.Entry<String,Session>> iterator = sessions.entrySet().iterator();
		while(iterator.hasNext()) {
			Map.Entry<String, Session> entry = iterator.next();
			Session session = entry.getValue();
			if(session.isExpired()) { 
				iterator.remove(); 
				System.out.println("세션이 제거됨");
			}
		}
	}
	
	//데몬 쓰레드로 동작 -> 특정 주기마다 만료된 세션을 찾아 제거
	private static void startRemoverEvent(long interval) {
		Thread thread = new Thread(()->{
			while(true) {
				try {
					Thread.sleep(interval);
					removeExpirySession();
				}catch(InterruptedException e) {
					Thread.currentThread().interrupt();
					System.out.println(e.getMessage());
					break;
				}
			}
		});
		thread.setDaemon(true); //서버 종료 시 자동 종료
		thread.start();
	}
}