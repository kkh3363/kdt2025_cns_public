package server_util;

public class TestSessionMain {
	//세션 제거 확인용
	public static void main(String[] args) throws Exception {
		SessionManager sm = SessionManager.getInstance(7); //n초마다 세션 만료검사 수행하는 세션 매니저 생성
		String sessionId = sm.generateSId();
		sm.addSession(sessionId, new Session(5000)); //5초가 만기인 세션 생성 후 추가
		Thread.sleep(10000); //10초 동안 메인 스레드 중단
	}
}