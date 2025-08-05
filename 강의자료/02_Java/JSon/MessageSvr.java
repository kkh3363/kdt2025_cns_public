package testjson.utilsjson;

import org.json.*;
import java.util.List;


public class MessageSvr {
    private JSONObject jsonObject;
    private String msgCommandType;
    public MessageSvr(){
        jsonObject = new JSONObject();
        msgCommandType = null;

    }
    public MessageSvr(String msgCommandType){
        this();
        this.msgCommandType = msgCommandType;
        jsonObject.put("command", msgCommandType);
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
    public String toString(){
        return jsonObject.toString();
    }

    public <T> void addList(String strKey, List<T> list){
        JSONArray jsonArray = new JSONArray(list);
        jsonObject.put(strKey, jsonArray);
    }
    public void clean(){
        jsonObject.clear();
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
