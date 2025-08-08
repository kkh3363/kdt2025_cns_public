package com.cnsu.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class ClientActionHandler implements ActionListener {
    ClientView clientView;

    public ClientActionHandler(ClientView clientView) {
        this.clientView = clientView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
            if ( e.getSource() == clientView.btnsend){
                clientView.sendMsgTextBox();
            }else if (e.getSource() == clientView.btnConnect) {
                clientView.setConnectionConfig();
                clientView.connectServer();
            }else if (e.getSource() == clientView.btnDisConnect) {
                try {
                    clientView.disConnect();
                } catch (IOException ignored) {

                }
            }
    }
}
