package com.miapp.web.servidor_web.models;

public class Estudiante {

    private int id;
    private String ru;
    private String ci;
    private String aPaterno;
    private String aMaterno;
    private String nombre;

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRu() {
        return ru;
    }

    public void setRu(String ru) {
        this.ru = ru;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getaPaterno() {
        return aPaterno;
    }

    public void setaPaterno(String aPaterno) {
        this.aPaterno = aPaterno;
    }

    public String getaMaterno() {
        return aMaterno;
    }

    public void setaMaterno(String aMaterno) {
        this.aMaterno = aMaterno;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    // toString para debug
    @Override
    public String toString() {
        return "Estudiante{" +
                "id=" + id +
                ", ru='" + ru + '\'' +
                ", ci='" + ci + '\'' +
                ", aPaterno='" + aPaterno + '\'' +
                ", aMaterno='" + aMaterno + '\'' +
                ", nombre='" + nombre + '\'' +
                '}';
    }
}
