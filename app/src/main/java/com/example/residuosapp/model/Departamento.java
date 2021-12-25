package com.example.residuosapp.model;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Departamento {
    public String nombreDept;
    public String id;

    Departamento() {

    }

    public String getName() {
        return nombreDept;
    }

    public void setName(String nombreDept) {
        this.nombreDept = nombreDept;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombreDept() {
        return nombreDept;
    }

    public void setNombreDept(String nombreDept) {
        this.nombreDept = nombreDept;
    }

    public static Comparator<Departamento> NameAZComparator = (c1, c2) -> c1.getName().compareTo(c2.getName());
}
