package server_manager;

import java.io.IOException;
import java.util.Scanner;

public class Main {
	public static void main(String[] args) {
		final String ipAddress;
		final int portNumber;
		//기본 데이터
		final int backlog = 100; 
		final int threadCnt = 100;
		
		try {
			MultiroomServer server = null;
			switch(args.length) {
				case 1:
					portNumber = Integer.parseInt(args[0]);
					server = new MultiroomServer(portNumber, backlog, threadCnt);
					break;
				case 2:
					ipAddress = args[0].trim();
					portNumber = Integer.parseInt(args[1]);
					server = new MultiroomServer(ipAddress, portNumber, backlog, threadCnt);
					break;
				default:
					throw new Exception("명령 인자 수 불일치");
			}
			server.start();
			System.out.println("서버를 종료하려면 q를 입력");
			Scanner sc = new Scanner(System.in);
			while(true) {
				String key=sc.nextLine();
				if(key.equals("q"))
					break;
			}
			sc.close();
			server.stop();
		}catch(Exception e) {
			System.out.println("[에러] 서버가 중단됨 : "+e.getMessage());
		}
	}
}
