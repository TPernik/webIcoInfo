package cz.madeta;


import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.fasterxml.jackson.core.JsonProcessingException;

import cz.madeta.icoJasonParse.ControllerIco;
//import cz.madeta.icoJasonParse.services.EntityServices;

public class test {

    public static void main(String[] args) throws JsonProcessingException{

        ControllerIco testControllerIco = new ControllerIco();

        List<String> ico = new ArrayList<String>();
        List<String> dic = new ArrayList<String>();

        
        //String dic = "24240478";
        //dic.add("09533702");
        dic.add("63275635");
        //ico.add("63275635");
        ico.add("26028891");
        //ico.add("63275635");

        //System.out.println(testControllerIco.fetchDataAres(ico));
        System.out.println(testControllerIco.fetchDataAres(ico));
    }
    
    
}
