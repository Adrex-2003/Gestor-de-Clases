package com.miapp.admin.models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Sesion {

    private final SimpleIntegerProperty id;
    private final SimpleStringProperty fecha;
    private final SimpleStringProperty descripcion;
    private final SimpleStringProperty tokenQr;

    public Sesion(int id, String fecha, String descripcion, String tokenQr) {
        this.id = new SimpleIntegerProperty(id);
        this.fecha = new SimpleStringProperty(fecha);
        this.descripcion = new SimpleStringProperty(descripcion);
        this.tokenQr = new SimpleStringProperty(tokenQr);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getFecha() {
        return fecha.get();
    }

    public SimpleStringProperty fechaProperty() {
        return fecha;
    }

    public String getDescripcion() {
        return descripcion.get();
    }

    public SimpleStringProperty descripcionProperty() {
        return descripcion;
    }

    public String getTokenQr() {
        return tokenQr.get();
    }

    public SimpleStringProperty tokenQrProperty() {
        return tokenQr;
    }
}
