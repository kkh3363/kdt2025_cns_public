package com.cnsu.common.adaptor;

import com.cnsu.common.dto.LoginMessageDto;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class JsonLoginMessageAdaptor {
    LoginMessageDto msgDto ;
    Moshi moshi;
    JsonAdapter<LoginMessageDto> msgAdaptor;
    public JsonLoginMessageAdaptor() {
        moshi = new Moshi.Builder().build();
        msgAdaptor = moshi.adapter(LoginMessageDto.class);
        msgDto = new LoginMessageDto();
    }
    public JsonLoginMessageAdaptor(String str) {
        this();
        msgDto = convertJsonToDto(str);
    }

    /**
     *
     * @return
     */
    public LoginMessageDto convertJsonToDto(String jsonString){
        LoginMessageDto msgJson;
        try {
            msgJson = msgAdaptor.fromJson(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return msgJson;
    }
    public String convertDtoToJson(LoginMessageDto tempDto){
        return msgAdaptor.toJson(tempDto);
    }
    public LoginMessageDto getMsgDto(){
        return msgDto;
    }

}
