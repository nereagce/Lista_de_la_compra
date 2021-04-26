package com.example.listadelacompra.preferencias;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.listadelacompra.R;

import java.util.Locale;

public class PreferenciasActivity extends AppCompatActivity implements Preferencias.CambiarIdioma{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.preferencias_activity);

    }

    public void cambiarIdioma(String idioma){
        //Cambiar la localización y así cambiar al idioma elegido en las preferencias
        Locale nuevaloc = new Locale(idioma);
        Locale.setDefault(nuevaloc);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        finish();
        startActivity(getIntent());

    }
}
