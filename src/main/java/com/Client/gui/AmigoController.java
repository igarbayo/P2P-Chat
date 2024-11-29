package com.Client.gui;

import com.Client.*;
import com.Client.security.ChaChaDecryption;
import com.Client.security.ChaChaEncryption;
import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AmigoController extends AbstractVentana {

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
    private ListView<HBox> listView;
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
    @FXML
    private Label usernameLabel;


    // Método para agregar texto al TextFlow
    public void agregarTexto(Mensaje mensaje, ObservableList<HBox> lista) {
        // El mensaje lo manda uno mismo (derecha)
        if (mensaje.getClienteOrigen().equals(this.getClient().getInfo().getUsuario())) {
            Label label = new Label(mensaje.StringDerecha());
            HBox hbox = new HBox(label);
            hbox.setAlignment(Pos.CENTER_RIGHT); // Asegúrate de que los elementos se alineen a la derecha
            lista.add(hbox);
        } // el mensaje lo manda la otra persona (izquierda)
        else {
            Label label = new Label(mensaje.StringIzquierda());
            HBox hbox = new HBox(label);
            hbox.setAlignment(Pos.CENTER_LEFT); // Asegúrate de que los elementos se alineen a la derecha
            lista.add(hbox);
        }

        // Desplazar siempre hacia abajo al agregar un nuevo texto
        desplazarHaciaAbajo();
    }

    // Método para desplazar el ScrollPane hacia la parte inferior
    private void desplazarHaciaAbajo() {
        // Esto asegura que el ScrollPane se desplaza a la parte inferior
        listView.scrollTo(listView.getItems().size() - 1);
    }

    // Método para vaciar el contendedor de chat
    public void vaciarLista() {
        //listView.getItems().clear(); // Elimina todos los nodos dentro del TextFlow
    }

    // Método para recuperar el chat
    public void recuperarChat(Chat chat, String user1, String user2) throws Exception {
        vaciarLista();
        ObservableList<HBox> listaChat = FXCollections.observableArrayList();
        if (chat!= null) {
            for (Mensaje mensaje : chat.getMensajes()) {
                // Desencriptamos el mensaje
                ChaChaDecryption.KeyNonce keyNonce = ChaChaDecryption.readKeyAndNonce(user1, user2);
                String decryptedContenido = ChaChaDecryption.decryptMessage(mensaje.getContenido(), keyNonce.getKey(), keyNonce.getNonce());
                Mensaje mensajeDecriptado = new Mensaje(mensaje.getClienteOrigen(), mensaje.getClienteDestino(), decryptedContenido, mensaje.getTiempoFormateado());

                agregarTexto(mensajeDecriptado, listaChat);
            }
            listView.setItems(listaChat);
        } else {
            listView.setItems(FXCollections.observableArrayList(new ArrayList<>()));
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
                    if (amigo.getClientInfo().getUsuario()!=null && amigo!=null) {
                        usernameAmigo.setText(amigo.getClientInfo().getUsuario());
                        // Mostramos el nombre del usuario conectado
                        usernameLabel.setText(this.getClient().getInfo().getUsuario());
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }

                try {
                    //Debug
                    //System.out.println(amigo);
                    //System.out.println(this.getServer().obtenerClienteInfo(amigo.getNombre()));

                    if (this.getServer().obtenerClienteInfo(amigo.getNombre()) != null) {
                        errorText.setVisible(!this.getServer().obtenerClienteInfo(amigo.getNombre()).isOnline());
                        botonEnviar.setDisable(!this.getServer().obtenerClienteInfo(amigo.getNombre()).isOnline());
                    }

                    //Creamos/obtenemos el chat si está logueado
                    if (this.getServer().estaLogueado(this.getClient().getInfo().getUsuario())) {
                        if (this.getClient().obtenerChat(amigo.getNombre()).isEmpty()) {
                            this.getClient().crearChat(amigo);

                            // Generamos las claves de cifrado
                            String user1 = this.getClient().getNombre();
                            String user2 = amigo.getNombre();

                            // Generar clave y nonce
                            byte[] key = ChaChaEncryption.generateKey(user1, user2);
                            byte[] nonce = ChaChaEncryption.generateNonce();

                            // Guardar clave y nonce en archivos para ambos usuarios
                            ChaChaEncryption.saveKeysAndNonce(user1, user2, key, nonce);
                        }
                        // Obtenemos la conversación entre los dos usuarios
                        Optional<Chat> chat = this.getClient().obtenerChat(amigo.getNombre());
                        //Mensaje message = new Mensaje(this.getClient().getInfo().getUsuario(), this.getServer().obtenerClienteInfo(amigo).getUsuario(), "Hola que tal");

                        if (chat.isPresent()) {
                            // Imprimimos el chat
                            recuperarChat(chat.get(), this.getClient().getNombre(), amigo.getNombre());
                            //chat.get().anadirMensaje(message);
                            this.getClient().actualizarChat(chat.get());
                            this.getServer().actualizarClienteInfo(this.getClient());

                        } else {
                            System.out.println("No hay chat");
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

    public void onbotonDejar(ActionEvent actionEvent) {
        try {
            // Obtener la información del amigo
            if (this.getServer().estaLogueado(this.getClient().getInfo().getUsuario())) {
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

                String elim = "Eliminada amistad con " + this.getClient().getNombre();
                amigoInterface.addNotificacion(elim);
            }

            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // CSS
            scene.getStylesheets().add(getClass().getResource("/styles/basic.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/button.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/colors.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/list-view.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/text-area.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/text-field.css").toExternalForm());

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


    public void onVolver(ActionEvent actionEvent) {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // CSS
            scene.getStylesheets().add(getClass().getResource("/styles/basic.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/button.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/colors.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/list-view.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/text-area.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/text-field.css").toExternalForm());

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
            if (this.getServer().estaLogueado(this.getClient().getInfo().getUsuario())) {
                ClientInfo amigoInfo = this.getServer().obtenerClienteInfo(amigo.getNombre());
                if (amigoInfo.isOnline()) {
                    if (textoAEnviar.getText() !=null) {
                        Mensaje mensaje = new Mensaje(this.getClient().getNombre(), amigo.getNombre(), textoAEnviar.getText());
                        // Obtenemos la conversación entre los dos usuarios
                        Optional<Chat> chat = this.getClient().obtenerChat(amigo.getNombre());
                        if (chat.isPresent()) {
                            // Encriptamos utilizando key y none
                            ChaChaDecryption.KeyNonce keynonce = ChaChaDecryption.readKeyAndNonce(this.getClient().getNombre(), amigo.getNombre());
                            String encryptedContenido = ChaChaEncryption.encryptMessage(mensaje.getContenido(), keynonce.getKey(), keynonce.getNonce());
                            Mensaje mensajeEncriptado = new Mensaje(mensaje.getClienteOrigen(), mensaje.getClienteDestino(), encryptedContenido, mensaje.getTiempoFormateado());

                            chat.get().anadirMensaje(mensajeEncriptado);
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

                            this.getClient().enviarMensaje(this.getClient().getInterface(amigo.getNombre()), mensajeEncriptado);
                        } else {
                            System.out.println("No hay chat");
                        }
                    }
                } else {
                    // nada
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


}
