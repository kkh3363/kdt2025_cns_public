package com.cnsu.server.controller;

import com.cnsu.common.adaptor.CommMessageAdaptor;
import com.cnsu.common.adaptor.JsonLoginMessageAdaptor;
import com.cnsu.common.dto.CommMessageDto;
import com.cnsu.common.dto.LoginMessageDto;
import com.cnsu.common.jsonutil.MessageForm;

public class LoginManager {
    LoginMessageDto loginMessageDto;
    JsonLoginMessageAdaptor jsonAdaptor;
    ClientActor clientActor ;

    public LoginManager(ClientActor clientActor,String str){
        this.clientActor = clientActor;
        setUserData(str);
    }
    public void setUserData(String str){
        jsonAdaptor = new JsonLoginMessageAdaptor(str);
        loginMessageDto = jsonAdaptor.getMsgDto();
    }

    public void loginCheck(){
        boolean bSuccess = false;
        CommMessageDto cmDto = new CommMessageDto();
        CommMessageAdaptor  adaptor = new CommMessageAdaptor();
        MessageForm msgForm = new MessageForm();

        if ( loginMessageDto.getId().equals("111") && loginMessageDto.getPassword().equals("222"))
            bSuccess = true;
        cmDto.setCommand("member");
        cmDto.setType("login");

        if (bSuccess)
            msgForm.addMessage("response","1");
        else
            msgForm.addMessage("response","0");
        cmDto.setData( msgForm.toString());

        clientActor.sendMessageToServer( adaptor.convertDtoToJson(cmDto) );

    }
    public String toString(){
        return new String("id: "+ loginMessageDto.getId() + " , password:"+loginMessageDto.getPassword());
    }
}
