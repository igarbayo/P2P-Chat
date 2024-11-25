package com.Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LogoutController extends AbstractVentana{

    @FXML
    private Button botonSi;
    @FXML
    private Button botonNo;

    // Para cambiar la ventana anterior
    private Stage oldStage;
    public Stage getOldStage() {
        return oldStage;
    }
    public void setOldStage(Stage oldStage) {
        this.oldStage = oldStage;
    }

    // Constructor
    public LogoutController() {
        this.oldStage = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        Platform.runLater(() -> {
            System.out.println("LOGOUT");
            System.out.println(this.getClient().getInfo());
        });
    }

    @FXML
    public void onSi(ActionEvent actionEvent) {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InicioCliente-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            oldStage.setScene(scene);
            oldStage.show();

            // Obtener el Stage actual a partir del control (ej. un botón)
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close(); // Cierra el Stage

            // Pasa la instancia del servidor y del cliente al controlador de la nueva ventana
            InicioController controller = fxmlLoader.getController();
            controller.setServer(this.getServer());
            controller.setClient(this.getClient());
            controller.setIP(this.getClient().getIP());
            controller.setPuerto(this.getClient().getPuerto());

            this.getClient().cerrarConexion();

            // Ponemos vacia la ClientInfo de la instancia de Client actual
            this.getClient().getInfo().setOnline(false);
            this.getServer().actualizarClienteInfo(this.getClient().getInfo());
            this.getClient().setInfo(null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onNo(ActionEvent actionEvent) {
        Stage stage = (Stage) botonNo.getScene().getWindow(); // Obtener el Stage actual
        stage.close(); // Cerrar la ventana
    }
}
