package com.example.mariaDbForIcodic.DBO;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.example.mariaDbForIcodic.model.EntityIcodic;
import com.example.mariaDbForIcodic.services.EntityServices;

public class DataBaseEntryCreation {
    
    public static void main(String[] args) {
        
        try(AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class)){
            EntityServices entityServices = context.getBean(EntityServices.class);  

            EntityIcodic newEntityIcodic = new EntityIcodic();
            newEntityIcodic.setIcodic("test3");
            newEntityIcodic.setName("test3");

            EntityIcodic createdEntityIcodic = entityServices.createEntity(newEntityIcodic);

            if (createdEntityIcodic != null) {
                System.out.println("New entity created with ID: " + createdEntityIcodic.getId());
            } else {
                System.out.println("Failed to create a new entity. Check for errors.");
            }
        }
    }
}
