package com.cnsu.server;

import com.cnsu.server.controller.ServerController;

import java.awt.*;

public class ServerMain {
    static ServerController svrControl ;
    public static void main(String[] args) {
         svrControl = new ServerController();//.getInstance();

    }
}
