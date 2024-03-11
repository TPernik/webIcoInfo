package cz.madeta.icoJasonParse.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;

@Entity
@Table(name = "icodic_tbl")
//Data
//AllArgsConstructor
//NoArgsConstructor
public class EntityIcodic {
    
    private Long id;
    
    private String name;
    private String icodic;

    public EntityIcodic(){

    }

    public EntityIcodic(String mame, String icodic){
        this.name = name;
        this.icodic = icodic;
    }

    //getters/setters

    @Id
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
    public String getIcocic(){
        return icodic;
    }

    public void setIcodic(String icodic){
        this.icodic = icodic;
    }

    //@Override
    //public String toString(){
    //    return "Company{" + 
    //            ", name='" + name + '\'' +
    //            ", Ico/Dic= " + icodic +
    //            '}';
    //}
    
}
