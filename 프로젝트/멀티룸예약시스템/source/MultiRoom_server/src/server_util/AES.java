package server_util;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

//암호화 통신을 위한 AES-256 양방향 암호화 알고리즘 사용
public class AES {
	
	//AES 암호화, CBC(블록 암호화 모드), PKCS5 부족한 바이트만큼 패딩
	private final static String algorithms = "AES/CBC/PKCS5Padding";
	
	//초기 비밀키
	private final static String privateKey = "xxxxxxxxxxxxkdtprojectxxxxxxxxxx"; //32byte
	
	//암호화에 사용되는 키
	private static String key = privateKey;
	
	//키 교체
	public static void changeKey(String key) {
		AES.key = key;
	}
	
	//초기화 벡터 IV : 암호화와 복호화 시의 초기화 벡터는 같아야 함.
	//서버 <-> 클라이언트로 전송 시에 초기화 벡터를 앞에 추가해서 보낸다.
	//ex) IV+암호화된 데이터(JSONString) -> 앞의 16개의 문자를 잘라서 초기화벡터로 사용 
	//IV를 사용해서 데이터를 복호화하고 데이터를 파싱한다. 
	
	//초기화 벡터 생성 - 16byte
	public static String generateIV() {
		String randomValue = String.valueOf(Math.random());
		String iv = PasswordHash.getHashText(randomValue);
		return iv.substring(0, 16);
	}
	
	//암호화
	public static String getEncrypt(String iv,String data) {
		try {
			String result;
			Cipher cipher = Cipher.getInstance(algorithms); //암/복호화 객체
			
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
			
			//초기화 벡터로 spec 생성
			IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes()); 
			
			//암호화 알고리즘 적용
			cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivParamSpec);
			
			//평문 암호화 수행 및 반환 (Base64로 인코딩/디코딩 수행 -> 24byte 암호문 생성)
			byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
			result = Base64.getEncoder().encodeToString(encrypted);
			return result;
		}catch(Exception e) {
			System.out.println("암호화 오류");
			e.printStackTrace();
		}
		return null;
	}
	
	//복호화
	public static String getDecrypt(String iv,String data) {
		try {
			String result;
			Cipher cipher = Cipher.getInstance(algorithms);
			SecretKeySpec keySpec = new SecretKeySpec(key.getBytes(), "AES");
			IvParameterSpec ivParamSpec = new IvParameterSpec(iv.getBytes()); 
			
			//복호화 적용
			cipher.init(Cipher.DECRYPT_MODE,keySpec,ivParamSpec);
			
			//암호문 복호화 수행 및 반환
			byte[] decodedBytes = Base64.getDecoder().decode(data);
			byte[] decrypted = cipher.doFinal(decodedBytes);
			result = new String(decrypted, StandardCharsets.UTF_8);
			return result;
		}catch(Exception e) {
			System.out.println("복호화 오류");
			e.printStackTrace();
		}
		return null;
	}
}
