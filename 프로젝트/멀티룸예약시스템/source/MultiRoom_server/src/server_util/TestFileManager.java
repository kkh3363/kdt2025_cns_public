package server_util;

import java.io.File;

import server_util.FileManager.PATH;

public class TestFileManager {

	public static void main(String[] args) {
		
		if(FileManager.createDir(PATH.IMAGE, "/room2")) {
			//디렉터리 생성 시 수행할 내용..
		}
		
		if(FileManager.deleteDir(PATH.IMAGE, "/room3", true)) {
			//디렉터리 삭제 시 수행할 내용...
		}
		
		//디렉터리 내부 파일 주소(이름포함) 가져오기 -> DB에 저장할 때 활용 가능
		String[] fileNames = FileManager.getFileNames(PATH.IMAGE, "/room2");
		if(fileNames!=null) {
			for(String name : fileNames) {
				System.out.print(name +" ");
			}
			System.out.println();
		}
		
		//파일 처리 예제
		byte[] bytes = new String("test123124").getBytes();
		if(FileManager.writeFile(PATH.IMAGE, "/room/54.png", bytes)) {
			//파일 생성 시 수행할 내용
		}
		
		File file=FileManager.getFile(PATH.IMAGE, "/room/54.png");
		if(file!=null) {
			System.out.println("저장된 파일 : "+file.getName());
		}
		
	
	}

}