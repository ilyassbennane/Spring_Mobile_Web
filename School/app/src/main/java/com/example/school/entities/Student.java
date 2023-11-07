package com.example.school.entities;

public class Student {
    private int id;
    private String name;
    private String email;
    private int phone;
    private String codeFiliere;
    private String Username;
    private String password;

    public Student(int id,String name, String email, int phone, String codeFiliere) {
        this.id=id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.codeFiliere = codeFiliere;
    }
    public Student(String name,String email,int phone ) {
        this.id=id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.codeFiliere = codeFiliere;
    }

    public Student() {

    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getCodeFiliere() {
        return codeFiliere;
    }

    public void setCodeFiliere(String codeFiliere) {
        this.codeFiliere = codeFiliere;
    }
    public void setUsername(String Username) {
        this.Username = Username;
    }
    public void setPassword(String password) {
        this.password = password;
    }


}
