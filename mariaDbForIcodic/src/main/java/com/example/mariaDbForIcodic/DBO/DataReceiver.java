package com.example.mariaDbForIcodic.DBO;



import java.sql.Time;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.mariaDbForIcodic.model.EntityDic;
import com.example.mariaDbForIcodic.model.EntityIco;
import com.example.mariaDbForIcodic.model.EntityIcodic;
import com.example.mariaDbForIcodic.services.EntityServices;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.annotation.PostConstruct;


@RestController
@Component
public class DataReceiver {

    @Autowired
    private EntityServices entityServices;

    @PostConstruct
    public void postConstruct() {
        System.out.println("DataReciever bean has been created!");
    }

    @PostMapping(value = "/icodicDbAdd")
    public void receiveAndInsertData(@RequestBody String icodic) throws JsonMappingException, JsonProcessingException {
        
            ObjectMapper objectMapper = new ObjectMapper();
            EntityDic icodics = objectMapper.readValue(icodic, EntityDic.class);
            //EntityIco icos = objectMapper.readValue(icodic, EntityIco.class);
            //EntityDic dics = objectMapper.readValue(icodic, EntityDic.class);

            
            String icodicString = icodics.getIcodic();
            String nameString = icodics.getName();
            var currentTime = LocalDateTime.now();
            String unreliablePayer = icodics.getUnreliablePayer();
            EntityIcodic icodicname = new EntityIcodic(nameString, icodicString, currentTime);
            EntityIco icodicnameIco = new EntityIco(nameString, icodicString, currentTime);
            
            EntityDic icodicnameDic;
            if(unreliablePayer == null){
                unreliablePayer = "";
                icodicnameDic = new EntityDic(nameString, icodicString, currentTime, unreliablePayer);
            }else{
                icodicnameDic = new EntityDic(nameString, icodicString, currentTime, unreliablePayer);
            }
            
            
            //List<EntityIcodic> sameIcodic= entityServices.findEntitiesByIcodics(icodicString);
            if(!entityServices.entityExists(icodicString)){

                entityServices.createEntity(icodicname);
            }else{
                entityServices.updateEntity(icodicname);
            }

            if(!entityServices.entityExistsIco(icodicString)){

                entityServices.createEntityIco(icodicnameIco);
            }else{
                entityServices.updateEntityIco(icodicnameIco);
            }

            if(!entityServices.entityExistsDic(icodicString)){

                entityServices.createEntityDic(icodicnameDic);
            }else{
                entityServices.updateEntityDic(icodicnameDic);
            }
            
        
    }
}
