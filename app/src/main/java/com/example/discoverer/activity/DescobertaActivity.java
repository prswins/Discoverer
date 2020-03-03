package com.example.discoverer.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.discoverer.R;
import com.example.discoverer.model.Ponto;
import com.example.discoverer.model.Usuario;
import com.google.ar.core.Anchor;
import com.google.ar.core.Frame;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.FrameTime;
import com.google.ar.sceneform.HitTestResult;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;

import java.util.Collection;

public class DescobertaActivity extends AppCompatActivity {


    private ArFragment arFragment;
    private Boolean isModelPlaced = false;
    private Ponto pontoAtual;
    private TextView textTitulo, textDescricao;
    private Button botaoSair;
    private AlertDialog alerta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_descoberta);
        textTitulo = findViewById(R.id.textViewRATitulo);
        textDescricao = findViewById(R.id.textViewRADescricao);
        botaoSair = findViewById(R.id.buttonARCancelar);

        botaoSair.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                retornarActivityCancel();
            }
        });

        if (getIntent().getExtras().getString("titulo") == null){
            Log.d("recuperado", "recuperado: vazio");
        }else{
            Log.d("recuperado", "recuperado: "+getIntent().getExtras().getString("titulo"));
            textTitulo.setText(getIntent().getExtras().getString("titulo"));
            textDescricao.setText(getIntent().getExtras().getString("desc"));
        }
       /* Intent intent = getIntent();
        Bundle dados = new Bundle();
        dados = intent.getExtras();
        if (dados.isEmpty()){
            Log.d("onCreate", "onCreate: vazio" );
        }else {
            textTitulo.setText(dados.getString("titulo").toString());
            //textDescricao.setText(dados.getString("desc").toString());
        }*/



        /*
        if(getIntent() != null){
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            //Log.d("getIntent", "recuperarUsuario: getIntent: " + getIntent().getBundleExtra("usuario"));
            if(bundle.getSerializable("ponto") != null){
                pontoAtual = (Ponto) bundle.getSerializable("ponto");

                Log.d("recuperarPonto", "recuperarPonto: "+pontoAtual.getNome() + " "
                        + pontoAtual.getDescricao() +"  "
                        + pontoAtual.getPontuacao()+"  "
                );
                textTitulo.setText(pontoAtual.getNome());
                textDescricao.setText(pontoAtual.getDescricao());


            }else{
                Log.d("recuperarPonto", "recuperarPonto: "+pontoAtual.getNome() + " ");

                textTitulo.setText("erro");
                textDescricao.setText("erro");
            }
        }*/

        arFragment = (ArFragment)getSupportFragmentManager().findFragmentById(R.id.mainFragmentAR2);



    // objeto eh adicionado com click//
        if (arFragment != null) {
            arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener(){
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                    Toast.makeText(getApplicationContext(),R.string.clicar_no_plano,Toast.LENGTH_LONG).show();
                    /*Anchor anchor = hitResult.createAnchor();

                    ModelRenderable.builder()
                            .setSource(getApplicationContext(), R.raw.bear)
                            .build()
                            .thenAccept(renderable -> adicionarModelo3d(anchor, renderable))
                            .exceptionally(
                                    throwable -> {
                                        Toast.makeText(getApplicationContext(),"errro ao exibir objeto",Toast.LENGTH_LONG).show();
                                        Log.e("RENDERABLEOBJETO", "Unable to load Renderable.", throwable);
                                        return null;
                                    });*/



                }
            });
        }

        arFragment.getArSceneView().getScene().addOnUpdateListener(this::onUpdate);

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
                    .setSource(getApplicationContext(), R.raw.trofeu)
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


                //retornarActivityOK();
                exibirMensagem();

            }
        });
        arFragment.getArSceneView().getScene().addChild(anchorNode);


    }
    private void retornarActivityOK(){
        Toast.makeText(arFragment.getContext(),R.string.bonus_obtido,Toast.LENGTH_LONG).show();
        String resultado =  "confirmado";
        Intent returnIntent = new Intent();
        returnIntent.putExtra("resultado",resultado);
        setResult(RESULT_OK,returnIntent);
        finish();

    }

    private void retornarActivityCancel(){

        String resultado =  "cancelado";
        Intent returnIntent = new Intent();
        returnIntent.putExtra("resultado",resultado);
        setResult(RESULT_OK,returnIntent);
        finish();

    }
    private void exibirMensagem() {
        //LayoutInflater é utilizado para inflar nosso layout em uma view.
        //-pegamos nossa instancia da classe
        LayoutInflater li = getLayoutInflater();

        //inflamos o layout alerta.xml na view
        View view = li.inflate(R.layout.alert_descoberta, null);
        //definimos para o botão do layout um clickListener

        view.findViewById(R.id.textViewAlertDescoberta);
        view.findViewById(R.id.bt).setOnClickListener(new View.OnClickListener() {
            public void onClick(View arg0) {
                //exibe um Toast informativo.
               // Toast.makeText(DescobertaActivity.this, "alerta.dismiss()", Toast.LENGTH_SHORT).show();
                retornarActivityOK();
                //desfaz o alerta.
                alerta.dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        alerta = builder.create();
        alerta.show();
    }



}
