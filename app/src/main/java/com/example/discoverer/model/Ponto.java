package com.example.discoverer.model;

import com.google.android.gms.maps.model.LatLng;

public class Ponto {
    String id,nome, status, descricao;
    LatLng localizacao;
    Double visibilidade, pontuacao;

    public static final String STATUS_ATIVO = "ativo";
    public static final String STATUS_INATIVO = "inativo";

    public Ponto() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LatLng getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(LatLng localizacao) {
        this.localizacao = localizacao;
    }

    public Double getVisibilidade() {
        return visibilidade;
    }

    public void setVisibilidade(Double visibilidade) {
        this.visibilidade = visibilidade;
    }

    public Double getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Double pontuacao) {
        this.pontuacao = pontuacao;
    }
}
