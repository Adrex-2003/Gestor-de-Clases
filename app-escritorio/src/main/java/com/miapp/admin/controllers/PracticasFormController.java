package com.miapp.admin.controllers;

import com.miapp.admin.models.Practica;
import com.miapp.admin.database.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.time.LocalDate;

public class PracticasFormController {

    @FXML
    private TextField txtPractica;
    @FXML
    private DatePicker dpInicio;
    @FXML
    private DatePicker dpFin;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private Button btnGuardar;

    private Practica practica;
    private String tipoPractica;

    public void setPractica(Practica practica) {
        this.practica = practica;
        if (practica != null) {
            txtPractica.setText(practica.getPractica());
            dpInicio.setValue(LocalDate.parse(practica.getFechaInicio()));
            dpFin.setValue(LocalDate.parse(practica.getFechaFin()));
            txtDescripcion.setText(practica.getDescripcion());
        }
    }

    public void setTipoPractica(String tipoPractica){
        this.tipoPractica = tipoPractica;
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
            conn.setAutoCommit(false); // Iniciar la transacción

            if (practica == null) {
                // Insertar nueva práctica
                String sqlInsertPractica = "INSERT INTO Practicas (tipo, titulo, fecha_inicio, fecha_fin, descripcion) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlInsertPractica, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setString(1, tipoPractica);
                    stmt.setString(2, nombre);
                    stmt.setString(3, inicio.toString());
                    stmt.setString(4, fin.toString());
                    stmt.setString(5, descripcion);
                    int affectedRows = stmt.executeUpdate();

                    if (affectedRows == 0) {
                        throw new SQLException("Error al crear la práctica, no se afectaron filas.");
                    }

                    // Obtener el ID de la práctica recién creada
                    int nuevaPracticaId = -1;
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            nuevaPracticaId = generatedKeys.getInt(1);
                        } else {
                            throw new SQLException("Error al obtener el ID de la práctica, no se obtuvo un ID generado.");
                        }
                    }

                    // Insertar notas iniciales (0.0) para todos los estudiantes
                    String sqlSelectEstudiantes = "SELECT id FROM Estudiantes";
                    try (Statement stmtEstudiantes = conn.createStatement();
                         ResultSet rsEstudiantes = stmtEstudiantes.executeQuery(sqlSelectEstudiantes)) {

                        String sqlInsertNota = "INSERT INTO NotasPracticas (practica_id, estudiante_id, nota) VALUES (?, ?, ?)";
                        try (PreparedStatement stmtNotas = conn.prepareStatement(sqlInsertNota)) {
                            while (rsEstudiantes.next()) {
                                int estudianteId = rsEstudiantes.getInt("id");
                                stmtNotas.setInt(1, nuevaPracticaId);
                                stmtNotas.setInt(2, estudianteId);
                                stmtNotas.setDouble(3, 0.0);
                                stmtNotas.addBatch(); // Agregar al lote para inserción masiva
                            }
                            stmtNotas.executeBatch(); // Ejecutar todas las inserciones
                        }
                    }
                }
            } else {
                // Editar práctica existente
                String sql = "UPDATE Practicas SET titulo = ?, fecha_inicio = ?, fecha_fin = ?, descripcion = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, nombre);
                    stmt.setString(2, inicio.toString());
                    stmt.setString(3, fin.toString());
                    stmt.setString(4, descripcion);
                    stmt.setInt(5, practica.getId());
                    stmt.executeUpdate();
                }
            }

            conn.commit(); // Confirmar la transacción
            cerrarVentana();

        } catch (SQLException e) {
            try (Connection conn = Database.getConnection()) {
                conn.rollback(); // Revertir la transacción en caso de error
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
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