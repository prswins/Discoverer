package com.example.discoverer.activity;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.example.discoverer.config.ConfiguracaoFirebase;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.discoverer.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class DesafioActivity extends AppCompatActivity implements OnMapReadyCallback {
    LinearLayout linearLayoutInserirDescoberta, linearLayoutDesafio;
    TextView textViewInformacao;
    EditText editDescobertaNome, editDescobertaDescricao, editDescobertaVisibilidade, editDescobertaPontuacao, editDesafioTitulo, editDesafioDesc, editDesafioP;
    Button botaoIniciarParar, botaoInserirPonto, botaoConcluirDescoberta, botaoFinalziarDesafio;
    String desafioTitulo;
    String desafioDescricao;
    String desafioPontuacao;
    boolean inserindoDescoberta = false;
    boolean gravandoDesafio = false;

    private GoogleMap mMap;
    private FirebaseAuth autenticacao;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng localizacaoAtual;
    private LatLng descobertaAtual;
    private Marker marcadorInicio;
    private Marker marcadorAtual;

    List<LatLng> localizacaoPercorrida = new ArrayList<>();
    List<LatLng>listaDescobertas = new ArrayList<>();
    private PolylineOptions polyline = new PolylineOptions();

    private Desafio desafioAtual = new Desafio();
    List<Ponto> listaPontos = new ArrayList<>();

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_desafio);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Criar Desafios");
        setSupportActionBar(toolbar);
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        recuperarLocalizacaoUsuario();
        inicializarComponentes();
        inicializar();


    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void inicializar(){
        limparCampos();
        textViewInformacao.setText(R.string.inserir_desafio_info_inicial);
        linearLayoutInserirDescoberta.setVisibility(View.GONE);
        linearLayoutDesafio.setVisibility(View.GONE);
        botaoFinalziarDesafio.setVisibility(View.GONE);
        botaoFinalziarDesafio.setBackgroundColor(getResources().getColor(R.color.marron));
        botaoFinalziarDesafio.setTextColor(getResources().getColor(R.color.marronEscuro));
        botaoInserirPonto.setVisibility(View.GONE);
        botaoIniciarParar.setVisibility(View.VISIBLE);


    }

    private void limparCampos() {
        editDescobertaNome.setText(null);
        editDesafioP.setText(null);
        editDesafioDesc.setText(null);
        desafioPontuacao = null;
        desafioDescricao = null;
        desafioTitulo =null;
        listaPontos.clear();
        listaDescobertas.clear();
    }

    public boolean verificarDadosDesafio(){
        boolean continuar = false;
        if (editDesafioTitulo.getText() != null ){
            if(editDesafioDesc.getText() != null ){
                if(editDesafioP.getText() != null){
                    continuar=true;
                }
            }
        }else{
            linearLayoutDesafio.setVisibility(View.VISIBLE);
        }
        return continuar;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void comecarDesafio(){
        textViewInformacao.setText(R.string.inserir_descoberta_info_inicial);
        botaoIniciarParar.setVisibility(View.GONE);
        botaoInserirPonto.setVisibility(View.VISIBLE);
        botaoFinalziarDesafio.setVisibility(View.VISIBLE);
        botaoFinalziarDesafio.setBackgroundColor(getResources().getColor(R.color.marronEscuro));
        botaoFinalziarDesafio.setTextColor(getResources().getColor(R.color.marron));
        linearLayoutDesafio.setVisibility(View.VISIBLE);





    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void inserirDescoberta(){
        recuperarDadosDesafio();
        textViewInformacao.setText(R.string.inserir_descoberta_dados);
        linearLayoutInserirDescoberta.setVisibility(View.VISIBLE);
        linearLayoutDesafio.setVisibility(View.GONE);
        botaoInserirPonto.setVisibility(View.GONE);
        botaoConcluirDescoberta.setVisibility(View.VISIBLE);
        botaoFinalziarDesafio.setVisibility(View.GONE);


        descobertaAtual = localizacaoAtual;



    }

    public boolean verificarInsersaoDescobertas(){
        boolean camposOk = false;
        if(editDescobertaNome.getText() == null || (editDescobertaNome.getText()).equals("") ){
            alertaMensagem(R.string.alert_descoberta_campoTITULO_titulo,
                    R.string.alert_descoberta_campoTITULO_mensagem,
                    R.string.alert_descoberta_campoTITULO_botao_confirmar);
        }else{
            if(editDescobertaDescricao.getText() == null || (editDescobertaDescricao.getText()).equals("")){
                alertaMensagem(R.string.alert_desafio_campoDescricao_titulo,
                        R.string.alert_descoberta_campoDescricao_mensagem,
                        R.string.alert_descoberta_campoDescricao_botao_confirmar);
            }else{
                if(editDescobertaPontuacao.getText() == null || (editDescobertaPontuacao.getText()).equals("")){
                    alertaMensagem(R.string.alert_descoberta_campoPontuacao_titulo,
                            R.string.alert_descoberta_campoPontuacao_mensagem,
                            R.string.alert_descoberta_campoPontuacao_botao_confirmar);
                }else{
                    if(editDescobertaVisibilidade.getText() == null || (editDescobertaVisibilidade.getText()).equals("")){
                        alertaMensagem(R.string.alert_descoberta_campoVisibilidade_titulo,
                                R.string.alert_descoberta_campoVisibilidade_mensagem,
                                R.string.alert_descoberta_campoVisibilidade_botao_confirmar);
                    }else{
                        camposOk = true;
                    }
                }
            }
        }



        return camposOk;
    }

    public void finalizarDescoberta(){

        if(verificarInsersaoDescobertas()){
            listaDescobertas.add(descobertaAtual);
            textViewInformacao.setText(R.string.inserir_descoberta_info_inicial);
            botaoFinalziarDesafio.setVisibility(View.VISIBLE);
            botaoInserirPonto.setVisibility(View.VISIBLE);
            linearLayoutInserirDescoberta.setVisibility(View.GONE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void finalizarDesafio(){
        recuperarDadosDesafio();
        adicionarMarcadorFim(localizacaoAtual);
        if(verificarDadosDesafio()){
            confirmarFinalizacaoDesafio();
            mapaFinal();
        }else{
            Toast.makeText(DesafioActivity.this,
                    "Preencher todos os dados do desafio ",
                    Toast.LENGTH_LONG).show();
        }
        inicializar();

    }

    private void confirmarFinalizacaoDesafio() {
        if((desafioTitulo == null) || (desafioTitulo.equals("") )){
            //if(editDesafioTitulo.getText() == null || (editDesafioTitulo.getText()).equals("") ){
            editDesafioTitulo.setHintTextColor(Color.RED);
            alertaMensagem(R.string.alert_desafio_campoTITULO_titulo,
                    R.string.alert_desafio_campoTITULO_mensagem,
                    R.string.alert_desafio_campoTITULO_botao_confirmar);
        }else{
            if((desafioDescricao == null) || (desafioDescricao.equals(""))){
                editDesafioDesc.setHintTextColor(Color.RED);
                //if(editDesafioDescricao.getText() == null || (editDesafioDescricao.getText()).equals("")){
                alertaMensagem(R.string.alert_desafio_campoDescricao_titulo,
                        R.string.alert_desafio_campoDescricao_mensagem,
                        R.string.alert_desafio_campoDescricao_botao_confirmar);
            }else{
                if((desafioPontuacao == null)||(desafioPontuacao.equals(""))){
                    editDesafioP.setHintTextColor(Color.RED);
                    //if(editDescobertaPontuacao.getText() == null || (editDescobertaPontuacao.getText()).equals("")){
                    alertaMensagem(R.string.alert_desafio_campoPontuacao_titulo,
                            R.string.alert_desafio_campoPontuacao_mensagem,
                            R.string.alert_desafio_ccampoPontuacao_botao_confirmar);
                }else{

                    alertaConclusaoDesafio();
                }
            }
        }


    }
    private void alertaConclusaoDesafio() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.alert_desafio_concluido_titulo);
        builder.setMessage(R.string.alert_desafio_concluido_mensagem);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.alert_desafio_concluido_botao_confirmar, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                criarDesafioAtual();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void alertaMensagem(int titulo,int mensagem,int confirmacao) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(titulo));
        builder.setMessage(getString(mensagem));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(confirmacao), new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void inicializarComponentes() {

        linearLayoutInserirDescoberta = findViewById(R.id.linearLayoutDescoberta);
        linearLayoutDesafio = findViewById(R.id.linearLayoutDesafio);


        editDescobertaNome = findViewById(R.id.editTextInserirDescobertaNome);
        editDescobertaDescricao = findViewById(R.id.editTextInserirDescobertaDescricao);
        editDescobertaVisibilidade = findViewById(R.id.editTextInserirDescobertaVisibilidades);
        editDescobertaPontuacao = findViewById(R.id.editTextInserirDescobertaPontuacao);

        botaoIniciarParar = findViewById(R.id.buttonComecarParar);
        botaoInserirPonto = findViewById(R.id.buttonInserirDescoberta);
        botaoConcluirDescoberta = findViewById(R.id.buttonConcluirDescoberta);
        botaoFinalziarDesafio = findViewById(R.id.buttonFinalizarDesafio);
        textViewInformacao = findViewById(R.id.textViewDescricaoInsersao);

        editDesafioTitulo = findViewById(R.id.editTextDesafioTitulo);
        editDesafioDesc = findViewById(R.id.editTextDesafioDescricao);
        editDesafioP = findViewById(R.id.editTextDesafioPontuacao);

        /*
        editDesafioTitulo.setText(null);
        editDesafioDesc.setText(null);
        editDesafioP.setText(null);
        editDescobertaNome.setText(null);
        editDescobertaDescricao.setText(null);
        editDescobertaPontuacao.setText(null);
        editDescobertaVisibilidade.setText(null);*/



        botaoIniciarParar.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                gravandoDesafio = !gravandoDesafio;
                comecarDesafio();

            }
        });

        botaoInserirPonto.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                inserirDescoberta();
            }
        });

        botaoConcluirDescoberta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adicionarMarcadorDescoberta(localizacaoAtual);
                finalizarDescoberta();
            }
        });

        botaoFinalziarDesafio.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                adicionarMarcadorFim(localizacaoAtual);
                gravandoDesafio = !gravandoDesafio;
                finalizarDesafio();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(localizacaoAtual, 20));

                if(gravandoDesafio){
                    adicionarMarcadorCaminhando(localizacaoAtual);
                    if(localizacaoPercorrida.size() < 2){
                        adicionarMarcadorInicio(localizacaoAtual);
                    }
                    if(localizacaoPercorrida.size() > 1 && marcadorInicio != null){
                        centralizarMarcadores(marcadorAtual);
                    }
                }else{
                    adicionarMarcadorComum(localizacaoAtual);
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
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ) {
            // TODO: Consider calling
            //    Activity#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1, locationListener);

            return;
        }


    }


    private void centralizarMarcadores(Marker marcador){

        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        builder.include( marcadorInicio.getPosition() );
        Log.d("centralizarMarcadores: ", "centralizarMarcadores: "+marcadorInicio.getPosition());
        builder.include( marcador.getPosition() );

        LatLngBounds bounds = builder.build();

        int largura = getResources().getDisplayMetrics().widthPixels;
        int altura = getResources().getDisplayMetrics().heightPixels;
        int espacoInterno = (int) (largura * 0.20);

        mMap.moveCamera(
                CameraUpdateFactory.newLatLngBounds(bounds,largura,altura,espacoInterno)
        );

    }


    public void adicionarMarcadorCaminhando(LatLng latLng){
        mMap.clear();
        localizacaoPercorrida.add(localizacaoAtual);
        Log.d("localizacaoPercorrida ","onLocationChanged: "+localizacaoPercorrida.get(localizacaoPercorrida.size()-1));
        mMap.addPolyline(polyline.add(localizacaoPercorrida.get(localizacaoPercorrida.size()-1)).width(5).color(Color.RED));
        marcadorAtual = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.localizacao_marcando)));


    }

    public void adicionarMarcadorComum(LatLng latLng){
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.localizacao_usuario)));
    }

    public void adicionarMarcadorInicio(LatLng latLng) {
        mMap.clear();
        localizacaoPercorrida.add(localizacaoAtual);
        marcadorInicio =  mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bandeira_inicio)));

    }
    public void adicionarMarcadorDescoberta(LatLng latLng) {

        mMap.clear();
        localizacaoPercorrida.add(localizacaoAtual);
        listaDescobertas.add(localizacaoAtual);
        listaPontos.add(criarPontoDescoberta(localizacaoAtual));
        Log.d("localizacaoPercorrida ","onLocationChanged: "+localizacaoPercorrida.get(localizacaoPercorrida.size()-1));
        mMap.addPolyline(polyline.add(localizacaoPercorrida.get(localizacaoPercorrida.size()-1)).width(5).color(Color.RED));
        marcadorAtual =  mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.binoculo)));

    }
    public void adicionarMarcadorFim(LatLng latLng) {
        mMap.clear();
        localizacaoPercorrida.add(localizacaoAtual);

        Log.d("localizacaoPercorrida ","onLocationChanged: "+localizacaoPercorrida.get(localizacaoPercorrida.size()-1));
        mMap.addPolyline(polyline.add(localizacaoPercorrida.get(localizacaoPercorrida.size()-1)).width(5).color(Color.RED));
        marcadorAtual =  mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.bandeira_fim)));


    }


    public void mapaFinal(){
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(localizacaoPercorrida.get(0)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bandeira_inicio)));
        mMap.addMarker(new MarkerOptions().position(localizacaoPercorrida.get(localizacaoPercorrida.size()-1)).icon(BitmapDescriptorFactory.fromResource(R.drawable.bandeira_fim)));
        for(int i = 0; i < localizacaoPercorrida.size(); i++){
            mMap.addPolyline(polyline.add(localizacaoPercorrida.get(i)).width(5).color(Color.RED));
        }
        for(int i = 0; i < listaDescobertas.size(); i++){

            mMap.addMarker(new MarkerOptions().position(listaDescobertas.get(i)).icon(BitmapDescriptorFactory.fromResource(R.drawable.binoculo)));
        }




    }

    private void criarDesafioAtual() {
        desafioAtual.setLocalizacaoInicial( localizacaoPercorrida.get(0));
        desafioAtual.setLocalizacaoFinal(localizacaoPercorrida.get(localizacaoPercorrida.size()-1));
        if(listaPontos.size() > 0){
            desafioAtual.setListaPontos(listaPontos);
        }
        desafioAtual.setCaminho(localizacaoPercorrida);
        recuperarDadosDesafio();
        desafioAtual.setTitulo(desafioTitulo);
        desafioAtual.setDescricao(desafioDescricao);
        //desafioAtual.setPontuacao( Integer.parseInt(desafioPontuacao));
        desafioAtual.setStatus(Desafio.STATUS_ATIVO);
        desafioAtual.calcularDistancia();
        desafioAtual.salvar();
    }



    private void recuperarDadosDesafio() {
        desafioTitulo = editDesafioTitulo.getText().toString();
        desafioDescricao = editDesafioDesc.getText().toString();
        desafioPontuacao = editDesafioP.getText().toString();
        Log.d("recuperarDadosDesafio", "recuperarDadosDesafio: "+desafioTitulo+ ","+desafioDescricao+","+desafioPontuacao);

    }

    private Ponto criarPontoDescoberta(LatLng  latLng){
        Ponto novoPonto = new Ponto();
        novoPonto.setNome(editDescobertaNome.getText().toString());
        novoPonto.setLocalizacao(latLng);
        novoPonto.setDescricao(editDesafioDesc.getText().toString());

        //  novoPonto.setVisibilidade(Double.valueOf(String.valueOf(editDescobertaVisibilidade.getText())));
        novoPonto.setPontuacao(Double.valueOf(String.valueOf(editDescobertaPontuacao.getText())));
        novoPonto.setStatus(Ponto.STATUS_ATIVO);

        limparCamposdescoberta();

        return novoPonto;
    }


    private void limparCamposdescoberta() {
        editDescobertaNome.setText(null);
        editDescobertaDescricao.setText(null);
        editDescobertaVisibilidade.setText(null);
        editDescobertaPontuacao.setText(null);
    }
}
