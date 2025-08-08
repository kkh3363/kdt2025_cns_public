package com.cnsu.common.adaptor;

import com.cnsu.common.dto.CommMessageDto;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;

public class CommMessageAdaptor {
    CommMessageDto msgDto ;
    Moshi moshi;
    JsonAdapter<CommMessageDto> msgAdaptor;
    public CommMessageAdaptor() {
        moshi = new Moshi.Builder().build();
        msgAdaptor = moshi.adapter(CommMessageDto.class);
        msgDto = new CommMessageDto();
    }
    public CommMessageAdaptor(String str) {
        this();
        if ( str !=null && !str.isEmpty() )
            msgDto = convertJsonToDto(str);
    }

    /**
     *
     * @return
     */
    public CommMessageDto convertJsonToDto(String jsonString){
        CommMessageDto msgJson;
        try {
            msgJson = msgAdaptor.fromJson(jsonString);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return msgJson;
    }
    public String convertDtoToJson(CommMessageDto tempDto){
        return msgAdaptor.toJson(tempDto);
    }

    public void cleanDto(){
        msgDto.setCommand("");
        msgDto.setData("");
    }
    public void setCommand(String str){
        msgDto.setCommand(str);
    }
    public void setData(String strData){
        msgDto.setData(strData);
    }
    public String getJson(){
        return msgAdaptor.toJson(msgDto);
    }

//    public <T>T getJsonToDto(String jsonString){
//        Moshi moshi = new Moshi.Builder().build();
//        Type type = T.class;
//        JsonAdapter<T> msgAdaptor = moshi.adapter(T.class);
//        T retDto;
//        try {
//            retDto = msgAdaptor.fromJson(jsonString);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return retDto;
//    }
//    public <T> JsonAdapter<T> getAdapter(Type type) {
//        return moshi.adapter(type);
//    }


    /**
     *
     * @return
     */
    public CommMessageDto getMsgDto() {
        return msgDto;
    }
}
