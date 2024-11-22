package com.Client;

import com.Server.ServerInterface;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.rmi.RemoteException;

public abstract class AbstractVentana implements Initializable {

    // Referencia al servidor
    private ServerInterface server;
    public void setServer(ServerInterface server) {
        this.server = server;
    }
    public ServerInterface getServer() {
        return server;
    }

    // Referencia al cliente
    private Client client;
    public void setClient(Client client) {
        this.client = client;
    }
    public Client getClient() {
        return client;
    }


    public void recargar(Stage stage, String string) {
        Platform.runLater(() -> {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(string));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // Carga el stage
            stage.setScene(scene);
            stage.show();

            // Pasa la instancia del servidor
            PrincipalController controller = fxmlLoader.getController();
            controller.setServer(this.getServer());
            this.setClient(client);
            controller.setClient(client);
            fxmlLoader.setController(controller);
        });
    }


    public void handleWindowClose() throws RemoteException {
        // Cuando la ventana se cierra, se establece setOnline a false
        if (this.getClient() != null && this.getClient().getInfo() != null) {
            this.getClient().getInfo().setOnline(false);  // Establece el estado de "online" a false
            this.getServer().actualizarClienteInfo(this.getClient().getInfo());
            this.getClient().cerrarConexion();
        }
    }

}
