package com.Client;

import com.Server.Server;
import com.Server.ServerInterface;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class InicioController extends AbstractVentana {

    // Atributos
    private String IP;
    private int puerto;

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public int getPuerto() {
        return puerto;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    // Elementos gráficos
    @FXML
    private AnchorPane anchorPane;
    @FXML
    private Text errorText;
    @FXML
    private TextField usernameTextField;
    @FXML
    private TextField passwordTextField;

    // Métodos de acciones

    @FXML
    public void onBotonLogin(ActionEvent event) {
        String username=usernameTextField.getText();
        String password=passwordTextField.getText();
        //Aquí iria comprobacion de contraseña y usuario validos
        //-
        //-
        if (username.isEmpty()||password.isEmpty()) {
            mostrarError("Por favor, cubra ambos campos");
            return;
        }

        try{
            Client client = new Client();
            ClientInfo info = new ClientInfo(username, password);
            client.setInfo(info);

            if(this.getServer().existeCliente(client)){
                // Cargamos la informacion del usuario desde el servidor
                // Verificamos la contraseña
                //this.getServer().getInterface(username);
                if ((info = this.getServer().obtenerClienteInfo(username)) != null) {
                    if (password.equals(info.getContrasena())) {
                        client.setInfo(info);
                        client.getInfo().setOnline(true);
                        this.getServer().actualizarClienteInfo(client);

                        // Registro RMI
                        client.registrarCliente(IP, puerto);
                        this.getServer().anadirClienteOnLine(client);

                        // Loader
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
                        Scene scene = new Scene(fxmlLoader.load());

                        // Carga el stage
                        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        stage.setScene(scene);
                        stage.show();

                        // Pasa la instancia del servidor y del cliente
                        PrincipalController controller = fxmlLoader.getController();
                        controller.setServer(this.getServer());
                        client.setIP(IP);
                        client.setPuerto(puerto);
                        client.setPrincipalController(controller);
                        this.setClient(client);

                        String conectado = "Conectado: " + this.getClient().getInfo().getUsuario();
                        this.getServer().notificarAmigos(client, conectado);

                        controller.setClient(client);
                        fxmlLoader.setController(controller);

                        //Debug
                        System.out.println(this.getClient().getInfo().getListaAmigos());
                    } else {
                        mostrarError("Usuario o contraseña incorrectos");
                    }
                } else {
                    mostrarError("Usuario o contraseña incorrectos");
                }
            }else{
                mostrarError("Usuario o contraseña incorrectos");
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }


    @FXML
    public void onBotonRegistrar(ActionEvent event) {
        String username = usernameTextField.getText();
        String password = passwordTextField.getText();

        if (username == null || password == null || username.trim().isEmpty() || password.trim().isEmpty()) {
            mostrarError("Por favor, complete ambos campos correctamente");
            return;
        }

        try {
            ServerInterface server = this.getServer();
            if (server == null) {
                mostrarError("Error: No hay conexión con el servidor");
                return;
            }

            // Verificar si el usuario ya existe
            if (server.existeCliente(username.trim())) {
                mostrarError("Este nombre de usuario ya existe");
                return;
            }

            // Crear la información del cliente
            ClientInfo info = new ClientInfo(username.trim(), password);

            // Registrar la información del cliente en el servidor

            // Crear el cliente local después del registro exitoso
            Client client = new Client();
            client.setInfo(info);

            // Configurar el estado online y registro RMI
            //if (IP == null || puerto == 0) {
            //    mostrarError("Error: IP o puerto no configurados");
            //    return;
            //}

            // Registrar el cliente en RMI
            client.registrarCliente(IP, puerto);
            this.getServer().anadirCliente(client);
            server.anadirClienteOnLine(client);

            // Actualizar estado online
            info.setOnline(true);
            server.actualizarClienteInfo(client);

            // Cargar la ventana principal
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            PrincipalController controller = fxmlLoader.getController();
            controller.setServer(server);
            client.setIP(IP);
            client.setPuerto(puerto);
            client.setPrincipalController(controller);

            this.setClient(client);
            controller.setClient(client);

            String conectado = "Conectado: " + this.getClient().getInfo().getUsuario();
            this.getServer().notificarAmigos(client, conectado);

            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (RemoteException e) {
            mostrarError("Error de conexión con el servidor: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            mostrarError("Error al cargar la interfaz: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }


    @FXML
    private void mostrarError(String error) {
        errorText.setText(error);
        if(!errorText.isVisible()) {
            errorText.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        Platform.runLater(() -> {
            errorText.setVisible(false);
        });
    }

}
