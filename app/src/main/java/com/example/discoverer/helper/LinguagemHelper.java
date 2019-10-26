package com.example.discoverer.helper;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import java.util.Locale;

import static android.os.Build.VERSION_CODES.N;

public class LinguagemHelper {
    public static void trocarLinguagem(Resources res){
        Configuration configuracao;
        configuracao = new Configuration(res.getConfiguration());
        if(configuracao.locale ==  Locale.ENGLISH){
            configuracao.locale = new Locale("pt");

        }else if(configuracao.locale !=  Locale.ENGLISH){
            configuracao.locale =  Locale.ENGLISH;
        }



       /* switch (linguagem){
            case "es" :
                configuracao.locale =  Locale.ENGLISH;
                break;
            default:
                configuracao.locale = Locale.getDefault();
        }*/
        res.updateConfiguration(configuracao, res.getDisplayMetrics());

    }
}
