package cz.madeta.icoJasonParse.services;

import org.hibernate.mapping.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cz.madeta.icoJasonParse.model.EntityIcodic;
import cz.madeta.icoJasonParse.repository.EntityRepository;

//@Service
public class EntityServices {

    private final EntityRepository entityRepository;
    
    //@Autowired
    public EntityServices(EntityRepository entityRepository) {
        this.entityRepository = entityRepository;
    }

    public void processDataAndSaveToDatabase(String name, String icodic) {
        try {
            EntityIcodic entity = new EntityIcodic(name, icodic);
            entityRepository.save(entity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public EntityIcodic saveEntity(EntityIcodic EntityIcodic) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'saveEntity'");
    }

    public Iterable<EntityIcodic> getAllEntities() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAllEntities'");
    }
} 
