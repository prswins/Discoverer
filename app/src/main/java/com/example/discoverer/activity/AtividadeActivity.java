package com.example.discoverer.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.discoverer.R;
import com.example.discoverer.helper.LinguagemHelper;
import com.example.discoverer.model.Desafio;
import com.example.discoverer.model.Ponto;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
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

public class AtividadeActivity extends AppCompatActivity implements OnMapReadyCallback {
    public static final String ATIVIDADE_AGUARDANDO_INICIO = "aguardando";
    public static final String ATIVIDADE_INICIADA = "iniciada";
    public static final String ATIVIDADE_PARADA = "parada";
    public static final String ATIVIDADE_FINALIZANDO = "finalizando";

    public String status_atual;
    private GoogleMap mMap;
    private Button buttonIniciar, buttonFinalizar, buttonAr;
    TextView pontuacao, tempo;
    LinearLayout layoutAR;
    private PolylineOptions polyline = new PolylineOptions();
    Desafio desafioAtual = new Desafio();
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localizacaoAtual;
    ArFragment arFragment;
    private Boolean isModelPlaced = false;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private Chronometer cronometro;
    private boolean atividadeRodando = false;
    private long milesegundosPausado;
    private long tempoTotal;
    private String titulo;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        layoutAR = findViewById(R.id.linearLayoutAR);
        layoutAR.setVisibility(View.GONE);
        buttonIniciar = findViewById(R.id.buttonIniciar);
        buttonIniciar.setText(R.string.botao_iniciar_iniciar);
        buttonFinalizar = findViewById(R.id.buttonFinalizar);
        buttonFinalizar.setBackgroundColor(getResources().getColor(R.color.marronEscuro));
        buttonFinalizar.setTextColor(getResources().getColor(R.color.marron));
        buttonFinalizar.setVisibility(View.GONE);
        pontuacao = findViewById(R.id.textViewPontuacao);
        pontuacao.setText(R.string.text_tempo+"99999");
        tempo = findViewById(R.id.textViewTempo);
        tempo.setText(R.string.text_tempo);



        cronometro = (Chronometer) findViewById(R.id.chonometro);
        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!atividadeRodando) {
                    iniciarAtividade();
                } else {
                    pararAtividade();
                }
            }
        });
        buttonFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalizarAtividade();
            }
        });



        inicializarComponentes();
       // recuperarDesafio();
       recuperarLocalizacaoUsuario();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
         arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.atividadeFragmentAR);
         arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);
        recuperarDesafio();

    }

    private Desafio recuperarDesafio() {
        final Desafio[] novo = {new Desafio()};
        String idDesafio = (String) this.getIntent().getStringExtra("desafio");
        setTitle("Desafio: "+ (String) this.getIntent().getStringExtra("nomeDesafio"));
        Log.d("string intent", "recuperarDesafio: "+idDesafio);
        if (idDesafio != null ){
            DatabaseReference desafio = FirebaseDatabase.getInstance().getReference().child("desafios").child(idDesafio);
            if(desafio!=null){
                desafio.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.getChildrenCount() > 0 ){

                            for(DataSnapshot ds : dataSnapshot.getChildren()){

                                desafioAtual.setId((String) ds.child("id").getValue());
                                desafioAtual.setTitulo((String) ds.child("titulo").getValue());
                                desafioAtual.setDescricao((String) ds.child("descricao").getValue());

                                /*String sDistancia = String.valueOf(ds.child("distancia").getValue());
                                double dDistancia = Double.valueOf(sDistancia);
                               desafioAtual.setDistancia(dDistancia);
                                desafioAtual.setPontuacao(Integer.parseInt(ds.child("pontuacao").getValue().toString()));

                                desafioAtual.setLocalizacaoInicial(
                                        new LatLng(
                                                Double.parseDouble(ds.child("localizacaoInicial").child("latitude").getValue().toString()),
                                                Double.parseDouble(ds.child("localizacaoInicial").child("longitude").getValue().toString()))
                                );

                                desafioAtual.setLocalizacaoFinal(
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
                                desafioAtual.setListaPontos(pontos);

                                final List<LatLng> caminho = new ArrayList<>();
                                for(DataSnapshot dsCaminho :ds.child("caminho").getChildren()){
                                    LatLng local = new LatLng(
                                            Double.parseDouble(dsCaminho.child("latitude").getValue().toString()),
                                            Double.parseDouble(dsCaminho.child("longitude").getValue().toString())
                                    );
                                    caminho.add(local);
                                }
                                desafioAtual.setCaminho(caminho);*/

                                Log.d("desafio1", "onDataChange: desafios: "+desafioAtual.getTitulo());

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
        return novo[0];
    }


    private void carregarDesafioMapa() {
        mMap.clear();
        List<Marker> listaMarcadores = new ArrayList<>();
        Marker inicio = mMap.addMarker(new MarkerOptions().position(desafioAtual.getLocalizacaoInicial()).icon(BitmapDescriptorFactory.fromResource(R.drawable.bandeira_inicio)));

        for (Ponto p : desafioAtual.getListaPontos()) {
            mMap.addPolyline(polyline.add(p.getLocalizacao()).width(5).color(Color.RED));
            listaMarcadores.add(mMap.addMarker(new MarkerOptions().position(p.getLocalizacao()).icon(BitmapDescriptorFactory.fromResource(R.drawable.binoculo))));
        }
        listaMarcadores.add(mMap.addMarker(new MarkerOptions().position(desafioAtual.getLocalizacaoFinal()).icon(BitmapDescriptorFactory.fromResource(R.drawable.bandeira_fim))));
        centralizarMarcadores(inicio, listaMarcadores);

    }

    private void centralizarMarcadores(Marker marcadorInicio, List<Marker> marcadores) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marcadorInicio.getPosition());

        for (Marker m : marcadores) {
            builder.include(m.getPosition());
        }

        LatLngBounds bounds = builder.build();

        int largura = getResources().getDisplayMetrics().widthPixels;
        int altura = getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.20);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(bounds, largura, altura, espacoInterno)
        );

    }


    public void iniciarAtividade() {
        status_atual = ATIVIDADE_INICIADA;
        buttonFinalizar.setVisibility(View.VISIBLE);
        buttonIniciar.setText(R.string.botao_iniciar_pausado);
        buttonFinalizar.setVisibility(View.GONE);
        startCronometro();
    }



    public void pararAtividade() {
        status_atual = ATIVIDADE_PARADA;
        buttonIniciar.setText(R.string.botao_iniciar_iniciar);
        buttonFinalizar.setText(R.string.botao_finalizar);
        buttonFinalizar.setVisibility(View.VISIBLE);
        pausarCronometro();
    }
    private void startCronometro() {
        if(!atividadeRodando) {
            cronometro.setBase(SystemClock.elapsedRealtime() - milesegundosPausado);
            cronometro.start();
            atividadeRodando = true;
        }
    }
    private void pausarCronometro() {
        if(atividadeRodando){
            cronometro.stop();
            tempoTotal = SystemClock.elapsedRealtime() - (cronometro.getBase());
            milesegundosPausado = SystemClock.elapsedRealtime() - cronometro.getBase();
            atividadeRodando = false;
        }
    }

    public void finalizarAtividade() {

        status_atual = ATIVIDADE_FINALIZANDO;
        Toast.makeText(getApplicationContext(), " tempo total: "+(tempoTotal/1000) ,Toast.LENGTH_LONG).show();
        buttonFinalizar.setVisibility(View.GONE);
    }


    public void inicializarComponentes() {
        status_atual = ATIVIDADE_AGUARDANDO_INICIO;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng meuLocalAtual = new LatLng(location.getLatitude(), location.getLongitude());
                localizacaoAtual = meuLocalAtual;
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, locationListener);

            return;
        }


    }

    private void onUpdate(FrameTime frameTime) {
        if (isModelPlaced) {
            return;
        }
        Frame frame = arFragment.getArSceneView().getArFrame();

        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);
        for (Plane plane : planes) {
            if (plane.getTrackingState() == TrackingState.TRACKING) {

                Anchor anchor = plane.createAnchor(plane.getCenterPose());

                makeBear(anchor);


                break;
            }
        }
    }

    private void makeBear(Anchor anchor) {
        isModelPlaced = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ModelRenderable.builder()
                    .setSource(getApplicationContext(), R.raw.bear)
                    .build()
                    .thenAccept(renderable -> adicionarModelo3d(anchor, renderable))
                    .exceptionally(
                            throwable -> {
                                Toast.makeText(getApplicationContext(), "errro ao exibir objeto", Toast.LENGTH_LONG).show();
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
                Toast.makeText(arFragment.getContext(), "Ola !!!", Toast.LENGTH_LONG).show();

            }
        });
        arFragment.getArSceneView().getScene().addChild(anchorNode);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
            getMenuInflater().inflate(R.menu.menu_atividade,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSairAtividade:
                startActivity(new Intent(this, MainActivity.class));
                finishAffinity();
                break;

            case R.id.menuLinguagem:

                LinguagemHelper.trocarLinguagem(getResources());
                break;
        }

        return super.onOptionsItemSelected(item);
    }


}
