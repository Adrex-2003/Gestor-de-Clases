package com.miapp.admin.models;

import javafx.beans.property.*;

public class NotaPractica {

    private final IntegerProperty id;
    private final IntegerProperty practicaId;
    private final IntegerProperty estudianteId;
    private final DoubleProperty nota;

    public NotaPractica(int id, int practicaId, int estudianteId, double nota) {
        this.id = new SimpleIntegerProperty(id);
        this.practicaId = new SimpleIntegerProperty(practicaId);
        this.estudianteId = new SimpleIntegerProperty(estudianteId);
        this.nota = new SimpleDoubleProperty(nota);
    }

    // ID
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    // Practica ID
    public int getPracticaId() { return practicaId.get(); }
    public void setPracticaId(int practicaId) { this.practicaId.set(practicaId); }
    public IntegerProperty practicaIdProperty() { return practicaId; }

    // Estudiante ID
    public int getEstudianteId() { return estudianteId.get(); }
    public void setEstudianteId(int estudianteId) { this.estudianteId.set(estudianteId); }
    public IntegerProperty estudianteIdProperty() { return estudianteId; }

    // Nota
    public double getNota() { return nota.get(); }
    public void setNota(double nota) { this.nota.set(nota); }
    public DoubleProperty notaProperty() { return nota; }
}
