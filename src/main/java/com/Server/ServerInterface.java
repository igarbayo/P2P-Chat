// P2P. Computación Distribuida
// Curso 2024 - 2025
// Ignacio Garbayo y Carlos Hermida

package com.Server;
import com.Client.ClientInfo;
import com.Client.ClientInterface;

import java.rmi.*;
import java.util.List;
import java.util.Map;

public interface ServerInterface extends Remote {

    // Imprime "hola" por pantalla
    void hola() throws RemoteException;
    // Carga información de base de datos
    void cargarInformacionClientes() throws RemoteException;
    // Guarda información en base de datos
    void guardarInformacionClientes() throws RemoteException;
    // Añade una solicitud de amistad
    void anadirSolicitud(String origen, String destino) throws RemoteException;
    // Elimina una solicitud de amistad
    void eliminarSolicitud(String origen, String destino) throws RemoteException;
    // Devuelve la interfaz de un usuario
    ClientInterface getInterface(String username) throws RemoteException;
    // Envia una notificacion a los amigos de un usuario
    void notificarAmigos(ClientInterface client, String mensaje) throws RemoteException;
    // Actualiza la info guardada para un usuario (a partir de interface)
    void actualizarClienteInfo(ClientInterface clientInfo) throws RemoteException;
    // Actualiza la info guardada para un usuario (a partir de info)
    void actualizarClienteInfo(ClientInfo clientInfo) throws RemoteException;
    // Devuelve la info de un usuario
    ClientInfo obtenerClienteInfo(String username) throws RemoteException;
    // Obtiene la lista de solicitudes de amistad pendientes
    List<String> getSolicitudes(ClientInterface client) throws RemoteException;
    // Comprueba si existe un cliente (a partir de interface)
    boolean existeCliente(ClientInterface client) throws RemoteException;
    // Comprueba si existe un cliente (a partir de usuario)
    boolean existeCliente(String username) throws RemoteException;
    // Añade un cliente
    void anadirCliente(ClientInterface client) throws RemoteException;
    // Añadeu un cliente en estado online
    void anadirClienteOnLine(ClientInterface client) throws RemoteException;
    // Obtiene el mapa de usuarios en línea
    Map<String, ClientInterface> obtenerAmigosEnLinea(ClientInterface cliente) throws RemoteException;
    // Obtiene los amigos de un usuario
    List<String> obtenerAmigos(String usuario) throws RemoteException;
    // Notifica a los usuarios en una lista
    void notificar(List<ClientInterface> clientes, String mensaje) throws RemoteException;
    // Elimina a un usuario del mapa de clientes conectados
    void eliminarDeClientesConectados(ClientInterface cliente) throws RemoteException;
    // Verfica si un usuario está en linea (para mayor seguridad)
    boolean estaLogueado(String usuario) throws RemoteException;
    // En un cierre repentino, cierra la sesión de todos los clientes para guardar
    void setClientesAOffline() throws RemoteException;
    // Actualiza el estado en línea (o no) de un usuario
    void actualizarClienteEnLinea(ClientInterface cliente, int Online) throws RemoteException;

}