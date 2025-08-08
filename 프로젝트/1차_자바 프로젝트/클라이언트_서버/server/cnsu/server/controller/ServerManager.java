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
import java.util.concurrent.ThreadPoolExecutor;

public class ServerManager {
    final int DefaultPort = 12345;
    int portNumber;
    ServerSocket serverSocket;
    Socket client;
    //Vector<ClientActor> clientList = new Vector<ClientActor>();
    Object lock = new Object();
    //private final ExecutorService threadPool;

    private  ThreadPoolExecutor threadPool;
    private  Queue<ClientActor> clientActorQueue;
    private Vector<ClientActor> clientActors;
    private int threadCount = 10;
    ServerMainView serverView ;
    SessionManager sessionManager;
    public ServerManager( ) {
        this.portNumber = DefaultPort;
        this.clientActors = new Vector<>();
        serverView = new ServerMainView(this);
    }
    public void setPortNumber(int portNumber){
        this.portNumber =portNumber;
    }

    public void startServer() {
        if ( threadPool!=null && !threadPool.isShutdown()) {
            serverView.appendTextLog("!! 서버가 실행 중입니다.");
            return;
        }
        serverView.appendTextLog("서버가 시작됩니다.");
        this.threadPool = (ThreadPoolExecutor)Executors.newFixedThreadPool(threadCount);
        this.clientActorQueue = new ConcurrentLinkedQueue<>();
        try {
            if (portNumber < 1)
                portNumber = DefaultPort;
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
        if ( sessionManager == null ) {
            sessionManager = new SessionManager(clientActors);
            sessionManager.start();
        }

        Thread thread=new Thread(()->{
            try {
                while(true) {
                    Socket client = serverSocket.accept(); //통신 소켓 연결 대기
                    ClientActor clientActor=new ClientActor(client,   this); //통신 소켓과 서버 연결
                    threadPool.execute(clientActor);
                    clientActor.start();
                    clientActorQueue.add(clientActor);
                    clientActors.add( clientActor );
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
        try {
            if (!serverSocket.isClosed())
                serverSocket.close();
            //연결된 소켓 모두 닫기
            clientActorQueue.stream().forEach(client -> {
                try {
                    client.closeClient();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            });
            clientActorQueue.clear();
            threadPool.shutdownNow();
            if ( sessionManager != null ) {
                sessionManager.interrupt();
                sessionManager = null;
            }
            clientActors.clear();
        } catch(Exception e){
            System.out.println("Server Down : "+ e.getMessage());
        }

        System.out.println("[server] Server shutdown");
    }
    public void printClientQueue(){
        System.out.println("ThreadPool :: " + threadPool.getActiveCount());
        System.out.println("-----Queue----");
        for(ClientActor client: clientActors){
            System.out.println(client.toString());
        }

    }
    public void removeClietnActor( ClientActor clientActor){

    }
}
