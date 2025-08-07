package com.cnsu.server.controller;

import com.cnsu.server.view.ServerMainView;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerManager {
    int portNumber;
    ServerSocket serverSocket;
    Socket client;
    Vector<ClientActor> clientList = new Vector<ClientActor>();
    Object lock = new Object();
    private final ExecutorService threadPool;
    private final Queue<ClientActor> clientSockets;
    private int threadCount = 2;
    ServerMainView serverView ;

    public ServerManager( ) {
        this.threadPool = Executors.newFixedThreadPool(threadCount);
        this.portNumber = 12345;
        this.clientSockets = new ConcurrentLinkedQueue<>();
        serverView = new ServerMainView(this);

    }
    public void setPortNumber(int portNumber){
        this.portNumber =portNumber;
    }

    public void startServer() {
        try {
            if (portNumber < 1)
                portNumber = 12345;
            serverSocket = new ServerSocket(portNumber);
        }catch(UnknownHostException e) {
            System.out.println("잘못된 IP주소");
        }catch(IllegalArgumentException e) {
            System.out.println("유효하지 않은 포트 번호");
        }catch(BindException e) {
            System.out.println("사용 중인 포트 번호거나 비활성IP 주소");
        }catch(IOException e) {
            System.out.println("well-known 포트 사용 시도(권한 필요)");
        }
        Thread thread=new Thread(()->{
            try {
                while(true) {
                    Socket client = serverSocket.accept(); //통신 소켓 연결 대기
                    ClientActor clientActor=new ClientActor(client,   this); //통신 소켓과 서버 연결
                    threadPool.execute(clientActor);
                    //clientActor.start();
                    clientSockets.add(clientActor);
                }
            }catch(IOException e) {
                System.out.println("[socket] "+e.getMessage());
            }catch(Exception e) {
                System.out.println("[server] "+e.getMessage());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
    public void stopServer() {
        threadPool.shutdownNow();
        try {
            if (!serverSocket.isClosed())
                serverSocket.close();
            //연결된 소켓 모두 닫기
            clientSockets.stream().forEach(client -> {
                client.close();
            });
        } catch(Exception e){
            System.out.println("Server Down : "+ e.getMessage());
        }

        System.out.println("[server] Server shutdown");
    }
}
