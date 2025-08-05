package com.cnsu.server.listener;

import com.cnsu.server.controller.ServerController;
import com.cnsu.server.view.ServerMainView;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ServerActionHandler implements ActionListener {
    ServerMainView view;
    ServerController svrControl;
    public ServerActionHandler(ServerMainView view, ServerController svrControl) {
        this.view = view;
        this.svrControl = svrControl;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==view.btnstartServer){
            view.appendTextLog("서버를 시작합니다\n");
            svrControl.startServer();
        }else if(e.getSource()==view.btnstop){
            view.appendTextLog("서버를 중지합니다\n");
            svrControl.stopServer();
        }else if ( e.getSource() == view.btnchangeport){
            String port = JOptionPane.showInputDialog(view,
                    "port를 입력하세요",JOptionPane.INFORMATION_MESSAGE);
            svrControl.setPortNumber(Integer.parseInt(port));
        }

    }
}
