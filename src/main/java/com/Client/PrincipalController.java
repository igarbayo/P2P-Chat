package com.Client;

import com.Server.ServerInterface;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

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
    @FXML
    private Button botonLogout;
    @FXML
    private Label usernameLabel;



    // Disposición inicial
    @Override
    public void initialize(URL url, ResourceBundle resources) {

        Platform.runLater(() -> {

            // Configurar un shutdown hook para manejar la desconexión cuando se cierra el programa con SIGINT
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    handleWindowClose();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }));

            // Obtenemos el stage actual
            Stage stage = (Stage) usernameLabel.getScene().getWindow();

            // Configuramos el cierre seguro cuando se pulsa la X
            stage.setOnCloseRequest(event -> {
                try {
                    handleWindowClose();
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            });

            // Debug
            /*if (this.getClient() != null) {
                System.out.println("Cliente en PrincipalController: " + this.getClient().getInfo());
            } else {
                System.out.println("El cliente es null en PrincipalController");
            }*/

            // Mostramos el nombre del usuario conectado
            usernameLabel.setText(this.getClient().getInfo().getUsuario());

            try {
                // Manejo de lista de amigos
                List<String> list;
                List<ClientInfo> amigos = null;
                if (this.getServer() != null && this.getClient() != null) {
                    amigos = this.getServer().obtenerAmigos(this.getClient());
                }

                //System.out.println("Lista de amigos: " + amigos);

                if (amigos != null) {
                    // Construir la lista de nombres con estados
                    list = amigos.stream()
                            .map(amigo -> amigo.getUsuario() + (amigo.isOnline() ? " [Online]" : " [Offline]"))
                            .collect(Collectors.toList()); // Usamos Collectors.toList() aquí
                } else {
                    list = new ArrayList<>(); // Si obtenerAmigos devuelve null, asigna una lista vacía.
                }

                // Asignar los datos a la ListView
                listaAmigos.setItems(FXCollections.observableArrayList(list));


                // Manejo de lista de solicitudes
                if (this.getClient() != null) {
                    List<String> solicitudes;
                    solicitudes = this.getClient().getInfo().getListaSolicitudes();
                    for (String username : solicitudes) {
                        HBox hbox = new HBox();
                        Label label = new Label(username);
                        Button acceptButton = new Button("V");
                        Button rejectButton = new Button("X");

                        // Asignar acción de aceptar
                        acceptButton.setOnAction(event -> {
                            ClientInfo clientInfo = null;
                            try {
                                clientInfo = this.getServer().obtenerClienteInfo(username);
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                            this.getClient().aceptarSolicitudAmistad(clientInfo);
                            try {
                                this.getServer().actualizarClienteInfo(this.getClient().getInfo());
                                // Borrar el resto de solicitudes con el mismo idGrupo de la lista de 'this'
                                for (String usr : this.getClient().getInfo().getListaSolicitudes()) {
                                    ClientInfo info = this.getServer().obtenerClienteInfo(usr);
                                    if (info.getIdGrupo() != null && info.getIdGrupo().equals(clientInfo.getIdGrupo())) {
                                        this.getClient().getInfo().getListaSolicitudes().remove(usr);
                                        this.getServer().actualizarClienteInfo(this.getClient().getInfo());
                                    }
                                }
                                // Actualización de datos en servidor
                                this.getServer().actualizarClienteInfo(this.getClient().getInfo());
                                this.getServer().actualizarClienteInfo(clientInfo);
                                // recargar la ventana gráfica
                                this.recargar(stage, "PrincipalCliente-view.fxml");
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }

                            //System.out.println(this.getClient().getInfo());
                        });

                        // Asignar acción de rechazar
                        rejectButton.setOnAction(event -> {
                            ClientInfo clientInfo = null;
                            try {
                                clientInfo = this.getServer().obtenerClienteInfo(username);
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                            this.getClient().rechazarSolicitudAmistad(clientInfo);
                            try {
                                this.getServer().actualizarClienteInfo(this.getClient().getInfo());
                                // recargar la ventana gráfica
                                this.recargar(stage, "PrincipalCliente-view.fxml");
                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }
                            //System.out.println(this.getClient().getInfo());
                        });

                        // Añadir solicitudes a la lista
                        hbox.getChildren().addAll(label, acceptButton, rejectButton);
                        listaSolicitudes.getItems().add(hbox);
                    }
                } else {
                    listaSolicitudes.setItems(FXCollections.observableList(new ArrayList<>()));
                }

            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }

        });


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
            // Guarda el stage actual
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Pasa la instancia del servidor y del cliente al controlador de la nueva ventana
            AbandonarController controller = fxmlLoader.getController();
            controller.setOldStage(stage);
            controller.setServer(this.getServer());
            controller.setClient(this.getClient());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    @FXML
    public void onLogout(ActionEvent event) {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Logout-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Crear un nuevo Stage para la nueva ventana
            Stage newStage = new Stage();
            newStage.setScene(scene);
            newStage.show();
            // Guarda el stage actual
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();

            // Pasa la instancia del servidor y del cliente al controlador de la nueva ventana
            LogoutController controller = fxmlLoader.getController();
            controller.setOldStage(stage);
            controller.setServer(this.getServer());
            controller.setClient(this.getClient());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private void handleWindowClose() throws RemoteException {
        // Cuando la ventana se cierra, se establece setOnline a false
        if (this.getClient() != null && this.getClient().getInfo() != null) {
            this.getClient().getInfo().setOnline(false);  // Establece el estado de "online" a false
            this.getServer().actualizarClienteInfo(this.getClient().getInfo());
        }
    }

}
