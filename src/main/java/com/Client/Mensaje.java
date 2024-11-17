package com.Client;

public class Mensaje {

    private Client clienteOrigen;
    private Client clienteDestino;
    private String contenido;

    public Client getClienteOrigen() {
        return clienteOrigen;
    }

    public void setClienteOrigen(Client clienteOrigen) {
        this.clienteOrigen = clienteOrigen;
    }

    public Client getClienteDestino() {
        return clienteDestino;
    }

    public void setClienteDestino(Client clienteDestino) {
        this.clienteDestino = clienteDestino;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Mensaje(Client clienteOrigen, Client clienteDestino, String contenido) {
        this.clienteOrigen = clienteOrigen;
        this.clienteDestino = clienteDestino;
        this.contenido = contenido;
    }
}
