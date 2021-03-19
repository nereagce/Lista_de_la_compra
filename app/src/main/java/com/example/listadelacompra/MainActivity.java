package com.example.listadelacompra;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements DialogHacerCompra.ListenerdelDialogo{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    public void misProductos(View view){
        //cambiar a la actividad de mis productos
        Intent i = new Intent (this, MisProductos.class);
        startActivity(i);
    }

    public void abrirPreferencias(View view){
        //cambiar a la actividad de minimos
        Intent i = new Intent (this, PreferenciasActivity.class);
        startActivity(i);
    }

    public void minimos(View view){
        //cambiar a la actividad de minimos
        Intent i = new Intent (this, Minimos.class);
        startActivity(i);
    }

    public void generarLista(View view){
        //cambiar a la actividad de config
        Intent i = new Intent (this, GenerarLista.class);
        startActivity(i);
    }

    public void abrirNavegador(View view){
        //cambiar a la actividad de config
        DialogFragment dialogoalerta= new DialogHacerCompra();
        dialogoalerta.show(getSupportFragmentManager(), "etiqueta");
    }

    @Override
    public void alpulsarAbrir(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(i);
    }
}