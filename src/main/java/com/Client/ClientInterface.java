package com.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

public interface ClientInterface extends Remote {

    void addAmigoOnline(String nombre, ClientInterface client) throws RemoteException;
    void addNotificacion(String notificacion) throws RemoteException;
    List<String> getNotificaciones() throws RemoteException;

    void addChat(Chat chat) throws RemoteException;

    ClientInterface getInterface(String username) throws RemoteException;

    void crearChat(ClientInterface clienteDestino) throws RemoteException;

    void recargarVentana() throws RemoteException;

    String getNombre() throws RemoteException;

    boolean getOnline() throws RemoteException;

    void notificarClientes(Map<String, ClientInterface> mapa, String mensaje) throws RemoteException;

    ClientInfo getClientInfo() throws RemoteException;

    PrincipalController getController() throws RemoteException;
    
    Map<String, ClientInterface> getAmigosOnline() throws RemoteException;

    void setAmigosOnline(Map<String, ClientInterface> amigosOnline) throws RemoteException;

    void setListaAmigos(List<String> lista) throws RemoteException;

    List<String> obtenerNombresDeUsuario(List<ClientInterface> listaClientInterface) throws RemoteException;

    void setPrincipalController(PrincipalController principalController) throws RemoteException;
    // Método que se llama para enviar un mensaje a otro cliente
    void recibirMensaje(Mensaje mensaje) throws RemoteException;

    public boolean enviarMensaje(ClientInterface clientDestino, Mensaje mensaje) throws RemoteException;

    // Método para recibir confirmación de amistad
    void confirmarAmistad(String username) throws RemoteException;

    //Metodo para recibir informacion del server
    void recibirNotificacion(String mensaje)throws RemoteException;

    void setOffline(Map<String, ClientInterface> mapa) throws RemoteException;
    void setOnline(Map<String, ClientInterface> mapa) throws RemoteException;



}
