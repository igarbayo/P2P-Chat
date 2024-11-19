package com.Client;

import com.Server.ServerInterface;
import javafx.fxml.Initializable;

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
}
