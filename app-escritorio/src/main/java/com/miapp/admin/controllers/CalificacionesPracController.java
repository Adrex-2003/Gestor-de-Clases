package com.miapp.admin.controllers;

import com.miapp.admin.database.Database;
import com.miapp.admin.models.EstudianteCalificacion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.converter.DoubleStringConverter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.Predicate;

public class CalificacionesPracController {

    @FXML private TextField txtBuscador;
    @FXML private TableView<EstudianteCalificacion> tableView;
    @FXML private TableColumn<EstudianteCalificacion, String> colRU;
    @FXML private TableColumn<EstudianteCalificacion, String> colApellidoPaterno;
    @FXML private TableColumn<EstudianteCalificacion, String> colApellidoMaterno;
    @FXML private TableColumn<EstudianteCalificacion, String> colNombre;
    @FXML private TableColumn<EstudianteCalificacion, Double> colNota;
    @FXML private Label lblMensaje;
    @FXML private Label lblTitulo;

    private int practicaId;
    private ObservableList<EstudianteCalificacion> listaEstudiantes = FXCollections.observableArrayList();
    private FilteredList<EstudianteCalificacion> filteredList;

    public void setPracticaInfo(int id, String titulo) {
        this.practicaId = id;
        lblTitulo.setText("Calificación de Práctica: " + titulo);
        loadEstudiantesYNotas();
    }

    @FXML
    public void initialize() {
        // Configurar las factorías de valor para cada columna
        colRU.setCellValueFactory(new PropertyValueFactory<>("ru"));
        colApellidoPaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoPaterno"));
        colApellidoMaterno.setCellValueFactory(new PropertyValueFactory<>("apellidoMaterno"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colNota.setCellValueFactory(new PropertyValueFactory<>("nota"));

        // Hacer la columna de calificación editable
        colNota.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colNota.setOnEditCommit(event -> {
            EstudianteCalificacion estudiante = event.getRowValue();
            Double nuevaNota = event.getNewValue();

            if (nuevaNota != null && nuevaNota >= 0 && nuevaNota <= 100) {
                estudiante.setNota(nuevaNota);
                // Guardar la nueva calificación en la base de datos
                saveNota(estudiante.getEstudianteId(), nuevaNota);
                lblMensaje.setTextFill(Color.web("#02ec0aff"));
                lblMensaje.setText("Nota guardada.");
            } else {
                lblMensaje.setTextFill(Color.RED);
                lblMensaje.setText("Error: La calificación debe estar entre 0 y 100.");
                // Revertir el valor de la celda al valor anterior
                tableView.refresh();
            }
        });

        // Configurar la lista filtrada para la búsqueda
        filteredList = new FilteredList<>(listaEstudiantes, p -> true);
        tableView.setItems(filteredList);

        // Listener para el campo de búsqueda
        txtBuscador.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(createPredicate(newValue));
        });
    }

    private void loadEstudiantesYNotas() {
        listaEstudiantes.clear();
        String sql = "SELECT e.id, e.ru, e.aPaterno, e.aMaterno, e.nombre, np.nota " +
                     "FROM Estudiantes e " +
                     "LEFT JOIN NotasPracticas np ON e.id = np.estudiante_id AND np.practica_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, practicaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int estudianteId = rs.getInt("id");
                String ru = rs.getString("ru");
                String paterno = rs.getString("aPaterno");
                String materno = rs.getString("aMaterno");
                String nombre = rs.getString("nombre");
                // La nota puede ser null si no hay un registro, por eso usamos Double
                Double nota = rs.getObject("nota") != null ? rs.getDouble("nota") : 0.0;

                listaEstudiantes.add(new EstudianteCalificacion(estudianteId, ru, paterno, materno, nombre, nota));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error de Carga", "No se pudieron cargar los datos de estudiantes y notas.");
        }
    }

    private void saveNota(int estudianteId, double nota) {
        // Intentar actualizar la nota si ya existe un registro
        String updateSql = "UPDATE NotasPracticas SET nota = ? WHERE estudiante_id = ? AND practica_id = ?";
        // Si no se actualizó, insertar un nuevo registro
        String insertSql = "INSERT INTO NotasPracticas (estudiante_id, practica_id, nota) VALUES (?, ?, ?)";

        try (Connection conn = Database.getConnection()) {
            // 1. Intentar actualizar el registro
            try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                updateStmt.setDouble(1, nota);
                updateStmt.setInt(2, estudianteId);
                updateStmt.setInt(3, practicaId);
                int filasAfectadas = updateStmt.executeUpdate();

                // 2. Si no se actualizó ninguna fila, significa que no existe el registro,
                //    así que procedemos a insertarlo.
                if (filasAfectadas == 0) {
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                        insertStmt.setInt(1, estudianteId);
                        insertStmt.setInt(2, practicaId);
                        insertStmt.setDouble(3, nota);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            lblMensaje.setText("Error al guardar: " + e.getMessage());
        }
    }

    private Predicate<EstudianteCalificacion> createPredicate(String searchText) {
        return estudiante -> {
            if (searchText == null || searchText.trim().isEmpty()) {
                return true;
            }
            String lowerCaseFilter = searchText.toLowerCase();
            return estudiante.getApellidoPaterno().toLowerCase().contains(lowerCaseFilter) ||
                   estudiante.getApellidoMaterno().toLowerCase().contains(lowerCaseFilter) ||
                   estudiante.getNombre().toLowerCase().contains(lowerCaseFilter) ||
                   estudiante.getRu().toLowerCase().contains(lowerCaseFilter);
        };
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void handleCerrar(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}
