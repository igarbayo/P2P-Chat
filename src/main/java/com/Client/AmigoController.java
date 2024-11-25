package com.Client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.ResourceBundle;

public class AmigoController extends AbstractVentana{

    // Atributo añadido para Amigo
    private String amigo;
    public String getAmigo() {
        return amigo;
    }
    public void setAmigo(String amigo) {
        this.amigo = amigo;
    }

    @FXML
    private VBox chatBox;
    @FXML
    private Label usernameAmigo;
    @FXML
    private Button botonDejar;
    @FXML
    private Button botonEnviar;

    // Método para agregar un nuevo mensaje al chat
    public void addMessage(String message) {
        // Crear un nuevo Label para el mensaje
        Label newMessage = new Label(message);

        // Agregar el mensaje al VBox
        chatBox.getChildren().add(newMessage);

        // Opcional: Hacer que el ScrollPane siempre muestre el mensaje más reciente
        chatBox.autosize(); // Esto ajustará el tamaño del VBox automáticamente
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {

            // Código de inicialización aquí

            // Mostramos el nombre del usuario conectado
            usernameAmigo.setText(amigo);

            try {
                System.out.println(amigo);
                System.out.println(this.getServer().obtenerClienteInfo(amigo));
                if (this.getClient().obtenerChat(this.getServer().obtenerClienteInfo(amigo)).isEmpty()) {
                    this.getClient().crearChat(this.getServer().obtenerClienteInfo(amigo));
                }

                // Obtenemos la conversación entre los dos usuarios
                Optional<Chat> chat = this.getClient().obtenerChat(this.getServer().obtenerClienteInfo(amigo));
                Mensaje message = new Mensaje(this.getClient().getInfo(), this.getServer().obtenerClienteInfo(amigo), "Hola que tal");


                if (chat.isPresent()) {
                    chat.get().anadirMensaje(message);
                    this.getClient().actualizarChat(chat.get());
                    this.getServer().actualizarClienteInfo(this.getClient().getInfo());
                    for (Mensaje m : chat.get().getMensajes()) {
                        System.out.println(m);
                        addMessage(m.toString());
                    }
                } else {
                    System.out.println("No hay chat");
                }

            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }


        });
    }
}
