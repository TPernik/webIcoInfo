package com.example.mariaDbForIcodic.model;

import java.sql.Date;

import org.springframework.data.annotation.LastModifiedDate;
import java.time.LocalDateTime;    
import jakarta.persistence.Column;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "icodic_tbl2")
//Data
//AllArgsConstructor
//NoArgsConstructor
public class EntityIcodic {
    
    private Long id;
    
    private String name;
    public String icodic;
    private LocalDateTime updatedAt;

    public EntityIcodic(){

    }

    public EntityIcodic(String name, String icodic, LocalDateTime updatedAt){
        this.name = name;
        this.icodic = icodic;
        this.updatedAt = updatedAt;
    }

    //getters/setters

    @Id
    @Column(name = "Id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    public Long getId(){
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    @Column(name = "Company_name", nullable = false)
    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    @Column(name = "ico_dic", nullable = false)
    public String getIcodic(){
        return icodic;
    }

    public void setIcodic(String icodic){
        this.icodic = icodic;
    }

    @Column(name = "updated_at", nullable = false)
    @LastModifiedDate
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
}
