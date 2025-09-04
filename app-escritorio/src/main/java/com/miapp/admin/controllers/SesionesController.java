package com.miapp.admin.controllers;

import com.miapp.admin.database.Database;
import com.miapp.admin.models.Sesion;
import com.miapp.admin.utils.QRGenerator;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SesionesController {

    // Tabla de sesiones
    @FXML
    private TableView<Sesion> tablaSesiones;
    @FXML
    private TableColumn<Sesion, Integer> colId;
    @FXML
    private TableColumn<Sesion, String> colFecha;
    @FXML
    private TableColumn<Sesion, String> colDescripcion;
    @FXML
    private TableColumn<Sesion, Void> colAcciones;
    @FXML
    private Button btnNuevaSesion;

    private final ObservableList<Sesion> sesiones = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configurar columnas de sesiones
        colId.setCellValueFactory(data -> data.getValue().idProperty().asObject());
        colFecha.setCellValueFactory(data -> data.getValue().fechaProperty());
        colDescripcion.setCellValueFactory(data -> data.getValue().descripcionProperty());

        // Agregar botones de acción (Ver / QR)
        agregarBotonesAcciones();

        // Cargar sesiones desde la base de datos
        cargarSesiones();

        // Abrir formulario para nueva sesión
        btnNuevaSesion.setOnAction(e -> abrirNuevaSesion());
    }

    private void cargarSesiones() {
        sesiones.clear();
        String sql = "SELECT id, fecha, descripcion, token_qr FROM Sesiones ORDER BY fecha DESC";

        try (Connection conn = Database.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sesion s = new Sesion(
                        rs.getInt("id"),
                        rs.getString("fecha"),
                        rs.getString("descripcion"),
                        rs.getString("token_qr"));
                sesiones.add(s);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        tablaSesiones.setItems(sesiones);
    }

    private void agregarBotonesAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnVer = new Button();
            private final Button btnQR = new Button();
            private final Button btnEditar = new Button();
            private final Button btnEliminar = new Button();

            {
                ImageView iconVer = new ImageView(new Image(getClass().getResourceAsStream("/icons/eye.png")));
                ImageView iconQR = new ImageView(new Image(getClass().getResourceAsStream("/icons/qrcode.png")));
                ImageView iconEditar = new ImageView(new Image(getClass().getResourceAsStream("/icons/pencil.png")));
                ImageView iconEliminar = new ImageView(new Image(getClass().getResourceAsStream("/icons/trash.png")));

                // Tamaño de los iconos
                iconVer.setFitWidth(15);
                iconVer.setFitHeight(15);
                iconQR.setFitWidth(15);
                iconQR.setFitHeight(15);
                iconEditar.setFitWidth(15);
                iconEditar.setFitHeight(15);
                iconEliminar.setFitWidth(15);
                iconEliminar.setFitHeight(15);

                // Asignar iconos a botones
                btnVer.setGraphic(iconVer);
                btnQR.setGraphic(iconQR);
                btnEditar.setGraphic(iconEditar);
                btnEliminar.setGraphic(iconEliminar);

                // Tooltips
                btnVer.setTooltip(new Tooltip("Ver estudiantes"));
                btnQR.setTooltip(new Tooltip("Ver QR"));
                btnEditar.setTooltip(new Tooltip("Editar sesión"));
                btnEliminar.setTooltip(new Tooltip("Eliminar sesión"));

                // Acciones
                btnVer.setOnAction(event -> {
                    Sesion sesion = getTableView().getItems().get(getIndex());
                    abrirVentanaEstudiantes(sesion.getId());
                });
                btnQR.setOnAction(event -> {
                    Sesion sesion = getTableView().getItems().get(getIndex());
                    mostrarQR(sesion);
                });
                btnEditar.setOnAction(event -> {
                    Sesion sesion = getTableView().getItems().get(getIndex());
                    abrirEditarSesion(sesion);
                });
                btnEliminar.setOnAction(event -> {
                    Sesion sesion = getTableView().getItems().get(getIndex());
                    eliminarSesion(sesion);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(5, btnVer, btnQR, btnEditar, btnEliminar);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void mostrarQR(Sesion sesion) {
        try {
            String carpetaQR = "./qrs/";
            File dir = new File(carpetaQR);
            if (!dir.exists())
                dir.mkdirs();

            String rutaQR = carpetaQR + "qr_" + sesion.getId() + ".png";
            File archivoQR = new File(rutaQR);

            if (!archivoQR.exists()) {
                QRGenerator.generarQR(sesion.getTokenQr(), rutaQR);
            }

            Image qrImage = new Image(archivoQR.toURI().toString());
            ImageView iv = new ImageView(qrImage);
            iv.setFitWidth(250);
            iv.setFitHeight(250);
            StackPane root = new StackPane(iv);

            Stage stage = new Stage();
            stage.setScene(new Scene(root, 300, 300));
            stage.setTitle("QR Sesión #" + sesion.getId());
            stage.show();

        } catch (WriterException | IOException e) {
            e.printStackTrace();
        }
    }

    private void abrirNuevaSesion() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SesionesForm.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            // Aquí enlazamos el CSS
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Nueva Sesión");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarSesiones(); // recargar tabla al cerrar
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void abrirVentanaEstudiantes(int idSesion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/ListaAsistencias.fxml"));
            Parent root = loader.load();

            ListaAsistenciaController controller = loader.getController();
            controller.setIdSesion(idSesion); // Pasar id de sesión

            Stage stage = new Stage();

            Scene scene = new Scene(root);

            // Enlazamos el CSS
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);

            stage.setTitle("Estudiantes - Sesión #" + idSesion);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void eliminarSesion(Sesion sesion) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Está seguro de que desea eliminar la sesión #" + sesion.getId() + "?",
                ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                String sql = "DELETE FROM Sesiones WHERE id = ?";
                try (Connection conn = Database.getConnection();
                        PreparedStatement ps = conn.prepareStatement(sql)) {

                    ps.setInt(1, sesion.getId());
                    int filas = ps.executeUpdate();

                    if (filas > 0) {
                        Alert info = new Alert(Alert.AlertType.INFORMATION,
                                "Sesión eliminada correctamente.", ButtonType.OK);
                        info.showAndWait();
                        cargarSesiones(); // recargar tabla
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert error = new Alert(Alert.AlertType.ERROR,
                            "Error al eliminar la sesión.", ButtonType.OK);
                    error.showAndWait();
                }
            }
        });
    }

    private void abrirEditarSesion(Sesion sesion) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SesionesForm.fxml"));
            Parent root = loader.load();

            SesionesFormController controller = loader.getController();
            controller.cargarSesion(sesion); // PASAMOS LA SESIÓN A EDITAR

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            // Enlazamos el CSS
            scene.getStylesheets().add(getClass().getResource("/css/style.css").toExternalForm());

            stage.setScene(scene);
            stage.setTitle("Editar Sesión #" + sesion.getId());
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            cargarSesiones(); // recargar tabla al cerrar
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

}
