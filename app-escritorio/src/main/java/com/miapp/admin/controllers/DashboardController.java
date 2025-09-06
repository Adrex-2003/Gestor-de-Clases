package com.miapp.admin.controllers;

import com.miapp.admin.database.Database;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.Parent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardController {

    // -------------------------------
    // CARDS
    // -------------------------------
    @FXML
    private Label lblTotalEstudiantes;
    @FXML
    private Label lblTotalSesiones;
    @FXML
    private Label lblTotalPracticaA;

    @FXML
    private Label lblTotalPracticaP;

    @FXML
    private Label lblTotalParticipaciones;

    // -------------------------------
    // CONTENEDOR CENTRAL
    // -------------------------------
    @FXML
    private StackPane contentArea;
    @FXML
    private VBox homePane; // panel de inicio con los cards

    @FXML
    public void initialize() {
        actualizarCards();
        mostrarInicio(); // al iniciar, mostrar cards
    }

    // -------------------------------
    // BOTONES DEL DASHBOARD
    // -------------------------------

    @FXML
    private void mostrarInicio() {
        contentArea.getChildren().clear();
        contentArea.getChildren().add(homePane);
        actualizarCards();
    }

    @FXML
    private void cargarEstudiantesEnCentro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Estudiantes.fxml"));
            Parent estudiantesPane = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(estudiantesPane);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cargarSesionesEnCentro() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Sesiones.fxml"));
            Parent sesionesPane = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(sesionesPane);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    
    // Método público para cargar las prácticas del auxiliar
    @FXML
    private void abrirPracticasAuxiliar() {
        cargarPracticas("AUXILIAR");
    }

    // Método público para cargar las prácticas del docente
    @FXML
    private void abrirPracticasDocente() {
        cargarPracticas("DOCENTE");
    }

    // Método privado y reutilizable para cargar la ventana de prácticas
    private void cargarPracticas(String tipoPractica) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Practicas.fxml"));
            Parent practicasPane = loader.load();
            PracticasController controller = loader.getController();
            controller.setTipoPractica(tipoPractica);

            contentArea.getChildren().clear();
            contentArea.getChildren().add(practicasPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void salir() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Desea salir de la aplicación?", ButtonType.YES, ButtonType.NO);
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                System.exit(0);
            }
        });
    }

    private void actualizarCards() {
        lblTotalEstudiantes.setText(String.valueOf(obtenerTotal("Estudiantes")));
        lblTotalSesiones.setText(String.valueOf(obtenerTotal("Sesiones")));
        lblTotalPracticaA.setText(String.valueOf(obtenerTotal("Practicas")));
        lblTotalPracticaP.setText(String.valueOf(obtenerTotal("Practicas")));
        lblTotalParticipaciones.setText(String.valueOf(obtenerTotal("Participaciones")));
        
    }

    private int obtenerTotal(String tabla) {
        String sql = "SELECT COUNT(*) AS total FROM " + tabla;
        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            if (rs.next()) {
                return rs.getInt("total");
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return 0;
    }
}
