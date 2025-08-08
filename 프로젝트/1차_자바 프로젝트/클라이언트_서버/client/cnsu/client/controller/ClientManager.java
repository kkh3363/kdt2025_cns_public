package com.cnsu.client.controller;


import com.cnsu.client.view.ClientView;
import com.cnsu.client.view.LoginView;
import com.cnsu.common.adaptor.CommMessageAdaptor;
import com.cnsu.common.dto.CommMessageDto;
import com.cnsu.common.jsonutil.MessageForm;

import javax.swing.*;
import java.io.IOException;

public class ClientManager {
    ClientView clientView ;
    LoginView loginView;
    SocketManager socketManager ;
    public ClientManager() {
        socketManager = new SocketManager(this);
    }

    public void connectServer(){
        socketManager.getConnection();
    }

    public void loginServer(LoginView loginView, String strData){
        this.loginView = loginView;
        CommMessageDto msgJson  = new CommMessageDto();
        CommMessageAdaptor adaptor = new CommMessageAdaptor();
        msgJson.setCommand("member");
        msgJson.setType("login");
        msgJson.setData(strData);
        String strSend= adaptor.convertDtoToJson( msgJson);

        connectServer();
        socketManager.sendToServer( strSend );

    }
    public void mainViewStart(){
        clientView = new ClientView();
        loginView.dispose();
    }
    public void receiveMessage( String msg){
        CommMessageAdaptor jmsg = new CommMessageAdaptor();
        CommMessageDto msgDto = jmsg.convertJsonToDto( msg);
        //System.out.println("Receive :: " + msg);

        switch( msgDto.getCommand() ){
            case "member" :
                procMember( msgDto);
                break;
            default:
                System.out.println("Unknown :: "+ msg);
        }

    }
    ///
    public void procMember( CommMessageDto msgDto ){

        switch (msgDto.getType() ){
            case "login" :
                MessageForm dataForm= new MessageForm( msgDto.getData() );
                int nRet = Integer.parseInt(dataForm.getValue("response"));
                if ( nRet == 1) {
                    System.out.println("Login Success");
                    mainViewStart();
                }else {
                    System.out.println("Login fail");
                    JOptionPane.showMessageDialog(null,
                            " 로그인 실패..", "알림"
                            , JOptionPane.ERROR_MESSAGE);
                    try {
                        socketManager.stopConnect();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;

        }

    }


}
