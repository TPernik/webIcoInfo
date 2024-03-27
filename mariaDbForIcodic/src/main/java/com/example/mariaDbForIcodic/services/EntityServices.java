package com.example.mariaDbForIcodic.services;

import java.time.LocalDateTime;
import java.util.List;
import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.mariaDbForIcodic.model.EntityDic;
import com.example.mariaDbForIcodic.model.EntityIco;
import com.example.mariaDbForIcodic.model.EntityIcodic;
import com.example.mariaDbForIcodic.repository.EntityRepository;
import com.example.mariaDbForIcodic.repository.EntityRepositoryDic;
import com.example.mariaDbForIcodic.repository.EntityRepositoryIco;

import jakarta.annotation.PostConstruct;
import jakarta.validation.Valid;

//service for manipulating entities uses repo CRUD methods 
@Service
public class EntityServices {

    private final EntityRepository entityRepository;
    private final EntityRepositoryIco entityRepositoryIco;
    private final EntityRepositoryDic entityRepositoryDic;
    
    @Autowired
    public EntityServices(EntityRepository entityRepository, EntityRepositoryIco entityRepositoryIco, EntityRepositoryDic entityRepositoryDic){
        this.entityRepository = entityRepository;
        this.entityRepositoryIco = entityRepositoryIco;
        this.entityRepositoryDic = entityRepositoryDic;
    }
//icodic    
    //Creates entry in DB
    public EntityIcodic createEntity(EntityIcodic icodic){
        return entityRepository.save(icodic);
    }
    
    //basically useless but i like it
    @PostConstruct
    public void postConstruct() {
        System.out.println("EntityServices bean has been created!");
    }
    
    // Method to find entities by name
    public List<EntityIcodic> findEntitiesByName(String name) {
        return entityRepository.findByName(name);
    }

    //updates entry in DB
    public EntityIcodic updateEntity(String icodic, EntityIcodic icodicname){
     
        List<EntityIcodic> entities = entityRepository.findByIcodic(icodic);

        if(!entities.isEmpty()){
            
            for (EntityIcodic existingIcodic: entities){

                existingIcodic.setId(existingIcodic.getId());
                existingIcodic.setUpdatedAt(LocalDateTime.now());
                existingIcodic.setIcodic(icodic);
                existingIcodic.setName(icodicname.getName());

                return entityRepository.save(existingIcodic);
            }
            
        }
        return entityRepository.save(null);

    }

    // Method to find entities by icodic
    public List<EntityIcodic> findEntitiesByIcodics(String icodic) {
        return entityRepository.findByIcodic(icodic);
    }
    //useless
    public EntityIcodic saveEntity(@Valid @RequestBody EntityIcodic icodic) {
        return entityRepository.save(icodic);
    }

    //finds if entry already exists
    public boolean entityExists(String icodicString) {
        List<EntityIcodic> entities = entityRepository.findByIcodic(icodicString);
        return !entities.isEmpty();
    }
    
    //not in use rn
    public List<EntityIcodic> getAllEntities() {
        // TODO Auto-generated method stub
        return entityRepository.findAll();
    }
//ico
    //creates entry in ico DB
    public EntityIco createEntityIco(EntityIco icodicname){
        return entityRepositoryIco.save(icodicname);
    }

    // Method to find entities by name
    public List<EntityIco> findEntitiesByNameIco(String name) {
        return entityRepositoryIco.findByName(name);
    }

    //updates already existing ico DB entry
    public EntityIco updateEntityIco(String icodic, EntityIco updatedEntityIco){
     
        List<EntityIco> entities = entityRepositoryIco.findByIcodic(icodic);

        if(!entities.isEmpty()){
            
            for (EntityIco existingIco: entities){

                existingIco.setId(existingIco.getId());
                existingIco.setUpdatedAt(LocalDateTime.now());
                existingIco.setIcodic(icodic);
                existingIco.setName(updatedEntityIco.getName());

                return entityRepositoryIco.save(existingIco);
            }
            
        }
        return entityRepositoryIco.save(null);

    }
    // Method to find entities by ico
    public List<EntityIco> findEntitiesByIco(String icodic) {
        return entityRepositoryIco.findByIcodic(icodic);
    }
    
    //useless
    @SuppressWarnings("null")
    public EntityIco saveEntityIco(@Valid @RequestBody EntityIco icodic) {
        return entityRepositoryIco.save(icodic);
    }

    //find if entry already exists in Ico DB 
    public boolean entityExistsIco(String icodicString) {
        List<EntityIco> entities = entityRepositoryIco.findByIcodic(icodicString);
        return !entities.isEmpty();
    }

    //not using rn
    public List<EntityIco> getAllEntitiesIco() {
        // TODO Auto-generated method stub
        return entityRepositoryIco.findAll();
    }

//dic

    //creates dic DB entry 
     public EntityDic createEntityDic(EntityDic icodic){
        return entityRepositoryDic.save(icodic);
    }
    // Method to find entities by name
    public List<EntityDic> findEntitiesByNameDic(String name) {
        return entityRepositoryDic.findByName(name);
    }

    //updates entry if it already exists in dic DB
    public EntityDic updateEntityDic(String icodic, EntityDic updatedEntityDic){
     
        List<EntityDic> entities = entityRepositoryDic.findByIcodic(icodic);

        if(!entities.isEmpty()){
            
            for (EntityDic existingDic : entities){

                existingDic.setId(existingDic.getId());
                existingDic.setUpdatedAt(LocalDateTime.now());
                existingDic.setUnreliablePayer(updatedEntityDic.unreliablePayer);
                existingDic.setIcodic(icodic);
                existingDic.setName(updatedEntityDic.getName());

                return entityRepositoryDic.save(existingDic);
            }
            
        }
        return entityRepositoryDic.save(null);

    }
    // Method to find entities by dic
    public List<EntityDic> findEntitiesByDic(String icodic) {
        return entityRepositoryDic.findByIcodic(icodic);
    }
    
    //useless
    public EntityDic saveEntityDic(@Valid @RequestBody EntityDic icodic) {
        return entityRepositoryDic.save(icodic);
    }

    //find out if entry based on this dic exists in dic DB
    public boolean entityExistsDic(String icodicString) {
        List<EntityDic> entities = entityRepositoryDic.findByIcodic(icodicString);
        return !entities.isEmpty();
    }

    //Not using rn
    public List<EntityDic> getAllEntitiesDic() {
        return entityRepositoryDic.findAll();
    }

    //86400000
    @Scheduled(fixedRate = 86400000)
    public void updateDatabaseDic(){
        List<EntityDic> entities = entityRepositoryDic.findAll();
        for (EntityDic entityDic : entities) {
            LocalDateTime entityDateTime = entityDic.getUpdatedAt();
            LocalDateTime currDateTime = LocalDateTime.now();

            Duration duration = Duration.between(entityDateTime, currDateTime);
            long difference = duration.toDays();
            if(difference > 7){
                entityRepositoryDic.deleteById(entityDic.getId());
            }
        }
    }
    @Scheduled(cron = "0 0 * * * *")
    public void updateDatabaseIco(){
        List<EntityIco> entities = entityRepositoryIco.findAll();
        for (EntityIco entityIco : entities) {
            LocalDateTime entityDateTime = entityIco.getUpdatedAt();
            LocalDateTime currDateTime = LocalDateTime.now();

            Duration duration = Duration.between(entityDateTime, currDateTime);
            long difference = duration.toDays();
            if(difference > 7){
                entityRepositoryIco.deleteById(entityIco.getId());
            }
        }
    }
} 
