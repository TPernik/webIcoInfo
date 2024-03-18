package cz.madeta.icoJasonParse;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.URI;
import java.time.Duration;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
                    
                        objares.put("name", jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getString("obchodniJmeno"));
                        objares.put("ico", jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getString("ico"));
                        String dic = jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).optString("dic", "-1");
                        dic = dic.replaceAll("-1", "");
                        objares.put("dic", dic);
                        objares.put("country", jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").getString("nazevStatu"));
                    
                        String city = jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").getString("nazevObce").isEmpty() ?
                                jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").getString("nazevCastiObce") :
                                jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").getString("nazevObce");
                        objares.put("city", city);
                    
                        int zip = jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").getInt("psc");
                    objares.put("zip", String.valueOf(zip));
                    objares.put("street", jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").getString("nazevUlice"));
                    
                    int homeNum = jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").optInt("cisloDomovni", -1);              
                    int indicNum =  jsondata.getJSONArray("ekonomickeSubjekty").getJSONObject(i).getJSONObject("sidlo").optInt("cisloOrientacni", -1); 
                    
                    String number = String.valueOf(homeNum) + "/" +
                            String.valueOf(indicNum);
                    number = number.replaceAll("-1", "");
                    
                    objares.put("number", number);
                    
                        String fullstreet = objares.getString("street") + " " + objares.getString("number");
                        objares.put("fullstreet", fullstreet.trim());
                    
                        // Here you would send the response back with status 200
                        // This part is framework-specific and would depend on the Java web framework being used
                    
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
                                //objDph.append("allData", allObject);
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
                allObject.append("addracc", addObjects);
                
                for (int i = 0; i<=dic.size()-1; i++){
                    if(dic.size() == 1){
                        String nameDb = jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceRozsirenyResponse").getJSONObject("statusPlatceDPH").getString("nazevSubjektu");
                        int icodic = jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceRozsirenyResponse").getJSONObject("statusPlatceDPH").getInt("dic");
                        String unreliablePayer = jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceRozsirenyResponse").getJSONObject("statusPlatceDPH").getString("nespolehlivyPlatce");
                        String icodicDb = String.valueOf(icodic);

                        //this calls the DB RESTapi and POSTs data for entries
                        JSONObject JsonBody = new JSONObject();
                        JsonBody.put("name", nameDb);
                        JsonBody.put("icodic", icodicDb);
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
                        int icodic = jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceRozsirenyResponse").getJSONArray("statusPlatceDPH").getJSONObject(i).getInt("dic");
                        String unreliablePayer = jsonObject.getJSONObject("soapenv:Envelope").getJSONObject("soapenv:Body").getJSONObject("StatusNespolehlivyPlatceRozsirenyResponse").getJSONArray("statusPlatceDPH").getJSONObject(i).getString("nespolehlivyPlatce");
                        String icodicDb = String.valueOf(icodic);

                        //this calls the DB RESTapi and POSTs data for entries
                        JSONObject JsonBody = new JSONObject();
                        JsonBody.put("name", nameDb);
                        JsonBody.put("icodic", icodicDb);
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




