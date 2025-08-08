package com.cnsu.common.jsonutil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

public class MessageForm {
    private JSONObject jsonObject;
    private String msgCommandType;

    public MessageForm( ) {
        jsonObject = new JSONObject();
        msgCommandType = null;
    }
    public MessageForm(String str) {
        jsonObject = new JSONObject(str);
        msgCommandType = null;
    }
    public String toString(){
        return jsonObject.toString();
    }
    public String getValue(String key){
        return (String)jsonObject.get(key);
    }
    public <T> void addList(String strKey, List<T> list){
        JSONArray jsonArray = new JSONArray(list);
        jsonObject.put(strKey, jsonArray);
    }
    public void clean(){
        jsonObject.clear();
    }
    public void addMessage(String strKey, String strValue){
        jsonObject.put(strKey, strValue);
    }
    public void addMessage(String strKey, int intValue){
        jsonObject.put(strKey, intValue);
    }
    public void addCommand(String strValue){
        jsonObject.put("command", strValue);
    }
    public JSONObject getJsonObject(String strJson){
        JSONObject jobj= null;
        if ( strJson.length() < 1 )
            return jobj;
        jobj = new JSONObject(strJson);
        jsonObject = jobj;
        return jobj;
    }
}
