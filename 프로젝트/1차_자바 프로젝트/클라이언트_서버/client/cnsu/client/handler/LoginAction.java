package com.cnsu.client.handler;

import com.cnsu.client.controller.ClientManager;
import com.cnsu.client.view.LoginView;
import com.cnsu.common.adaptor.JsonLoginMessageAdaptor;
import com.cnsu.common.dto.CommMessageDto;
import com.cnsu.common.dto.LoginMessageDto;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginAction implements ActionListener {
    LoginView loginView;

    public LoginAction( LoginView loginView) {
        this.loginView = loginView;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==loginView.loginButton){


            JsonLoginMessageAdaptor adaptor = new JsonLoginMessageAdaptor();
            LoginMessageDto dto =  loginView.getLoginMessageDto();
            String strMsg = adaptor.convertDtoToJson(dto);

            loginView.getClientManager().loginServer( loginView, strMsg);
        }
    }
}
