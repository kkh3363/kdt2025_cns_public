package com.cnsu.client;

import com.cnsu.client.controller.ClientManager;
import com.cnsu.client.view.LoginView;

public class ClientMain {
    public static void main(String[] args) {
       // ClientView clietView = new ClientView();
        ClientManager clientManager = new ClientManager();
        LoginView loginView = new LoginView(clientManager);
    }
}
