package mychat3.server;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class ClientChatView extends JFrame {
	 JPanel contentPane;
	 JTextField txtinput;
	 JTextArea taChat;
	 JButton btnsend;
	 JList lstconnect;
	 Socket socket;
	 String ip;
	 int port;
	 String nickname;
	 InputStream is;
	 InputStreamReader isr;
	 BufferedReader br;
	 
	 OutputStream os;
	 PrintWriter pw;
	 Vector<String> userlist = new Vector<String>();
	 StringTokenizer token;
	
	public ClientChatView(String ip,int port,String nickname) {
		this.ip = ip;
		this.port = port;
		this.nickname = nickname;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 758, 478);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		taChat = new JTextArea();
		
		JScrollPane scroll = new JScrollPane(taChat);
		scroll.setBounds(12, 10, 501, 375);
		contentPane.add(scroll);
		
		txtinput = new JTextField();
		txtinput.setBounds(12, 395, 378, 35);
		contentPane.add(txtinput);
		txtinput.setColumns(10);
		
		btnsend = new JButton("\uC11C\uBC84\uB85C\uC804\uC1A1");
		btnsend.setFont(new Font("HY견고딕", Font.BOLD, 14));
		btnsend.setBounds(402, 395, 109, 35);
		contentPane.add(btnsend);
		
		JLabel lblNewLabel = new JLabel("\uC811\uC18D\uC790:");
		lblNewLabel.setFont(new Font("HY견고딕", Font.BOLD, 14));
		lblNewLabel.setBounds(519, 10, 120, 35);
		contentPane.add(lblNewLabel);
		
		lstconnect = new JList();//nickname이 출력
		lstconnect.setBounds(525, 47, 205, 108);
		contentPane.add(lstconnect);
		lstconnect.setListData(userlist);
		setVisible(true);//화면에 JFrame이 보여지기 위해 필요
		connectServer();
		startEvent();
	}
	public void startEvent(){
		ClientChatListener listener = new ClientChatListener(this);
		txtinput.addActionListener(listener);
		btnsend.addActionListener(listener);
	}

	public void connectServer(){
		try {
			socket = new Socket(ip, port);
			if(socket!=null){
				ioWork();
			}
			sendMsg(nickname);
			userlist.add(nickname);

			Thread t1 = new Thread(new Runnable() {
				@Override
				public void run() {
					while(true){
						String msg;
						try {
							msg = br.readLine();
							System.out.println("서버로 부터 수신된 메시지>>"
							+msg);
							filteringMsg(msg);
						} catch (IOException e) {
							//1.=====서버쪽에서 연결이 끊어지는 경우
							//먼저 사용한 자원을 반납한다.========
							try {
								is.close();
								isr.close();
								br.close();
								os.close();
								pw.close();
								socket.close();
								JOptionPane.showMessageDialog(null,
										"서버와 접속이 끊어짐",
										"알림",
										JOptionPane.ERROR_MESSAGE);
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							break;
						}
						
					}
				}
			});
			t1.start();

			//taChat.append(msg);
	
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void ioWork(){
		try {
			is = socket.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			
			os = socket.getOutputStream();
			pw = new PrintWriter(os,true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void sendMsg(String msg){
		System.out.println("클라이언트가 서버에게 메시지 전송:"+msg);
		pw.println(msg);
	}
	
	private void filteringMsg(String msg){
		token = new StringTokenizer(msg,"/");
		String protocol = token.nextToken();
		String message = token.nextToken();
		System.out.println("프로토콜:"+protocol+",메시지:"+message);
		if(protocol.equals("new")){
			//새로운 사용자가 접속하면 nickname리스트를 저장하는 벡터에 추가
			userlist.add(message);
			lstconnect.setListData(userlist);
			taChat.append("********"+message+
									"님이 입장하셨습니다.*******\n");
		}else if(protocol.equals("old")){
			userlist.add(message);
			lstconnect.setListData(userlist);
		}else if(protocol.equals("chatting")){
			String nickname = token.nextToken();
			taChat.append(nickname+">>"+message+"\n");
		}else if(protocol.equals("out")){
			userlist.remove(message);
			lstconnect.setListData(userlist);
			taChat.append("*******"+nickname+"님이 퇴장하셨습니다.**\n");
		}

	}

}
