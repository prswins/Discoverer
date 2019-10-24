package com.example.discoverer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.discoverer.R;
import com.example.discoverer.model.Desafio;
import com.example.discoverer.model.Usuario;

import java.text.NumberFormat;
import java.util.List;

public class DesafioAdapter extends RecyclerView.Adapter<DesafioAdapter.MyViewHolder> {
    private List<Desafio> desafios;
    private Context context;
    private Usuario jogador;

    public DesafioAdapter(List<Desafio> desafios, Context context, Usuario jogador) {
        this.desafios = desafios;
        this.context = context;
        this.jogador = jogador;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_desafios, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Desafio desafio = desafios.get(position);
        holder.titulo.setText(desafio.getTitulo());
        NumberFormat format = NumberFormat.getInstance();
        format.setMaximumFractionDigits(3);
        format.setMinimumFractionDigits(1);
        format.setMinimumIntegerDigits(1);
        format.setMaximumIntegerDigits(3);
        holder.distancia.setText(String.valueOf(format.format(desafio.getDistancia()/1000))+" Km");
        holder.descricao.setText(desafio.getDescricao());

    }

    @Override
    public int getItemCount() {
        return desafios.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView titulo, distancia, descricao;





        public MyViewHolder(View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.textAdapterDesafioTitulo);
            distancia = itemView.findViewById(R.id.textAdapterDesafioDistancia);
            descricao = itemView.findViewById(R.id.textAdapterDesafioDescricao);

        }
    }
}
