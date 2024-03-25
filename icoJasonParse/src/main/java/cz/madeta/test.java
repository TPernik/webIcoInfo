package cz.madeta;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lowagie.text.DocumentException;

import cz.madeta.icoJasonParse.ControllerIco;
//import cz.madeta.icoJasonParse.services.EntityServices;
import jakarta.servlet.http.HttpServletResponse;

public class test {

    public static void main(String[] args) throws DocumentException, IOException{

        ControllerIco testControllerIco = new ControllerIco();

        List<String> ico = new ArrayList<String>();
        List<String> dic = new ArrayList<String>();

        
        //String dic = "24240478";
        //dic.add("09533702");
        dic.add("5706210818");
        //ico.add("63275635");
        ico.add("24220019");
        //ico.add("63275635");

        //System.out.println(testControllerIco.fetchDataAres(ico));
        //System.out.println(testControllerIco.fetchDataAres(ico));
        System.out.println(testControllerIco.printPDF("10833722"));
        
    }
        //private ControllerIco pdfControllerIco = new ControllerIco();
        //public test(ControllerIco tesControllerIco){
        //    this.pdfControllerIco = pdfControllerIco;
        //}
        //public void testPDF(HttpServletResponse response){
        //    
        //}
}
