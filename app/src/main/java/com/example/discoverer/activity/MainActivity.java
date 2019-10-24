package com.example.discoverer.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discoverer.R;
import com.example.discoverer.RecyclerItemClickListener;
import com.example.discoverer.adapter.DesafioAdapterMain;
import com.example.discoverer.helper.Permissoes;
import com.example.discoverer.model.Desafio;
import com.example.discoverer.model.Ponto;
import com.example.discoverer.model.Usuario;
import com.google.android.gms.maps.model.LatLng;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private TextView textViewSaudacao, textViewBreveDescricao, textViewTituloSessao1, textViewDescricaoSesao1;
    private RecyclerView recyclerDesafios;
    private DesafioAdapterMain desafioAdapter;
    private String[] permissoes = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private Usuario usuarioLocal = new Usuario();
    private List<Desafio> listaDesafio = new ArrayList<>();
    private ArFragment arFragment;
    private Boolean auth = false;
    private Boolean isModelPlaced = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Discoverer");

        Permissoes.validarPermissoes(permissoes, this, 1);

        Intent intent = getIntent();
        Bundle dados = new Bundle();
        dados = intent.getExtras();

        if(dados != null){
            String user_token = dados.getString("usuarioToken");
            Log.d("intent", "onCreate: "+user_token);
            if(user_token !=null){
                auth = true;
                String nomeUsuario = dados.getString("usuarioNome");
                Log.d("intent", " onCreate"+nomeUsuario);
                usuarioLocal.setNome(nomeUsuario);
            }


        }else {
            Log.d("intent", "bundle nulo ");
        }


        //firebaseRef = ConfiguracaoFirebase.getFirebaseDatabase();
        recuperarDesafios();
        inicializarComponentes();




    }
    private void recuperarDesafios(){

        DatabaseReference desafios = FirebaseDatabase.getInstance().getReference().child("desafios");
        //Query desafiosPesquisa = desafios.child("status").equalTo("ATIVADO");

        desafios.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() > 0 ){
                    Log.d("desafio1", "onDataChange: desafios: "+dataSnapshot.getValue(Desafio.class));
                    recyclerDesafios.setVisibility(View.VISIBLE);


                    for(DataSnapshot ds : dataSnapshot.getChildren()){

                        Desafio desafio = new Desafio();
                        desafio.setId((String) ds.child("id").getValue());
                        desafio.setTitulo((String) ds.child("titulo").getValue());
                        desafio.setDescricao((String) ds.child("descricao").getValue());
                        String sDistancia = String.valueOf(ds.child("distancia").getValue());
                        double dDistancia = Double.valueOf(sDistancia);
                        desafio.setDistancia(dDistancia);
                        desafio.setPontuacao(Integer.parseInt(ds.child("pontuacao").getValue().toString()));

                        desafio.setLocalizacaoInicial(
                                new LatLng(
                                        Double.parseDouble(ds.child("localizacaoInicial").child("latitude").getValue().toString()),
                                        Double.parseDouble(ds.child("localizacaoInicial").child("longitude").getValue().toString()))
                        );

                        desafio.setLocalizacaoFinal(
                                new LatLng(
                                        Double.parseDouble(ds.child("localizacaoFinal").child("latitude").getValue().toString()),
                                        Double.parseDouble(ds.child("localizacaoFinal").child("longitude").getValue().toString()))
                        );


                        final  List<Ponto> pontos = new ArrayList<>();
                        for(DataSnapshot dsListaPontos : ds.child("listaPontos").getChildren()){
                            Ponto ponto = new Ponto();
                            ponto.setNome(
                                    dsListaPontos.child("nome").getValue().toString()
                            );
                            ponto.setDescricao(
                                    dsListaPontos.child("descricao").getValue().toString()
                            );
                            ponto.setPontuacao(
                                    dsListaPontos.child("pontuacao").getValue(Double.class)
                            );
                            ponto.setVisibilidade(
                                    dsListaPontos.child("visibilidade").getValue(Double.class)
                            );
                            ponto.setStatus(
                                    dsListaPontos.child("status").getValue().toString()
                            );
                            ponto.setLocalizacao(
                                    new LatLng(
                                            Double.parseDouble(dsListaPontos.child("localizacao").child("latitude").getValue().toString()),
                                            Double.parseDouble(dsListaPontos.child("localizacao").child("longitude").getValue().toString())
                                    )
                            );

                            //Log.d("ponto", "ponto: "+ponto.toString());
                            pontos.add(ponto);

                        }
                        desafio.setListaPontos(pontos);

                        final List<LatLng> caminho = new ArrayList<>();
                        for(DataSnapshot dsCaminho :ds.child("caminho").getChildren()){
                            LatLng local = new LatLng(
                                    Double.parseDouble(dsCaminho.child("latitude").getValue().toString()),
                                    Double.parseDouble(dsCaminho.child("longitude").getValue().toString())
                            );
                            caminho.add(local);
                        }
                        desafio.setCaminho(caminho);
                        listaDesafio.add(desafio);
                        Log.d("desafio1", "onDataChange: desafios: "+desafio.getTitulo());
                        desafioAdapter.notifyDataSetChanged();
                    }



                }else{
                    //textDesafios.setVisibility(View.VISIBLE);
                    //recyclerDesafios.setVisibility(View.GONE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void inicializarComponentes() {
        textViewSaudacao =findViewById(R.id.textViewSaudacao);
        textViewBreveDescricao = findViewById(R.id.textViewBreveDescricao);
        textViewTituloSessao1 = findViewById(R.id.textViewTituloSessao1);
        textViewDescricaoSesao1 = findViewById(R.id.textViewDescSessao1);
        recyclerDesafios = findViewById(R.id.RecyclerViewSessao1);


        if(auth== false){
            textViewSaudacao.setText(R.string.main_saudacao_visitante);
            textViewBreveDescricao.setText(R.string.main_descricao_app);
            textViewDescricaoSesao1.setText(R.string.main_descricao_sessao);
        }else{
            textViewSaudacao.setText(textViewSaudacao.getText()+" "+ usuarioLocal.getNome());
            textViewBreveDescricao.setText(R.string.main_descricao_app_logado);
            textViewDescricaoSesao1.setText(R.string.main_descricao_sessao_logado);
        }








        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.mainFragmentAR);
        // objeto eh adicionado com click//
        /*
        if (arFragment != null) {
            arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener(){
                @Override
                public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                    Anchor anchor = hitResult.createAnchor();

                    ModelRenderable.builder()
                            .setSource(getApplicationContext(), R.raw.bear)
                            .build()
                            .thenAccept(renderable -> adicionarModelo3d(anchor, renderable))
                            .exceptionally(
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(),"errro ao exibir objeto",Toast.LENGTH_LONG).show();
                                        Log.e("RENDERABLEOBJETO", "Unable to load Renderable.", throwable);
                                        return null;
                                    });



                }
            });
        }*/

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);

        desafioAdapter = new DesafioAdapterMain(listaDesafio,getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerDesafios.setLayoutManager(layoutManager);
        recyclerDesafios.setHasFixedSize(true);
        recyclerDesafios.setAdapter(desafioAdapter);
        recyclerDesafios.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerDesafios,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Desafio desafio = (Desafio) listaDesafio.get(position);
                                Toast.makeText(getApplicationContext(),"Carregando desafio "+ desafio.getTitulo(), Toast.LENGTH_LONG).show();
                                Log.d("onItemClick: ", "onItemClick: "+listaDesafio.get(position).getTitulo());


                                Intent i  = new Intent(getApplicationContext(), AtividadeActivity.class);
                                //Bundle extras = new Bundle();
                               // extras.putSerializable("desafio", desafio);
                              //  i.putExtras(extras);
                                Log.d("main activity", "onItemClick: "+listaDesafio.get(position).getId());
                                i.putExtra("desafio", desafio.getId());
                                startActivity(i);

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }));



    }

    private void onUpdate(FrameTime frameTime) {
        if(isModelPlaced){
            return;
        }
        Frame frame = arFragment.getArSceneView().getArFrame();

        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);
        for (Plane plane: planes){
            if(plane.getTrackingState() == TrackingState.TRACKING){

                Anchor anchor = plane.createAnchor(plane.getCenterPose());

                makeBear(anchor);


                break;
            }
        }
    }

    private void makeBear(Anchor anchor){
        isModelPlaced = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ModelRenderable.builder()
                    .setSource(getApplicationContext(), R.raw.bear)
                    .build()
                    .thenAccept(renderable -> adicionarModelo3d(anchor, renderable))
                    .exceptionally(
                            throwable -> {
                                Toast.makeText(getApplicationContext(),"errro ao exibir objeto",Toast.LENGTH_LONG).show();
                                Log.e("RENDERABLEOBJETO", "Unable to load Renderable.", throwable);
                                return null;
                            });
        }
    }


    private void adicionarModelo3d(Anchor anchor, ModelRenderable modelRenderable) {


        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setRenderable(modelRenderable);
        anchorNode.setOnTapListener(new Node.OnTapListener() {
            @Override
            public void onTap(HitTestResult hitTestResult, MotionEvent motionEvent) {
                Toast.makeText(arFragment.getContext(),"Ola !!!",Toast.LENGTH_LONG).show();

            }
        });
        arFragment.getArSceneView().getScene().addChild(anchorNode);


    }

    public void abrirTelaLogin(View view){
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int permissaoResultado : grantResults){
            if( permissaoResultado == PackageManager.PERMISSION_DENIED){
                alertaValidacaoPermissao();
            }
        }

    }
    @Override
    protected void onStart() {
        super.onStart();

    }

    private void alertaValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(auth != true){
            getMenuInflater().inflate(R.menu.menu_main,menu);
        }else
            getMenuInflater().inflate(R.menu.menu_generico,menu);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair:

                finish();
                break;

            case R.id.menuLogin:
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                break;
            case R.id.menuPerfil:
                Intent i2 = new Intent(this, PerfilActivity.class);
                startActivity(i2);
                finish();
                break;

            case R.id.menuRanking:
                Intent i3 = new Intent(this, RankingActivity.class);
                startActivity(i3);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
