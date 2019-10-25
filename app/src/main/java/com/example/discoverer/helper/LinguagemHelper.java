package com.example.discoverer.helper;

import android.content.res.Configuration;
import android.content.res.Resources;

import java.util.Locale;

public class LinguagemHelper {
    public static void trocarLinguagem(Resources res, String linguagem){
        Configuration configuracao;
        configuracao = new Configuration(res.getConfiguration());

        switch (linguagem){
            case "es" :
                configuracao.locale =  Locale.ENGLISH;
                break;
            case   "PT":
                configuracao.locale = Locale.getDefault();
                break;

            default:
                configuracao.locale = Locale.getDefault();
        }
        res.updateConfiguration(configuracao, res.getDisplayMetrics());

    }
}
