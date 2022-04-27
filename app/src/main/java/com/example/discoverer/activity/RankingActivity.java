package com.example.discoverer.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discoverer.R;
import com.example.discoverer.adapter.RankingAdapter;
import com.example.discoverer.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RankingActivity extends AppCompatActivity {
    RecyclerView recyclerViewRanking;
    TextView textViewDescricaoRanking, textViewListaVazia;
    public List<Usuario> listaUsuarios = new ArrayList<>();
    RankingAdapter rankingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        inicializarComponentes();
        recuperarUsuarios();
    }


    private void inicializarComponentes() {

        recyclerViewRanking = findViewById(R.id.recyclerUsuarios);
        textViewDescricaoRanking = findViewById(R.id.textViewDescricaoRanking);
        textViewListaVazia = findViewById(R.id.textViewListaVazia);
        textViewListaVazia.setVisibility(View.GONE);





        rankingAdapter = new RankingAdapter(listaUsuarios,getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewRanking.setLayoutManager(layoutManager);
        recyclerViewRanking.setHasFixedSize(true);
        recyclerViewRanking.setAdapter(rankingAdapter);

    }
    public void recuperarUsuarios(){
        //DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference().child("usuarios");
        List<Usuario> lUsuario = new ArrayList<>();
        DatabaseReference usuariosRef = FirebaseDatabase.getInstance().getReference();
        Query query = usuariosRef.child("usuarios").orderByChild("pontuacao");
        //if(usuariosRef !=null){
        if(query !=null){
            recyclerViewRanking.setVisibility(View.VISIBLE);
            textViewListaVazia.setVisibility(View.GONE);
            query.addValueEventListener(new ValueEventListener() {
                //usuariosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Log.d("recuperarUsuarios()", "DataSnapshot "+dataSnapshot.toString());

                    for(DataSnapshot ds : dataSnapshot.getChildren()){
                        Usuario usuario = new Usuario();
                        Log.d("recuperarUsuarios()", "onDataChange: ds: "+ds.toString());
                        // Usuario usuarioRecuperado =(Usuario) ds.getValue(Usuario.class);

                        String nome,nDesc, distP, pontos;
                        double distPD;
                        if ((ds.child("nome").getValue()) == null){
                            nome = ds.child("email").getValue().toString();
                        }else{
                            nome = ds.child("nome").getValue().toString();
                        }

                        nDesc = String.valueOf(ds.child("numeroDescobertas").getValue(Integer.class));

                        distPD = ds.child("distanciaPercorrida").getValue(Double.class);
                       // distPD = distPD/1000;
                        distP = String.valueOf(distPD);
                        pontos = String.valueOf(ds.child("pontuacao").getValue(Double.class));
                        usuario.setNome(nome);
                        usuario.setNumeroDescobertas(Integer.parseInt(nDesc));
                        usuario.setPontuacao(Double.parseDouble(pontos));
                        usuario.setDistanciaPercorrida(Double.parseDouble(distP));
                        Log.d("usuario", "onDataChange: "+usuario.toString());

                        lUsuario.add(usuario);

                      /*  Log.d("listaUsuario", "onDataChange: "+listaUsuarios.get(listaUsuarios.size()-1));
                        Log.d("ds.getValue(Usuario.class);", " / "+
                                        ds.child("nome").getValue().toString()+" / "+
                                + ds.child("numeroDescobertas").getValue(Integer.class)+" / "+
                                        ds.child("distanciaPercorrida").getValue(Double.class)+" / "+
                                        ds.child("pontuacao").getValue(Double.class)+" / "

                                );*/

                       /* usuarioRecuperado.setNome(
                                        ds.child("nome").getValue().toString());
                        Log.d("usuarioRecuperado", "nome: "+usuarioRecuperado.getNome());
                        usuarioRecuperado.setNumeroDescobertas(
                                                ds.child("numeroDescobertas").getValue(Integer.class));
                        Log.d("usuarioRecuperado", "descobertas: "+usuarioRecuperado.getNumeroDescobertas());
                        usuarioRecuperado.setDistanciaPercorrida(
                                                ds.child("distanciaPercorrida").getValue(Double.class));
                        Log.d("usuarioRecuperado", "distancia: "+usuarioRecuperado.getDistanciaPercorrida());
                        usuarioRecuperado.setPontuacao(
                                        ds.child("pontuacao").getValue(Double.class));
                        Log.d("usuarioRecuperado", "pontuacao: "+usuarioRecuperado.getPontuacao());

                        Log.d("recuperarUsuarios()", "onDataChange: usuarioRecuperado: "+usuarioRecuperado.toString());
                        listaUsuarios.add(usuarioRecuperado);*/
                    }


                    /*if (listaUsuarios.size() == 1){
                        listaUsuarios.add(usuarioRecuperado);
                    }else{
                        if ((Double.parseDouble(usuario.getPontuacao()) < listaUsuarios.get(0).getPontuacao()){
                            listaUsuarios.add(usuarioRecuperado);
                        }else{
                            Usuario aux = listaUsuarios.get(0);
                            listaUsuarios.set(0,usuario);
                            listaUsuarios.add(aux);
                        }
                    }*/

                    listaUsuarios.addAll(ordenarLista(lUsuario));
                    rankingAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Log.d("usuariosRef.addValueEventListener", "onCancelled: "+databaseError.toString());

                }
            });


        }else{
            textViewListaVazia.setVisibility(View.VISIBLE);
            recyclerViewRanking.setVisibility(View.GONE);
        }
    }
    public List<Usuario> ordenarLista(List<Usuario> listaUsuario){
        List<Usuario> listaAuxiliar = new ArrayList<>();
        int tamanhoLista = listaUsuario.size();
        for (int i = 1; i <= tamanhoLista; i++){
            listaAuxiliar.add(listaUsuario.get(tamanhoLista-i));
        }

        return listaAuxiliar;
    }

}
