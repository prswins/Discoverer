package com.example.discoverer.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discoverer.R;
import com.example.discoverer.model.Usuario;

import java.text.NumberFormat;
import java.util.List;


public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.MyViewHolder> {
    private List<Usuario> usuarios;
    private Context context;

    public RankingAdapter(List<Usuario> usuarios, Context context){
        this.usuarios = usuarios;
        this.context = context;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_lista_usuario, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Log.d("RankingAdapter", "onBindViewHolder: "+usuarios.get(position).getNome());
        Usuario usuario = usuarios.get(position);
        holder.nome.setText(usuario.getNome());
        NumberFormat formatoDistancia = NumberFormat.getInstance();
        formatoDistancia.setMaximumFractionDigits(3);
        formatoDistancia.setMinimumFractionDigits(1);
        formatoDistancia.setMinimumIntegerDigits(1);
        formatoDistancia.setMaximumIntegerDigits(3);
        NumberFormat formatoPontuacao = NumberFormat.getInstance();
        formatoPontuacao.setMinimumIntegerDigits(1);
        formatoPontuacao.setMaximumIntegerDigits(10);
        holder.distancia.setText(String.valueOf(formatoDistancia.format(usuario.getDistanciaPercorrida()/1000))+" Km");
        // holder.distancia.setText(String.valueOf(usuario.getDistanciaPercorrida()/1000)+" Km");
        holder.posicao.setText((position+1)+"°");
        String pontuacao = (String.valueOf(formatoPontuacao.format(usuario.getPontuacao())));
        holder.pontuacao.setText(pontuacao);
        if(usuario.getPontuacao() > 1){
            holder.descPontuacao.setText( R.string.main_unidade_medida_pontuacao_plural);

        }else {
            holder.descPontuacao.setText( R.string.main_unidade_medida_pontuacao_singular);
        }


    }

    @Override
    public int getItemCount() {
        return usuarios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView nome, distancia,  pontuacao,descPontuacao, posicao;



        public MyViewHolder(View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.textViewRNome);
            distancia = itemView.findViewById(R.id.textViewRDistancia);
            pontuacao = itemView.findViewById(R.id.textViewRPontuacao);
            posicao = itemView.findViewById(R.id.textViewRPosicao);
            descPontuacao = itemView.findViewById(R.id.textViewPontosDesc);


        }
    }
}
