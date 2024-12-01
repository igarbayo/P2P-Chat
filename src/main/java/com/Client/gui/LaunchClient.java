// P2P. Computación Distribuida
// Curso 2024 - 2025
// Ignacio Garbayo y Carlos Hermida

package com.Client.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LaunchClient extends Application {
    ConexionController conexionControlador;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader conexionLoader = new FXMLLoader(getClass().getResource("ConexionCliente-view.fxml"));
        Scene conexionScene= new Scene(conexionLoader.load());

        // CSS
        conexionScene.getStylesheets().add(getClass().getResource("/styles/basic.css").toExternalForm());
        conexionScene.getStylesheets().add(getClass().getResource("/styles/button.css").toExternalForm());
        conexionScene.getStylesheets().add(getClass().getResource("/styles/colors.css").toExternalForm());
        conexionScene.getStylesheets().add(getClass().getResource("/styles/list-view.css").toExternalForm());
        conexionScene.getStylesheets().add(getClass().getResource("/styles/text-area.css").toExternalForm());
        conexionScene.getStylesheets().add(getClass().getResource("/styles/text-field.css").toExternalForm());

        Stage conexionStage = new Stage();
        conexionStage.setScene(conexionScene);
        conexionStage.setResizable(false);
        conexionStage.setOnCloseRequest(event -> {
            System.out.println("Se ha cerrado la ventana de establecimiento de conexión");
            System.exit(0);
        });
        conexionControlador = conexionLoader.getController();
        conexionStage.show();

    }
    public static void main(String[] args) {
        launch();
    }
}
