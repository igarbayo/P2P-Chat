package com.Client;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class ConexionController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Text errorText;

    @FXML
    private TextField hostTextField;

    @FXML
    private Spinner<Integer> puertoSpinner;

    private ServerInterface server;

    //Metodo ejecutado tras click en el boton de conexion.
    @FXML
    public void onBotonConexion(ActionEvent event) {
        String nombreHost;
        if(hostTextField.getText().isEmpty()){
            nombreHost = "localhost";
        } else {
            nombreHost = hostTextField.getText();
        }

        int puerto = puertoSpinner.getValue();
        String registryURL = "rmi://" + nombreHost + ":" + puerto+"/server";

        try{
            server = (ServerInterface) Naming.lookup(registryURL);

            // Cargar el archivo FXML
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("InicioCliente-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load());

            // Carga el stage
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();

            // Pasa la instancia del servidor
            InicioController controller = fxmlLoader.getController();
            controller.setServer(server);
            controller.setIP(nombreHost);
            controller.setPuerto(puerto);

        } catch(NotBoundException | MalformedURLException | RemoteException e){
            System.out.println("Excepcion conexion: " + e.getMessage());
            mostarError("No se pudo conectar con el servidor");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    //Hace visible el mensaje de error de conexion en caso de error
    @FXML
    private void mostarError(String error){
        errorText.setText(error);

        if(!errorText.isVisible()){
            errorText.setVisible(true);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resources) {
        //Mensaje de error invisible
        errorText.setVisible(false);
        //No permite que el spinner tome valores del 0 al 1023.
        SpinnerValueFactory<Integer> valorDefault = new SpinnerValueFactory.IntegerSpinnerValueFactory(1024, 49152, 6789);
        puertoSpinner.setValueFactory(valorDefault);
    }

}
