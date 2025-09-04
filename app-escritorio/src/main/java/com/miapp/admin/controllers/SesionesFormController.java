package com.miapp.admin.controllers;

import com.miapp.admin.database.Database;
import com.miapp.admin.models.Sesion;
import com.miapp.admin.utils.QRGenerator;
import com.google.zxing.WriterException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

public class SesionesFormController {

    @FXML private DatePicker dpFecha;
    @FXML private TextField txtDescripcion;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private Sesion sesionActual = null; // Para edición

    @FXML
    public void initialize() {
        btnGuardar.setOnAction(e -> {
            if (sesionActual == null) {
                guardarSesion(); // Crear nueva sesión
            } else {
                actualizarSesion(); // Editar sesión existente
            }
        });
        btnCancelar.setOnAction(e -> cerrarVentana());
    }

    // Cargar sesión existente para edición
    public void cargarSesion(Sesion sesion) {
        this.sesionActual = sesion;
        dpFecha.setValue(LocalDate.parse(sesion.getFecha())); // Convertir String a LocalDate
        txtDescripcion.setText(sesion.getDescripcion());
    }

    // Crear nueva sesión
    private void guardarSesion() {
        if (dpFecha.getValue() == null || txtDescripcion.getText().isEmpty()) {
            mostrarAlerta("Error", "Completa todos los campos");
            return;
        }

        String fecha = dpFecha.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String descripcion = txtDescripcion.getText();
        String tokenQR = UUID.randomUUID().toString();
        String fechaExpiracionQR = LocalDateTime.now().plusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        try (Connection conn = Database.getConnection()) {
            // Insertar nueva sesión
            String sqlSesion = "INSERT INTO Sesiones (descripcion, token_qr, fecha, fecha_expiracion_qr) VALUES (?, ?, ?, ?)";
            PreparedStatement psSesion = conn.prepareStatement(sqlSesion, PreparedStatement.RETURN_GENERATED_KEYS);
            psSesion.setString(1, descripcion);
            psSesion.setString(2, tokenQR);
            psSesion.setString(3, fecha);
            psSesion.setString(4, fechaExpiracionQR);
            psSesion.executeUpdate();

            // Obtener ID de la sesión insertada
            ResultSet rs = psSesion.getGeneratedKeys();
            int idSesion = 0;
            if (rs.next()) {
                idSesion = rs.getInt(1);
            }

            // Crear carpeta 'qr' si no existe
            File carpetaQR = new File("qr");
            if (!carpetaQR.exists()) carpetaQR.mkdirs();

            // Generar QR
            String rutaQR = "qr/qr_" + idSesion + ".png";
            QRGenerator.generarQR(tokenQR, rutaQR);

            cerrarVentana();

        } catch (SQLException | WriterException | IOException ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al guardar la sesión o generar el QR.");
        }
    }

    // Editar sesión existente
    private void actualizarSesion() {
        if (dpFecha.getValue() == null || txtDescripcion.getText().isEmpty()) {
            mostrarAlerta("Error", "Completa todos los campos");
            return;
        }

        String fecha = dpFecha.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String descripcion = txtDescripcion.getText();

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE Sesiones SET descripcion = ?, fecha = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, descripcion);
            ps.setString(2, fecha);
            ps.setInt(3, sesionActual.getId());
            ps.executeUpdate();

            cerrarVentana();

        } catch (SQLException ex) {
            ex.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al actualizar la sesión.");
        }
    }

    // Cerrar ventana
    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    // Mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
