package com.example.discoverer.helper;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.discoverer.config.ConfiguracaoFirebase;
import com.example.discoverer.model.Usuario;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class UsuarioFirebase {
    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();


    }
    public static boolean atualizarNomeUsuario(String nome){
    try {
        FirebaseUser user = getUsuarioAtual();
        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest
                .Builder()
                .setDisplayName(nome)
                .build();
        user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(!task.isSuccessful()){
                    Log.d("perfil"," erro ao atualizar");
                }
            }
        });
        return true;
    }catch (Exception e){
        e.printStackTrace();
        return false;
    }
    }

    public static Usuario recuperarUsuarioEspecifico(String id){
        final Usuario usuarioRecuperado = new Usuario();
        DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios")
                .child( id );
        if(usuariosRef != null){
            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario usuario = dataSnapshot.getValue( Usuario.class );

                    usuarioRecuperado.setNome(usuario.getNome());
                    usuarioRecuperado.setEmail(usuario.getEmail());
                    usuarioRecuperado.setEndereco(usuario.getEndereco());
                    usuarioRecuperado.setID(usuario.getID());
                    usuarioRecuperado.setGenero(usuario.getGenero());
                    usuarioRecuperado.setNumeroDescobertas(usuario.getNumeroDescobertas());
                    usuarioRecuperado.setDistanciaPercorrida(usuario.getDistanciaPercorrida());
                    usuarioRecuperado.setPontuacao(usuario.getPontuacao());
                    Log.d("recuperarUsu", "onDataChange: "+usuarioRecuperado.getNome());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
        return usuarioRecuperado;
    }

    public static List<Usuario> recuperarUsuarios(){
        List<Usuario> listaUsuarios = new ArrayList<>();
        DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("usuarios");
        if(usuariosRef !=null){
            usuariosRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Usuario usuario = dataSnapshot.getValue( Usuario.class );
                    Usuario usuarioRecuperado = new Usuario();
                    Log.d("recuperarUsuarios()", "onDataChange: "+usuarioRecuperado.toString());
                    usuarioRecuperado.setNome(usuario.getNome());
                    usuarioRecuperado.setEmail(usuario.getEmail());
                    usuarioRecuperado.setEndereco(usuario.getEndereco());
                    usuarioRecuperado.setID(usuario.getID());
                    usuarioRecuperado.setGenero(usuario.getGenero());
                    usuarioRecuperado.setNumeroDescobertas(usuario.getNumeroDescobertas());
                    usuarioRecuperado.setDistanciaPercorrida(usuario.getDistanciaPercorrida());
                    usuarioRecuperado.setPontuacao(usuario.getPontuacao());
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
                    listaUsuarios.add(usuarioRecuperado);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }


        return  listaUsuarios;
    }

    public static Usuario recuperarUsuarioLogado(){

        FirebaseUser user = getUsuarioAtual();
        final Usuario usuarioRecuperado = new Usuario();
        if(user != null ){

            DatabaseReference usuariosRef = ConfiguracaoFirebase.getFirebaseDatabase()
                    .child("usuarios")
                    .child( getIdentificadorUsuario() );
            usuariosRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Usuario usuario = dataSnapshot.getValue( Usuario.class );
                    Log.d("recuperarUsuario", "onDataChange: "+usuario.getNome());
                    usuarioRecuperado.setNome(usuario.getNome());
                    usuarioRecuperado.setEmail(usuario.getEmail());
                    usuarioRecuperado.setEndereco(usuario.getEndereco());
                    usuarioRecuperado.setID(usuario.getID());
                    usuarioRecuperado.setGenero(usuario.getGenero());
                    usuarioRecuperado.setNumeroDescobertas(usuario.getNumeroDescobertas());
                    usuarioRecuperado.setDistanciaPercorrida(usuario.getDistanciaPercorrida());
                    usuarioRecuperado.setPontuacao(usuario.getPontuacao());
                    Log.d("recuperarUsuario", "onDataChange: "+usuarioRecuperado.getNome());

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        return usuarioRecuperado;
    }

    public static String getIdentificadorUsuario(){
        return getUsuarioAtual().getUid();
    }

    public static void addDesafioGeoFire(double lat, double lon, String idDesafio, String tipoPonto){
        int tamString = idDesafio.length();

        //Define nó de local de usuário
        DatabaseReference localUsuario = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("atividade").child(idDesafio);
        GeoFire geoFire = new GeoFire(localUsuario);

        //Configura localização
        geoFire.setLocation(
                idDesafio+"_"+tipoPonto,
                new GeoLocation(lat, lon),
                new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if( error != null ){
                            Log.d("Erro", "Erro ao salvar local!");
                        }
                    }
                }
        );

    }



}
