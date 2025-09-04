package com.miapp.admin.controllers;

import com.miapp.admin.database.Database;
import com.miapp.admin.models.Estudiante;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class EstudiantesController {

    @FXML private TableView<Estudiante> tableEstudiantes;
    @FXML private TableColumn<Estudiante, String> colRU;
    @FXML private TableColumn<Estudiante, String> colCI;
    @FXML private TableColumn<Estudiante, String> colAPaterno;
    @FXML private TableColumn<Estudiante, String> colAMaterno;
    @FXML private TableColumn<Estudiante, String> colNombre;
    @FXML private Button btnAgregar;
    @FXML private Button btnEditar;
    @FXML private Button btnEliminar;

    private ObservableList<Estudiante> listaEstudiantes = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar columnas
        colRU.setCellValueFactory(data -> data.getValue().ruProperty());
        colCI.setCellValueFactory(data -> data.getValue().ciProperty());
        colAPaterno.setCellValueFactory(data -> data.getValue().aPaternoProperty());
        colAMaterno.setCellValueFactory(data -> data.getValue().aMaternoProperty());
        colNombre.setCellValueFactory(data -> data.getValue().nombreProperty());

        // Botones
        btnAgregar.setOnAction(e -> abrirFormulario(null));
        btnEditar.setOnAction(e -> {
            Estudiante seleccionado = tableEstudiantes.getSelectionModel().getSelectedItem();
            if(seleccionado != null) abrirFormulario(seleccionado);
        });
        btnEliminar.setOnAction(e -> eliminarEstudiante());

        cargarEstudiantes();
    }

    private void cargarEstudiantes() {
        listaEstudiantes.clear();
        try (Connection conn = Database.getConnection()) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM Estudiantes");
            while(rs.next()) {
                Estudiante e = new Estudiante(
                        rs.getInt("id"),
                        rs.getString("ru"),
                        rs.getString("ci"),
                        rs.getString("aPaterno"),
                        rs.getString("aMaterno"),
                        rs.getString("nombre")
                );
                listaEstudiantes.add(e);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        tableEstudiantes.setItems(listaEstudiantes);
    }

    private void abrirFormulario(Estudiante estudiante) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/EstudiantesForm.fxml"));
            Parent root = loader.load();

            EstudianteFormController controller = loader.getController();
            controller.setEstudiante(estudiante);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle(estudiante == null ? "Agregar Estudiante" : "Editar Estudiante");
            stage.getScene().getStylesheets().add(getClass().getResource("/css/Style.css").toExternalForm());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarEstudiantes();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarEstudiante() {
        Estudiante seleccionado = tableEstudiantes.getSelectionModel().getSelectedItem();
        if(seleccionado == null) return;

        try (Connection conn = Database.getConnection()) {
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("DELETE FROM Estudiantes WHERE id = " + seleccionado.getId());
            listaEstudiantes.remove(seleccionado);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}
