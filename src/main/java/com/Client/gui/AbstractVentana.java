// P2P. Computación Distribuida
// Curso 2024 - 2025
// Ignacio Garbayo y Carlos Hermida

package com.Client.gui;

import com.Client.Client;
import com.Client.ClientInfo;
import com.Client.ClientInterface;
import com.Server.ServerInterface;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.RemoteException;

public abstract class AbstractVentana implements Initializable {

    // Referencia al servidor
    private ServerInterface server;
    public void setServer(ServerInterface server) {
        this.server = server;
    }
    public ServerInterface getServer() {
        return server;
    }

    // Referencia al cliente
    private Client client;
    public void setClient(Client client) {
        this.client = client;
    }
    public Client getClient() {
        return client;
    }

    private String ip;
    private int puerto;

    private boolean isClosing = false;

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setPuerto(int puerto) {
        this.puerto = puerto;
    }

    public String getIp() {
        return ip;
    }

    public int getPuerto() {
        return puerto;
    }

    public void safeHandleWindowClose() {
        if (isClosing) return; // Evitar múltiples ejecuciones
        isClosing = true;

        try {
            handleWindowClose(); // Tu lógica de cierre
        } catch (RemoteException e) {
            e.printStackTrace(); // Manejo de errores
        } finally {
            //Platform.exit(); // Cierra JavaFX de manera segura
            System.exit(0);  // Termina el programa
        }
    }

    public void handleWindowClose() throws RemoteException {
        // Cuando la ventana se cierra, se establece setOnline a false
        if (this.getClient() != null && this.getClient().getInfo() != null) {
            ClientInfo info = this.getClient().getInfo();
            info.setOnline(false);
            this.getClient().setInfo(info);
            this.getServer().actualizarClienteInfo(this.getClient());
            if (this.getClient().getAmigosOnLine()!=null) {
                for(ClientInterface amigo : this.getClient().getAmigosOnLine().values()){
                    this.getServer().actualizarClienteInfo(amigo);
                }
            }
            this.getServer().eliminarDeClientesConectados(this.getClient());
            this.getClient().cerrarConexion();
        }
    }

    public boolean estaServidorDisponible() {
        try {
            // Llama a un método remoto simple como "ping" o verifica algún estado
            server.hola();
            return true;
        } catch (java.rmi.ConnectException e) {
            System.err.println("Servidor no disponible: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Error al verificar el servidor: " + e.getMessage());
            return false;
        }
    }


}
