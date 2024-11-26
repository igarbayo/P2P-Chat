package com.Client;

import com.Server.ServerInterface;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import javafx.event.ActionEvent;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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
    private Button botonLogout;
    @FXML
    private Label usernameLabel;
    @FXML
    private transient TextFlow consola;
    @FXML
    private transient ScrollPane scrollPane;

    private static final int MAX_MESSAGES = 100;



    private void openAmigoView(String amigoSeleccionado) {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Amigo-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Carga el stage
            Stage stage = (Stage) usernameLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            AmigoController controller = fxmlLoader.getController();
            controller.setServer(this.getServer());
            controller.setClient(this.getClient());
            controller.setAmigo(amigoSeleccionado); // Aquí pasas el nombre o la información del amigo seleccionado
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


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
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            printEnConsola("Bienvenido: Iniciado correctamente");

            try {
                // Manejo de lista de amigos
                List<String> list;
                List<ClientInfo> amigos = new ArrayList<>();
                if (this.getServer() != null && this.getClient() != null) {
                    amigos = this.getServer().obtenerAmigos(this.getClient().getInfo());
                }

                //System.out.println("Lista de amigos: " + amigos);

                if (!amigos.isEmpty()) {
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


                // Agregar el evento de selección para la ListView de amigos
                listaAmigos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        // Extrae el nombre del amigo seleccionado
                        String amigoSeleccionado = newValue.split(" ")[0];

                        // Llamar al método para abrir la ventana correspondiente
                        openAmigoView(amigoSeleccionado);

                        // Restablece la selección a nula después de abrir la ventana
                        Platform.runLater(() -> listaAmigos.getSelectionModel().clearSelection());
                    }
                });


            } catch (Exception e) {
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
    //Metodo para imprimir ciertas notificaciones en la consola
    @FXML
    public void printEnConsola(String mensaje){

        //Coge el tiempo actual para imprimirlo en formato texto
        LocalTime tiempoActual = LocalTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String tiempoFormateado=tiempoActual.format(formato);

        //Formatea el texto
        Text text = new Text("["+tiempoFormateado+"] "+mensaje+"\n");

        //Añado un objeto texto a la consola.
        //borra el texto antiguo
        consola.getChildren().add(text);
        if(consola.getChildren().size() > MAX_MESSAGES){
            consola.getChildren().remove(MAX_MESSAGES);
        }


    }

}
