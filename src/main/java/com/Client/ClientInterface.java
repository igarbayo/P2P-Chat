package com.Client;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {

    void setPrincipalController(PrincipalController principalController) throws RemoteException;
    // Método que se llama para enviar un mensaje a otro cliente
    void recibirMensaje(Mensaje mensaje) throws RemoteException;

    // Método para recibir notificación de otro cliente conectado
    void notificarConexion(String username) throws RemoteException;

    // Método para recibir notificación de desconexión
    void notificarDesconexion(String username) throws RemoteException;

    // Método para recibir notificación de solicitud de amistad
    void recibirSolicitudAmistad(String fromUser) throws RemoteException;

    // Método para recibir confirmación de amistad
    void confirmarAmistad(String username) throws RemoteException;

    //Metodo para recibir informacion del server
    void recibirNotificacion(String mensaje)throws RemoteException;
}
