package com.cnsu.Client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
            }
    }
}
