package com.Client;

import java.util.Objects;

public class Mensaje {

    // Atributos
    private ClientInfo clienteOrigen;
    private ClientInfo clienteDestino;
    private String contenido;

    // Getters y setters
    public ClientInfo getClienteOrigen() {
        return clienteOrigen;
    }
    public void setClienteOrigen(ClientInfo clienteOrigen) {
        if (clienteOrigen!=null) {
            this.clienteOrigen = clienteOrigen;
        }
    }
    public ClientInfo getClienteDestino() {
        return clienteDestino;
    }
    public void setClienteDestino(ClientInfo clienteDestino) {
        if (clienteDestino!=null) {
            this.clienteDestino = clienteDestino;
        }
    }
    public String getContenido() {
        return contenido;
    }
    public void setContenido(String contenido) {
        if (contenido!=null) {
            this.contenido = contenido;
        }
    }

    // Constructores
    public Mensaje(ClientInfo clienteOrigen, ClientInfo clienteDestino, String contenido) {
        if (clienteOrigen!=null) {
            this.clienteOrigen = clienteOrigen;
        }
        if (clienteDestino!=null) {
            this.clienteDestino = clienteDestino;
        }
        if (contenido!=null) {
            this.contenido = contenido;
        }
    }

    // Equals para comparaciones
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Mensaje mensaje = (Mensaje) o;
        return Objects.equals(clienteOrigen, mensaje.clienteOrigen) && Objects.equals(clienteDestino, mensaje.clienteDestino) && Objects.equals(contenido, mensaje.contenido);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clienteOrigen, clienteDestino, contenido);
    }

    @Override
    public String toString() {
        return "[" + clienteOrigen.getUsuario() + "] " + contenido;
    }
}
