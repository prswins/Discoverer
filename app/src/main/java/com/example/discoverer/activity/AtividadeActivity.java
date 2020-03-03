package com.example.discoverer.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.discoverer.R;
import com.example.discoverer.config.ConfiguracaoFirebase;
import com.example.discoverer.helper.LinguagemHelper;
import com.example.discoverer.helper.UsuarioFirebase;
import com.example.discoverer.model.Atividade;
import com.example.discoverer.model.Desafio;
import com.example.discoverer.model.Ponto;
import com.example.discoverer.model.Usuario;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.Inflater;

public class AtividadeActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String ATIVIDADE_AGUARDANDO_INICIO = "aguardando";
    public static final String ATIVIDADE_INICIADA = "iniciada";
    public static final String ATIVIDADE_PARADA = "parada";
    public static final String ATIVIDADE_FINALIZANDO = "finalizando";

    public String status_atual;
    private GoogleMap mMap;
    private Button buttonIniciar, buttonFinalizar, buttonAr;
    TextView pontuacao, tempo, status,cRegressiva;
    LinearLayout layoutAR, layoutSuperior, layoutInferior;
    private PolylineOptions polyline = new PolylineOptions();
    final private Desafio desafioAtual = new Desafio();
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localizacaoAtual;
    //ArFragment arFragment;
   // private Boolean isModelPlaced = false;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseRef;
    private Chronometer cronometro;
    private boolean atividadeRodando = false;
    private long milesegundosPausado;
    private long tempoTotal;
    boolean flagInicio = false;
    boolean flagFim = false;
    boolean usuarioAutenticado;
    private Double pontuacaoTotal;
    private List<Ponto> listaPontosDescobertos = new ArrayList<>();
    final Usuario usuarioLocal = new Usuario();
   // private String objeto3DAtual;
   // private String msgObj;
    static final int ACTIVITY_2_REQUEST = 1;
    AlertDialog dialogQuit, dialogFim;

    BaseArFragment realdadeAumentada;
    final CountDownTimer contador = new CountDownTimer(3000,1000) {
        @Override
        public void onTick(long millisUntilFinished) {
            final int TOTAL_MILLIS = 25 * 60 * 1000;
            final int COUNT_DOWN_INTERVAL = 1000;
            final String TIME_FORMAT = "%d";

            cRegressiva.setVisibility(View.VISIBLE);
            cRegressiva.setText(String.format(
                    TIME_FORMAT,
                    TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))));
            layoutInferior.setVisibility(View.GONE);
            layoutSuperior.setVisibility(View.GONE);


        }

        @Override
        public void onFinish() {

            cRegressiva.setVisibility(View.GONE);
            layoutSuperior.setVisibility(View.VISIBLE);
            layoutInferior.setVisibility(View.VISIBLE);
            startCronometro();
        }
    };




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atividade);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        layoutSuperior = findViewById(R.id.linearLayoutSuperior);
        layoutInferior = findViewById(R.id.linearLayoutInferior);


        buttonIniciar = findViewById(R.id.buttonIniciar);
        buttonFinalizar = findViewById(R.id.buttonFinalizar);

        pontuacao = findViewById(R.id.textViewPontuacao);
        pontuacaoTotal = 0.0;
        pontuacao.setText(String.valueOf(pontuacaoTotal));

        tempo = findViewById(R.id.textViewTempo);
        tempo.setText(R.string.text_tempo);

        status = findViewById(R.id.textViewStatus);
        status_atual = AtividadeActivity.ATIVIDADE_AGUARDANDO_INICIO;
        status.setText(status_atual);
        ajustarLayout();

        cRegressiva = findViewById(R.id.textViewContagemR);
        cRegressiva.setVisibility(View.GONE);



        cronometro = (Chronometer) findViewById(R.id.chonometro);
        buttonIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!atividadeRodando) {
                    contador.start();

                } else {
                    pausarCronometro();


                }

            }
        });
        buttonFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                status_atual = AtividadeActivity.ATIVIDADE_FINALIZANDO;
                ajustarLayout();

                Toast.makeText(getApplicationContext(), " tempo total: "+(tempoTotal/1000) ,Toast.LENGTH_LONG).show();
                if (usuarioAutenticado){

                    //salvarDadosAtividade();
                }else
                    realizarAutenticacao();



            }
        });

        recuperarDesafio();
        verificarAutenticacao();
        recuperarLocalizacaoUsuario();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

       // arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.atividadeFragmentAR);

      //  arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);

        //layoutAR.setVisibility(View.GONE);





    }
    private void verificarAutenticacao(){
        if (this.getIntent().getStringExtra("idUsuario") == null){
            usuarioAutenticado = false;
        }else {
            usuarioAutenticado = true;
        }
    }

    private void recuperarDesafio() {
        String idDesafio = (String) this.getIntent().getStringExtra("desafio");
        String nomeDesafio =  getString(R.string.atividade_desafio_titulo);
        nomeDesafio = nomeDesafio + (String) this.getIntent().getStringExtra("nomeDesafio");
        setTitle(nomeDesafio);
        Log.d("string intent", "recuperarDesafio: "+idDesafio);
        if (idDesafio != null ){
            DatabaseReference desafio = FirebaseDatabase.getInstance().getReference().child("desafios").child(idDesafio);
            if(desafio!=null){
                desafio.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        desafioAtual.setTitulo((String) dataSnapshot.child("titulo").getValue());
                        Log.d("recuperarDesafio", "desafioAtual.getTitulo: "+desafioAtual.getTitulo());

                        desafioAtual.setId((String) dataSnapshot.child("id").getValue());
                        Log.d("recuperarDesafio", "desafioAtual.getId: "+desafioAtual.getId());

                        desafioAtual.setDescricao((String) dataSnapshot.child("descricao").getValue());
                        Log.d("recuperarDesafio", "desafioAtual.getDescricao: "+desafioAtual.getDescricao());

                        String sDistancia = String.valueOf(dataSnapshot.child("distancia").getValue());
                        double dDistancia = Double.valueOf(sDistancia);
                        desafioAtual.setDistancia(dDistancia);
                        Log.d("recuperarDesafio", "desafioAtual.getDistancia: "+desafioAtual.getDistancia());
                        desafioAtual.setPontuacao(Integer.parseInt(dataSnapshot.child("pontuacao").getValue().toString()));
                        Log.d("recuperarDesafio", "desafioAtual.getPontuacao: "+desafioAtual.getPontuacao());

                        desafioAtual.setLocalizacaoInicial(
                                new LatLng(
                                        Double.parseDouble(dataSnapshot.child("localizacaoInicial").child("latitude").getValue().toString()),
                                        Double.parseDouble(dataSnapshot.child("localizacaoInicial").child("longitude").getValue().toString()))
                        );
                        Log.d("recuperarDesafio", "desafioAtual.getLocalizacaoInicial: "+desafioAtual.getLocalizacaoInicial());


                        desafioAtual.setLocalizacaoFinal(
                                new LatLng(
                                        Double.parseDouble(dataSnapshot.child("localizacaoFinal").child("latitude").getValue().toString()),
                                        Double.parseDouble(dataSnapshot.child("localizacaoFinal").child("longitude").getValue().toString()))
                        );
                        Log.d("recuperarDesafio", "desafioAtual.getLocalizacaoFinal: "+desafioAtual.getLocalizacaoFinal());


                        final  List<Ponto> pontos = new ArrayList<>();


                        for(DataSnapshot dsListaPontos : dataSnapshot.child("listaPontos").getChildren()){
                            // Log.d("pontos", "dataSnapshot.child(\"listaPontos\").getChildren()"+dsListaPontos.getValue());

                            Ponto ponto = new Ponto();
                            ponto.setNome(
                                    dsListaPontos.child("nome").getValue().toString()
                            );
                            //  Log.d("ponto", "dsListaPontos.getValue()"+dsListaPontos.child("nome").getValue().toString());
                            //  Log.d("ponto", "ponto.getNome()"+ponto.getNome());

                            ponto.setDescricao(
                                    dsListaPontos.child("descricao").getValue().toString()
                            );
                            // Log.d("ponto", "dsListaPontos.getValue()"+dsListaPontos.child("descricao").getValue().toString());
                            // Log.d("ponto", "ponto.getDescricao()"+ponto.getDescricao());

                            ponto.setPontuacao(
                                    dsListaPontos.child("pontuacao").getValue(Double.class)
                            );
                            // Log.d("ponto", "dsListaPontos.getValue()"+dsListaPontos.child("pontuacao").getValue(Double.class));
                            // Log.d("ponto", "ponto.getPontuacao()"+ponto.getPontuacao());

                            ponto.setVisibilidade(
                                    dsListaPontos.child("visibilidade").getValue(Double.class)
                            );
                            //Log.d("ponto", "dsListaPontos.getValue()"+dsListaPontos.child("visibilidade").getValue(Double.class));
                            // Log.d("ponto", "ponto.getVisibilidade"+ponto.getVisibilidade());

                            ponto.setStatus(
                                    dsListaPontos.child("status").getValue().toString()
                            );
                            //  Log.d("ponto", "dsListaPontos.getValue()"+dsListaPontos.child("status").getValue().toString());
                            // Log.d("ponto", "ponto.getStatus()"+ponto.getStatus());

                            ponto.setLocalizacao(
                                    new LatLng(
                                            Double.parseDouble(dsListaPontos.child("localizacao").child("latitude").getValue().toString()),
                                            Double.parseDouble(dsListaPontos.child("localizacao").child("longitude").getValue().toString())
                                    )
                            );
                            //   Log.d("ponto", "dsListaPontos.getValue()"+dsListaPontos.child("localizacao").child("latitude").getValue().toString());
                            //   Log.d("ponto", "dsListaPontos.getValue()"+dsListaPontos.child("localizacao").child("longitude").getValue().toString());
                            //   Log.d("ponto", "ponto.getLocalizacao()"+ponto.getLocalizacao());

                            pontos.add(ponto);
                        }
                        desafioAtual.setListaPontos(pontos);
                        // Log.d("pontos", "desafioAtual.getListaPontos: "+desafioAtual.getListaPontos());

                        final List<LatLng> caminho = new ArrayList<>();
                        for(DataSnapshot dsCaminho :dataSnapshot.child("caminho").getChildren()){
                            LatLng local = new LatLng(
                                    Double.parseDouble(dsCaminho.child("latitude").getValue().toString()),
                                    Double.parseDouble(dsCaminho.child("longitude").getValue().toString())
                            );
                            caminho.add(local);
                        }
                        desafioAtual.setCaminho(caminho);
                        // Log.d("recuperarDesafio", "desafioAtual.getCaminho: "+desafioAtual.getCaminho());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }
    }

    private void carregarDesafioMapa() {
        mMap.clear();
        List<Marker> listaMarcadores = new ArrayList<>();
        Log.d("mapa", "carregarDesafioMapa: "+desafioAtual.getLocalizacaoInicial());
        Marker inicio = mMap.addMarker(new MarkerOptions()
                .position(desafioAtual.getLocalizacaoInicial())
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.bandeira_inicio))
                .flat(true)
                .title(desafioAtual.getTitulo())

        );
        mMap.addPolyline(polyline.add(desafioAtual.getLocalizacaoInicial()).width(5).color(Color.RED));

        UsuarioFirebase.addDesafioGeoFire(desafioAtual.getLocalizacaoInicial().latitude,desafioAtual.getLocalizacaoInicial().longitude ,desafioAtual.getId(),"ini");

        for(LatLng caminho : desafioAtual.getCaminho()){
            mMap.addPolyline(polyline.add(caminho).width(5).color(Color.RED));

        }



        for (Ponto p : desafioAtual.getListaPontos()) {
            mMap.addPolyline(polyline.add(p.getLocalizacao()).width(5).color(Color.RED));
            listaMarcadores.add(mMap.addMarker(new MarkerOptions()
                            .position(p.getLocalizacao())
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.binoculo))
                            .flat(true)
                            .title(p.getNome())
                            .snippet(p.getDescricao())
                    )
            );
            UsuarioFirebase.addDesafioGeoFire(p.getLocalizacao().latitude,p.getLocalizacao().longitude ,desafioAtual.getId(),"des"+p.getNome());

        }
        listaMarcadores.add(mMap.addMarker(new MarkerOptions()
                        .position(desafioAtual.getLocalizacaoFinal())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.bandeira_fim))
                        .flat(true)
                        .title("FIM")

                )
        );
        mMap.addPolyline(polyline.add(desafioAtual.getLocalizacaoFinal()).width(5).color(Color.RED));
        UsuarioFirebase.addDesafioGeoFire(desafioAtual.getLocalizacaoFinal().latitude,desafioAtual.getLocalizacaoFinal().longitude ,desafioAtual.getId(),"fim");
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
    private void centralizarMarcador(LatLng local) {
        mMap.moveCamera(
                CameraUpdateFactory.newLatLng(local)
        );
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
    }



    private void ajustarLayout() {
        switch (status_atual){
            //localizacao encontrada, esperando botao start
            case AtividadeActivity.ATIVIDADE_AGUARDANDO_INICIO:
                        buttonIniciar.setVisibility(View.VISIBLE);
                        buttonIniciar.setText(R.string.botao_iniciar_iniciar);
                        buttonFinalizar.setVisibility(View.GONE);
                        status.setText(ATIVIDADE_AGUARDANDO_INICIO);
                break;

            //atividade iniciada, botao iniciar ira pausar, habilitar finalizar
            case AtividadeActivity.ATIVIDADE_INICIADA:
                        buttonIniciar.setVisibility(View.VISIBLE);
                        buttonIniciar.setText(R.string.botao_iniciar_pausado);
                        buttonFinalizar.setVisibility(View.GONE);
                        status.setText(ATIVIDADE_INICIADA);
                break;
            //atividade pausa, pode finalizar a atividade ou continua
            case AtividadeActivity.ATIVIDADE_PARADA:
                        buttonIniciar.setVisibility(View.VISIBLE);
                        buttonIniciar.setText(R.string.botao_iniciar_iniciar);
                        buttonFinalizar.setVisibility(View.VISIBLE);
                        buttonFinalizar.setText(R.string.botao_finalizar);
                        status.setText(ATIVIDADE_PARADA);
                break;


            //finalizando atividade
            case AtividadeActivity.ATIVIDADE_FINALIZANDO:
                        buttonIniciar.setVisibility(View.GONE);
                        buttonFinalizar.setVisibility(View.VISIBLE);
                        buttonFinalizar.setText(R.string.botao_finalizar_desafio);
                        status.setText(ATIVIDADE_FINALIZANDO);
            default :
                buttonIniciar.setVisibility(View.VISIBLE);
                buttonIniciar.setVisibility(View.VISIBLE);
                buttonIniciar.setText("default");
                buttonFinalizar.setText("default");
                status.setText("default");
                break;
        }

    }



    private void startCronometro() {
        if(!atividadeRodando) {
            cronometro.setBase(SystemClock.elapsedRealtime() - milesegundosPausado);
            cronometro.start();
            atividadeRodando = true;
            status_atual = AtividadeActivity.ATIVIDADE_INICIADA;
            ajustarLayout();
        }
    }
    private void pausarCronometro() {
        if(atividadeRodando){
            cronometro.stop();
            tempoTotal = SystemClock.elapsedRealtime() - (cronometro.getBase());
            milesegundosPausado = SystemClock.elapsedRealtime() - cronometro.getBase();
            atividadeRodando = false;
            status_atual = AtividadeActivity.ATIVIDADE_PARADA;
            ajustarLayout();
        }
    }

    private void realizarAutenticacao() {
        FirebaseAuth autenticacao;
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        FirebaseUser user = UsuarioFirebase.getUsuarioAtual();
        Log.d("realizarAutenticacao", "realizarAutenticacao: "+user.getDisplayName() + ","+ user.getUid());
        salvarDadosAtividade();

    }

    private void salvarDadosAtividade() {
        Atividade atitivadeAtual = new Atividade(usuarioLocal.getID(),desafioAtual.getId(),tempoTotal,pontuacaoTotal,listaPontosDescobertos.size());
        atitivadeAtual.salvar();
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(desafioAtual!=null){
            carregarDesafioMapa();
        }

    }


    //passando como parametro a localizacao do ponto que sera monitorado
    private void iniciarMonitoramento(final LatLng localDestino, int visibilidade, String desafioID){
        Log.d("iniciarMonitoramento", "iniciarMonitoramento: ");
        DatabaseReference localDesafio = ConfiguracaoFirebase.getFirebaseDatabase()
                .child("geofire").child(desafioID);
        GeoFire geoFire = new GeoFire(localDesafio);

        Log.d("iniciarMonitoramento", "DatabaseReference: "+localDesafio);



        //Adiciona círculo no ponto a ser descoberto
        final Circle circulo = mMap.addCircle(
                new CircleOptions()
                        .center( localDestino )
                        .radius(visibilidade)//em metros
                        .fillColor(Color.argb(90,255, 153,0))
                        .strokeColor(Color.argb(190,255,152,0))
        );

        final GeoQuery geoQuery = geoFire.queryAtLocation(
                new GeoLocation(localDestino.latitude, localDestino.longitude),
                (0.02)//em km (0.05 50 metros)
        );
        Log.d("iniciarMonitoramento", "geoQuery: "+geoQuery);


        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Log.d("geogire", "onKeyEntered: entrou: " + key.toString());
                String idDesafio = key.substring(0,20);
                String tipo = key.substring(20,23);
                String identificacaoKey = key.substring(23,key.length());
                Log.d("identificarLocal:", "onKeyEntered: key:"+key);
                Log.d("identificarLocal:", "onKeyEntered: idDesafio:"+idDesafio);
                Log.d("identificarLocal:", "onKeyEntered: tipo:"+tipo + ": tamanho"+tipo.length());
                Log.d("identificarLocal:", "onKeyEntered: identificacaoKey:"+identificacaoKey);

                Log.d("geogire", " teste id  "+ idDesafio +": "+ idDesafio.length() + "////////"+desafioAtual.getId()+ ": "+ desafioAtual.getId().length());
                if (idDesafio.equals(desafioAtual.getId())){
                    switch (tipo){
                        case "ini":
                            AtividadeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"ini", Toast.LENGTH_LONG).show();
                                }
                            });

                            break;

                        case "fim":
                            Log.d("geofire", "onKeyEntered: fim");
                            pontuacaoTotal = pontuacaoTotal + desafioAtual.getPontuacao();
                            pontuacao.setText(String.valueOf(pontuacaoTotal));
                            pausarCronometro();

                            AtividadeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    exibirMensagemFIM();
                                    Toast.makeText(getApplicationContext(),"fim", Toast.LENGTH_LONG).show();
                                    //layoutAR.setVisibility(View.VISIBLE);
                                }
                            });


                            break;

                        case "des":
                            Log.d("geofire", "ENTROU NA DESCOPBERTA");
                            AtividadeActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"des", Toast.LENGTH_LONG).show();
                                }
                            });

                            for(Ponto p:desafioAtual.getListaPontos()){
                                if (identificacaoKey.equals(p.getNome())){
                                    addPontoEncontrado(p);

                                    //objeto3DAtual = tipo;
                                    //ponto = p;
                                    abrirAR(p.getNome(),p.getDescricao());
                                    Log.d("identificarLocal", "Descoberta:"+p.getNome());
                                    Log.d("identificarLocal", "descoberta:"+identificacaoKey);


                                }
                            }

                            pausarCronometro();

                            break;
                    }
                }


            }

            @Override
            public void onKeyExited(String key) {
                Log.d("onKeyEntered", "onKeyEntered: está fora da área!"+key);


            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Toast.makeText(getApplicationContext(),"moveu"+ key ,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d("onGeoQueryError", "onGeoQueryError: "+error.toString());
            }
        });

    }

    public void addPontoEncontrado(Ponto p){
        Log.d("addPontoEncontrado", "listaPontosDescobertos.contains(p): "+listaPontosDescobertos.contains(p) +" "+p.getNome());
        Log.d("addPontoEncontrado", "!listaPontosDescobertos.contains(p): "+!listaPontosDescobertos.contains(p) +" "+p.getNome());
        if(!listaPontosDescobertos.contains(p)){
            listaPontosDescobertos.add(p);
            pontuacaoTotal = pontuacaoTotal + p.getPontuacao();
            pontuacao.setText(String.valueOf(pontuacaoTotal));

        }
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void recuperarLocalizacaoUsuario() {

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng meuLocalAtual = new LatLng(location.getLatitude(), location.getLongitude());
                localizacaoAtual = meuLocalAtual;
               // iniciarMonitoramento(localizacaoAtual,5,desafioAtual.getId());
                //centralizarMarcador(meuLocalAtual);
                if (atividadeRodando == true){
                    iniciarMonitoramento(localizacaoAtual,5,desafioAtual.getId());
                    centralizarMarcador(meuLocalAtual);
                }
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, locationListener);

            return;
        }


    }
/*
    private void onUpdate(FrameTime frameTime) {
        if (isModelPlaced) {
            return;
        }
        Frame frame = arFragment.getArSceneView().getArFrame();

        Collection<Plane> planes = frame.getUpdatedTrackables(Plane.class);
        for (Plane plane : planes) {
            if (plane.getTrackingState() == TrackingState.TRACKING) {

                Anchor anchor = plane.createAnchor(plane.getCenterPose());

                make3Dobjeto(anchor);


                break;
            }
        }
    }

    private void make3Dobjeto(Anchor anchor) {
        Resources obj;

        switch (objeto3DAtual){
            case "ini":
                msgObj = getString(R.string.msgObj_ini);
                break;
            case "des":
                msgObj = getString(R.string.msgObj_des);
                break;
            case"fim":
                msgObj = getString(R.string.msgObj_fim);
                break;

        }

        isModelPlaced = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ModelRenderable.builder()
                    .setSource(getApplicationContext(), R.raw.trofeu)
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
                Toast.makeText(arFragment.getContext(), msgObj, Toast.LENGTH_LONG).show();
                //layoutAR.setVisibility(View.GONE);
                //layoutInferior.setEnabled(true);
            }
        });
        arFragment.getArSceneView().getScene().addChild(anchorNode);


    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_atividade,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSairAtividade:
                exibirAlerta();


                break;

        }

        return super.onOptionsItemSelected(item);
    }
    private void exibirAlerta(){
        AlertDialog.Builder builder = new AlertDialog.Builder(AtividadeActivity.this);
        builder.setTitle(R.string.sair_atividade_dialog_titulo);
        builder.setMessage(R.string.sair_atividade_dialog_mensagem);
        builder.setPositiveButton(R.string.atividade_sair_botao_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finishAffinity();

            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogQuit = builder.create();
        dialogQuit.show();
    }

    private void abrirAR(String nome, String descricao){
        //Log.d("abirirAR", "abrirAR: "+p.getNome());
        //Ponto p = new Ponto();
//        p.setDescricao("descricao");
        Intent i = new Intent(this, DescobertaActivity.class);
      //  i.putExtra("ponto", p);
        i.putExtra("titulo", nome);

        i.putExtra("desc", descricao);
        startActivityForResult(i, ACTIVITY_2_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ACTIVITY_2_REQUEST) {
            if(resultCode == RESULT_OK){
                String resultado = data.getStringExtra("resultado");
                //Coloque no EditText
               // seuEditText.setText(resultado);
                Log.d("retornoactivity", "onActivityResult: "+ resultado);
            }
        }
    }

    private void exibirMensagemFIM() {
        //LayoutInflater é utilizado para inflar nosso layout em uma view.
        //-pegamos nossa instancia da classe
        LayoutInflater li = getLayoutInflater();

        //inflamos o layout alerta.xml na view
        View view = li.inflate(R.layout.alert_atividade_fim, null);
        //definimos para o botão do layout um clickListener


        view.findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {

                dialogFim.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        dialogFim = builder.create();
        dialogFim.show();
    }

    @Override
    protected void onStop(){
        super.onStop();

        // DatabaseReference localDesafio = ConfiguracaoFirebase.getFirebaseDatabase()
        //         .child("atividade").child(desafioAtual.getKey());
        // GeoFire geoFire = new GeoFire(localDesafio);
        // geoFire.removeLocation(desafioAtual.getKey());



    }

}
