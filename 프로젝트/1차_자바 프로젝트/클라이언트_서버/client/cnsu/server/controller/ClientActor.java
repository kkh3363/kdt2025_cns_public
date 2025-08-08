package com.cnsu.server.controller;

import com.cnsu.common.adaptor.CommMessageAdaptor;
import com.cnsu.common.dto.CommMessageDto;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

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
    public ClientActor(Socket client,ServerManager serverManager) {
        super();
        this.client = client;
        this.serverManager = serverManager;
        serverManager.addClientToQueue(this);
        ioWork();
    }

    public void ioWork() {
        try {
            is = client.getInputStream();
            isr = new InputStreamReader(is);
            br = new BufferedReader(isr);

            os = client.getOutputStream();
            pw = new PrintWriter(os, true);

            //nickname = br.readLine();
            //serverManager.serverView.appendTextLog("***********" + nickname +
            //        "님이 입장하셨습니다.*******\n");
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
                System.out.println("ActionCommand Error...");
                //JOptionPane.showMessageDialog(null,
                 //       "사용자와 접속이 끊어짐","알림"
                 //       ,JOptionPane.ERROR_MESSAGE);
                try {
                    is.close();
                    isr.close();
                    br.close();
                    os.close();
                    pw.close();
                    client.close();
                    //JOptionPane.showMessageDialog(null,
                    //        "서버와 접속이 끊어짐",
                   //         "알림",
                    //        JOptionPane.ERROR_MESSAGE);
                    System.out.println("Close Io Error...");
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
                break;
            }

        }
    }
    private void actionCommand(String msg){
        CommMessageAdaptor jsonAdaptor = new CommMessageAdaptor(msg);
        CommMessageDto msgDto = jsonAdaptor.getMsgDto();

        StringBuilder msgData=new StringBuilder();
        switch ( msgDto.getCommand() ){
            case "member":
                msgData.append("member :: ");
                LoginManager loginManager = new LoginManager(this, msgDto.getData());
                loginManager.loginCheck();
                serverManager.serverView.appendTextLog((loginManager.toString() + "\n"));
                break;
            case "event":
                msgData.append("event :: ");
                break;
            default:
                msgData.append("other :: ");
        }
        msgData.append(msgDto.getData());
        serverManager.serverView.appendTextLog(msgData.toString()+"\n");

    }
    public void sendMessageToServer(String msg){
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
