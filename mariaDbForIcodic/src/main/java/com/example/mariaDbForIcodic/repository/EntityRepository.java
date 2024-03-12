package com.example.mariaDbForIcodic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mariaDbForIcodic.model.EntityIcodic;

@Repository
public interface EntityRepository extends JpaRepository<EntityIcodic, Long> {
   
    List<EntityIcodic> findByName(String name);

    List<EntityIcodic> findByIcodic(String icodic);
}