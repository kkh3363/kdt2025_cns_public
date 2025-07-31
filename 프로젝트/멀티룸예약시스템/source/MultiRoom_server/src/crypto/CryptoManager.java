package crypto;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

/*
  1. 서버와 클라이언트는 각각 프로그램 실행 시 RSA 암호화(공개키와 비밀키를 생성 후 보관) 수행
  2. 클라이언트가 서버에 연결 시 서버는 자신의 공개키를 먼저 클라이언트에 전송
  3. 클라이언트는 이를 확인 후 자신의 공개키를 서버에 전송
  4. 서버는 세션키를 생성 후 클라이언트의 공개키를 가지고 세션키를 암호화 전송
  5. 클라이언트는 보낸 데이터를 비밀키로 복호화 한 뒤 세션키를 저장
  6. 이후 서버와 클라이언트는 세션키로 AES 암호화 통신함
*/ 
public class CryptoManager {
	//RSA 공개키 알고리즘
	private ConcurrentHashMap<String,String> keyPair;
	public CryptoManager(){
		keyPair = new ConcurrentHashMap<String,String>();
		HashMap<String, String> temp = RSA.createKeyPair();
		String publicKey = temp.get("publicKey");
		String privateKey = temp.get("privateKey");
		keyPair.put("publicKey", publicKey);
		keyPair.put("privateKey", privateKey);
	}
	
	//공개키 반환용
	public String getPublicKey() {
		return keyPair.get("publicKey");
	}
	
	//현재 비밀키로 복호화
	public String decryptText(String encryptText) {
		return RSA.decrypt(encryptText, keyPair.get("privateKey"));
	}
}
