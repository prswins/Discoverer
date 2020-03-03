package com.example.discoverer.model;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.discoverer.config.ConfiguracaoFirebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Atividade implements Serializable {
    private String idUsuario, idDesafio, idAtividade, key;
    private Long tempoTotal;
    private Double pontuacao;
    private int totalDescobertas;
    private String data;

    public String getKey() {
        return key;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getIdDesafio() {
        return idDesafio;
    }

    public void setIdDesafio(String idDesafio) {
        this.idDesafio = idDesafio;
    }

    public String getIdAtividade() {
        return idAtividade;
    }

    public void setIdAtividade(String idAtividade) {
        this.idAtividade = idAtividade;
    }

    public Long getTempoTotal() {
        return tempoTotal;
    }

    public void setTempoTotal(Long tempoTotal) {
        this.tempoTotal = tempoTotal;
    }

    public Double getPontuacao() {
        return pontuacao;
    }

    public void setPontuacao(Double pontuacao) {
        this.pontuacao = pontuacao;
    }

    public int getTotalDescobertas() {
        return totalDescobertas;
    }

    public void setTotalDescobertas(int totalDescobertas) {
        this.totalDescobertas = totalDescobertas;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public Atividade(String idUsuario, String idDesafio, Long tempoTotal, Double pontuacao, int totalDescobertas) {
        this.idUsuario = idUsuario;
        this.idDesafio = idDesafio;
        this.tempoTotal = tempoTotal;
        this.pontuacao = pontuacao;
        this.totalDescobertas = totalDescobertas;
        SimpleDateFormat formatarData = new SimpleDateFormat("dd-MM-yyyy, hh:mm:ss, zzz");
        this.data = formatarData.format( new Date());
    }

    public void salvar(){
        DatabaseReference firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        DatabaseReference atividades = firebaseRef.child("atividades");
        String atividade  = atividades.push().getKey();
        setIdAtividade(atividade);
        atividades.child(getIdAtividade()).setValue(this);
        atividades.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("atividade", "onDataChange: "+dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
