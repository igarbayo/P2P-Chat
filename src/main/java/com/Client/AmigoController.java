package com.Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.net.URL;
import java.rmi.RemoteException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AmigoController extends AbstractVentana{

    // Atributo añadido para recargar
    private Stage stagePrincipal;
    public Stage getStagePrincipal() {
        return stagePrincipal;
    }
    public void setStagePrincipal(Stage stagePrincipal) {
        this.stagePrincipal = stagePrincipal;
    }

    // Atributo añadido para Amigo
    private ClientInterface amigo;
    public ClientInterface getAmigo() {
        return amigo;
    }
    public void setAmigo(ClientInterface amigo) {
        this.amigo = amigo;
    }

    @FXML
    private transient TextFlow textFlow;
    @FXML
    private transient ScrollPane scrollPane;
    @FXML
    private Label usernameAmigo;
    @FXML
    private Button botonDejar;
    @FXML
    private Button botonEnviar;
    @FXML
    private Button botonVolver;
    @FXML
    private Text errorText;
    @FXML
    private TextArea textoAEnviar;


    // Método para agregar texto al TextFlow
    public void agregarTexto(Mensaje mensaje) {
        //Coge el tiempo actual para imprimirlo en formato texto
        Text text = new Text(mensaje.toString() + "\n");
        textFlow.getChildren().add(text);

        // Desplazar siempre hacia abajo al agregar un nuevo texto
        desplazarHaciaAbajo();
    }

    // Método para desplazar el ScrollPane hacia la parte inferior
    private void desplazarHaciaAbajo() {
        // Esto asegura que el ScrollPane se desplaza a la parte inferior
        scrollPane.setVvalue(1.0);  // Establece el valor vertical al máximo (parte inferior)
    }

    // Método para vaciar el contendedor de chat
    public void vaciarTextFlow() {
        textFlow.getChildren().clear(); // Elimina todos los nodos dentro del TextFlow
    }

    // Método para recuperar el chat
    public void recuperarChat(Chat chat) {
        vaciarTextFlow();
        if (chat!= null) {
            for (Mensaje mensaje : chat.getMensajes()) {
                agregarTexto(mensaje);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        // Crear el scheduler
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Definir la tarea a ejecutar
        Runnable task = () -> {
            Platform.runLater(() -> {

                // Código de inicialización aquí
                errorText.setVisible(false);

                // Mostramos el nombre del usuario conectado
                try {
                    if (amigo.getNombre()!=null && amigo!=null) {
                        usernameAmigo.setText(amigo.getNombre());
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

                try {
                    //Debug
                    System.out.println(amigo);
                    System.out.println(this.getServer().obtenerClienteInfo(amigo.getNombre()));

                    if (this.getServer().obtenerClienteInfo(amigo.getNombre()) != null) {
                        errorText.setVisible(!this.getServer().obtenerClienteInfo(amigo.getNombre()).isOnline());
                        botonEnviar.setDisable(!this.getServer().obtenerClienteInfo(amigo.getNombre()).isOnline());
                    }

                    //Creamos/obtenemos el chat
                    if (this.getClient().obtenerChat(amigo.getNombre()).isEmpty()) {
                        this.getClient().crearChat(amigo);
                    }
                    // Obtenemos la conversación entre los dos usuarios
                    Optional<Chat> chat = this.getClient().obtenerChat(amigo.getNombre());
                    //Mensaje message = new Mensaje(this.getClient().getInfo().getUsuario(), this.getServer().obtenerClienteInfo(amigo).getUsuario(), "Hola que tal");



                    if (chat.isPresent()) {
                        // Imprimimos el chat
                        recuperarChat(chat.get());
                        //chat.get().anadirMensaje(message);
                        this.getClient().actualizarChat(chat.get());
                        this.getServer().actualizarClienteInfo(this.getClient());
                        
                    } else {
                        System.out.println("No hay chat");
                    }

                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }


            });
        };

        // Programar la tarea para que se ejecute cada segundo
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);



    }

    public void onbotonDejar(ActionEvent actionEvent) {
        try {
            // Obtener la información del amigo
            ClientInterface amigoInterface = this.getClient().getInterface(amigo.getNombre());
            ClientInfo amigoInfo = amigoInterface.getClientInfo();

            // Eliminar la amistad
            this.getClient().eliminarAmigo(amigoInfo);
            // A mano en el destino
            List<String> amigosDest = amigoInfo.getListaAmigos();
            amigosDest.remove(this.getClient().getNombre());
            amigoInterface.setListaAmigos(amigosDest);
            //System.out.println("Desde dejar: " + amigoInterface.getClientInfo().getListaAmigos() + " || " + amigosDest);

            // Actualizar la información en el servidor para ambos usuarios
            this.getServer().actualizarClienteInfo(this.getClient());
            this.getServer().actualizarClienteInfo(amigoInterface);

            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Carga el stage
            Stage stage = (Stage) botonDejar.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            PrincipalController controller = fxmlLoader.getController();
            controller.setServer(this.getServer());
            controller.setClient(this.getClient());

            String elim = "Eliminada amistad con " + this.getClient().getNombre();
            amigoInterface.addNotificacion(elim);
            this.getClient().notificarRecarga(amigoInterface);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void onVolver(ActionEvent actionEvent) {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Carga el stage
            Stage stage = (Stage) botonDejar.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            PrincipalController controller = fxmlLoader.getController();
            controller.setServer(this.getServer());
            controller.setClient(this.getClient());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void onEnviar(ActionEvent actionEvent) {
        try {
            ClientInfo amigoInfo = this.getServer().obtenerClienteInfo(amigo.getNombre());
            if (amigoInfo.isOnline()) {
                if (textoAEnviar.getText() !=null) {
                    Mensaje mensaje = new Mensaje(this.getClient().getNombre(), amigo.getNombre(), textoAEnviar.getText());
                    // Obtenemos la conversación entre los dos usuarios
                    Optional<Chat> chat = this.getClient().obtenerChat(amigo.getNombre());
                    if (chat.isPresent()) {
                        chat.get().anadirMensaje(mensaje);
                        this.getClient().actualizarChat(chat.get());

                        // Recopilamos la información del destinatario
                        //System.out.println("Nombre: " + this.getClient().getAmigosOnLine());
                        //System.out.println(this.getClient().getAmigosOnLine().get(amigo).getClientInfo());
                        /*ClientInterface amigoInterface = this.getClient().getAmigosOnLine().get(amigo);
                        System.out.println(amigoInterface.getNombre());
                        amigoInterface.recibirMensaje(mensaje);*/

                        this.getServer().actualizarClienteInfo(this.getClient());
                        /*for (Mensaje m : chat.get().getMensajes()) {
                            System.out.println(m);
                            agregarTexto(m);
                        }*/

                        this.getClient().enviarMensaje(this.getClient().getInterface(amigo.getNombre()), mensaje);
                    } else {
                        System.out.println("No hay chat");
                    }
                }
            } else {
                // nada
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


}
