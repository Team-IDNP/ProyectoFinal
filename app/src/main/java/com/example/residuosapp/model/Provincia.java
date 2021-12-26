package com.example.residuosapp.model;

import java.util.Comparator;

public class Provincia {
    String id;

    String departamentoId;
    String nombreProv;

    public Provincia(){}

    public String getNombreProv() {
        return nombreProv;
    }

    public void setNombreProv(String nombreProv) {
        this.nombreProv = nombreProv;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return nombreProv;
    }
    public String getName2() {
        return nombreProv;
    }

    public void setName2(String nombreProv) {
        this.nombreProv = nombreProv;
    }

    public String getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(String departamentoID) {
        this.departamentoId = departamentoID;
    }

    public static Comparator<Provincia> NameAZComparator = (c1, c2) -> c1.getName().compareTo(c2.getName());
}
