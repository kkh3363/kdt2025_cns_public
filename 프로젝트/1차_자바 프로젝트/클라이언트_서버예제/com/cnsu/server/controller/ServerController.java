package com.cnsu.server.controller;

import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import com.cnsu.server.view.ServerMainView;

public class ServerController {
    //private static ServerController serverController;
    ServerMainView serverView ;
    int portNumber;
    ServerSocket server;
    Socket client;
    Vector<ClientActor> clientList = new Vector<ClientActor>();
    Object lock = new Object();

//    public static ServerController getInstance(){
//        if ( serverController == null){
//            serverController = new ServerController();
//        }
//            return serverController;
//    }
    public ServerController(){
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    serverView = new ServerMainView(ServerController.this);
                    //frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public void setPortNumber(int portNumber){
        this.portNumber =portNumber;
    }
    public void startServer(){
        try {
            if ( portNumber < 1 )
                portNumber = 12345;
            server = new ServerSocket(portNumber);

            if(server!=null){
                connection();
            }
           // serverView.appendTextLog("사용자 접속 대기중\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stopServer(){
        if ( server != null){
            try {
                synchronized (lock) {
                    server.close();
                }
            }catch (IOException e) {
                e.printStackTrace();
                System.out.println("[서버소켓통신에러]");
            }

        }
    }

    /**
     *
     */
    public void connection(){
        Thread th = new Thread(new Runnable(){
            @Override
            public void run() {
                while(true){
                    try {
                        client = server.accept();
                        synchronized (lock) {
                            String ip = client.getInetAddress().getHostAddress();
                            //serverView.appendTextLog(ip + "사용자접속!!\n");
                            System.out.println(ip + "사용자접속!!");
                            ClientActor clientActor = new ClientActor(client,  ServerController.this, clientList);
                            clientActor.start();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }
        });
        th.start();
    }
}
