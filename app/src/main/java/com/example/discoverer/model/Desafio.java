package com.example.discoverer.model;

import android.location.Location;

import com.example.discoverer.config.ConfiguracaoFirebase;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.List;

public class Desafio implements Serializable {

    public static final String STATUS_ATIVO = "ativo";
    public static final String STATUS_INATIVO = "inativo";
    private String id, titulo,descricao, status, key;
    private double distancia;
    private int pontuacao;
    private LatLng localizacaoInicial, localizacaoFinal;
    private List<Ponto> listaPontos;
    private List<LatLng> caminho;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public Desafio(String id, String titulo, String descricao, String status, double distancia, int pontuacao, LatLng localizacaoInicial, LatLng localizacaoFinal, List<Ponto> listaPontos, List<LatLng> caminho) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.status = status;
        this.distancia = distancia;
        this.pontuacao = pontuacao;
        this.localizacaoInicial = localizacaoInicial;
        this.localizacaoFinal = localizacaoFinal;
        this.listaPontos = listaPontos;
        this.caminho = caminho;
    }


    public int getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(int pontuacao) {
        this.pontuacao = pontuacao;
    }

    public List<LatLng> getCaminho() {
        return caminho;
    }

    public void setCaminho(List<LatLng> caminho) {
        this.caminho = caminho;
    }


    public double getDistancia() {
        return distancia;
    }

    public void setDistancia(double distancia) {
        this.distancia = distancia;
    }

    public Desafio() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LatLng getLocalizacaoInicial() {
        return localizacaoInicial;
    }

    public void setLocalizacaoInicial(LatLng localizacaoInicial) {
        this.localizacaoInicial = localizacaoInicial;
    }

    public LatLng getLocalizacaoFinal() {
        return localizacaoFinal;
    }

    public void setLocalizacaoFinal(LatLng localizacaoFinal) {
        this.localizacaoFinal = localizacaoFinal;
    }

    public List<Ponto> getListaPontos() {
        return listaPontos;
    }

    public void setListaPontos(List<Ponto> listaPontos) {
        this.listaPontos = listaPontos;
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference desafios = firebaseRef.child("desafios");
        String idDesafio    = desafios.push().getKey();
        setId(idDesafio);

        desafios.child(getId()).setValue(this);

    }
    public void calcularDistancia(){
        Location lInicio = new Location("localInicial");
        lInicio.setLatitude(localizacaoInicial.latitude);
        lInicio.setLongitude(localizacaoInicial.longitude);

        Location lFinal = new Location("localFinal");
        lFinal.setLatitude(localizacaoFinal.latitude);
        lFinal.setLongitude(localizacaoFinal.longitude);

        Float distancia = lInicio.distanceTo(lFinal);

        setDistancia(distancia);

    }

}

