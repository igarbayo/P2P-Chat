package com.Client;

import com.Server.Server;
import com.Server.ServerInterface;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    // Atributos
    private ClientInfo info;
    // los chats se almacenan en cada instancia de cliente de forma local, cada vez que se arranca
    // se borran cuando se cierra la instancia del cliente, puesto que la app tampoco deja mandar
    // mensajes si un cliente no está conectado
    private List<Chat> chats;

    // Getters y Setters
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

    // Constructor
    public Client() {
        this.info = null;
        this.chats = new ArrayList<Chat>();
    }

    // Métodos

    /**
     *
     * @param client
     * @param mensaje
     */
    public void enviarMensaje(Client client, String mensaje) {

    }

    /**
     *
     * @param client
     */
    public void recibirMensaje(Client client) {

    }

    /**
     *
     * @param usuario
     * @param constrasena
     */
    public void signup(String usuario, String constrasena) {

    }

    /**
     *
     * @param usuario
     * @param constrasena
     */
    public void login(String usuario, String constrasena) {

    }



    /**
     *
     * @param
     */
    public void aceptarSolicitudAmistad(ClientInfo clienteSolicitante) {
        // Verificar que el cliente solicitante no sea nulo
        if (clienteSolicitante != null && this.info != null) {

            // 1. Establecer el idGrupo de 'clienteSolicitante' si es null.
            if (clienteSolicitante.getIdGrupo() == null) {
                int fechaActual = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
                System.out.println(fechaActual);
                clienteSolicitante.setIdGrupo(fechaActual);  // Asignar la fecha actual como idGrupo de 'clienteSolicitante'.
            }

            // 2. Establecer el idGrupo de 'this'
            this.info.setIdGrupo(clienteSolicitante.getIdGrupo());  // Asignar el idGrupo de 'clienteSolicitante' a 'this'.

            // 3. Borrar la solicitud de la lista de 'this'
            this.info.getListaSolicitudes().remove(clienteSolicitante.getUsuario());

            // (Opcional) Confirmación
            System.out.println("Solicitud de amistad aceptada: " + this.getInfo().getIdGrupo() + clienteSolicitante.getIdGrupo());
        } else {
            // Si los objetos son nulos, manejar el error
            System.out.println("Error: Cliente o información no válida.");
        }
    }


    public void rechazarSolicitudAmistad(ClientInfo clienteSolicitante) {
        // Eliminar la solicitud de la lista de solicitudes
        this.info.getListaSolicitudes().remove(clienteSolicitante.getUsuario());
    }

    public void dejarAmistad(ClientInfo client) {

    }

    public boolean existe(String usuario) {
        return false;
    }

    public List<String> obtenerNombresDeUsuario(List<ClientInfo> listaClientInfo) {
        return listaClientInfo.stream()  // Convierte la lista en un stream
                .map(ClientInfo::getUsuario) // Mapea cada ClientInfo a su atributo usuario
                .collect(Collectors.toList()); // Recoge los resultados en una lista
    }










}
