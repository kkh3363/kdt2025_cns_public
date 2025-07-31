package crypto;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordHash {
	public static String getHashText(String data) {
		try {
			//SHA-256 단방향 해싱 알고리즘 적용
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			String salt = "salt";
			String saltedData = data + salt;
			byte[] hash = digest.digest(saltedData.getBytes("UTF-8"));
			StringBuffer byte2Hex = new StringBuffer();
			for (byte b : hash) {
				String hex = Integer.toHexString(0xff&b); //바이트 16진수 변환
				if (hex.length() == 1) 
					byte2Hex.append('0'); //16진수 1자리 표기 -> 앞에 0추가
				byte2Hex.append(hex);
			}
			return byte2Hex.toString();
		}catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}catch(UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
}
