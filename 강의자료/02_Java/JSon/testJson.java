package testjson.utilsjson;

import java.util.ArrayList;
import java.util.List;

public class testJson {

    public static void func1(){

    }
    public static void main(String[] args) {
        MessageSvr msg = new MessageSvr("login");
        List<String> list = new ArrayList<>();
        list.add("kim");
        list.add("lee");

        msg.addMessage("name", "kim");
        msg.addMessage("age", 19);
        //msg.addCommand("login");
        msg.addList( "data", list);
        System.out.println(msg);
        String json2 = msg.toString();
        for( int i=0; i < 2; i++) {

            String jsonData = "{\"param1\":\"111\",\"param2\":\"222\"}";
            System.out.println(i+ " || "+msg.getJsonObject(json2).toString());

        }
        func1();
    }
}
