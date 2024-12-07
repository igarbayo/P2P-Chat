package com.Client.gui;

import com.Client.ClientInfo;
import com.Client.security.Bcrypt;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class CambiarController extends AbstractVentana {

    @FXML
    private Label usernameLabel;
    @FXML
    private TextField newPassword;
    @FXML
    private TextField oldPassword;
    @FXML
    private Button botonCambiar;
    @FXML
    private Text errorText;

    private boolean valido = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //Mensaje de error invisible
        Platform.runLater(() -> {
            // Mostramos el nombre del usuario conectado
            usernameLabel.setText(this.getClient().getInfo().getUsuario());
            errorText.setVisible(false);
        });
    }


    @FXML
    public void onCambiar(ActionEvent event) {
        // Obtenemos el stage actual
        Stage stage = (Stage) botonCambiar.getScene().getWindow();

        // Variable de mensaje de error
        valido = true;
        String passwdNueva = newPassword.getText();
        String passwdVieja = oldPassword.getText();
        try {
            if (this.getServer().estaLogueado(this.getClient().getInfo().getUsuario())) {
                ClientInfo info;
                if ((info = this.getServer().obtenerClienteInfo(this.getClient().getInfo().getUsuario())) != null) {
                    String hashedPassword = info.getContrasena();
                    if (Bcrypt.verifyPassword(passwdVieja, hashedPassword)) {
                        // Si la contraseña vieja coincide con la almacenada en la BDD
                        // Hashear la contraseña
                        String newHashedPassword = Bcrypt.hashPassword(passwdNueva);
                        info.setContrasena(newHashedPassword);
                        this.getServer().actualizarClienteInfo(info);
                        this.getClient().getInfo().setContrasena(newHashedPassword);
                        // Volvemos al stage anterior
                        stage.close();
                    } else {
                        valido = false;
                    }
                } else {
                    valido = false;
                }
            } else {
                valido = false;
            }
            errorText.setVisible(!valido);



        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
