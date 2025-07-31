package server_util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

//삭제 시 동시성 제어가 필요할 수도 있음
public class FileManager {
	
	private static final String rootPath = new File("").getAbsolutePath()+"/";
	private static final String dbconfigPath = rootPath+"/dbconfig/";
	private static final String imagePath = rootPath+"/image/"; 
	
	//외부에서 파일 접근 경로를 제한
	public static enum PATH{ ROOT, IMAGE, DBCONFIG };
	
	private static String pathSelector(PATH path) {
		switch(path) {
			case ROOT:
				return rootPath;
			case IMAGE:
				return imagePath;
			case DBCONFIG:
				return dbconfigPath;
			default :
				return null;
		}
	}
	
	//디렉터리 생성
	public static boolean createDir(PATH path, String dirName) {
		boolean isCreated;
		String location=pathSelector(path);
		if(location==null) return false;
		try {
			Path dirPath = Paths.get(location+dirName);
			Files.createDirectory(dirPath);
			System.out.println("[서버] 디렉터리 생성 : "+dirPath.getFileName());
			isCreated=true;			
		}catch(FileAlreadyExistsException e) {
			System.out.println("[서버] 디렉터리가 이미 존재함");
			isCreated=true;
		}catch(NoSuchFileException e) {
			System.out.println("[서버] 파일을 찾지 못함");
			isCreated=false;
		}catch(IOException e) {
			System.out.println("[서버] "+e.getMessage());
			isCreated=false;
		}
		return isCreated;
	}
	
	//재귀 삭제
	private static int deleteRecursiveDir(File file) {
		int count=0; //삭제된 파일 수
		if(file.isDirectory()) {
			File[] files = file.listFiles();
			if(files != null) {
				for(File f : files) {
					count=deleteRecursiveDir(f); 
				}
			}
		}
		//현재 파일 삭제
		if(file.delete()) { //삭제 성공 케이스
			return ++count;
		}else { //현재 파일 삭제 못한 경우
			return count;
		}
	}
	
	//디렉터리 삭제
	public static boolean deleteDir(PATH path, String dirName, boolean recur) {
		boolean isDeleted;
		String location=pathSelector(path);
		if(location==null) return false;
		
		try {
			Path dirPath = Paths.get(location+dirName);
			if(Files.exists(dirPath)&&Files.isDirectory(dirPath)) {
				if(recur) {
					File directory = new File(location+dirName);
					deleteRecursiveDir(directory);
				}else {
					Files.delete(dirPath);
				}
				System.out.println("[서버] 디렉터리 삭제 : "+dirPath.getFileName());
			}else {
				System.out.println("[서버] 없거나 디렉터리가 아님");
			}
			isDeleted=true;
		}catch(IOException e) {
			System.out.println(e.getMessage()+" 내부에 파일이 존재하여 삭제할 수 없음");
			isDeleted=false;
		}
		return isDeleted;
	}
	
	//특정 디렉터리 안의 파일 이름 가져오기
	public static String[] getFileNames(PATH path, String dirName) {
		String location=pathSelector(path);
		if(location==null) return null;
		
		Path filePath = Paths.get(location+dirName);
		if(Files.exists(filePath)) {
			File file = new File(location+dirName);
			File[] files = file.listFiles();
			String[] fileNames = null;
			if(files!=null) {
				fileNames=new String[files.length];
				for(int i=0;i<files.length;++i) {
					fileNames[i] = dirName+"/"+files[i].getName();
				}
			}
			return fileNames;
		}else
			return null;
	}
	
	//파일 추가
	public static boolean writeFile(PATH path, String subPath, byte[] bytesFile) {
		boolean isWrited;
		String location=pathSelector(path);
		if(location==null) return false;
		try {
			Path filePath = Paths.get(location+subPath);
			Files.write(filePath, bytesFile);
			System.out.println("[서버] 파일 생성 : "+filePath.getFileName());
			isWrited = true;
		}catch(IOException e) {
			System.out.println("[예외] 파일 쓰기 중 오류 : "+e.getMessage());
			isWrited = false;
		}
		return isWrited;
	}
	
	//String 쓰기
	public static boolean writeFile(PATH path, String subPath, String data) {
		boolean isWrited;
		String location=pathSelector(path);
		if(location==null) return false;
		try {
			File file = new File(location+subPath);
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.write(data);
			System.out.println("[서버] 파일 생성 : "+file.getName());
			isWrited = true;
			writer.close();
		}catch(IOException e) {
			System.out.println("[예외] 파일 쓰기 중 오류 : "+e.getMessage());
			isWrited = false;
		}
		return isWrited;
	}
	
	//파일 삭제
	public static boolean deleteFile(PATH path, String subPath) {
		boolean isDeleted;
		String location=pathSelector(path);
		if(location==null) return false;
		try {
			Path filePath = Paths.get(location+subPath);
			if(Files.exists(filePath)) {
				Files.delete(filePath);
			}else { }
			isDeleted = true;
			System.out.println("[서버] 파일 삭제 : "+filePath.getFileName());
		}catch(IOException e) {
			System.out.println(e.getMessage()+" 파일을 삭제할 수 없음");
			isDeleted=false;
		}
		return isDeleted;
	}
	
	//파일 가져오기
	public static File getFile(PATH path, String subPath) {
		String location=pathSelector(path);
		if(location==null) return null;
		
		try {
			Path filePath = Paths.get(location+subPath);
			if(Files.exists(filePath)) {
				return new File(location+subPath);
			}else {
				throw new IOException("파일 없음");
			}
		}catch(IOException e) {
			System.out.println("[예외] 파일 가져오기 : "+e.getMessage());
		}
		return null;
	}
}
