package com.Client;

import com.Server.ServerInterface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Client extends Application {


    private ClientInfo info;
    // los chats se almacenan en cada instancia de cliente de forma local, cada vez que se arranca
    // se borran cuando se cierra la instancia del cliente, puesto que la app tampoco deja mandar
    // mensajes si un cliente no está conectado
    private List<Chat> chats;

    public ClientInfo getInfo() {
        return info;
    }

    public void setInfo(ClientInfo info) {
        this.info = info;
    }

    public List<Chat> getChats() {
        return chats;
    }

    public void setChats(List<Chat> chats) {
        this.chats = chats;
    }

    ConexionController conexionControlador;
    InicioController inicioControlador;
    public Client() {
        this.info = null;
        this.chats = new ArrayList<Chat>();
    }

    // Métodos
    public void enviarMensaje(Client client, String mensaje) {

    }

    public void recibirMensaje(Client client) {

    }

    public void signup(String usuario, String constrasena) {

    }

    public void login(String usuario, String constrasena) {

    }

    public void enviarSolicitudAmistad(Client client) {

    }

    public void aceptarSolicitudAmistad(Client client) {

    }

    public void rechazarSolicitudAmistad(Client client) {

    }

    public void hacerseAmigo(Client client) {

    }

    public void dejarAmistad(Client client) {

    }

    public boolean existe(String usuario) {
        return false;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader conexionLoader = new FXMLLoader(getClass().getResource("ConexionCliente-view.fxml"));
        Scene conexionScene= new Scene(conexionLoader.load());
        Stage conexionStage = new Stage();
        conexionStage.setScene(conexionScene);
        conexionStage.setResizable(false);
        conexionStage.setOnCloseRequest(event -> {
            System.out.println("Se ha cerrado la ventana de establecimiento de conexión");
            System.exit(0);
        });
        conexionControlador = conexionLoader.getController();
        conexionStage.show();

    }
    public static void main(String[] args) {
        launch();
    }

}
