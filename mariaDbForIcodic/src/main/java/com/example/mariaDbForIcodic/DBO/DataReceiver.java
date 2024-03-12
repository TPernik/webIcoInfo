package com.example.mariaDbForIcodic.DBO;



import java.sql.Time;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
            EntityIcodic icodics = objectMapper.readValue(icodic, EntityIcodic.class);
            
            String icodicString = icodics.getIcodic();
            String nameString = icodics.getName();
            var currentTime = LocalDateTime.now();
            EntityIcodic icodicname = new EntityIcodic(nameString, icodicString, currentTime);
            //List<EntityIcodic> sameIcodic= entityServices.findEntitiesByIcodics(icodicString);
            if(!entityServices.entityExists(icodicString)){

                entityServices.createEntity(icodicname);
            }
            
        
    }
}
