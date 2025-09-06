package com.miapp.admin.models;

import javafx.beans.property.*;

public class EstudianteCalificacion {

    private final SimpleIntegerProperty estudianteId;
    private final SimpleStringProperty ru;
    private final SimpleStringProperty apellidoPaterno;
    private final SimpleStringProperty apellidoMaterno;
    private final SimpleStringProperty nombre;
    private final SimpleDoubleProperty nota;

    public EstudianteCalificacion(int estudianteId, String ru, String apellidoPaterno, String apellidoMaterno, String nombre, Double nota) {
        this.estudianteId = new SimpleIntegerProperty(estudianteId);
        this.ru = new SimpleStringProperty(ru);
        this.apellidoPaterno = new SimpleStringProperty(apellidoPaterno);
        this.apellidoMaterno = new SimpleStringProperty(apellidoMaterno);
        this.nombre = new SimpleStringProperty(nombre);
        this.nota = new SimpleDoubleProperty(nota);
    }

    // Getters para los campos de datos
    public int getEstudianteId() {
        return estudianteId.get();
    }

    public String getRu() {
        return ru.get();
    }

    public String getApellidoPaterno() {
        return apellidoPaterno.get();
    }

    public String getApellidoMaterno() {
        return apellidoMaterno.get();
    }

    public String getNombre() {
        return nombre.get();
    }

    public double getNota() {
        return nota.get();
    }

    // Setters para los campos editables
    public void setNota(double nota) {
        this.nota.set(nota);
    }

    // Property getters para los cellValueFactory
    public SimpleStringProperty ruProperty() {
        return ru;
    }

    public SimpleStringProperty apellidoPaternoProperty() {
        return apellidoPaterno;
    }

    public SimpleStringProperty apellidoMaternoProperty() {
        return apellidoMaterno;
    }

    public SimpleStringProperty nombreProperty() {
        return nombre;
    }

    public SimpleDoubleProperty notaProperty() {
        return nota;
    }
}
