package com.Client;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Objects;

public class ClientInfo implements Serializable {

    // Atributos
    private String usuario;
    private String contrasena;
    private List<String> listaAmigos;
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
    public List<String> getListaAmigos() {
        return listaAmigos;
    }
    public void setListaAmigos(List<String> listaAmigos) {
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
        this.listaAmigos = new ArrayList<>();
        this.online = false;
    }
    public ClientInfo(String usuario, String contrasena, List<String> listaAmigos, boolean online) {
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
