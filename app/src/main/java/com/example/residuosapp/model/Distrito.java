package com.example.residuosapp.model;

import java.util.Comparator;

public class Distrito {
    String id;

    String provinciaId;
    String nombreDist;
    String emailDist;

    Distrito(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProvinciaId() {
        return provinciaId;
    }

    public void setProvinciaId(String provinciaId) {
        this.provinciaId = provinciaId;
    }

    public String getNombreDist() {
        return nombreDist;
    }

    public void setNombreDist(String nombreDist) {
        this.nombreDist = nombreDist;
    }

    public String getEmailDist() {
        return emailDist;
    }

    public void setEmailDist(String emailDist) {
        this.emailDist = emailDist;
    }

    public String getName() {
        return nombreDist;
    }

    public static Comparator<Distrito> NameAZComparator = (c1, c2) -> c1.getName().compareTo(c2.getName());

}
