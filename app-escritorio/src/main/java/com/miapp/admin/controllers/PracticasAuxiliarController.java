package com.miapp.admin.controllers;

import com.miapp.admin.models.Practica;
import com.miapp.admin.database.Database;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class PracticasAuxiliarController {

    @FXML private TableView<Practica> tablaPracticas;
    @FXML private TableColumn<Practica, Integer> colId;
    @FXML private TableColumn<Practica, String> colPractica;
    @FXML private TableColumn<Practica, String> colFechaInicio;
    @FXML private TableColumn<Practica, String> colFechaFin;
    @FXML private TableColumn<Practica, String> colDescripcion;
    @FXML private TableColumn<Practica, Void> colAcciones;
    @FXML private Button btnNuevaPractica;

    private ObservableList<Practica> listaPracticas = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        cargarColumnas();
        cargarPracticas();
        agregarBotonesAcciones();
    }

    private void cargarColumnas() {
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colPractica.setCellValueFactory(data -> data.getValue().practicaProperty());
        colFechaInicio.setCellValueFactory(data -> data.getValue().fechaInicioProperty());
        colFechaFin.setCellValueFactory(data -> data.getValue().fechaFinProperty());
        colDescripcion.setCellValueFactory(data -> data.getValue().descripcionProperty());
    }

    private void cargarPracticas() {
        listaPracticas.clear();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM Practicas WHERE tipo = 'AUXILIAR'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Practica p = new Practica(
                        rs.getInt("id"),
                        rs.getString("practica"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_fin"),
                        rs.getString("descripcion")
                );
                listaPracticas.add(p);
            }
            tablaPracticas.setItems(listaPracticas);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirNuevaPractica() {
        abrirFormulario(null);
    }

    private void abrirEditarPractica(Practica practica) {
        abrirFormulario(practica);
    }

    private void abrirFormulario(Practica practica) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/PracticaForm.fxml"));
            VBox root = loader.load();

            PracticasFormController controller = loader.getController();
            controller.setPractica(practica);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle(practica == null ? "Nueva Práctica" : "Editar Práctica");
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Recargar tabla al cerrar
            cargarPracticas();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void agregarBotonesAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnEditar.setOnAction(event -> {
                    Practica practica = getTableView().getItems().get(getIndex());
                    abrirEditarPractica(practica);
                });

                btnEliminar.setOnAction(event -> {
                    Practica practica = getTableView().getItems().get(getIndex());
                    eliminarPractica(practica);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, btnEditar, btnEliminar);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void eliminarPractica(Practica practica) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText(null);
        alert.setContentText("¿Desea eliminar la práctica: " + practica.getPractica() + "?");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try (Connection conn = Database.getConnection()) {
                String sql = "DELETE FROM Practicas WHERE id = ?";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setInt(1, practica.getId());
                stmt.executeUpdate();
                cargarPracticas();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
