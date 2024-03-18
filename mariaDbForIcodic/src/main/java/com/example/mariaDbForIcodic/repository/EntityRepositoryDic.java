package com.example.mariaDbForIcodic.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.mariaDbForIcodic.model.EntityDic;

//repository for dic database uses CRUD from JpaRepository has additional method for finding by certain values

@Repository
public interface EntityRepositoryDic extends JpaRepository<EntityDic, Long> {
   
    List<EntityDic> findByName(String name);

    List<EntityDic> findByIcodic(String icodic);
}