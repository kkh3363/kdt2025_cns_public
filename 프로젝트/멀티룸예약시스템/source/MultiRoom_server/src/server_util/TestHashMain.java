package server_util;

public class TestHashMain {
	public static void main(String[] args) {
		String password = "sdn12312512";
		String hashPwd = PasswordHash.getHashText(password);
		if(hashPwd!=null)
			System.out.println(hashPwd);
		//패스워드는 해시값으로 변경해서 저장
		//패스워드는 찾아서 변경하는 개념이 아닌 새로 만드는 개념으로 접근
	}
}
