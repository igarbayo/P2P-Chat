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
import java.util.ResourceBundle;

public class InicioController extends AbstractVentana {

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

            if(this.getServer().existeCliente(info)){
                // Cargamos la informacion del usuario desde el servidor
                // Verificamos la contraseña
                // Quitñe uso de cargarDatos, daba problemas (no sé muy bien por qué)
                if ((info = this.getServer().obtenerClienteInfo(username)) != null) {
                    if (password.equals(info.getContrasena())) {
                        client.setInfo(info);
                        client.getInfo().setOnline(true);
                        this.getServer().actualizarClienteInfo(client.getInfo());
                        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
                        Scene scene = new Scene(fxmlLoader.load());

                        // Carga el stage
                        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        stage.setScene(scene);
                        stage.show();

                        // Pasa la instancia del servidor y del cliente
                        PrincipalController controller = fxmlLoader.getController();
                        controller.setServer(this.getServer());
                        this.setClient(client);
                        controller.setClient(client);
                        fxmlLoader.setController(controller);
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
        String username=usernameTextField.getText();
        String password=passwordTextField.getText();
        //Aquí iria comprobacion de contraseña y usuario validos
        //-
        //-
        if (username.isEmpty()||password.isEmpty()) {
            mostrarError("Por favor, cubra ambos campos");
            return;
        }

        try {
            Client client = new Client();
            ClientInfo info = new ClientInfo(username, password);
            client.setInfo(info);

            if(this.getServer().existeCliente(info)){
                mostrarError("Este usuario ya existe");
                return;
            }else{
                this.getServer().anadirCliente(client);
                client.getInfo().setOnline(true);
                this.getServer().actualizarClienteInfo(client.getInfo());
                // Cargar el archivo FXML
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PrincipalCliente-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                // Carga el stage
                Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.show();

                // Pasa la instancia del servidor
                PrincipalController controller = fxmlLoader.getController();
                controller.setServer(this.getServer());
                this.setClient(client);
                controller.setClient(client);
                fxmlLoader.setController(controller);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
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
        errorText.setVisible(false);
        Client client = new Client();
    }

}
