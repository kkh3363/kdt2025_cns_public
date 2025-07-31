package crypto;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;

//Base64 인코더/디코더 파일 전송용
public class B64 {
	
	//BASE64 인코딩
	public static String encoder(File file) {
        try{
        	FileInputStream fis = new FileInputStream(file);
        	//byte 변환
        	byte[] fileData = new byte[(int)file.length()];
            fis.read(fileData);
            //Base64 인코딩
            String encodedFile = Base64.getEncoder().encodeToString(fileData);
            fis.close();
            return encodedFile;
        } catch (Exception e) {
    		return null;
        }
	}
	
	//BASE64 디코딩
	public static byte[] decoder(String encodedFile) {
		return Base64.getDecoder().decode(encodedFile);
	}
	
	//변환 테스트용
	public static String convertHexString(byte[] bytes) {
		StringBuffer sb = new StringBuffer();
		for(byte b : bytes)
			sb.append(String.format("%02x", b));
		return sb.toString();
	}
	
}
