package com.cnsu.server.controller;

import java.net.SocketException;
import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;

public class SessionManager extends Thread{
    Vector<ClientActor> clientActors;

    public SessionManager(Vector<ClientActor> clientActors ) {
        this.clientActors = clientActors;
    }

    @Override
    public void run() {
        while ( true){
            try {
                sleep(5000);
                System.out.println("Session :: check start ------- "+ clientActors.size());
                while ( checkRunning() );

            } catch (InterruptedException e){
                //e.printStackTrace();
                System.out.println(e.getMessage());
                break;
            }catch (Exception e) {
                //e.printStackTrace();
                System.out.println(e.getMessage());
                break;
            }
        }
        System.out.println("SessionManager stopped");
    }
    private boolean checkRunning() throws SocketException {
        for (ClientActor client : clientActors ){

            if ( client.client == null ){
                System.out.println("Session :: client remove from list ");
                clientActors.remove(client);
                return true;
            }
            else {
                System.out.println("keep : " + client.client.getKeepAlive());
                if (!client.client.isConnected()) {
                    System.out.println("Session :: Disconnect ");
                    clientActors.remove(client);
                    return true;
                }
            }
        }
        return false;
    }
}
