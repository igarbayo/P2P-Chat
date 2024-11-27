package com.Client;

import com.Server.Server;
import com.Server.ServerInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.ResourceBundle;

public class SolicitudController extends AbstractVentana {

    @FXML
    private TextField nombreUsuario;
    @FXML
    private Text errorText;
    @FXML
    private Button botonSolicitud;

    private boolean valido = true;



    @Override
    public void initialize(URL url, ResourceBundle resources) {
        //Mensaje de error invisible
        errorText.setVisible(false);
    }

    @FXML
    public void onSolicitud(ActionEvent actionEvent) {

        // Obtenemos el stage actual
        Stage stage = (Stage) botonSolicitud.getScene().getWindow();

        // Obtenemos el destinatario
        String username = nombreUsuario.getText();

        // Variable de mensaje de error
        valido = true;

        // Funcionamiento principal
        if (username !=null && !username.isEmpty()) {
            try {
                valido = this.getServer().existeCliente(username);

                // Si existe el cliente
                if (valido) {
                    // Verificar si el destinatario existe en el servidor
                    ClientInterface destinatario = this.getServer().getInterface(username);
                    // Verificar que origen y destinatario no sean ya amigos
                    if (destinatario != null && !this.getClient().getInfo().getListaAmigos().contains(destinatario.getNombre())) {
                        // Agregar esta solicitud a la lista de solicitudes del destinatario
                        this.getServer().anadirSolicitud(this.getClient().getNombre(), destinatario.getNombre());
                        String solic = "Recibida solicitud de " + this.getClient().getNombre();
                        this.getClient().notificarRecarga(destinatario, solic);
                        stage.close();
                    } else {
                        valido = false;
                    }
                    System.out.println(valido);
                    errorText.setVisible(!valido);
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
