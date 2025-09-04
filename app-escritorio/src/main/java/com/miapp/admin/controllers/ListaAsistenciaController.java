package com.miapp.admin.controllers;

import com.miapp.admin.database.Database;
import com.miapp.admin.models.Estudiante;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ListaAsistenciaController {

    @FXML
    private TableView<Estudiante> tablaEstudiantes;
    @FXML
    private TableColumn<Estudiante, String> colAPaterno;
    @FXML
    private TableColumn<Estudiante, String> colAMaterno;
    @FXML
    private TableColumn<Estudiante, String> colNombres;
    @FXML
    private TableColumn<Estudiante, String> colAsistencia;

    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCerrar;

    private final ObservableList<Estudiante> estudiantes = FXCollections.observableArrayList();
    private int idSesion; // id de la sesión actual

    public void setIdSesion(int idSesion) {
        this.idSesion = idSesion;
        cargarEstudiantes();
    }

    @FXML
    public void initialize() {
        // Configuración de columnas
        colAPaterno.setCellValueFactory(data -> data.getValue().aPaternoProperty());
        colAMaterno.setCellValueFactory(data -> data.getValue().aMaternoProperty());
        colNombres.setCellValueFactory(data -> data.getValue().nombreProperty());

        // Columna editable para asistencia
        colAsistencia.setCellValueFactory(data -> data.getValue().ciProperty());
        colAsistencia.setCellFactory(ComboBoxTableCell.forTableColumn("Presente", "Ausente"));
        colAsistencia.setEditable(true);

        tablaEstudiantes.setItems(estudiantes);
        tablaEstudiantes.setEditable(true);

        // Botones
        btnGuardar.setOnAction(e -> guardarCambios());
        btnCerrar.setOnAction(e -> cerrarVentana());
    }

    private void cargarEstudiantes() {
        estudiantes.clear();
        String sql = "SELECT e.id, e.aPaterno, e.aMaterno, e.nombre, " +
                     "CASE WHEN a.presente = 1 THEN 'Presente' ELSE 'Ausente' END AS asistencia " +
                     "FROM Estudiantes e " +
                     "LEFT JOIN Asistencias a ON e.id = a.id_estudiante AND a.id_sesion = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idSesion);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Estudiante est = new Estudiante(
                        rs.getInt("id"),
                        "", "", // ru y ci pueden ir vacíos
                        rs.getString("aPaterno"),
                        rs.getString("aMaterno"),
                        rs.getString("nombre")
                );
                est.setCi(rs.getString("asistencia"));
                estudiantes.add(est);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void guardarCambios() {
    String sqlUpsert = "INSERT INTO Asistencias (id_estudiante, id_sesion, presente) " +
                       "VALUES (?, ?, ?) " +
                       "ON CONFLICT(id_estudiante, id_sesion) " +
                       "DO UPDATE SET presente = excluded.presente";

    try (Connection conn = Database.getConnection();
         PreparedStatement ps = conn.prepareStatement(sqlUpsert)) {

        for (Estudiante est : estudiantes) {
            ps.setInt(1, est.getId());
            ps.setInt(2, idSesion);
            ps.setInt(3, est.getCi().equals("Presente") ? 1 : 0);
            ps.addBatch();
        }
        ps.executeBatch();

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Asistencias guardadas correctamente.", ButtonType.OK);
        alert.showAndWait();

    } catch (SQLException e) {
        e.printStackTrace();
        Alert alert = new Alert(Alert.AlertType.ERROR, "Error al guardar asistencias.", ButtonType.OK);
        alert.showAndWait();
    }
}


    private void cerrarVentana() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }
}
