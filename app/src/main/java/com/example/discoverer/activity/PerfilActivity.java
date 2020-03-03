package com.example.discoverer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.discoverer.R;
import com.example.discoverer.model.Usuario;

public class PerfilActivity extends AppCompatActivity {
    Usuario usuarioLocal;
    TextView nome, totalKm, pontuacao, totalDescobertas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inicializarComponentes();


    }
    public void recuperarUsuario(){
        if(getIntent() != null){
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            //Log.d("getIntent", "recuperarUsuario: getIntent: " + getIntent().getBundleExtra("usuario"));
           if(bundle.getSerializable("usuario") != null){
                usuarioLocal = (Usuario) bundle.getSerializable("usuario");

                Log.d("recuperarUsuario", "PerfilrecuperarUsuario: "+usuarioLocal.getNome() + " "
                                                                        + usuarioLocal.getID() +"  "
                                                                        + usuarioLocal.getEmail() +"  "
                                                                      );

                Log.d("perfilrecueprar", "recuperar descobertas: "+usuarioLocal.getNumeroDescobertas());


                nome.setText(usuarioLocal.getNome());
                totalKm.setText( String.valueOf(usuarioLocal.getDistanciaPercorrida())+" Km");
                pontuacao.setText(String.valueOf( usuarioLocal.getPontuacao()));
                totalDescobertas.setText(String.valueOf(usuarioLocal.getNumeroDescobertas()));
            }else{
                recarregarUsuario();
            }
        }
    }

    private void recarregarUsuario() {


    }

    public void inicializarComponentes(){

        nome = findViewById(R.id.textViewPerfilNome);
        totalKm = findViewById(R.id.textViewDistKm);
        pontuacao = findViewById(R.id.textViewTotalPontuacao);
        totalDescobertas = findViewById(R.id.textViewTotalDescobertas);
        recuperarUsuario();
    }

    public void abrirTelaDesafio(View view){
        Intent i = new Intent(this, DesafioActivity.class);
        Bundle extras = new Bundle();
         extras.putSerializable("usuario", usuarioLocal);
        i.putExtras(extras);
        startActivity(i);
    }

}
