package com.example.discoverer.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.discoverer.R;
import com.google.android.material.snackbar.Snackbar;

public class AtividadeActivity extends AppCompatActivity {
    public static final String ATIVIDADE_AGUARDANDO_INICIO = "aguardando";
    public static final String ATIVIDADE_INICIADA = "iniciada";
    public static final String ATIVIDADE_PARADA = "parada";
    public static final String ATIVIDADE_FINALIZANDO = "finalizando";

    public String status_atual;

    Button buttonIniciar;
    TextView pontuacao, tempo;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



    }
    public void iniciarAtividade(){
        status_atual = ATIVIDADE_INICIADA;
    }
    public void pararAtividade(){
        status_atual = ATIVIDADE_PARADA;
    }
    public void finalizarAtividade(){
        status_atual = ATIVIDADE_FINALIZANDO;
    }
    public void inicilizarcomponente(){
        status_atual = ATIVIDADE_AGUARDANDO_INICIO;

        pontuacao.findViewById(R.id.textViewPontuacao);
        tempo.findViewById(R.id.textViewTempo);

        buttonIniciar = findViewById(R.id.buttonIniciar);
        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Iniciando atividade", Snackbar.LENGTH_LONG).show();

                iniciarAtividade();
            }
        });
    }

}
