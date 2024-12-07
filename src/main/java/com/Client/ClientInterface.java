// P2P. Computación Distribuida
// Curso 2024 - 2025
// Ignacio Garbayo y Carlos Hermida

package com.Client;

import com.Client.gui.PrincipalController;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public interface ClientInterface extends Remote {

    // Añade un amigo al mapa de amigos online
    void addAmigoOnline(String nombre, ClientInterface client) throws RemoteException;
    // Añade una notificación
    void addNotificacion(String notificacion) throws RemoteException;
    // Devuelve las notificaciones
    CopyOnWriteArrayList<String> getNotificaciones() throws RemoteException;
    // Añade un chat con un amigo
    void addChat(Chat chat) throws RemoteException;
    // Obtiene la interfaz de un amigo
    ClientInterface getInterface(String username) throws RemoteException;
    // Crea un chat con un amigo
    void crearChat(ClientInterface clienteDestino) throws RemoteException;
    // Obtiene el nombre guardado en el servidor
    String getNombre() throws RemoteException;
    // Verifica si está online
    boolean getOnline() throws RemoteException;
    // Devuelve la información del usuario
    ClientInfo getClientInfo() throws RemoteException;
    // Obtiene el controlador principal asociado al usuario
    PrincipalController getController() throws RemoteException;
    // Devuelve el mapa de amigos online
    Map<String, ClientInterface> getAmigosOnline() throws RemoteException;
    // Establece los amigos online
    void setAmigosOnline(Map<String, ClientInterface> amigosOnline) throws RemoteException;
    // Establece los amigos
    void setListaAmigos(List<String> lista) throws RemoteException;
    // Establece el controlador principal asociado
    void setPrincipalController(PrincipalController principalController) throws RemoteException;
    // Recibe un mensaje de otro cliente
    void recibirMensaje(Mensaje mensaje) throws RemoteException;
    // Envía un mensake
    boolean enviarMensaje(ClientInterface clientDestino, Mensaje mensaje) throws RemoteException;
    // Confirma una solicitud de amistad
    void confirmarAmistad(String username) throws RemoteException;
    // Recibe una notificación para imprimir
    void recibirNotificacion(String mensaje)throws RemoteException;
    // Establece el bit online en las info asociadas a su usuario de todos sus amigos
    void setOnline(Map<String, ClientInterface> mapa) throws RemoteException;
    // Añade un amigo a su mapa de amigos online
    void addAmigoOnline(ClientInterface client) throws RemoteException;
    // Guarda claves de encriptado
    void saveForUser(String username, byte[] key, byte[] nonce) throws RemoteException, IOException;
    // Obtener la info de un amigo
    ClientInfo obtenerAmigoInfo(String usernameAmigo) throws RemoteException;
}
