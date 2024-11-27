package com.Client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

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
    @FXML
    private Button botonVolver;
    @FXML
    private Text errorText;
    @FXML
    private TextArea textoAEnviar;


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
            errorText.setVisible(false);

            // Mostramos el nombre del usuario conectado
            usernameAmigo.setText(amigo);

            try {
                System.out.println(amigo);
                System.out.println(this.getServer().obtenerClienteInfo(amigo));
                if (this.getClient().obtenerChat(amigo).isEmpty()) {
                    this.getClient().crearChat(amigo);
                }

                // Obtenemos la conversación entre los dos usuarios
                Optional<Chat> chat = this.getClient().obtenerChat(amigo);
                //Mensaje message = new Mensaje(this.getClient().getInfo().getUsuario(), this.getServer().obtenerClienteInfo(amigo).getUsuario(), "Hola que tal");


                if (chat.isPresent()) {
                    //chat.get().anadirMensaje(message);
                    this.getClient().actualizarChat(chat.get());
                    this.getServer().actualizarClienteInfo(this.getClient());
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

    public void onbotonDejar(ActionEvent actionEvent) {
        try {
            // Obtener la información del amigo
            ClientInterface amigoInterface = this.getServer().getInterface(amigo);
            ClientInfo amigoInfo = amigoInterface.getClientInfo();

            // Eliminar la amistad
            this.getClient().eliminarAmigo(amigoInfo);
            // A mano en el destino
            List<String> amigosDest = amigoInfo.getListaAmigos();
            amigosDest.remove(this.getClient().getNombre());
            amigoInterface.setListaAmigos(amigosDest);
            System.out.println("Desde dejar: " + amigoInterface.getClientInfo().getListaAmigos() + " || " + amigosDest);

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
            this.getClient().notificarRecarga(amigoInterface, elim);
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
            ClientInfo amigoInfo = this.getServer().obtenerClienteInfo(amigo);
            if (amigoInfo.isOnline()) {
                if (textoAEnviar.getText() !=null) {
                    Mensaje mensaje = new Mensaje(this.getClient().getNombre(), amigo, textoAEnviar.getText());
                    // Obtenemos la conversación entre los dos usuarios
                    Optional<Chat> chat = this.getClient().obtenerChat(amigo);
                    if (chat.isPresent()) {
                        chat.get().anadirMensaje(mensaje);
                        this.getClient().actualizarChat(chat.get());

                        // Recopilamos la información del destinatario
                        System.out.println("Nombre: " + this.getClient().getAmigosOnLine());
                        //System.out.println(this.getClient().getAmigosOnLine().get(amigo).getClientInfo());
                        /*ClientInterface amigoInterface = this.getClient().getAmigosOnLine().get(amigo);
                        System.out.println(amigoInterface.getNombre());
                        amigoInterface.recibirMensaje(mensaje);*/

                        this.getServer().actualizarClienteInfo(this.getClient());
                        for (Mensaje m : chat.get().getMensajes()) {
                            System.out.println(m);
                            addMessage(m.toString());
                        }
                    } else {
                        System.out.println("No hay chat");
                    }
                }
            } else {
                errorText.setVisible(true);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
