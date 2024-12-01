package com.Client.gui;

import com.Client.Client;
import com.Client.ClientInfo;
import com.Client.ClientInterface;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import java.util.concurrent.locks.ReentrantLock;

public class LogoutController extends AbstractVentana {

    @FXML
    private Button botonSi;
    @FXML
    private Button botonNo;
    @FXML
    private Label usernameLabel;
    private static final ReentrantLock lock = new ReentrantLock();

    // Para cambiar la ventana anterior
    private Stage oldStage;
    public Stage getOldStage() {
        return oldStage;
    }
    public void setOldStage(Stage oldStage) {
        this.oldStage = oldStage;
    }

    // Constructor
    public LogoutController() {
        this.oldStage = null;
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        Platform.runLater(() -> {
            // Mostramos el nombre del usuario conectado
            usernameLabel.setText(this.getClient().getInfo().getUsuario());

            System.out.println("LOGOUT");
            System.out.println(this.getClient().getInfo());
            for(String amigo : this.getClient().getInfo().getListaAmigos()){
                try {
                    if(this.getClient().getInterface(amigo).getOnline()){
                        System.out.println("TRUE");
                    }else{
                        System.out.println("FALSE");
                    }
                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @FXML
    public void onSi(ActionEvent actionEvent) {
        try {
            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InicioCliente-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // CSS
            scene.getStylesheets().add(getClass().getResource("/styles/basic.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/button.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/colors.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/list-view.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/text-area.css").toExternalForm());
            scene.getStylesheets().add(getClass().getResource("/styles/text-field.css").toExternalForm());

            oldStage.setScene(scene);
            oldStage.show();

            // Obtener el Stage actual a partir del control (ej. un botón)
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.close(); // Cierra el Stage
            // Pasa la instancia del servidor y del cliente al controlador de la nueva ventana
            InicioController controller = fxmlLoader.getController();
            controller.setServer(this.getServer());
            controller.setClient(this.getClient());
            controller.setIP(this.getClient().getIP());
            controller.setPuerto(this.getClient().getPuerto());

            if (this.getServer().estaLogueado(this.getClient().getInfo().getUsuario())) {
                // Ponemos offline la ClientInfo de la instancia de Client actual
                lock. lock();
                try {
                    ClientInfo info = this.getClient().getInfo();
                    info.setOnline(false);
                    this.getClient().setInfo(info);
                    this.getServer().actualizarClienteInfo(this.getClient());

                    if (this.getClient().getAmigosOnLine()!=null) {
                        //this.getClient().setOffline(this.getClient().getAmigosOnLine());
                        for(ClientInterface amigo : this.getClient().getAmigosOnLine().values()){
                            this.getServer().actualizarClienteInfo(amigo);
                            System.out.println(amigo.getNotificaciones());
                        }

                    } else {
                        System.out.println("Amigos nulos");
                    }

                    System.out.println("===========================\n" +
                            "Información de amigos y del propio cliente:\n" +
                            "=======================================\n" +
                            "Usuario de this.client() "+this.getClient().getInfo().getUsuario()+"\n" +
                            "Estado online: +"+(this.getClient().getOnline()?"[Online]" : " [Offline]") +"\n"+
                            "Estado online con getClientInfo: "+(this.getClient().getClientInfo().isOnline()?"[Online]" : " [Offline]") + "\n" +
                            "Amigos: \n" +
                            "------------------------------------------\n");
                    for(ClientInterface amigo1 : this.getClient().getAmigosOnLine().values()){
                        System.out.println(amigo1.getClientInfo().getUsuario());
                        //ClientInterface amigo3=this.getServer().getInterface(amigo1.getClientInfo().getUsuario());
                        for(ClientInterface amigo2 : amigo1.getAmigosOnline().values()){
                            System.out.println(amigo1.getClientInfo().getUsuario());
                            System.out.println(amigo2.getClientInfo().getUsuario());
                            System.out.println((amigo2.getClientInfo().isOnline()?"[Online]" : " [Offline]")+"\n" +
                                    "Ahora con getInerface: \n" +
                                    (this.getServer().obtenerClienteInfo(amigo2.getClientInfo().getUsuario()).isOnline()?"[Online]" : " [Offline]"));
                            System.out.println(amigo2.getOnline()?"[Online]" : " [Offline]");
                            System.out.println("información del amigo: \n");
                            System.out.println(amigo1.getClientInfo().getUsuario());
                            System.out.println((amigo1.getClientInfo().isOnline()?"[Online]" : " [Offline]")+"\n" +
                                    "Ahora con getInerface: \n" +
                                    (this.getServer().obtenerClienteInfo(amigo1.getClientInfo().getUsuario()).isOnline()?"[Online]" : " [Offline]"));
                            System.out.println(amigo1.getOnline()?"[Online]" : " [Offline]");


                        }
                    }

                    this.getServer().eliminarDeClientesConectados(this.getClient());
                    this.getClient().cerrarConexion();


                } finally {
                    lock.unlock();
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onNo(ActionEvent actionEvent) {
        Stage stage = (Stage) botonNo.getScene().getWindow(); // Obtener el Stage actual
        stage.close(); // Cerrar la ventana
    }
}
