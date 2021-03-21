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

        //Cargar la base de datos en modo lectura para hacer la consulta
        BaseDeDatos gestorDB = new BaseDeDatos (this, "miDB", null, 1);
        SQLiteDatabase bd = gestorDB.getReadableDatabase();

        //Leer del fichero en nombre de usuario
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

        //Mediante una consulta a la base de datos, conseguir los nombres, cantidades y fechas de caducidad de los productos del usuario
        Cursor c = bd.rawQuery("SELECT n.nombre, c.caducidad, c.cant FROM (SELECT p.id, p.nombre FROM Productos AS p WHERE userID='"+nombreUsuario+"') AS n INNER JOIN Cantidades AS c ON n.id = c.productoID",null);
        ArrayList<Integer> cantidades = new ArrayList<Integer>();
        ArrayList<String> nombres = new ArrayList<String>();
        ArrayList<String> caducidades = new ArrayList<String>();
        while (c.moveToNext()){ //Guardar los datos en ArrayList
            int cant = c.getInt(2);
            cantidades.add(cant);
            String nom = c.getString(0);
            nombres.add(nom);
            String cad = c.getString(1);
            caducidades.add(cad);
        }

        //Utilizar los ArrayList para rellenar el ListView con los datos obtenidos
        ListView productos= (ListView) findViewById(R.id.listProductos);
        MisProductosListView eladap= new MisProductosListView(getApplicationContext(),nombres,cantidades,caducidades);
        productos.setAdapter(eladap);
    }

    public void añadirProducto(View view){
        //Abre un diálogo para introducir el producto que hemos comprado
        DialogFragment dialogoalerta= new DialogoAñadirProducto();
        dialogoalerta.show(getSupportFragmentManager(), "etiqueta");
    }

    public void eliminarProducto(View view){
        //Abre un diálogo para introducir el producto que hemos consumido
        DialogFragment dialogoalerta= new DialogoEliminarProducto();
        dialogoalerta.show(getSupportFragmentManager(), "etiqueta");
    }


    @Override
    public void alpulsarAñadir(String nombre, String cant, String date) {
        //Cargar la base de datos tanto en modo lectura como en modo escritura
        BaseDeDatos gestorDB = new BaseDeDatos (this, "miDB", null, 1);
        SQLiteDatabase bdR = gestorDB.getReadableDatabase();
        SQLiteDatabase bdW = gestorDB.getWritableDatabase();

        //Leer del ficher de texto el nombre del usuario conectado
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
        if( nombre.trim().equals("") || date==null||cant.equals("0")) {
            //Notificar que alguno de los campos está vacío
            NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
            elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setContentTitle(getString(R.string.alerta))
                    .setContentText(getString(R.string.norelleno))
                    .setSubText(getString(R.string.extrainfo))
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
            //Comprobaremos en la base de datos si ese producto ya existe.
            Cursor c = bdR.rawQuery("SELECT p.id FROM Productos AS p WHERE p.userID='" + nombreUsuario + "' AND p.nombre='" + nombre + "'", null);
            int id;
            if (!c.moveToNext()) { //Si no existe, lo registraremos y guardaremos su id (es necesario hacer otra consulta, ya que el id es autoincrement)
                ContentValues nuevo = new ContentValues();
                nuevo.put("nombre", nombre);
                nuevo.put("userID", nombreUsuario);
                bdW.insert("Productos", null, nuevo);
                Cursor cu = bdR.rawQuery("SELECT p.id AS id FROM Productos AS p WHERE p.userID='" + nombreUsuario + "' AND p.nombre='" + nombre + "'", null);
                cu.moveToNext();
                id = cu.getInt(cu.getColumnIndex("id"));
            } else { //Si existe, guardaremos su id
                id = c.getInt(c.getColumnIndex("id"));
            }

            //Comprobamos si ya registro de ese producto con la fecha de caducidad seleccionada por el usuario
            Cursor cur = bdR.rawQuery("SELECT c.cant FROM Cantidades AS c WHERE c.productoID=" + id + " AND c.caducidad='" + date + "'", null);
            if (cur.moveToNext()) { //Si existe, sumaremos la cantidad comprada a la ya registrada
                int cantidad = cur.getInt(cur.getColumnIndex("cant"));
                int update = cantidad + Integer.parseInt(cant);
                ContentValues modificacion = new ContentValues();
                modificacion.put("cant", update);
                String[] argumentos = new String[]{String.valueOf(id), date};
                bdW.update("Cantidades", modificacion, "productoID=? AND caducidad=?", argumentos);
            } else { //Si no existe, realizaremos un nuevo registro
                ContentValues nuevo = new ContentValues();
                nuevo.put("cant", cant);
                nuevo.put("productoID", id);
                nuevo.put("caducidad", date);
                bdW.insert("Cantidades", null, nuevo);
            }

            //Recargar la actividad para que aparezcan las actualizaciones
            finish();
            startActivity(getIntent());
        }
    }

    @Override
    public void alpulsarEliminar(String nom, String cant) {
        //Cargar la base de datos tanto en lectura como en escritura
        BaseDeDatos gestorDB = new BaseDeDatos (this, "miDB", null, 1);
        SQLiteDatabase bdR = gestorDB.getReadableDatabase();
        SQLiteDatabase bdW = gestorDB.getWritableDatabase();

        //Leer del fichero de texto el nombre de usuario
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

        //Comprobaremos mediante una consulta si el producto que se está intntando eliminar existe
        Cursor c = bdR.rawQuery("SELECT p.id FROM Productos AS p WHERE p.userID='"+nombreUsuario+"' AND p.nombre='"+nom+"'", null);
        int id;
        if(!c.moveToNext()){ //Si no existe, se le notificará al usuario
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
        }else{ //Si existe
            //Recogeremos el id del producto y la cantidad a eliminar.
            id = c.getInt(c.getColumnIndex("id"));
            int cantBorrar = Integer.valueOf(cant);
            //Leeremos de la base de datos las cantidades y los id
            Cursor cu = bdR.rawQuery("SELECT c.id,c.cant FROM Cantidades AS c WHERE c.productoID="+id, null);
            //Conseguiremos la cantidad total del producto que el usuario tiene guardada
            Cursor cSum = bdR.rawQuery("SELECT SUM(c.cant) AS suma FROM Cantidades AS c WHERE c.productoID="+id, null);
            cSum.moveToNext();
            int sum = cSum.getInt(cSum.getColumnIndex("suma"));
            if(cantBorrar>sum){ //Si la cantidad a eliminar es mayor a la existente, se le notificará el error al usuario
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
                //Guardaremos en un parámetro la cantidad que quedará después de eliminar
                int quedan = sum-cantBorrar;


                while(cu.moveToNext()){//Para cada registro
                    //Guardar el id y la cantidad
                    int cantHay = cu.getInt(cu.getColumnIndex("cant"));
                    int idCant = cu.getInt(cu.getColumnIndex("id"));

                    if (cantBorrar >= cantHay) { //Si la cantidad a eliminar es mayor o igual a la del registro
                        //Actualizamos la cantidad a borrar restandole la de este registro y lo eliminamos de la base de datos
                        cantBorrar = cantBorrar - cantHay;
                        bdW.delete("Cantidades", "id=" + idCant, null);
                    } else {//Si la cantidad a eliminar es menor a la del registro
                        //Actualizaremos el registro restandole dicha cantidad y le daremos a cantBorrar el valor 0 para indicar que hemos terminado
                        int update = cantHay - cantBorrar;
                        cantBorrar = 0;
                        ContentValues modificacion = new ContentValues();
                        modificacion.put("cant", update);
                        String[] argumentos = new String[]{String.valueOf(idCant)};
                        bdW.update("Cantidades", modificacion, "id=?", argumentos);
                    }
                }
                //Leeremos de la base de datos cuál es la cantidad mínima del producto que el usuario desea tener
                Cursor cmin = bdR.rawQuery("SELECT p.cantMin FROM Productos AS p WHERE id="+id,null);
                cmin.moveToNext();
                if(quedan<cmin.getInt(cmin.getColumnIndex("cantMin"))){ //Si la cantidad restante es menos al minimo
                    //Notificar al usuario que las existencias están por debajo del mínimo marcado
                    NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
                    elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                            .setContentTitle(getString(R.string.alerta))
                            .setContentText(getString(R.string.horadecompra))
                            .setSubText(getString(R.string.extrainfo))
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
        //Recargar la actividad para que muestre las actualizaciones
        finish();
        startActivity(getIntent());
    }
}
