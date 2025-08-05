package com.cnsu.Client;

import com.cnsu.common.jsonutil.MessageForm;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.EmptyBorder;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.StringTokenizer;


public class ClientView extends JFrame {
    JPanel contentPane;
    JTextField txtinput;
    JTextField txtIp;
    JTextField txtPort;
    JTextArea taChat;
    JButton btnsend;
    JButton btnConnect;
    JList lstconnect;
    String ip;
    int port;
    Socket socket;
    InputStream is;
    InputStreamReader isr;
    BufferedReader br;

    OutputStream os;
    PrintWriter pw;
    StringTokenizer token;
    String nickname;
    public ClientView()   {
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

        txtIp = new JTextField("127.0.0.1");
        txtIp.setBounds(550, 300, 100, 35);
        contentPane.add(txtIp);
        txtIp.setColumns(10);

        txtPort = new JTextField("12345");
        txtPort.setBounds(550, 350, 100, 35);
        contentPane.add(txtPort);
        txtPort.setColumns(10);

        btnConnect = new JButton("서버연결");
        btnConnect.setFont(new Font("HY견고딕", Font.BOLD, 14));
        btnConnect.setBounds(550, 395, 109, 35);
        contentPane.add(btnConnect);

        JLabel lblNewLabel = new JLabel("\uC811\uC18D\uC790:");
        lblNewLabel.setFont(new Font("HY견고딕", Font.BOLD, 14));
        lblNewLabel.setBounds(519, 10, 120, 35);
        contentPane.add(lblNewLabel);

        lstconnect = new JList();//nickname이 출력
        lstconnect.setBounds(525, 47, 205, 108);
        contentPane.add(lstconnect);
        //lstconnect.setListData(userlist);
        btnConnect.addActionListener(new ClientActionHandler(this));
        btnsend.addActionListener(new ClientActionHandler(this));

        setVisible(true);

        nickname = "hong";
    }
    public void setConnectionConfig(){
        this.ip = txtIp.getText();
        this.port = Integer.parseInt(txtPort.getText());
    }

    public void connectServer(){
        try {
            socket = new Socket(ip, port);
            if(socket!=null){
                ioWork();
            }
            sendMsg(nickname);
            //userlist.add(nickname);

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
                            appendLog("연결 성공..");
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
                                appendLog("연결 실패...");
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
    ///
    ///
    public void sendMsgTextBox(){
        String msg = txtinput.getText();
        System.out.println("클라이언트가 서버에게 메시지 전송:"+msg);
        MessageForm msgForm = new MessageForm();
        msgForm.addMessage("command", "chat");
        msgForm.addMessage("data", msg);
        pw.println(msgForm);
    }
    private void filteringMsg(String msg){
        token = new StringTokenizer(msg,"/");
        String protocol = token.nextToken();
        String message = token.nextToken();
        System.out.println("프로토콜:"+protocol+",메시지:"+message);
        if(protocol.equals("new")){
            //새로운 사용자가 접속하면 nickname리스트를 저장하는 벡터에 추가
            //userlist.add(message);
           // lstconnect.setListData(userlist);
            taChat.append("********"+message+
                    "님이 입장하셨습니다.*******\n");
        }else if(protocol.equals("old")){
            //userlist.add(message);
           // lstconnect.setListData(userlist);
        }else if(protocol.equals("chatting")){
            String nickname = token.nextToken();
            taChat.append(nickname+">>"+message+"\n");
        }else if(protocol.equals("out")){
            //userlist.remove(message);
            //lstconnect.setListData(userlist);
            taChat.append("*******"+nickname+"님이 퇴장하셨습니다.**\n");
        }

    }
    public void appendLog(String msg){
        taChat.append(msg+"\n");
    }
}
