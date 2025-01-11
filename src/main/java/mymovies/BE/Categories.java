package mymovies.BE;

import java.security.Timestamp;
import java.time.LocalDateTime;

public class Categories {
    private int id;
    private String name;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

    public Categories(int id, String name, LocalDateTime created_at, LocalDateTime updated_at) {
        this.id = id;
        this.name = name;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public LocalDateTime getCreated_at(){
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at){
        this.created_at = created_at;
    }

    public LocalDateTime getUpdated_at(){
        return updated_at;
    }

    public void setUpdated_at(LocalDateTime updated_at){
        this.updated_at = updated_at;
    }



}
