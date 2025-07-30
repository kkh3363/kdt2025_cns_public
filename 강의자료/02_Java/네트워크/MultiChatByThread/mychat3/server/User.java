package mychat3.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JOptionPane;

public class User extends Thread {
	Socket client;
	InputStream is;
	InputStreamReader isr;
	BufferedReader br;

	OutputStream os;
	PrintWriter pw;
	String nickname;
	ChatServerView serverView;
	Vector<User> userlist = new Vector<User>();
	StringTokenizer token;
	public User(Socket client, ChatServerView serverView,
						Vector<User> userlist) {
		super();
		this.client = client;
		this.serverView = serverView;
		this.userlist = userlist;
		ioWork();
	}

	public void ioWork(){
		try {
			is = client.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			
			os = client.getOutputStream();
			pw = new PrintWriter(os,true);

			nickname = br.readLine();
			serverView.taclientlist
			.append("***********"+nickname+
					"님이 입장하셨습니다.*******\n");
			
			broadCast("new/"+nickname);//기존사용자에게 메시지 보내기
			int size  = userlist.size();
			for (int i = 0; i <size; i++) {
				User user = userlist.get(i);
				sendMsg("old/"+user.nickname);
			}
			userlist.add(this);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void run(){
		while(true){
			try {
				String msg = br.readLine();
				serverView.taclientlist.append(nickname+
						"로 부터 전송된 메시지:"+msg+"\n");
				filteringMsg(msg);
			} catch (IOException e) {
				//2.====메시지를 계속 읽어주는데 클라이언트와 접속이 끊어지는경우
				//  벡터에서 사용자 빼고 모든 클라이언트에게 알려준다.=======
				JOptionPane.showMessageDialog(null,
							"사용자와 접속이 끊어짐","알림"
							,JOptionPane.ERROR_MESSAGE);
				try {
					is.close();
					isr.close();
					br.close();
					os.close();
					pw.close();
					client.close();
					JOptionPane.showMessageDialog(null,
							"서버와 접속이 끊어짐",
							"알림",
							JOptionPane.ERROR_MESSAGE);
					userlist.remove(this);
					broadCast("out/"+nickname);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				break;
			}
			
		}
	}

	private void filteringMsg(String msg){
		System.out.println("서버가 받은 클라이언트의 메시지:"+msg);
		token = new StringTokenizer(msg, "/");
		String protocol = token.nextToken();
		if(protocol.equals("chatting")){
			String message = token.nextToken();
			String nickname = token.nextToken();
			broadCast("chatting/"+message+"/"+nickname);
		}
	}

	private void sendMsg(String msg){
		pw.println(msg);
	}

	private void broadCast(String msg){
		int size = userlist.size();
		for (int i = 0; i <size; i++) {
			User user = userlist.get(i);
			user.sendMsg(msg);
		}
	}

}



