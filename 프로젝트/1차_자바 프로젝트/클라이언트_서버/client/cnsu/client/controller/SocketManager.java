package com.cnsu.client.controller;

import com.cnsu.common.dto.CommMessageDto;
import com.cnsu.common.jsonutil.MessageForm;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketManager {
    Socket clientSocket;
    String serverIp;
    int port;
    ClientManager clientManager;
    InputStream is;
    InputStreamReader isr;
    BufferedReader br;
    OutputStream os;
    PrintWriter pw;

    public SocketManager(ClientManager clientManager) {
        this.clientManager = clientManager;
        serverIp = "127.0.0.1";
        port = 12345;

    }
    public void getConnection(){
        if (clientSocket != null)
        {
            if (clientSocket.isConnected())
                return;
        }
        connectServer();
    }

    public void connectServer(){
        try {
            if ( clientSocket == null ) {
                clientSocket = new Socket(serverIp, port);
                clientSocket.setKeepAlive(true);
            }
            if(clientSocket != null){
                ioWork();
            }
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true){
                        String msg;
                        try {
                            msg = br.readLine();
                            clientManager.receiveMessage(msg);
                            //filterMessage(msg);
                            //System.out.println("서버로 부터 수신된 메시지>>"+msg);
                            // filteringMsg(msg);
                            // Client Manager한테 보내라....
                        } catch (IOException e) {
                            //1.=====서버쪽에서 연결이 끊어지는 경우
                            //먼저 사용한 자원을 반납한다.========
                            try {
//                                is.close();
//                                isr.close();
//                                br.close();
//                                os.close();
//                                pw.close();
//                                clientSocket.close();
                                stopConnect();
                                System.out.println("서버와 접속이 끊어짐");
//                                JOptionPane.showMessageDialog(null,
//                                        "서버와 접속이 끊어짐",
//                                        "알림",
//                                        JOptionPane.ERROR_MESSAGE);
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

        } catch (
                UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void ioWork(){
        try {
            is = clientSocket.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            os = clientSocket.getOutputStream();
            pw = new PrintWriter(os,true);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void sendToServer(String msg){
        if ( !clientSocket.isConnected() ) {
            System.out.println("Socket is closed");
            return;
        }
        pw.println(msg);
    }

    /**
     *
     * @param msg
     */
    public void stopConnect() throws IOException{
        if ( clientSocket == null )
            return;
        MessageForm msgForm = new MessageForm();
        msgForm.addMessage("command", "conn");
        msgForm.addMessage("data", "disconnect");
        sendToServer(msgForm.toString());

        System.out.println("stop socket");
        is.close();
        isr.close();
        br.close();
        os.close();
        pw.close();
        clientSocket.close();
        clientSocket = null;
    }
}
