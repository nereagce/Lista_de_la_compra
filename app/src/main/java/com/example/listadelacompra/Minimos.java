package com.example.listadelacompra;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
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

        //Cargar la base de datos en modo lectura
        BaseDeDatos gestorDB = new BaseDeDatos (this, "miDB", null, 1);
        SQLiteDatabase bd = gestorDB.getReadableDatabase();

        //Leer el nombre de usuario del fichero de texto
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

        //Leer de la base de datos las cantidades mínimas de cada producto marcadas por el usuario
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
        //Rellenar el listview con los datos obtenidos
        ListView productos= (ListView) findViewById(R.id.listMinimos);
        MinimosListView eladap= new MinimosListView(getApplicationContext(),nombres,cantidades);
        productos.setAdapter(eladap);
    }

    public void añadirMinimo(View view){
        //Abrir diálogo para editar la cantidad mínima de un producto
        DialogFragment dialogoalerta= new DialogoAñadirMinimo();
        dialogoalerta.show(getSupportFragmentManager(), "etiqueta");
    }

    @Override
    public void alpulsarAñadir(String nom, String cant) {
        //Cargar base de datos en modo lectura y escritura
        BaseDeDatos gestorDB = new BaseDeDatos (this, "miDB", null, 1);
        SQLiteDatabase bdW = gestorDB.getWritableDatabase();
        SQLiteDatabase bdR = gestorDB.getReadableDatabase();

        //Leer nombre de usuario del fichero de texto
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

        //Realizar la consulta para comprobar si el producto existe
        Cursor c = bdR.rawQuery("SELECT * FROM Productos AS p WHERE p.userID='"+nombreUsuario+"' AND nombre='"+nom+"'",null);
        if(c.moveToNext()) { //Si existe
            //Actualizar el registro con el mínimo indicado por el usuario
            ContentValues modificacion = new ContentValues();
            modificacion.put("cantMin", cant);
            String[] argumentos = new String[]{nom, nombreUsuario};
            bdW.update("Productos", modificacion, "nombre=? AND userID=?", argumentos);

            //Recargar la actividad
            finish();
            startActivity(getIntent());
        } else{//Si no existe
            //Notificar al usuario del error
            NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
            elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setContentTitle("Mensaje de Alerta")
                    .setContentText("El producto no existe")
                    .setSubText("Información extra")
                    .setVibrate(new long[]{0, 1000, 500, 1000})
                    .setAutoCancel(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                        NotificationManager.IMPORTANCE_DEFAULT);
                elCanal.setDescription("Descripción del canal");
                elCanal.enableLights(true);
                elCanal.setLightColor(Color.RED);
                elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                elCanal.enableVibration(true);
                elManager.createNotificationChannel(elCanal);
            }
            elManager.notify(1, elBuilder.build());
        }
    }

}
