package com.example.listadelacompra;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
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
            System.out.println("UPDATEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEE");
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
            //EXCEPCION PRODUCTO NO EXISTE
        }else{
            id = c.getInt(c.getColumnIndex("id"));
            Cursor cu = bdR.rawQuery("SELECT c.id,c.cant FROM Cantidades AS c WHERE c.productoID="+id, null);
            if(cu.getCount()==1){
                cu.moveToNext();
                int cantidad = cu.getInt(cu.getColumnIndex("cant"));
                int update = cantidad-Integer.valueOf(cant);
                ContentValues modificacion = new ContentValues();
                modificacion.put("cant",update);
                String[] argumentos = new String[] {String.valueOf(id)};
                bdW.update("Cantidades", modificacion, "productoID=?", argumentos);
            }
        }

        finish();
        startActivity(getIntent());
    }
}
