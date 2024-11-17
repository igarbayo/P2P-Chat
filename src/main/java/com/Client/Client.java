package com.Client;

import java.util.ArrayList;
import java.util.List;

public class Client {

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

}
