package com.miapp.admin.controllers;

import com.miapp.admin.models.Practica;
import com.miapp.admin.database.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class PracticasFormController {

    @FXML private TextField txtPractica;
    @FXML private DatePicker dpInicio;
    @FXML private DatePicker dpFin;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar;

    private Practica practica; // si es editar, se carga aquí

    public void setPractica(Practica practica) {
        this.practica = practica;
        if (practica != null) {
            txtPractica.setText(practica.getPractica());
            dpInicio.setValue(LocalDate.parse(practica.getFechaInicio()));
            dpFin.setValue(LocalDate.parse(practica.getFechaFin()));
            txtDescripcion.setText(practica.getDescripcion());
        }
    }

    @FXML
    private void guardarPractica() {
        String nombre = txtPractica.getText();
        LocalDate inicio = dpInicio.getValue();
        LocalDate fin = dpFin.getValue();
        String descripcion = txtDescripcion.getText();

        if (nombre.isEmpty() || inicio == null || fin == null) {
            mostrarAlerta("Error", "Todos los campos obligatorios deben estar llenos.");
            return;
        }

        try (Connection conn = Database.getConnection()) {
            if (practica == null) {
                // Insertar nueva práctica
                String sql = "INSERT INTO Practicas (tipo, practica, fecha_inicio, fecha_fin, descripcion) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, "AUXILIAR");
                stmt.setString(2, nombre);
                stmt.setString(3, inicio.toString());
                stmt.setString(4, fin.toString());
                stmt.setString(5, descripcion);
                stmt.executeUpdate();
            } else {
                // Editar práctica existente
                String sql = "UPDATE Practicas SET practica = ?, fecha_inicio = ?, fecha_fin = ?, descripcion = ? WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, nombre);
                stmt.setString(2, inicio.toString());
                stmt.setString(3, fin.toString());
                stmt.setString(4, descripcion);
                stmt.setInt(5, practica.getId());
                stmt.executeUpdate();
            }

            cerrarVentana();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error SQL", e.getMessage());
        }
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnGuardar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
