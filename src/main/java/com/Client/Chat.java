package com.Client;

import java.util.ArrayList;
import java.util.List;

public class Chat {

    private Client cliente1;
    private Client cliente2;
    private Integer idGrupo;
    private List<Mensaje> mensajes;

    public Client getCliente1() {
        return cliente1;
    }

    public void setCliente1(Client cliente1) {
        this.cliente1 = cliente1;
    }

    public Client getCliente2() {
        return cliente2;
    }

    public void setCliente2(Client cliente2) {
        this.cliente2 = cliente2;
    }

    public Integer getIdGrupo() {
        return idGrupo;
    }

    public void setIdGrupo(Integer idGrupo) {
        this.idGrupo = idGrupo;
    }

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    public Chat(Client cliente1, Client cliente2, Integer idGrupo) {
        this.cliente1 = cliente1;
        this.cliente2 = cliente2;
        this.idGrupo = idGrupo;
        this.mensajes = new ArrayList<>();
    }
}
