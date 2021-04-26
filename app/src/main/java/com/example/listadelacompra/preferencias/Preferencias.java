package com.example.listadelacompra.preferencias;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.example.listadelacompra.R;

import java.util.Locale;

public class Preferencias extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    CambiarIdioma miCambiarIdioma;

    public interface CambiarIdioma {
        void cambiarIdioma(String idioma);
    }


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.preferencias);

        miCambiarIdioma =(CambiarIdioma) getActivity();
    }
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }
    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        SharedPreferences.Editor editor= sharedPreferences.edit();
        switch (key) {
            case "switch": //Si la que ha cambiado es el switch de modo noche
                Boolean modonoche = sharedPreferences.getBoolean(key, false);
                if(modonoche){//Si está en true
                    //Activar el modo noche
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    editor.putBoolean(key,true);
                    editor.apply();
                } else{//Si está en false
                    //Activar el modo día
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    editor.putBoolean(key,false);
                    editor.apply();
                }
                break;
            case "list_preference_1": //Si la que ha cambiado es el idioma
                String idioma = sharedPreferences.getString(key, "ES");
                //Cambiarlo en base al que se ha elegido
                if(idioma.equals("ES")){
                    miCambiarIdioma.cambiarIdioma("es");
                    editor.putString(key,"ES");
                    editor.apply();
                }else if(idioma.equals("EU")){
                    miCambiarIdioma.cambiarIdioma("eu");
                    editor.putString(key,"EU");
                    editor.apply();
                }else{
                    miCambiarIdioma.cambiarIdioma("en");
                    editor.putString(key,"EN");
                    editor.apply();
                }

            default:
                break;
        }
    }
}
