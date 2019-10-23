package com.example.discoverer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.discoverer.R;
import com.example.discoverer.config.ConfiguracaoFirebase;
import com.example.discoverer.helper.UsuarioFirebase;
import com.example.discoverer.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LoginActivity extends AppCompatActivity {
    private EditText editEmail,editSenha,editNome, editEndereco;
    private LinearLayout linearLayoutLogin, linearLayoutCadastro;
    private Button buttonLogin;
    private Switch aSwitchSexo;
    private Usuario usuario = new Usuario();
    private FirebaseAuth autenticacao;
    private TextView textViewCadastrar;
    private boolean cadastrar = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        inicializarComponentes();
        getSupportActionBar().hide();



    }



    private void inicializarComponentes() {
        editEmail = findViewById(R.id.editTextEmailLogin);
        editSenha = findViewById(R.id.editTextSenhaLogin);
        editNome = findViewById(R.id.editTextNomeLogin);
        editEndereco = findViewById(R.id.editTextEnderecoLogin);
        linearLayoutLogin = findViewById(R.id.linearLayoutLogin);
        linearLayoutCadastro = findViewById(R.id.linearLayoutCadastro);
        aSwitchSexo = findViewById(R.id.switchSexo);
        buttonLogin = findViewById(R.id.buttonTelaLogin);
        textViewCadastrar = findViewById(R.id.textViewCadastrar);


        textViewCadastrar.setVisibility(View.VISIBLE);
        linearLayoutCadastro.setVisibility(View.GONE);
        buttonLogin.setText(R.string.tela_login_entrar);
        alteraLayoutLogin();



        textViewCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cadastrar = !cadastrar;
                alteraLayoutLogin();

            }
        });
    }

    public void alteraLayoutLogin(){
        if(cadastrar == true ){
            textViewCadastrar.setText(R.string.cancelar);
            linearLayoutCadastro.setVisibility(View.VISIBLE);
            buttonLogin.setText(R.string.tela_login_cadastrar);
        }else if(cadastrar == false ){
            textViewCadastrar.setText(R.string.tela_text_cadastrar);
            linearLayoutCadastro.setVisibility(View.GONE);
            buttonLogin.setText(R.string.tela_login_entrar);
        }

    }
    public void entrar(View view){
        Editable email = editEmail.getText();
        Editable senha = editSenha.getText();
        if(!email.toString().isEmpty()){
            if(!senha.toString().isEmpty()){
                usuario.setEmail(email.toString());
                usuario.setSenha(senha.toString());
                if(cadastrar == true){
                    recuperarDadosCadastro();
                    cadastrarUsuario(usuario);
                }else{
                    logarUsuario(usuario);
                }

            }else{
                Toast.makeText(LoginActivity.this, "Insira a senha",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(LoginActivity.this, "Insira o email",Toast.LENGTH_LONG).show();
        }

    }

    private void logarUsuario(final Usuario usuario){
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.signInWithEmailAndPassword(
                usuario.getEmail(),
                usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    String idUsuario = task.getResult().getUser().getUid();
                    String nomeUsuario = task.getResult().getUser().getDisplayName();
                    usuario.setID(idUsuario);

                    Toast.makeText(LoginActivity.this,
                            "Bem vindo "+ nomeUsuario,
                            Toast.LENGTH_LONG).show();
                    redirecionarUsuario(idUsuario, nomeUsuario);
                }else{
                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e ) {
                        excecao = "Usuário não está cadastrado.";
                        cadastrar = true;
                        alteraLayoutLogin();
                    }catch ( FirebaseAuthInvalidCredentialsException e ){
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();

                }
            }
        });
    }


    private void cadastrarUsuario(final Usuario usuario) {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                usuario.getEmail(),usuario.getSenha()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                 try {

                     String idUsuario = task.getResult().getUser().getUid();
                     usuario.setID(idUsuario);
                     usuario.setDistanciaPercorrida(0.0);
                     usuario.setPontuacao(0.0);
                     usuario.setNumeroDescobertas(0);
                     usuario.salvar();

                     UsuarioFirebase.atualizarNomeUsuario(usuario.getNome());


                     Toast.makeText(LoginActivity.this,
                             "cadastro efetuado "+task.getResult().getUser().getDisplayName(),
                             Toast.LENGTH_LONG).show();
                     redirecionarUsuario(idUsuario, usuario.getNome());

                 }catch (Exception e ){
                     e.printStackTrace();
                 }

                }else{


                    String excecao = "";
                    try {
                        throw task.getException();
                    }catch ( FirebaseAuthInvalidUserException e ) {
                        excecao = "Usuário não está cadastrado.";
                    }catch ( FirebaseAuthInvalidCredentialsException e ){
                        excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                    }catch (Exception e){
                        excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(LoginActivity.this,
                            excecao,
                            Toast.LENGTH_SHORT).show();


                }

            }

        });

    }

    private String verificaGeneroUsuario(){
        return aSwitchSexo.isChecked() ? "f" : "m";
    }

    private void recuperarDadosCadastro(){
        String nome,endereco;
        nome = editNome.getText().toString();
        endereco = editEndereco.getText().toString();
        if(!nome.isEmpty()){
            if(!endereco.isEmpty()){
                usuario.setNome(nome);
                usuario.setEndereco(endereco);
                usuario.setGenero(verificaGeneroUsuario());

            }else{
                Toast.makeText(LoginActivity.this, "Insira o endereco",Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(LoginActivity.this, "Insira o editDescobertaNome",Toast.LENGTH_LONG).show();
        }
    }

    public void redirecionarUsuario(String idUsuario, String nomeUsuario){
        Usuario usuario = UsuarioFirebase.recuperarUsuarioLogado();
        Log.d("redirecionarUsuario","redirecionarUsuario "+usuario.toString());
        Intent i = new Intent(this, MainActivity.class);
        Bundle extras = new Bundle();
        extras.putString("usuarioNome", nomeUsuario);
        extras.putString("usuarioToken", idUsuario);
        i.putExtras(extras);
        startActivity(i);

    }


}
