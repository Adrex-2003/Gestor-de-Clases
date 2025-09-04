package com.miapp.admin.models;

import javafx.beans.property.*;

public class Practica {
    private final IntegerProperty id;
    private final StringProperty practica;
    private final StringProperty fechaInicio;
    private final StringProperty fechaFin;
    private final StringProperty descripcion;

    // Constructor
    public Practica(int id, String practica, String fechaInicio, String fechaFin, String descripcion) {
        this.id = new SimpleIntegerProperty(id);
        this.practica = new SimpleStringProperty(practica);
        this.fechaInicio = new SimpleStringProperty(fechaInicio);
        this.fechaFin = new SimpleStringProperty(fechaFin);
        this.descripcion = new SimpleStringProperty(descripcion);
    }

    // Getters y setters tradicionales
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }

    public String getPractica() { return practica.get(); }
    public void setPractica(String practica) { this.practica.set(practica); }

    public String getFechaInicio() { return fechaInicio.get(); }
    public void setFechaInicio(String fechaInicio) { this.fechaInicio.set(fechaInicio); }

    public String getFechaFin() { return fechaFin.get(); }
    public void setFechaFin(String fechaFin) { this.fechaFin.set(fechaFin); }

    public String getDescripcion() { return descripcion.get(); }
    public void setDescripcion(String descripcion) { this.descripcion.set(descripcion); }

    // Propiedades para bindings
    public IntegerProperty idProperty() { return id; }
    public StringProperty practicaProperty() { return practica; }
    public StringProperty fechaInicioProperty() { return fechaInicio; }
    public StringProperty fechaFinProperty() { return fechaFin; }
    public StringProperty descripcionProperty() { return descripcion; }
}
