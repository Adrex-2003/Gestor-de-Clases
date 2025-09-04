package com.miapp.admin.controllers;

import com.miapp.admin.database.Database;
import com.miapp.admin.models.Estudiante;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EstudianteFormController {

    @FXML private TextField txtRU;
    @FXML private TextField txtCI;
    @FXML private TextField txtAPaterno;
    @FXML private TextField txtAMaterno;
    @FXML private TextField txtNombre;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Estudiante estudiante; // Para edición

    @FXML
    public void initialize() {
        btnGuardar.setOnAction(e -> guardar());
        btnCancelar.setOnAction(e -> cerrar());
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
        if (estudiante != null) {
            txtRU.setText(estudiante.getRu());
            txtCI.setText(estudiante.getCi());
            txtAPaterno.setText(estudiante.getAPaterno());
            txtAMaterno.setText(estudiante.getAMaterno());
            txtNombre.setText(estudiante.getNombre());
        }
    }

    @FXML
    private void guardar() {
        String ru = txtRU.getText().trim();
        String ci = txtCI.getText().trim();
        String aPaterno = txtAPaterno.getText().trim();
        String aMaterno = txtAMaterno.getText().trim();
        String nombre = txtNombre.getText().trim();

        if (ru.isEmpty() || ci.isEmpty() || aPaterno.isEmpty() || aMaterno.isEmpty() || nombre.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try (Connection conn = Database.getConnection()) {

            // Validar RU
            if (existeRU(conn, ru)) {
                mostrarAlerta("Error", "El RU ingresado ya existe.");
                return;
            }

            // Validar CI
            if (existeCI(conn, ci)) {
                mostrarAlerta("Error", "El CI ingresado ya existe.");
                return;
            }

            if (estudiante == null) {
                // Nuevo estudiante
                String sql = "INSERT INTO Estudiantes (ru, ci, aPaterno, aMaterno, nombre) VALUES (?, ?, ?, ?, ?)";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, ru);
                ps.setString(2, ci);
                ps.setString(3, aPaterno);
                ps.setString(4, aMaterno);
                ps.setString(5, nombre);
                ps.executeUpdate();
                mostrarAlertaInfo("Éxito", "Estudiante agregado correctamente.");
            } else {
                // Editar existente
                String sql = "UPDATE Estudiantes SET ru=?, ci=?, aPaterno=?, aMaterno=?, nombre=? WHERE id=?";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setString(1, ru);
                ps.setString(2, ci);
                ps.setString(3, aPaterno);
                ps.setString(4, aMaterno);
                ps.setString(5, nombre);
                ps.setInt(6, estudiante.getId());
                ps.executeUpdate();
                mostrarAlertaInfo("Éxito", "Estudiante actualizado correctamente.");
            }

            cerrar(); // cerrar formulario automáticamente

        } catch (SQLException ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al guardar el estudiante.");
        }
    }

    private boolean existeRU(Connection conn, String ru) throws SQLException {
        if (estudiante != null && ru.equals(estudiante.getRu())) return false;
        String sql = "SELECT COUNT(*) AS total FROM Estudiantes WHERE ru=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ru);
            var rs = ps.executeQuery();
            return rs.next() && rs.getInt("total") > 0;
        }
    }

    private boolean existeCI(Connection conn, String ci) throws SQLException {
        if (estudiante != null && ci.equals(estudiante.getCi())) return false;
        String sql = "SELECT COUNT(*) AS total FROM Estudiantes WHERE ci=?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ci);
            var rs = ps.executeQuery();
            return rs.next() && rs.getInt("total") > 0;
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void cerrar() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }
}
