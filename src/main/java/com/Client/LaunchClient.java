package com.Client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class LaunchClient extends Application {
    ConexionController conexionControlador;
    InicioController inicioControlador;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader conexionLoader = new FXMLLoader(getClass().getResource("ConexionCliente-view.fxml"));
        Scene conexionScene= new Scene(conexionLoader.load());
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
