package com.Client;

import com.Server.ServerInterface;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;

public class InicioController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Text errorText;

    @FXML
    private TextField usernameTextField;

    @FXML
    private TextField passwordTextField;

    private ServerInterface server;
    //private ClientInterface client;

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
