package crypto;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import java.util.HashMap;

public class RSA {
	//키 생성자
	public static HashMap<String, String> createKeyPair(){
		HashMap<String, String> stringKeyPair = new HashMap<String, String>();
		try {
			SecureRandom secureRandom = new SecureRandom();
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			//일반적으로 많이 사용-> (2048bit, 난수로 초기화)
			//개발단계에서는 512bit사용
			kpg.initialize(512, secureRandom);
			KeyPair keyPair = kpg.genKeyPair();
			
			//공개키(암호화, 전달용), 비밀키(복호화용) 생성
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			
			//공개키,비밀키 Base64 인코딩
			String stringPublicKey = Base64.getEncoder().encodeToString(publicKey.getEncoded());
			String stringPrivateKey = Base64.getEncoder().encodeToString(privateKey.getEncoded());
			
			stringKeyPair.put("publicKey", stringPublicKey);
			stringKeyPair.put("privateKey", stringPrivateKey);
		}catch(Exception e) {
			System.out.println("[키생성 오류] "+e.getMessage());
			return null;
		}
		return stringKeyPair;
	}
	
	//암호화(공개키)	
	public static String encrypt(String originText, String stringPublicKey) {
		String encryptedText = null;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] bytePublicKey = Base64.getDecoder().decode(stringPublicKey.getBytes());
			//X.509 : 공개키 정보를 인코딩할 때 사용하는 방식
			X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(bytePublicKey);
			//공개키 객체로 생성
			PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
			
			//만들어진 공개키로 암호화 모드 설정
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			
			byte[] encryptedBytes = cipher.doFinal(originText.getBytes());
			encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);
		}catch(Exception e) {
			System.out.println("[암호화 오류] "+e.getMessage());
			return null;
		}
		return encryptedText;
	}
	
	//복호화(비밀키)	
	public static String decrypt(String encryptedText, String stringPrivateKey) {
		String decryptedText = null;
		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			byte[] bytePrivateKey = Base64.getDecoder().decode(stringPrivateKey.getBytes());
			//PKCS#8 : 개인키 정보를 인코딩할 때 사용하는 방식
			PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(bytePrivateKey);
			//비밀키 객체 생성
			PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
			
			//개인키 객체로 복호화 모드 설정
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			
			//복호화
			byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText.getBytes());
			byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
			decryptedText = new String(decryptedBytes);
		}catch(Exception e) {
			System.out.println("[복호화 오류] "+e.getMessage());
			return null;
		}
		return decryptedText;
	}
}
