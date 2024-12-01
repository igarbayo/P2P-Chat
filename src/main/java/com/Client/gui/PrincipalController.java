package com.Client.gui;

import com.Client.ClientInterface;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

import javafx.event.ActionEvent;
import javafx.scene.layout.Region;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

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
    private ListView<Text> listView;
    private ObservableList<Text> lista;


    private static final ReentrantLock lockSolicitudes = new ReentrantLock(); // Candado
    private static final ReentrantLock lockAmigos = new ReentrantLock();

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

    }


    private void openAmigoView(String amigoSeleccionado) {
        Platform.runLater(() -> {
            try {
                // Cargar el archivo FXML
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Amigo-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                // CSS
                scene.getStylesheets().add(getClass().getResource("/styles/basic.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("/styles/button.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("/styles/colors.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("/styles/list-view.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("/styles/text-area.css").toExternalForm());
                scene.getStylesheets().add(getClass().getResource("/styles/text-field.css").toExternalForm());

                // Carga el stage
                if (usernameLabel.getScene() != null) {
                    stage = (Stage) usernameLabel.getScene().getWindow();
                    if (stage != null) {
                        Stage stage = (Stage) usernameLabel.getScene().getWindow();
                        stage.setScene(scene);
                        stage.show();
                    }
                }

                System.out.println("Desde origen: " + this.getServer().obtenerClienteInfo(amigoSeleccionado));

                AmigoController controller = fxmlLoader.getController();
                controller.setServer(this.getServer());
                controller.setClient(this.getClient());
                controller.setAmigo(this.getClient().getInterface(amigoSeleccionado)); // Aquí pasas el nombre o la información del amigo seleccionado
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

    }


    // Disposición inicial
    @Override
    public void initialize(URL url, ResourceBundle resources) {
        // Mensaje de bienvenida
        //Coge el tiempo actual para imprimirlo en formato texto
        LocalTime tiempoActual = LocalTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String tiempoFormateado=tiempoActual.format(formato);
        Text bienvenido = new Text("[" + tiempoFormateado + "] " + "Bienvenido");

        lista = FXCollections.observableArrayList();
        lista.add(0, bienvenido);

        // Crear el scheduler
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Definir la tarea a ejecutar
        Runnable task = () -> {
            //System.out.println("Tarea ejecutada a las: " + System.currentTimeMillis());
            // Lógica para actualizar la ventana
            Platform.runLater(() -> {
                if (usernameLabel.getScene() != null) {
                    stage = (Stage) usernameLabel.getScene().getWindow();
                    if (stage != null) {
                        Runtime.getRuntime().addShutdownHook(new Thread(this::safeHandleWindowClose));
                        stage.setOnCloseRequest(event -> {
                            safeHandleWindowClose();
                        });
                    }
                }

                // Mostramos el nombre del usuario conectado
                if (this.getClient().getInfo()!=null && this.getClient().getInfo().getUsuario()!=null) {
                    usernameLabel.setText(this.getClient().getInfo().getUsuario());
                }

                //this.mensajePendiente.add(mensaje);
                printEnConsola();

                try {
                    if (this.getServer().estaLogueado(this.getClient().getInfo().getUsuario())) {
                        // Manejo de amigos
                        lockAmigos.lock();
                        try {
                            amigosObservableList = FXCollections.observableArrayList();

                            if (this.getServer() != null && this.getClient() != null) {
                                List<String> amigosUsuarios = this.getServer().obtenerAmigos(this.getClient().getInfo().getUsuario());

                                for (String username : amigosUsuarios) {
                                    try {
                                        ClientInterface amigo = this.getClient().getInterface(username);
                                        if (this.getServer().obtenerClienteInfo(username) !=null) {
                                            String estado = username + (this.getServer().obtenerClienteInfo(username).isOnline() ? " [Online]" : " [Offline]");
                                            //String estado = username + (amigo.getOnline() ? " [ONLINE]" : " [OFFLINE]");
                                            amigosObservableList.add(estado);
                                        }
                                    } catch (RemoteException e) {
                                        e.printStackTrace();  // Aquí puedes manejar el error si no se puede obtener la información de un amigo
                                    }
                                }
                                listaAmigos.setItems(amigosObservableList);
                            }
                        } finally {
                            lockAmigos.unlock();
                        }


                        lockSolicitudes.lock();
                        try {
                            // Manejo de lista de solicitudes
                            // Vaciar el ListView
                            listaSolicitudes.getItems().clear();
                            if (this.getClient() != null) {
                                List<String> solicitudes;
                                solicitudes = this.getServer().getSolicitudes(this.getClient());
                                for (String username : solicitudes) {
                                    HBox hbox = new HBox();
                                    hbox.setAlignment(Pos.CENTER_LEFT);
                                    // Agregar el espacio en blanco (Region) que empuja los botones a la derecha
                                    Region spacer = new Region();
                                    HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS); // Hace que el spacer ocupe todo el espacio disponible
                                    Label label = new Label(username);
                                    Button acceptButton = new Button("V");
                                    acceptButton.setStyle("-fx-background-color: green;");
                                    Button rejectButton = new Button("X");
                                    rejectButton.setStyle("-fx-background-color: red;");

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

                                            // Actualizar amigos online
                                            this.getClient().addAmigoOnline(username, clientInterface);
                                            clientInterface.addAmigoOnline(this.getClient().getInfo().getUsuario(), this.getClient());

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
                                            //this.getClient().notificarRecarga(clientInterface);
                                            //this.recargar(stage, "PrincipalCliente-view.fxml");


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
                                            //this.getClient().notificarRecarga(clientInterface);
                                            //this.recargar(stage, "PrincipalCliente-view.fxml");

                                        } catch (RemoteException e) {
                                            throw new RuntimeException(e);
                                        }

                                    });

                                    // Añadir solicitudes a la lista
                                    hbox.getChildren().addAll(label, spacer, acceptButton, rejectButton);
                                    if (!listaSolicitudes.getItems().contains(hbox)) {
                                        listaSolicitudes.getItems().add(hbox);
                                    }
                                }
                            } else {
                                listaSolicitudes.setItems(FXCollections.observableList(new ArrayList<>()));
                            }
                        } finally {
                            lockSolicitudes.unlock();
                        }


                        lockAmigos.lock();
                        try {
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
                        } finally {
                            lockAmigos.unlock();
                        }
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            });

        };

        // Programar la tarea para que se ejecute cada segundo
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);

    }


    // Botones
    @FXML
    public void onEnviar(ActionEvent event) {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Solicitud-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // CSS
            scene.getStylesheets().add(getClass().getResource("/styles/basic.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/button.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/colors.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/list-view.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/text-area.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/text-field.css").toExternalForm());

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
    public void onLogout(ActionEvent event) {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Logout-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // CSS
            scene.getStylesheets().add(getClass().getResource("/styles/basic.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/button.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/colors.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/list-view.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/text-area.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/text-field.css").toExternalForm());

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
    public void printEnConsola() {

        //Coge el tiempo actual para imprimirlo en formato texto
        LocalTime tiempoActual = LocalTime.now();
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
        String tiempoFormateado=tiempoActual.format(formato);

        //Formatea el texto
        List<String> notificacionesCopia = new ArrayList<>(this.getClient().getListaNotificaciones());

        for (String notificacion : notificacionesCopia) {

            System.out.println(notificacion);
            Text text = new Text("[" + tiempoFormateado + "] " + notificacion);
            text.setStyle("-fx-fill: -fx--white;");

            try {
                // Eliminar la notificación de la lista original
                this.getClient().getNotificaciones().remove(notificacion);
            } catch (Exception e) {
                e.printStackTrace(); // Registra el error si ocurre
            }

            // Añadir el texto a la consola
            lista.add(0,text);
            System.out.println(text);
            //consola.requestLayout();

            // Limitar el tamaño de la consola
            if (lista.size() > MAX_MESSAGES) {
                lista.remove(MAX_MESSAGES);
            }
        }

        if (!lista.isEmpty()) {
            listView.setItems(lista);
        } else {
            listView.setItems(FXCollections.observableArrayList(new ArrayList<>()));
        }



        //Añado un objeto texto a la consola.
        //borra el texto antiguo




    }


}
