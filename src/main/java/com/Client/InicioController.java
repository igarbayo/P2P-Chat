package com.Client;

import com.Server.Server;
import com.Server.ServerInterface;
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
    @FXML
    private Button botonProbar;

    // Métodos de acciones
    @FXML
    public void probar(ActionEvent event) {
        try {
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

            // Creo un cliente de prueba
            Client client = new Client();
            this.setClient(client);
            controller.setClient(client);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void onBotonLogin(ActionEvent event) {
        String username=usernameTextField.getText();
        String password=passwordTextField.getText();
        //Aquí iria comprobacion de contraseña y usuario validos
        //-
        //-


        /*try{

        }catch(){

        }*/
    }

    @FXML
    public void onBotonRegistrar(ActionEvent event) {
        String username=usernameTextField.getText();
        String password=passwordTextField.getText();
        //Aquí iria comprobacion de contraseña y usuario validos
        //-
        //-

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
    }

}
