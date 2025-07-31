package MultiRoom;

import java.util.regex.Pattern;

public class RegExp {
	
	//이름 정규식
	public static boolean checkFormName(String str) {
		String pattern = "^([가-힣]+|[a-zA-z\\.\\s]+)$";
		//한글명 또는 영문명
		return Pattern.matches(pattern, str);
	}
	
	//ID 정규식
	public static boolean checkFormID(String str) {
		String pattern = "^[\\s\\S]+$";
		//1개 이상의 모든 문자열
		return Pattern.matches(pattern, str);
	}
	
	//비밀번호 정규식
	public static boolean checkFormPWD(String str) {
		String pattern = "^[\\s\\S]+$";
		//1개 이상의 모든 문자열
		return Pattern.matches(pattern, str);
	}
	
	//이메일 정규식
	public static boolean checkFormEmail(String str) {
		String pattern = "^[\\w+-\\_.]+@[\\w-]+\\.[a-zA-Z]+$";
		//@앞 문자열 : (영어 대소문자, 숫자, +, -, _ , .) 7가지 종류 문자 지원(문자 1개 이상)
		//@뒤 문자열 ~ \\. 앞 문자열 : (영어 대소문자, 숫자, -) 4가지 종류 문자 지원(문자 1개 이상)
		// \\. 뒤 문자열 : 영어 대소문자만 가능(문자 1개 이상)
		return Pattern.matches(pattern, str);
	}
	
	//전화번호 정규식
	public static boolean checkFormPhone(String str) {
		String pattern = "^\\d{2,3}-\\d{3,4}-\\d{4}$";
		// 2~3자리 - 3~4자리 - 4자리 
		return Pattern.matches(pattern, str);
	}
	
	public static void main(String[] argv) {
		String text = "";
		if(checkFormID(text)) {
			System.out.println("일치");
		}
		else {
			System.out.println("불일치");
		}
	}
}