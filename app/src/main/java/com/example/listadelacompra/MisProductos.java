package com.example.listadelacompra;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;

public class MisProductos extends AppCompatActivity implements DialogoAñadirProducto.ListenerdelDialogo, DialogoEliminarProducto.ListenerdelDialogo{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_productos);

        BaseDeDatos gestorDB = new BaseDeDatos (this, "miDB", null, 1);
        SQLiteDatabase bd = gestorDB.getReadableDatabase();

//HAY QUE CAMBIAR LA CONSULTA
        String[] campos = new String[] {"nombre", "cantidad"};
        //Cursor c = bd.query("Productos",campos,null,null,null,null,null);
        Cursor c = bd.rawQuery("SELECT n.nombre, c.caducidad, c.cant FROM (SELECT p.id, p.nombre FROM Productos AS p WHERE userID='nereagce') AS n INNER JOIN Cantidades AS c ON n.id = c.productoID",null);
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
    public void alpulsarAñadir(String nombre, String cant) {
        BaseDeDatos gestorDB = new BaseDeDatos (this, "miDB", null, 1);
        SQLiteDatabase bd = gestorDB.getWritableDatabase();
        System.out.println(nombre+cant);
        ContentValues nuevo = new ContentValues();
        nuevo.put("nombre", nombre);
        nuevo.put("cantidad", cant);
        bd.insert("Productos", null, nuevo);
    }

    @Override
    public void alpulsarEliminar(String nom, String cant) {

    }
}
