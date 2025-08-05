package testjson;

import testjson.utilsjson.MessageSvr;

import java.util.ArrayList;
import java.util.List;

public class Maintest {
    public static void main(String[] args) {
        MessageSvr msg = new MessageSvr("login");
        List<String> list = new ArrayList<>();
        list.add("kim");
        list.add("lee");

        msg.addMessage("name", "kim");
        msg.addMessage("age", 19);
        //msg.addCommand("login");
        msg.addList( "data", list);
        System.out.println(msg.toString());
        String json2 = msg.toString();
        for( int i=0; i < 100000000; i++) {

            String jsonData = "{\"param1\":\"111\",\"param2\":\"222\"}";
            System.out.println(i+ " || "+msg.getJsonObject(json2).toString());
        }
    }
}
