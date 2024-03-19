package com.example.mariaDbForIcodic;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;
import org.springframework.stereotype.Service;

import com.example.mariaDbForIcodic.DBO.DataReceiver;

//@SpringBootApplication
@Service
public class test {
    
    public static void main(String[] args) throws Exception{

        DataReceiver tesDataReceiver = new DataReceiver();

        List<String> ico = new ArrayList<String>();
        //List<String> dic = new ArrayList<String>();

        //var icodic = "{\"name\": \"testtest\", \"icodic\": \"261684986\"}";
        JSONObject Jsbody = new JSONObject();
        Jsbody.put("icodic", "06608795");
        Jsbody.put("name", "TEst");
        Jsbody.put("unreliablePayer", "Ano");
        //Jsbody.put("dicKey", true);
        //Jsbody.put("icoKey", false);
        String JsonString = Jsbody.toString();
        //String dic = "24240478";
        //dic.add("25836595");
        //dic.add("63275635");
        ico.add("63275635");
        ico.add("25836595");
        ico.add("63275635");

        tesDataReceiver.receiveAndInsertData(JsonString);
        //System.out.println(testControllerIco.fetchAdditionalInfoForUnreliablePayer(dic));
    }
}
