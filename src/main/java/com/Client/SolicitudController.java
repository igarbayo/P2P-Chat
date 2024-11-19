package com.Client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class SolicitudController extends AbstractVentana {

    @FXML
    private TextField nombreUsuario;
    @FXML
    private Text errorText;
    @FXML
    private Button botonSolicitud;

    private int err = 0;


    @Override
    public void initialize(URL url, ResourceBundle resources) {
        //Mensaje de error invisible
        errorText.setVisible(false);
    }

    @FXML
    public void onSolicitud(ActionEvent actionEvent) {
        String username = nombreUsuario.getText();
        err = 0;
        if (username !=null && !username.isEmpty()) {
            err = this.getClient().enviarSolicitudAmistad(username);
        }
        if (err == -1) {
            errorText.setVisible(true);
        } else {
            errorText.setVisible(false);
        }


    }
}
