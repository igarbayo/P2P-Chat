package com.Client;

import com.Server.ServerInterface;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class PrincipalController extends AbstractVentana {

    // Elementos gráficos
    @FXML
    private ListView<String> listaAmigos;
    @FXML
    private ListView<HBox> listaSolicitudes;
    @FXML
    private Button botonEnviar;
    @FXML
    private Button botonAbandonar;

    // Disposición inicial
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        try {
            // Manejo de lista de amigos
            List<String> list;
            List<ClientInfo> amigos = null;
            if (this.getServer() != null && this.getClient() != null) {
                amigos = this.getServer().obtenerAmigos(this.getClient());
            }
            if (amigos != null) {
                list = this.getClient().obtenerNombresDeUsuario(amigos);
            } else {
                list = new ArrayList<>(); // Si obtenerAmigos devuelve null, asigna una lista vacía.
            }
            listaAmigos.setItems(FXCollections.observableArrayList(list));

            // Manejo de lista de solicitudes
            if (this.getClient() != null) {
                List<ClientInfo> solicitudes;
                solicitudes = this.getClient().getInfo().getListaSolicitudes();
                for (ClientInfo clientInfo : solicitudes) {
                    HBox hbox = new HBox();
                    Label label = new Label(clientInfo.getUsuario());
                    Button acceptButton = new Button("✔");
                    Button rejectButton = new Button("❌");

                    // Asignar acciones a los botones
                    acceptButton.setOnAction(event -> {
                        this.getClient().aceptarSolicitudAmistad(clientInfo);
                    });
                    rejectButton.setOnAction(event -> {
                        this.getClient().rechazarSolicitudAmistad(clientInfo);
                    });
                    hbox.getChildren().addAll(label, acceptButton, rejectButton);
                    listaSolicitudes.getItems().add(hbox);
                }
            } else {
                listaSolicitudes.setItems(FXCollections.observableList(new ArrayList<>()));
            }

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }


    // Botones
    @FXML
    public void onEnviar(ActionEvent event) {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Solicitud-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Crear un nuevo Stage para la nueva ventana
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.show();

            // Pasa la instancia del servidor y del cliente al controlador de la nueva ventana
            SolicitudController controller = fxmlLoader.getController();
            controller.setServer(this.getServer());
            controller.setClient(this.getClient());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @FXML
    public void onAbandonar(ActionEvent event) {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Abandonar-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Crear un nuevo Stage para la nueva ventana
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.show();

            // Pasa la instancia del servidor y del cliente al controlador de la nueva ventana
            AbandonarController controller = fxmlLoader.getController();
            controller.setServer(this.getServer());
            controller.setClient(this.getClient());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

}
