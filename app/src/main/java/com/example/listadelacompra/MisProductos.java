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
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MisProductos extends AppCompatActivity implements DialogoAñadirProducto.ListenerdelDialogo, DialogoEliminarProducto.ListenerdelDialogo{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_productos);

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

        Cursor c = bd.rawQuery("SELECT n.nombre, c.caducidad, c.cant FROM (SELECT p.id, p.nombre FROM Productos AS p WHERE userID='"+nombreUsuario+"') AS n INNER JOIN Cantidades AS c ON n.id = c.productoID",null);
        ArrayList<Integer> cantidades = new ArrayList<Integer>();
        ArrayList<String> nombres = new ArrayList<String>();
        ArrayList<String> caducidades = new ArrayList<String>();
        while (c.moveToNext()){
            int cant = c.getInt(2);
            cantidades.add(cant);
            String nom = c.getString(0);
            nombres.add(nom);
            String cad = c.getString(1);
            caducidades.add(cad);
        }

        ListView productos= (ListView) findViewById(R.id.listProductos);
        MisProductosListView eladap= new MisProductosListView(getApplicationContext(),nombres,cantidades,caducidades);
        productos.setAdapter(eladap);
    }

    public void añadirProducto(View view){
        //añadir al listview
        //sacar un dialogo para meter el producto
        DialogFragment dialogoalerta= new DialogoAñadirProducto();
        dialogoalerta.show(getSupportFragmentManager(), "etiqueta");
    }

    public void eliminarProducto(View view){
        //añadir al listview
        //sacar un dialogo para meter el producto
        DialogFragment dialogoalerta= new DialogoEliminarProducto();
        dialogoalerta.show(getSupportFragmentManager(), "etiqueta");
    }


    @Override
    public void alpulsarAñadir(String nombre, String cant, String date) {
        BaseDeDatos gestorDB = new BaseDeDatos (this, "miDB", null, 1);
        SQLiteDatabase bdR = gestorDB.getReadableDatabase();
        SQLiteDatabase bdW = gestorDB.getWritableDatabase();

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

        Cursor c = bdR.rawQuery("SELECT p.id FROM Productos AS p WHERE p.userID='"+nombreUsuario+"' AND p.nombre='"+nombre+"'", null);
        int id;
        if(!c.moveToNext()){
            ContentValues nuevo = new ContentValues();
            nuevo.put("nombre", nombre);
            nuevo.put("userID", nombreUsuario);
            bdW.insert("Productos", null, nuevo);
            Cursor cu = bdR.rawQuery("SELECT p.id AS id FROM Productos AS p WHERE p.userID='"+nombreUsuario+"' AND p.nombre='"+nombre+"'", null);
            cu.moveToNext();
            id = cu.getInt(cu.getColumnIndex("id"));
        }else{
            id = c.getInt(c.getColumnIndex("id"));
        }

        Cursor cur = bdR.rawQuery("SELECT c.cant FROM Cantidades AS c WHERE c.productoID="+id+" AND c.caducidad='"+date+"'", null);
        if(cur.moveToNext()){
            int cantidad = cur.getInt(cur.getColumnIndex("cant"));
            int update = cantidad + Integer.parseInt(cant);
            ContentValues modificacion = new ContentValues();
            modificacion.put("cant",update);
            String[] argumentos = new String[] {String.valueOf(id), date};
            bdW.update("Cantidades", modificacion, "productoID=? AND caducidad=?", argumentos);
        }else{
            ContentValues nuevo = new ContentValues();
            nuevo.put("cant", cant);
            nuevo.put("productoID", id);
            nuevo.put("caducidad", date);
            bdW.insert("Cantidades", null, nuevo);
        }

        finish();
        startActivity(getIntent());
    }

    @Override
    public void alpulsarEliminar(String nom, String cant) {
        BaseDeDatos gestorDB = new BaseDeDatos (this, "miDB", null, 1);
        SQLiteDatabase bdR = gestorDB.getReadableDatabase();
        SQLiteDatabase bdW = gestorDB.getWritableDatabase();

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

        Cursor c = bdR.rawQuery("SELECT p.id FROM Productos AS p WHERE p.userID='"+nombreUsuario+"' AND p.nombre='"+nom+"'", null);
        int id;
        if(!c.moveToNext()){
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
        }else{
            id = c.getInt(c.getColumnIndex("id"));
            int cantBorrar = Integer.valueOf(cant);
            Cursor cu = bdR.rawQuery("SELECT c.id,c.cant FROM Cantidades AS c WHERE c.productoID="+id, null);
            Cursor cSum = bdR.rawQuery("SELECT SUM(c.cant) AS suma FROM Cantidades AS c WHERE c.productoID="+id, null);
            cSum.moveToNext();
            int sum = cSum.getInt(cSum.getColumnIndex("suma"));
            if(cantBorrar>sum){
                //NOTIF
                NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
                elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle("Mensaje de Alerta")
                        .setContentText("El número a eliminar es mayor que el número existente")
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
            }else {

                int quedan = sum-cantBorrar;

                while(cu.moveToNext()){
                    int cantHay = cu.getInt(cu.getColumnIndex("cant"));
                    int idCant = cu.getInt(cu.getColumnIndex("id"));

                    if (cantBorrar >= cantHay) {
                        cantBorrar = cantBorrar - cantHay;
                        bdW.delete("Cantidades", "id=" + idCant, null);
                    } else {
                        int update = cantHay - cantBorrar;
                        cantBorrar = 0;
                        ContentValues modificacion = new ContentValues();
                        modificacion.put("cant", update);
                        String[] argumentos = new String[]{String.valueOf(idCant)};
                        bdW.update("Cantidades", modificacion, "id=?", argumentos);
                    }
                }
                Cursor cmin = bdR.rawQuery("SELECT p.cantMin FROM Productos AS p WHERE id="+id,null);
                cmin.moveToNext();
                if(quedan<cmin.getInt(cmin.getColumnIndex("cantMin"))){
                    NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
                    elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                            .setContentTitle("Mensaje de Alerta")
                            .setContentText("¡Hora de hacer la compra! Las existencias de "+nom+" están por debajo del mínimo")
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

        finish();
        startActivity(getIntent());
    }
}
