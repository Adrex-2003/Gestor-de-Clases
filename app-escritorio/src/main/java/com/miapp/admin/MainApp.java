package com.miapp.admin;

import com.miapp.admin.database.Database;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application{
    @Override
    public void start(Stage primaryStage) throws Exception {
        Database.getConnection().close();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Dashboard.fxml"));
        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(getClass().getResource("/css/Style.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Sistema de Gestión de Asistencia");
        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
