package com.example.mariaDbForIcodic.services;

import java.util.List;
//import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
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
    public List<EntityIcodic> findEntitiesByName(String name) {
        return entityRepository.findByName(name);
    }
    public EntityIcodic updateEntity(EntityIcodic updatedEntity){
        if(entityRepository.existsById(updatedEntity.getId())){
            return entityRepository.save(updatedEntity);
        }else{
            return null;
        }
    }
    // Method to find entities by icodic
    public List<EntityIcodic> findEntitiesByIcodics(String icodic) {
        return entityRepository.findByIcodic(icodic);
    }
    @SuppressWarnings("null")
    public EntityIcodic saveEntity(@Valid @RequestBody EntityIcodic icodic) {
        return entityRepository.save(icodic);
    }
    public boolean entityExists(String icodicString) {
        List<EntityIcodic> entities = entityRepository.findByIcodic(icodicString);
        return !entities.isEmpty();
    }
    public Iterable<EntityIcodic> getAllEntities() {
        // TODO Auto-generated method stub
        return entityRepository.findAll();
    }
//ico
    public EntityIco createEntityIco(EntityIco icodicname){
        return entityRepositoryIco.save(icodicname);
    }
    // Method to find entities by name
    public List<EntityIco> findEntitiesByNameIco(String name) {
        return entityRepositoryIco.findByName(name);
    }
    public EntityIco updateEntityIco(EntityIco updatedEntityIco){
        if(entityRepositoryIco.existsById(updatedEntityIco.getId())){
            return entityRepositoryIco.save(updatedEntityIco);
        }else{
            return null;
        }
    }
    // Method to find entities by ico
    public List<EntityIco> findEntitiesByIco(String icodic) {
        return entityRepositoryIco.findByIcodic(icodic);
    }
    @SuppressWarnings("null")
    public EntityIco saveEntityIco(@Valid @RequestBody EntityIco icodic) {
        return entityRepositoryIco.save(icodic);
    }
    public boolean entityExistsIco(String icodicString) {
        List<EntityIco> entities = entityRepositoryIco.findByIcodic(icodicString);
        return !entities.isEmpty();
    }
    public Iterable<EntityIco> getAllEntitiesIco() {
        // TODO Auto-generated method stub
        return entityRepositoryIco.findAll();
    }

//dic

     public EntityDic createEntityDic(EntityDic icodic){
        return entityRepositoryDic.save(icodic);
    }
    // Method to find entities by name
    public List<EntityDic> findEntitiesByNameDic(String name) {
        return entityRepositoryDic.findByName(name);
    }
    public EntityDic updateEntityDic(EntityDic updatedEntityDic){
        if(entityRepositoryDic.existsById(updatedEntityDic.getId())){
            return entityRepositoryDic.save(updatedEntityDic);
        }else{
            return null;
        }
    }
    // Method to find entities by dic
    public List<EntityDic> findEntitiesByDic(String icodic) {
        return entityRepositoryDic.findByIcodic(icodic);
    }
    @SuppressWarnings("null")
    public EntityDic saveEntityDic(@Valid @RequestBody EntityDic icodic) {
        return entityRepositoryDic.save(icodic);
    }
    public boolean entityExistsDic(String icodicString) {
        List<EntityDic> entities = entityRepositoryDic.findByIcodic(icodicString);
        return !entities.isEmpty();
    }
    public Iterable<EntityDic> getAllEntitiesDic() {
        // TODO Auto-generated method stub
        return entityRepositoryDic.findAll();
    }

} 
