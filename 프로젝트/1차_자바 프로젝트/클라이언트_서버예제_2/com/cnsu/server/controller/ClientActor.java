package com.cnsu.server.controller;

import com.cnsu.common.jsonutil.MessageForm;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.Vector;

public class ClientActor   extends Thread{
    Socket client;
    InputStream is;
    InputStreamReader isr;
    BufferedReader br;

    OutputStream os;
    PrintWriter pw;
    String nickname;
    ServerManager serverManager;


    public ClientActor(){}
    public ClientActor(Socket client,ServerManager serverManager
                       ) {
        super();
        this.client = client;
        this.serverManager = serverManager;

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
            serverManager.serverView.appendTextLog("***********" + nickname +
                    "님이 입장하셨습니다.*******\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void run(){

        while(true){
            try {

                String msg = br.readLine();
                actionCommand(msg);
            } catch (IOException e) {

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

                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                break;
            }

        }
    }
    private void actionCommand(String msg){
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
        serverManager.serverView.appendTextLog(msgData.toString()+"\n");

    }
    private void sendMsg(String msg){
        pw.println(msg);
    }

    //소켓 연결 닫기
    public void close() {
        try {

            if(client!=null || !client.isClosed())
                client.close();
        }catch(Exception e) {

        }
        System.out.println("Connection Close");
    }
}
