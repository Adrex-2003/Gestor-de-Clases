package com.miapp.admin.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Asistencia {

    private final SimpleIntegerProperty idEstudiante;
    private final SimpleStringProperty ru;
    private final SimpleStringProperty nombre;
    private final SimpleIntegerProperty presente; // 0 = ausente, 1 = presente

    public Asistencia(int idEstudiante, String ru, String nombre, int presente) {
        this.idEstudiante = new SimpleIntegerProperty(idEstudiante);
        this.ru = new SimpleStringProperty(ru);
        this.nombre = new SimpleStringProperty(nombre);
        this.presente = new SimpleIntegerProperty(presente);
    }

    public int getIdEstudiante() {
        return idEstudiante.get();
    }

    public SimpleIntegerProperty idEstudianteProperty() {
        return idEstudiante;
    }

    public String getRu() {
        return ru.get();
    }

    public SimpleStringProperty ruProperty() {
        return ru;
    }

    public String getNombre() {
        return nombre.get();
    }

    public SimpleStringProperty nombreProperty() {
        return nombre;
    }

    public int getPresente() {
        return presente.get();
    }

    public SimpleIntegerProperty presenteProperty() {
        return presente;
    }

    public void setPresente(int valor) {
        this.presente.set(valor);
    }
}
