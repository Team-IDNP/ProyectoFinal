package com.example.residuosapp.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Alert {
    String id;
    String distritoId;
    String estado;
    String multimediaSend;
    String ubiLat;
    String ubiLong;
    String usuarioId;
    String fecha;
    public String lugar = "NULL";

    public Alert(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDistritoId() {
        return distritoId;
    }

    public void setDistritoId(String distritoId) {
        this.distritoId = distritoId;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMultimediaSend() {
        return multimediaSend;
    }

    public void setMultimediaSend(String multimediaSend) {
        this.multimediaSend = multimediaSend;
    }

    public String getUbiLat() {
        return ubiLat;
    }

    public void setUbiLat(String ubiLat) {
        this.ubiLat = ubiLat;
    }

    public String getUbiLong() {
        return ubiLong;
    }

    public void setUbiLong(String ubiLong) {
        this.ubiLong = ubiLong;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        map.put("distritoId", getDistritoId());
        map.put("estado", getEstado());
        map.put("multimedia", getMultimediaSend());
        map.put("ubiLat", getUbiLat());
        map.put("ubiLong", getUbiLong());
        map.put("usuarioId", getUsuarioId());
        map.put("fecha", getFecha());

        return map;
    }

    public static Comparator<Alert> DateAZComparator = (f1, f2) -> {
        try {
            Date start = new SimpleDateFormat("yyyy-MM-dd").parse(f1.getFecha());
            Date end = new SimpleDateFormat("yyyy-MM-dd").parse(f2.getFecha());
            return end.compareTo(start);

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    };

}
