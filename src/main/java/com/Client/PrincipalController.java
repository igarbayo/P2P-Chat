package com.Client;

import com.Server.ServerInterface;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
    private List<String> mensajePendiente;
    ObservableList<String> amigosObservableList;
    private Stage stage;

    public ObservableList<String> getAmigosObservableList() {
        return amigosObservableList;
    }

    public void setAmigosObservableList(ObservableList<String> amigosObservableList) {
        this.amigosObservableList = amigosObservableList;
    }

    /*public List<String> getMensajePendiente() {
        return mensajePendiente;
    }*/

    public void recargarVista() {
        // Lógica para actualizar la ventana
        Platform.runLater(() -> {

            System.out.println("3. Se procede a recargar la vista.");

            /*if (this.mensajePendiente== null || this.mensajePendiente.isEmpty()) {
                this.mensajePendiente = new ArrayList<>();
            }*/

            // Mostramos el nombre del usuario conectado
            usernameLabel.setText(this.getClient().getInfo().getUsuario());
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            this.printEnConsola();
            /*this.mensajePendiente.add(mensaje);
            for (String mensaje2: this.mensajePendiente) {
                if (mensaje2!=null) {
                    System.out.println("Mensaje: " + mensaje2);
                    printEnConsola(mensaje2);

                }

            }*/

            try {

                amigosObservableList = FXCollections.observableArrayList();

                if (this.getServer() != null && this.getClient() != null) {
                    List<String> amigosUsuarios = this.getServer().obtenerAmigos(this.getClient().getInfo().getUsuario());
                    for (String username : amigosUsuarios) {
                        try {
                            ClientInterface amigo = this.getServer().getInterface(username);
                            String estado = amigo.getClientInfo().getUsuario() + (amigo.getClientInfo().isOnline() ? " [Online]" : " [Offline]");
                            amigosObservableList.add(estado);
                        } catch (RemoteException e) {
                            e.printStackTrace();  // Aquí puedes manejar el error si no se puede obtener la información de un amigo
                        }
                    }
                    listaAmigos.setItems(amigosObservableList);
                }




                // Manejo de lista de solicitudes
                if (this.getClient() != null) {
                    List<String> solicitudes;
                    solicitudes = this.getServer().getSolicitudes(this.getClient());
                    for (String username : solicitudes) {
                        HBox hbox = new HBox();
                        Label label = new Label(username);
                        Button acceptButton = new Button("V");
                        Button rejectButton = new Button("X");

                        // Asignar acción de aceptar
                        acceptButton.setOnAction(event -> {
                            ClientInterface clientInterface = null;
                            try {
                                clientInterface = this.getServer().getInterface(username);
                                this.getClient().aceptarSolicitudAmistad(clientInterface.getClientInfo());

                                // Añadir cliente a mano
                                List<String> amigosDest = clientInterface.getClientInfo().getListaAmigos();
                                amigosDest.add(this.getClient().getNombre());
                                clientInterface.setListaAmigos(amigosDest);
                                System.out.println(clientInterface.getClientInfo().getListaAmigos());


                                clientInterface.confirmarAmistad(this.getClient().getNombre());
                                this.getServer().actualizarClienteInfo(this.getClient());
                                this.getServer().actualizarClienteInfo(clientInterface);
                                this.getServer().eliminarSolicitud(username, this.getClient().getNombre());

                                //Debug
                                System.out.println(clientInterface.getClientInfo().getListaAmigos());
                                System.out.println(this.getServer().obtenerAmigos(clientInterface.getNombre()));

                                // Ventana gráfica
                                this.getClient().addNotificacion("Aceptado");
                                String acepto = "Solicitud a " + this.getClient().getNombre() + " aceptada";
                                clientInterface.addNotificacion(acepto);
                                this.getClient().notificarRecarga(clientInterface);
                                this.recargar(stage, "PrincipalCliente-view.fxml");


                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }

                        });

                        // Asignar acción de rechazar
                        rejectButton.setOnAction(event -> {
                            ClientInterface clientInterface = null;
                            try {
                                clientInterface = this.getServer().getInterface(username);
                                this.getServer().eliminarSolicitud(username, this.getClient().getNombre());

                                // recargar la ventana gráfica

                                this.getClient().addNotificacion("rechazado");
                                String rechazo = "Solicitud a " + this.getClient().getNombre() + " rechazada";
                                clientInterface.addNotificacion(rechazo);
                                this.getClient().notificarRecarga(clientInterface);
                                this.recargar(stage, "PrincipalCliente-view.fxml");

                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }

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

    /*public void setMensajePendiente(List<String> mensajePendiente) {
        this.mensajePendiente = mensajePendiente;
    }*/
    public void anadirMensajePendiente(String mensaje) {
        if (mensaje == null || mensaje.trim().isEmpty()) {
            throw new IllegalArgumentException("El mensaje no puede ser null o vacío");
        }
        mensajePendiente.add(mensaje);
        System.out.println("Mensaje añadido a pendientes: " + mensaje);
    }

    private void openAmigoView(String amigoSeleccionado) {
        Platform.runLater(() -> {
            try {
                // Cargar el archivo FXML
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Amigo-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                // Carga el stage
                Stage stage = (Stage) usernameLabel.getScene().getWindow();
                stage.setScene(scene);
                stage.show();

                System.out.println("Desde origen: " + this.getServer().obtenerClienteInfo(amigoSeleccionado));

                AmigoController controller = fxmlLoader.getController();
                controller.setServer(this.getServer());
                controller.setClient(this.getClient());
                controller.setAmigo(amigoSeleccionado); // Aquí pasas el nombre o la información del amigo seleccionado
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }


    // Disposición inicial
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        // Lógica para actualizar la ventana
        Platform.runLater(() -> {

            // Configurar un shutdown hook para manejar la desconexión cuando se cierra el programa con SIGINT
            Runtime.getRuntime().addShutdownHook(new Thread(this::safeHandleWindowClose));

            // Obtenemos el stage actual
            stage = (Stage) usernameLabel.getScene().getWindow();

            // Configuramos el cierre seguro cuando se pulsa la X
            stage.setOnCloseRequest(event -> {
                //event.consume();
                safeHandleWindowClose();
            });

            /*if (this.mensajePendiente== null || this.mensajePendiente.isEmpty()) {
                this.mensajePendiente = new ArrayList<>();
            }*/

            // Mostramos el nombre del usuario conectado
            usernameLabel.setText(this.getClient().getInfo().getUsuario());
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            //this.mensajePendiente.add(mensaje);
            printEnConsola();

            try {

                amigosObservableList = FXCollections.observableArrayList();

                if (this.getServer() != null && this.getClient() != null) {
                    List<String> amigosUsuarios = this.getServer().obtenerAmigos(this.getClient().getInfo().getUsuario());

                    for (String username : amigosUsuarios) {
                        try {
                            ClientInterface amigo = this.getServer().getInterface(username);
                            String estado = amigo.getClientInfo().getUsuario() + (amigo.getClientInfo().isOnline() ? " [Online]" : " [Offline]");
                            amigosObservableList.add(estado);
                        } catch (RemoteException e) {
                            e.printStackTrace();  // Aquí puedes manejar el error si no se puede obtener la información de un amigo
                        }
                    }
                    listaAmigos.setItems(amigosObservableList);
                }


                //Si entra desde logIn mando notificación a sus amigos conectados de que hay un nuevo ususario.
                /*if(this.getClient().getIsNew()==1){
                    if(!amigosObservableList.isEmpty()){
                        for(String usuarioAmigo : amigosObservableList){
                            ClientInterface amigo = this.getServer().getInterface(usuarioAmigo);
                            if(amigo.getOnline()){
                                String notifConex="Tu amigo: "+this.getClient().getInfo().getUsuario()+"se ha conectado";
                                this.getClient().notificarRecarga(amigo,notifConex);
                            }
                        }
                        this.getClient().setIsNew(0);//lo pongo a 0 (ya no es nuevo)
                    }
                }*/


                // Manejo de lista de solicitudes
                if (this.getClient() != null) {
                    List<String> solicitudes;
                    solicitudes = this.getServer().getSolicitudes(this.getClient());
                    for (String username : solicitudes) {
                        HBox hbox = new HBox();
                        Label label = new Label(username);
                        Button acceptButton = new Button("V");
                        Button rejectButton = new Button("X");

                        // Asignar acción de aceptar
                        acceptButton.setOnAction(event -> {
                            ClientInterface clientInterface = null;
                            try {
                                clientInterface = this.getServer().getInterface(username);
                                this.getClient().aceptarSolicitudAmistad(clientInterface.getClientInfo());

                                // Añadir cliente a mano
                                List<String> amigosDest = clientInterface.getClientInfo().getListaAmigos();
                                amigosDest.add(this.getClient().getNombre());
                                clientInterface.setListaAmigos(amigosDest);
                                System.out.println(clientInterface.getClientInfo().getListaAmigos());

                                this.getServer().actualizarClienteInfo(this.getClient());
                                this.getServer().actualizarClienteInfo(clientInterface);
                                clientInterface.confirmarAmistad(this.getClient().getNombre());
                                this.getServer().eliminarSolicitud(username, this.getClient().getNombre());

                                //Debug
                                System.out.println(clientInterface.getClientInfo().getListaAmigos());
                                System.out.println(clientInterface.getClientInfo().getListaAmigos());
                                System.out.println(this.getServer().obtenerAmigos(clientInterface.getNombre()));

                                // Ventana gráfica
                                this.getClient().addNotificacion("Aceptado");
                                String acepto = "Solicitud a " + this.getClient().getNombre() + " aceptada";
                                clientInterface.addNotificacion(acepto);
                                this.getClient().notificarRecarga(clientInterface);
                                this.recargar(stage, "PrincipalCliente-view.fxml");


                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }

                        });

                        // Asignar acción de rechazar
                        rejectButton.setOnAction(event -> {
                            ClientInterface clientInterface = null;
                            try {
                                clientInterface = this.getServer().getInterface(username);
                                this.getServer().eliminarSolicitud(username, this.getClient().getNombre());

                                // recargar la ventana gráfica
                                this.getClient().addNotificacion("Rechazado");
                                String rechazo = "Solicitud a " + this.getClient().getNombre() + " rechazada";
                                clientInterface.addNotificacion(rechazo);
                                this.getClient().notificarRecarga(clientInterface);
                                this.recargar(stage, "PrincipalCliente-view.fxml");

                            } catch (RemoteException e) {
                                throw new RuntimeException(e);
                            }

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
    public void printEnConsola(){

        //Coge el tiempo actual para imprimirlo en formato texto
        LocalTime tiempoActual = LocalTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String tiempoFormateado=tiempoActual.format(formato);

        //Formatea el texto
        for(String notificacion: this.getClient().getListaNotificaciones()){
            System.out.println(notificacion);
            Text text = new Text("["+tiempoFormateado+"] "+notificacion+"\n");
            consola.getChildren().add(0,text);
            if(consola.getChildren().size() > MAX_MESSAGES){
                consola.getChildren().remove(MAX_MESSAGES);
            }
        }


        //Añado un objeto texto a la consola.
        //borra el texto antiguo




    }


}
