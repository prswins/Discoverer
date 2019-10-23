package com.example.discoverer.model;

import com.example.discoverer.config.ConfiguracaoFirebase;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;

public class Localizacao {
    private String id, idUsuario, latitude, longitude, status, tipo, alcance, pontuacao;
    private LatLng latLngLoclizacao;

    public static final String STATUS_ATIVO = "ativo";
    public static final String STATUS_INATIVO = "inativo";
    public static final String TIPO_PONTO_TRAJETO = "ponto_do_trajeto";
    public static final String TIPO_PONTO_PARA_DESCOBERTA = "ponto_para_descoberta";
    public static final String TRAJETO_INICIO = "trajeto_inicio";
    public static final String TRAJETO_FIM = "trajeto_fim";

    public Localizacao() {
    }

    public void salvarLocalizacao(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference localizacoes = firebaseRef.child("localizacoes");
        String idLocalizacao = localizacoes.push().getKey();
        setId(idLocalizacao);

        localizacoes.child(getId()).setValue(this);

    }

    public LatLng getLatLngLoclizacao() {
        return latLngLoclizacao;
    }

    public void setLatLngLoclizacao(LatLng latLngLoclizacao) {
        this.latLngLoclizacao = latLngLoclizacao;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getAlcance() {
        return alcance;
    }

    public void setAlcance(String alcance) {
        this.alcance = alcance;
    }

    public String getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(String pontuacao) {
        this.pontuacao = pontuacao;
    }
}
