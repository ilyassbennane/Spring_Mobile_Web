package com.example.school.entities;

public class Filiere {
    private Long id;
    private String code;
    private String libelle;
public Filiere(){

}
    public Filiere(long id,String code,String libelle){
        this.id=id;
        this.code=code;
        this.libelle=libelle;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getCode() {
        return code;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public String getLibelle() {
        return libelle;
    }
    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }



}

