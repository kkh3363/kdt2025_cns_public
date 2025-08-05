package com.cnsu.server.controller;

import com.cnsu.common.jsonutil.MessageForm;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClientActor extends  Thread {
    Socket client;
    InputStream is;
    InputStreamReader isr;
    BufferedReader br;

    OutputStream os;
    PrintWriter pw;
    String nickname;
    ServerController svrControl;
    Vector<ClientActor> clientList = new Vector<ClientActor>();


    public ClientActor(Socket client,ServerController svrControl
                       ,Vector<ClientActor> clientList) {
        super();
        this.client = client;
        this.svrControl = svrControl;//ServerController.getInstance();
        this.clientList = clientList;
        ioWork();
    }

    public void ioWork() {
        try {
            is = client.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            os = client.getOutputStream();
            pw = new PrintWriter(os, true);

            nickname = br.readLine();
            svrControl.serverView.appendTextLog("***********" + nickname +
                    "님이 입장하셨습니다.*******\n");

            clientList.add(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){

        while(true){
            try {

                String msg = br.readLine();
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
                    clientList.remove(this);
                //    broadCast("out/"+nickname);
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                break;
            }

        }
    }
    private void filteringMsg(String msg){
        MessageForm clientForm = new MessageForm(msg);
        StringBuilder msgData=new StringBuilder();
        switch ( clientForm.getValue("command")){
            case "chat":
                msgData.append("chat :: ");
                break;
            case "event":
                msgData.append("event :: ");
                break;
        }
        msgData.append(clientForm.getValue("data"));
        svrControl.serverView.appendTextLog(msgData.toString()+"\n");

    }
    private void sendMsg(String msg){
        pw.println(msg);
    }
}
