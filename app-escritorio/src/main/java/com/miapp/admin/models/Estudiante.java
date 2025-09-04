package com.miapp.admin.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Estudiante {
    private int id;
    private StringProperty ru;
    private StringProperty ci;
    private StringProperty aPaterno;
    private StringProperty aMaterno;
    private StringProperty nombre;
    private StringProperty asistencia = new SimpleStringProperty();
    public StringProperty asistenciaProperty() { return asistencia; }
    public String getAsistencia() { return asistencia.get(); }
    public void setAsistencia(String a) { asistencia.set(a); }

    public Estudiante(int id, String ru, String ci, String aPaterno, String aMaterno, String nombre) {
        this.id = id;
        this.ru = new SimpleStringProperty(ru);
        this.ci = new SimpleStringProperty(ci);
        this.aPaterno = new SimpleStringProperty(aPaterno);
        this.aMaterno = new SimpleStringProperty(aMaterno);
        this.nombre = new SimpleStringProperty(nombre);
    }

    // ID normal
    public int getId() {
        return id;
    }

    // Propiedades necesarias para TableView
    public StringProperty ruProperty() {
        return ru;
    }

    public StringProperty ciProperty() {
        return ci;
    }

    public StringProperty aPaternoProperty() {
        return aPaterno;
    }

    public StringProperty aMaternoProperty() {
        return aMaterno;
    }

    public StringProperty nombreProperty() {
        return nombre;
    }

    // Getters y setters normales (opcional)
    public String getRu() { return ru.get(); }
    public void setRu(String ru) { this.ru.set(ru); }

    public String getCi() { return ci.get(); }
    public void setCi(String ci) { this.ci.set(ci); }

    public String getAPaterno() { return aPaterno.get(); }
    public void setAPaterno(String aPaterno) { this.aPaterno.set(aPaterno); }

    public String getAMaterno() { return aMaterno.get(); }
    public void setAMaterno(String aMaterno) { this.aMaterno.set(aMaterno); }

    public String getNombre() { return nombre.get(); }
    public void setNombre(String nombre) { this.nombre.set(nombre); }
}
