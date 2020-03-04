package com.example.discoverer.model;

import com.example.discoverer.config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class Usuario implements Serializable {
     private  String ID;
    private String email;
    private String Senha;
    private String nome;
    private String endereco;
    private String genero;
    private Double distanciaPercorrida;
    private Double pontuacao;
    private Integer numeroDescobertas;

    public Double getDistanciaPercorrida() {
        return distanciaPercorrida;
    }

    public void setDistanciaPercorrida(Double distanciaPercorrida) {
        this.distanciaPercorrida = distanciaPercorrida;
    }

    public Double getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Double pontuacao) {
        this.pontuacao = pontuacao;
    }

    public Integer getNumeroDescobertas() {
        return numeroDescobertas;
    }

    public void setNumeroDescobertas(Integer numeroDescobertas) {
        this.numeroDescobertas = numeroDescobertas;
    }

    public String getID() {
        return ID;
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference usuarios = firebaseRef.child("usuarios").child(getID());
        usuarios.setValue(this);
    }
    public void salvarDesafio(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        //DatabaseReference usuarios = firebaseRef.child("usuarios").child(getID());
        DatabaseReference usuarios = firebaseRef.child("usuarios");


        Map<String, Object> desafiosUpdates = new HashMap<>();
        desafiosUpdates.put(getID(),Usuario.this);
        usuarios.updateChildren(desafiosUpdates);
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    @Exclude
    public String getSenha() {
        return Senha;
    }

    public void setSenha(String senha) {
        Senha = senha;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Usuario() {
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Usuario(String ID, String email, String senha, String nome, String endereco, String genero, Double distanciaPercorrida, Double pontuacao, Integer numeroDescobertas) {
        this.ID = ID;
        this.email = email;
        Senha = senha;
        this.nome = nome;
        this.endereco = endereco;
        this.genero = genero;
        this.distanciaPercorrida = distanciaPercorrida;
        this.pontuacao = pontuacao;
        this.numeroDescobertas = numeroDescobertas;
    }
}
