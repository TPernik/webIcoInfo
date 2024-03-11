package com.example.mariaDbForIcodic.Controlers;

import java.util.HashMap;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.mariaDbForIcodic.Exceptions.*;
import com.example.mariaDbForIcodic.model.EntityIcodic;
import com.example.mariaDbForIcodic.repository.EntityRepository;
import com.example.mariaDbForIcodic.services.EntityServices;;

@RestController
@RequestMapping("/api/v1")
public class EntityControler {

    @Autowired
    private EntityRepository entityRepository;

    @GetMapping("/icodic")
    public List< EntityIcodic >getAllEntities(){
        return entityRepository.findAll();
    }

    @GetMapping("/icodic/{id}")
    public ResponseEntity <EntityIcodic> getEntityicodicById(@PathVariable(value = "id") Long entityIcodicId)
    throws ResourceNotFoundException{
        EntityIcodic entityIcodic = entityRepository.findById(entityIcodicId)
            .orElseThrow(() -> new ResourceNotFoundException("Ico or Dic not found for this id ::" + entityIcodicId));
            return ResponseEntity.ok().body(entityIcodic);
    }

    @PostMapping("/icodic")
    public EntityIcodic createEntityIcodic(@Valid @RequestBody EntityIcodic entityIcodic){
        return entityRepository.save(entityIcodic);
    }

    @PutMapping("/icodic/{id}")
    public ResponseEntity <EntityIcodic> updateEntityIcodic(@PathVariable(value = "id") Long entityIcodicId,
        @Valid @RequestBody EntityIcodic entityIcodicDetails)throws ResourceNotFoundException{
            EntityIcodic entityIcodic = entityRepository.findById(entityIcodicId)
                .orElseThrow(() -> new ResourceNotFoundException("Ico or Dic not found for this id ::" + entityIcodicId));
            
            entityIcodic.setIcodic(entityIcodicDetails.getIcodic());
            entityIcodic.setName(entityIcodicDetails.getName());
            final EntityIcodic updatedEntityIcodic = entityRepository.save(entityIcodic);
            return ResponseEntity.ok(updatedEntityIcodic);
        }
    
    @DeleteMapping("/icodic/{id}")
    public Map <String, Boolean> deleteEntityIcodic(@PathVariable(value = "id") Long entityIcodicId)
    throws ResourceNotFoundException{
        EntityIcodic entityIcodic = entityRepository.findById(entityIcodicId)
            .orElseThrow(() -> new ResourceNotFoundException("Ico or Dic not found for this id ::" + entityIcodicId));

        entityRepository.delete(entityIcodic);
        Map <String, Boolean> response = new HashMap<>();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
    
}
