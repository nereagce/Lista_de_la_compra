package com.example.listadelacompra;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void configurar(View view){
        //cambiar a la actividad de config
        Intent i = new Intent (this, Configuracion.class);
        startActivity(i);
    }

    public void misProductos(View view){
        //cambiar a la actividad de mis productos
        Intent i = new Intent (this, MisProductos.class);
        startActivity(i);
    }

    public void heHechoLaCompra(View view){
        //cambiar a la actividad de he hecho la compra
        Intent i = new Intent (this, HeHechoLaCompra.class);
        startActivity(i);
    }

    public void generarLista(View view){
        //cambiar a la actividad de config
        Intent i = new Intent (this, GenerarLista.class);
        startActivity(i);
    }
}