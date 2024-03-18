package com.example.mariaDbForIcodic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mariaDbForIcodic.model.EntityIco;

//repository for ico database uses CRUD from JpaRepository has additional method for finding by certain values

@Repository
public interface EntityRepositoryIco extends JpaRepository<EntityIco, Long> {
   
    List<EntityIco> findByName(String name);

    List<EntityIco> findByIcodic(String icodic);
}