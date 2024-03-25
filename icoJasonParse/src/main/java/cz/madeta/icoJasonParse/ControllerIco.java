package cz.madeta.icoJasonParse;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.json.JSONObject;
import org.json.XML;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;


import jakarta.servlet.http.HttpServletResponse;

@SpringBootApplication
@RestController
@Service
public class ControllerIco {
    
    //Get List of icos by POST and than send another POST to get info from ARES about companies
    @PostMapping(value = "/fetchAres")
    public String fetchDataAres(@RequestBody List<String> ico) throws JsonProcessingException{
        
        String url = "https://ares.gov.cz/ekonomicke-subjekty-v-be/rest/ekonomicke-subjekty/vyhledat";
        String dbUrl = "http://localhost:8080/mariaDbForIcodic-0.0.1-SNAPSHOT/icodicDbAdd";
        var objectMapper = new ObjectMapper();
        Set<String> icos = new HashSet<>(ico);
        String requestBody = objectMapper.writeValueAsString(icos); 

            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(10000))
                    .POST(HttpRequest.BodyPublishers.ofString("{\"ico\": " + requestBody + " }"))
                    .setHeader("Content-Type", "application/json")
                    .setHeader("Accept", "application/json")
                    .build();
    
            CompletableFuture<HttpResponse<String>> futureResponse = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
            int moreEntries = icos.size();
            JSONObject data = new JSONObject();
            try {               
                
                HttpResponse<String> response = futureResponse.get();
                String responseBody = response.body();
                JSONObject jsondata = new JSONObject(responseBody);
    
                if (jsondata.has("kod") ){
                    throw new Exception(jsondata.getString("popis"));
                }
                JSONObject allObject = new JSONObject();
     
                for(int i = 0; i <= moreEntries - 1; i++){
                    JSONObject objares = new JSONObject();
                    
                    allObject.put("data", jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i));
                    
                    objares.put("name", jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).optString("obchodniJmeno", "-1"));
                    objares.put("ico", jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).optString("ico", "-1"));
                    String dic = jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).optString("dic", "-1");
                    dic = dic.replaceAll("-1", "");
                    objares.put("dic", dic);
                    objares.put("country", jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").optString("nazevStatu", "-1"));
                    
                    String city = jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").getString("nazevObce").isEmpty() ?
                    jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").optString("nazevCastiObce", "-1") :
                    jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").optString("nazevObce", "-1");
                    objares.put("city", city);
                    
                    int zip = jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").optInt("psc", -1);
                    objares.put("zip", String.valueOf(zip));
                    objares.put("street", jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").optString("nazevUlice", "-1"));
                    
                    int homeNum = jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").optInt("cisloDomovni", -1);              
                    int indicNum =  jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").optInt("cisloOrientacni", -1); 
                    
                    String number = String.valueOf(homeNum) + "/" +
                            String.valueOf(indicNum);
                    number = number.replaceAll("-1", "");
                    
                    objares.put("number", number);
                    
                    String fullstreet = objares.getString("street") + " " + objares.optString("number", "-1");
                    objares.put("fullstreet", fullstreet.trim());
                    
                    data.append("data", objares);
                    String icodicDb = jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getString("ico");
                    String nameDb = jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getString("obchodniJmeno");
                
                    JSONObject JsonBody = new JSONObject();
                    JsonBody.put("name", nameDb);
                    JsonBody.put("icodic", icodicDb);
                    JsonBody.put("unreliablePayer", "");
                    String JsonString = JsonBody.toString();
                    HttpClient clientDb = HttpClient.newHttpClient();
                    HttpRequest requestDB = HttpRequest.newBuilder()
                            .uri(URI.create(dbUrl))
                            .timeout(Duration.ofMillis(10000))
                            .POST(HttpRequest.BodyPublishers.ofString(JsonString))
                            .setHeader("Content-Type", "application/json")
                            .setHeader("Accept", "application/json")
                            .build();

                    CompletableFuture<HttpResponse<String>> dbResponse = clientDb.sendAsync(requestDB, HttpResponse.BodyHandlers.ofString());
                            
                    }
                    return "" + data;

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                // Handle the exception, possibly return an error response
            } catch (TimeoutException e) {
                // Handle timeout, possibly return an error response
            } catch (Exception e) {
                String errText = e.getMessage();
                // Handle other exceptions, possibly return an error response
            }
            

            return "true";
         
         
        }
        
        @PostMapping(value = "/fetchDPHStatus")
        public String fetchDataDPHStatus(@RequestBody List<String> dic) {
        
            String url = "https://adisrws.mfcr.cz/dpr/axis2/services/rozhraniCRPDPH.rozhraniCRPDPHSOAP";


            Set<String> dics = new HashSet<>(dic);
            StringBuilder soapXmBuilder = new StringBuilder();
            
            soapXmBuilder.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
            soapXmBuilder.append("    <soapenv:Body>\n");    
            soapXmBuilder.append("        <StatusNespolehlivyPlatceRequest xmlns=\"http://adis.mfcr.cz/rozhraniCRPDPH/\">\n");
            
            for (String i : dics){
                soapXmBuilder.append("      <dic>").append(i).append("</dic>\n");    
            }

            soapXmBuilder.append("        </StatusNespolehlivyPlatceRequest>\n");
            soapXmBuilder.append("    </soapenv:Body>\n");
            soapXmBuilder.append("</soapenv:Envelope>");

                String xmlsoap = soapXmBuilder.toString();
                
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofMillis(10000))
                    .header("Content-Type", "text/xml;charset=UTF-8")
                    .header("SOAPAction", "getStatusNespolehlivyPlatce").POST(BodyPublishers.ofString(xmlsoap))
                    .build();
                
                    CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                    
                    try {
                        String responseBody = response.thenApply(HttpResponse::body).get();
                        JSONObject jsonObject = XML.toJSONObject(responseBody);
                        String json = jsonObject.toString();

                        
                        JSONObject objDph = new JSONObject();
                        int moreEntries = dics.size();
                        JSONObject allObject = new JSONObject();

                        if(moreEntries > 1){
                            
                            int length = jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceResponse").getJSONArray("statusPlatceDPH").length();

                            for (int i = 0; i <= length-1; i++) {
                                
                                JSONObject data = new JSONObject();

                                allObject.put("data", jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceResponse").getJSONArray("statusPlatceDPH").getJSONObject(i));

                                data.put("nespolehlivyPlatce", jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceResponse").getJSONArray("statusPlatceDPH").getJSONObject(i).getString("nespolehlivyPlatce"));
                                data.put("cisloFu", jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceResponse").getJSONArray("statusPlatceDPH").getJSONObject(i).getInt("cisloFu"));
                                data.put("dic", jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceResponse").getJSONArray("statusPlatceDPH").getJSONObject(i).getInt("dic"));
                                data.put("status", jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceResponse").getJSONObject("status").getString("statusText"));
                                
                                objDph.append("data", data);
                                allObject.append("num", i);
                            
                            }
                            return "" + allObject;
                        }
                        else{

                            allObject.put("data", jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceResponse").getJSONObject("statusPlatceDPH"));

                            objDph.put("nespolehlivyPlatce", jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceResponse").getJSONObject("statusPlatceDPH").getString("nespolehlivyPlatce"));
                            objDph.put("cisloFu", jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceResponse").getJSONObject("statusPlatceDPH").getInt("cisloFu"));
                            objDph.put("dic", jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceResponse").getJSONObject("statusPlatceDPH").getInt("dic"));
                            objDph.put("status", jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceResponse").getJSONObject("status").getString("statusText"));

                        }
                        return "" + allObject;

                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                        // Handle the exception, possibly return an error response
                    } 
                

            return "true";
        }

        @PostMapping(value = "/printPDF")
        public ResponseEntity<byte[]>printPDF (@RequestBody String dic) throws DocumentException, IOException{
            //Set<String> dics = new HashSet<>(dic);
            List<String> dicNum = new ArrayList<String>();    
            dicNum.add(dic);

            String dicData = fetchAdditionalInfoFor(dicNum);
            JsonObject jsonObject = JsonParser.parseString(dicData).getAsJsonObject();

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4); 
            PdfWriter.getInstance(document, byteArrayOutputStream).setInitialLeading(12.5f);
            
            //response.setContentType("application/pdf");
            //response.setHeader("Content-Disposition", "attachment; filename=\"output.pdf\"");

            document.open();
            
            Font fontTitle = FontFactory.getFont(FontFactory.TIMES_BOLD, "UTF-8");
            fontTitle.setSize(24);
            String title = jsonObject.getAsJsonObject("data").getAsJsonObject("statusPlatceDPH").get("nazevSubjektu").getAsString();
            Paragraph titleParagraph = new Paragraph(title, fontTitle);
            titleParagraph.setAlignment(Paragraph.ALIGN_CENTER);

            Font fontBody = FontFactory.getFont(FontFactory.TIMES_ROMAN, "UTF-8");
            fontBody.setSize(14);
            
            document.add(titleParagraph);
            
            iterateJsonObject(jsonObject, document, fontBody);
            document.close();

            byte[] pdfBytes = byteArrayOutputStream.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType((MediaType.APPLICATION_PDF));
            headers.setContentDispositionFormData(title, title);

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
            //var test = jsonObject.getAsJsonObject("data").getAsJsonObject("statusPlatceDPH").getAsJsonObject("nazevSubjektu").getAsString();
            //return "" + test;
 
        }
        private void iterateJsonObject(JsonObject jsonObject, Document document, Font fontBody) throws DocumentException {
            for (java.util.Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                //if(entry instanceof ArrayList){
                    String key = entry.getKey();
                    JsonElement value = entry.getValue();
                    if (value.isJsonObject()) {
                        iterateJsonObject(value.getAsJsonObject(), document, fontBody);
                    }else if(value.isJsonArray()) {
                        JsonArray jsonArray = value.getAsJsonArray();
                        Paragraph arrayParagraph = new Paragraph(key, fontBody);
                        arrayParagraph.setAlignment(Paragraph.ALIGN_LEFT);
                        document.add(arrayParagraph);
            
                        for (int i = 0; i < jsonArray.size(); i++) {
                            JsonElement arrayElement = jsonArray.get(i);
                            Paragraph elementParagraph = new Paragraph(arrayElement.toString(), fontBody);
                            elementParagraph.setAlignment(Paragraph.ALIGN_LEFT);
                            document.add(elementParagraph);
                        }
                    }
                    else {
                        //for some reason doesn't work(bullshit)
                        if(key != "\"xmlns\""){

                            Paragraph bodyParagraph = new Paragraph(key + ": " + value, fontBody);
                            bodyParagraph.setAlignment(Paragraph.ALIGN_LEFT);
                            document.add(bodyParagraph);
                        }
                    }
                //}else{
                //    String key = entry.getKey();
                //    JsonElement value = entry.getValue();
        //
                //    if (value.isJsonObject() || value.isJsonArray()) {
                //    iterateJsonObject(value.getAsJsonArray(), document, fontBody);
                //    } else {
                //    Paragraph bodyParagraph = new Paragraph(key + ": " + value, fontBody);
                //    bodyParagraph.setAlignment(Paragraph.ALIGN_LEFT);
                //    document.add(bodyParagraph);
                //    }
                //}
                
            }
        }

        //this method is being called simultaneously with fetchDPHStatus and gives additional data
        @PostMapping(value = "/fetchAdditionalInfoFor")
        public String fetchAdditionalInfoFor(@RequestBody List<String> dic) {
        
            String url = "https://adisrws.mfcr.cz/dpr/axis2/services/rozhraniCRPDPH.rozhraniCRPDPHSOAP";
            String dbUrl = "http://localhost:8080/mariaDbForIcodic-0.0.1-SNAPSHOT/icodicDbAdd";

            Set<String> dics = new HashSet<>(dic);
            JSONObject allObject = new JSONObject();
            JSONObject addObjects = new JSONObject();

            //this creates a SOAP xml format
            StringBuilder soapXmBuilder = new StringBuilder();
            
            soapXmBuilder.append("<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\">\n");
            soapXmBuilder.append("    <soapenv:Body>\n");    
            soapXmBuilder.append("        <StatusNespolehlivyPlatceRequest xmlns=\"http://adis.mfcr.cz/rozhraniCRPDPH/\">\n");
            
            for (String i : dics){
                soapXmBuilder.append("      <dic>").append(i).append("</dic>\n");    
            }

            soapXmBuilder.append("        </StatusNespolehlivyPlatceRequest>\n");
            soapXmBuilder.append("    </soapenv:Body>\n");
            soapXmBuilder.append("</soapenv:Envelope>");

            String xmlsoap = soapXmBuilder.toString();
            
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofMillis(10000))
                .header("Content-Type", "text/xml;charset=UTF-8")
                .header("SOAPAction", "getStatusNespolehlivyPlatceRozsireny").POST(BodyPublishers.ofString(xmlsoap))
                .build();
            
            CompletableFuture<HttpResponse<String>> response = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

             try {
                String responseBody = response.thenApply(HttpResponse::body).get();
                JSONObject jsonObject = XML.toJSONObject(responseBody);
                allObject.put("data", jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceRozsirenyResponse"));
                
                allObject.remove("xmlns");
                allObject.remove("statusText");
                allObject.remove("statusCode");
                
                for (int i = 0; i<=dic.size()-1; i++){
                    if(dic.size() == 1){
                        JSONObject JsonBody = new JSONObject();
                        String nameDb = jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceRozsirenyResponse").getJSONObject("statusPlatceDPH").getString("nazevSubjektu");
                        Object dicObject = jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceRozsirenyResponse").getJSONObject("statusPlatceDPH").get("dic");
                        

                        if(dicObject instanceof Integer){
                            int icodic;
                            icodic = (int) dicObject;
                            String icodicDb = String.valueOf(icodic);
                            JsonBody.put("icodic", icodicDb);
                        }else if(dicObject instanceof Long){
                            Long icodic;
                            icodic = (Long) dicObject;
                            String icodicDb = String.valueOf(icodic);
                            JsonBody.put("icodic", icodicDb);
                        }else if(dicObject instanceof String){
                            String icodicDb;
                            icodicDb = (String) dicObject;
                            JsonBody.put("icodic", icodicDb);
                        }

                        String unreliablePayer = jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceRozsirenyResponse").getJSONObject("statusPlatceDPH").getString("nespolehlivyPlatce");
                        

                        //this calls the DB RESTapi and POSTs data for entries
                        JsonBody.put("name", nameDb);
                        JsonBody.put("unreliablePayer", unreliablePayer);
                        String JsonString = JsonBody.toString();
                        HttpClient clientDb = HttpClient.newHttpClient();
                        HttpRequest requestDB = HttpRequest.newBuilder()
                            .uri(URI.create(dbUrl))
                            .timeout(Duration.ofMillis(10000))
                            .POST(HttpRequest.BodyPublishers.ofString(JsonString))
                            .setHeader("Content-Type", "application/json")
                            .setHeader("Accept", "application/json")
                            .build();
                           CompletableFuture<HttpResponse<String>> dbResponse = clientDb.sendAsync(requestDB, HttpResponse.BodyHandlers.ofString());
                           
                    }else{
                        String nameDb = jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceRozsirenyResponse").getJSONArray("statusPlatceDPH").getJSONObject(i).getString("nazevSubjektu");
                        Object dicObject = jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceRozsirenyResponse").getJSONArray("statusPlatceDPH").getJSONObject(i).get("dic");
                        String unreliablePayer = jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceRozsirenyResponse").getJSONArray("statusPlatceDPH").getJSONObject(i).getString("nespolehlivyPlatce");
                        JSONObject JsonBody = new JSONObject();
                        
                        if(dicObject instanceof Integer){
                            int icodic;
                            icodic = (int) dicObject;
                            String icodicDb = String.valueOf(icodic);
                            JsonBody.put("icodic", icodicDb);
                        }else if(dicObject instanceof String){
                            String icodicDb;
                            icodicDb = (String) dicObject;
                            JsonBody.put("icodic", icodicDb);
                        }
                        //this calls the DB RESTapi and POSTs data for entries
                        JsonBody.put("name", nameDb);
                        JsonBody.put("unreliablePayer", unreliablePayer);
                        String JsonString = JsonBody.toString();
                        HttpClient clientDb = HttpClient.newHttpClient();
                        HttpRequest requestDB = HttpRequest.newBuilder()
                            .uri(URI.create(dbUrl))
                            .timeout(Duration.ofMillis(10000))
                            .POST(HttpRequest.BodyPublishers.ofString(JsonString))
                            .setHeader("Content-Type", "application/json")
                            .setHeader("Accept", "application/json")
                            .build();
                           CompletableFuture<HttpResponse<String>> dbResponse = clientDb.sendAsync(requestDB, HttpResponse.BodyHandlers.ofString());
                    }
               
                }

                return "" + allObject;
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                // Handle the exception, possibly return an error response
            } 
                

            return "true";
        }
        
    }




