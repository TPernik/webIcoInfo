package com.example.mariaDbForIcodic.services;

import java.util.List;
//import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.mariaDbForIcodic.model.EntityIcodic;
import com.example.mariaDbForIcodic.repository.EntityRepository;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

@Service
public class EntityServices {

    private final EntityRepository entityRepository;
    
    @Autowired
    public EntityServices(EntityRepository entityRepository){
        this.entityRepository = entityRepository;
    }

    public EntityIcodic createEntity(EntityIcodic icodic){
        return entityRepository.save(icodic);
    }

    //public void processDataAndSaveToDatabase(String name, String icodic, LocalTime) {
    //    
    //        EntityIcodic entity = new EntityIcodic(name, icodic);
    //        entityRepository.save(entity);
    //    
    //}

     @PostConstruct
    public void postConstruct() {
        System.out.println("EntityServices bean has been created!");
    }
    
    // Method to find entities by name
    //public List<EntityIcodic> findEntitiesByName(String name) {
    //    return entityRepository.findByName(name);
    //}
    //// Method to find entities by icodic
    //public List<EntityIcodic> findEntitiesByIcodics(String icodic) {
    //    return entityRepository.findByName(icodic);
    //}
    public EntityIcodic saveEntity(@Valid @RequestBody EntityIcodic icodic) {
        return entityRepository.save(icodic);
    }

    public Iterable<EntityIcodic> getAllEntities() {
        // TODO Auto-generated method stub
        return entityRepository.findAll();
    }
} 
