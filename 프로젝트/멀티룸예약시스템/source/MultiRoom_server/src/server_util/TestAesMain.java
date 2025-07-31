package server_util;

public class TestAesMain {
	public static void main(String[] args) {
		String text = "평문.....test";
		String iv = AES.generateIV();
		String encrypt=AES.getEncrypt(iv, text);
		if(encrypt != null)
			System.out.println(encrypt);
		String decrypt = AES.getDecrypt(iv, encrypt);
		if(decrypt != null)
			System.out.println(decrypt);
	}
}
