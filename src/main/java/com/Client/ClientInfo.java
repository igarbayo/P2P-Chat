package com.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientInfo {

    private String usuario;
    private String contrasena;
    private Integer idGrupo;
    private List<Client> listaSolicitudes;

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

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public List<Client> getListaSolicitudes() {
        return listaSolicitudes;
    }

    public void setListaSolicitudes(List<Client> listaSolicitudes) {
        this.listaSolicitudes = listaSolicitudes;
    }

    public ClientInfo(String usuario, String contrasena) {
        this.usuario = usuario;
        this.contrasena = contrasena;
        this.idGrupo = null;
        this.listaSolicitudes = new ArrayList<Client>();
    }

}
