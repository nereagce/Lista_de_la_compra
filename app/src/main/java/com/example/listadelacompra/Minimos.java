package com.example.listadelacompra;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;

public class Minimos extends AppCompatActivity implements DialogoAñadirMinimo.ListenerdelDialogo{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minimos);

        BaseDeDatos gestorDB = new BaseDeDatos (this, "miDB", null, 1);
        SQLiteDatabase bd = gestorDB.getReadableDatabase();

        BufferedReader ficherointerno = null;
        String nombreUsuario="";
        try {
            ficherointerno = new BufferedReader(new InputStreamReader(
                    openFileInput("nombreUsuario.txt")));
            nombreUsuario = ficherointerno.readLine();
            ficherointerno.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //HAY QUE CAMBIAR LA CONSULTA
        String[] campos = new String[] {"nombre", "cantMin"};
        String[] argumentos = new String[] {nombreUsuario};
        Cursor c = bd.query("Productos",campos,"userID=?",argumentos,null,null,null);
        ArrayList<String> nombres = new ArrayList<String>();
        ArrayList<Integer> cantidades = new ArrayList<Integer>();
        ArrayList<ImageButton> botones = new ArrayList<ImageButton>();
        while (c.moveToNext()){
            int cant = c.getInt(1);
            cantidades.add(cant);
            String nom = c.getString(0);
            nombres.add(nom);
        }
        ListView productos= (ListView) findViewById(R.id.listMinimos);
        MinimosListView eladap= new MinimosListView(getApplicationContext(),nombres,cantidades);
        productos.setAdapter(eladap);
    }

    public void añadirMinimo(View view){
        //cambiar a la actividad de config
        DialogFragment dialogoalerta= new DialogoAñadirMinimo();
        dialogoalerta.show(getSupportFragmentManager(), "etiqueta");
    }

    @Override
    public void alpulsarAñadir(String nom, String cant) {
        BaseDeDatos gestorDB = new BaseDeDatos (this, "miDB", null, 1);
        SQLiteDatabase bd = gestorDB.getWritableDatabase();

        BufferedReader ficherointerno = null;
        String nombreUsuario="";
        try {
            ficherointerno = new BufferedReader(new InputStreamReader(
                    openFileInput("nombreUsuario.txt")));
            nombreUsuario = ficherointerno.readLine();
            ficherointerno.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ContentValues modificacion = new ContentValues();
        modificacion.put("cantMin",cant);
        String[] argumentos = new String[] {nom, nombreUsuario};
        bd.update("Productos", modificacion, "nombre=? AND userID=?", argumentos);

        finish();
        startActivity(getIntent());
    }

}
