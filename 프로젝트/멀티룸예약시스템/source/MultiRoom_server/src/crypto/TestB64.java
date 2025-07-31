package crypto;

import java.io.File;
import java.io.FileInputStream;

import server_util.*;
import server_util.FileManager.PATH;

public class TestB64 {
	public static void main(String[] args) {
		//이미지를 가져옵니다.
		File file=FileManager.getFile(PATH.IMAGE, "/room/54.png");
		
		try{
			FileInputStream fis = new FileInputStream(file);
			byte[] fileData = new byte[(int)file.length()];	
			fis.read(fileData);
			String data=B64.convertHexString(fileData);
			System.out.println("인코딩 전 이진 데이터 확인 : "+data);	
			fis.close();
		}catch(Exception e) {}

		//Base64로 인코딩 합니다.
		String encodedFile = B64.encoder(file);
		System.out.println("인코딩 된 데이터 : "+encodedFile);
		
		//Base64로 디코딩 합니다.
		byte[] decodedFile = B64.decoder(encodedFile);
		String data=B64.convertHexString(decodedFile);
		System.out.println("디코딩 후 데이터 차이 확인 : "+data);
	}
}
