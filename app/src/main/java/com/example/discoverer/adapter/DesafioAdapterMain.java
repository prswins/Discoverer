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
import com.example.discoverer.model.Desafio;

import java.text.NumberFormat;
import java.util.List;

public class DesafioAdapterMain extends RecyclerView.Adapter<DesafioAdapterMain.MyViewHolder> {
    private List<Desafio> desafios;
    private Context context;

    public DesafioAdapterMain(List<Desafio> desafios, Context context) {
        this.desafios = desafios;
        this.context = context;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_desafios_main, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Desafio desafio = desafios.get(position);
        Log.d("onBindViewHolder", "onBindViewHolder: "+ desafio.toString());
        holder.titulo.setText(desafio.getTitulo());
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(3);
        format.setMinimumFractionDigits(1);
        format.setMinimumIntegerDigits(1);
        format.setMaximumIntegerDigits(3);
        holder.distancia.setText(String.valueOf(format.format(desafio.getDistancia()/1000))+" Km");
        if(desafio.getPontuacao() > 1){
            holder.descPontuacao.setText(R.string.main_unidade_medida_pontuacao_plural);
        }else {
            holder.descPontuacao.setText(R.string.main_unidade_medida_pontuacao_singular);
        }
        holder.descricao.setText(desafio.getDescricao());
        holder.pontuacao.setText(String.valueOf(desafio.getPontuacao()));

    }

    @Override
    public int getItemCount() {
        return desafios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo, distancia, descricao, pontuacao, descPontuacao;





        public MyViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textViewTituloAdapterMain);
            distancia = itemView.findViewById(R.id.textViewDistanciaAdapterMain);
            pontuacao = itemView.findViewById(R.id.textViewPontuacaoAdapterMain);
            descPontuacao = itemView.findViewById(R.id.textViewDescPontuacaoAdapterMain);
            descricao = itemView.findViewById(R.id.textViewDescricaoAdapterMain);


        }
    }
}
