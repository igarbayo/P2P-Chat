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

import java.net.URL;
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

    private boolean err = true;



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
        err = true;

        // FUncionamiento principal
        if (username !=null && !username.isEmpty()) {
            try {
                err = this.getServer().existeCliente(username);
                errorText.setVisible(!err);

                // Si existe el cliente
                if (err) {
                    // se envía la solicitud de amistad

                    // Verificar si el destinatario existe en el servidor
                    ClientInfo destinatario = this.getServer().obtenerClienteInfo(username);

                    //System.out.println(destinatario);

                    if (destinatario != null) {
                        // Agregar esta solicitud a la lista de solicitudes del destinatario
                        List<String> listaSolicitudes = destinatario.getListaSolicitudes();
                        if (!listaSolicitudes.contains(this.getClient().getInfo().getUsuario())) { // Evitar duplicados
                            listaSolicitudes.add(this.getClient().getInfo().getUsuario());
                            destinatario.setListaSolicitudes(listaSolicitudes); // Asegúrate de actualizar la referencia
                            this.getServer().actualizarClienteInfo(destinatario); // Actualiza en el servidor
                            //System.out.println(this.getClient().getInfo());
                        }
                    }
                }
                stage.close();

            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
