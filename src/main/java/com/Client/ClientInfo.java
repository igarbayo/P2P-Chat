package com.Client;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

public class ClientInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    // Atributos
    private String usuario;
    private String contrasena;
    private CopyOnWriteArrayList<String> listaAmigos;
    private boolean online;

    // Getters y Setters
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public String getContrasena() {
        return contrasena;
    }
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    public CopyOnWriteArrayList<String> getListaAmigos() {
        return listaAmigos;
    }
    public void setListaAmigos(CopyOnWriteArrayList<String> listaAmigos) {
        this.listaAmigos = listaAmigos;
    }
    public boolean isOnline() {
        return this.online;
    }
    public void setOnline(boolean online) {
        this.online = online;
    }

    // Constructores
    public ClientInfo(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.listaAmigos = new CopyOnWriteArrayList<>();
        this.online = false;
    }
    public ClientInfo(String usuario, String contrasena, CopyOnWriteArrayList<String> listaAmigos, boolean online) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.listaAmigos = listaAmigos;
        this.online = online;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ClientInfo that = (ClientInfo) o;
        return Objects.equals(usuario, that.usuario);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(usuario);
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "\n\tusuario='" + usuario + '\'' +
                ", \n\tcontrasena='" + contrasena + '\'' +
                ", \n\tlistaAmigos=" + listaAmigos +
                ", \n\tonline=" + online +
                '}';
    }
}
