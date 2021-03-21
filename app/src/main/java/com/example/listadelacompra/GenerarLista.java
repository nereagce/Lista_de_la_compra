package com.example.listadelacompra;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GenerarLista extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generar_lista);

        ListView lista = findViewById(R.id.listGen);

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

        //Cargar la base de datos en modo lectura
        BaseDeDatos gestorDB = new BaseDeDatos (this, "miDB", null, 1);
        SQLiteDatabase bdR = gestorDB.getReadableDatabase();

        //Hacer la consulta para recorrer todos los productos de este usuario
        Cursor c = bdR.rawQuery("SELECT p.id,p.cantMin,p.nombre FROM Productos AS p WHERE p.userID='"+nombreUsuario+"'", null);
        ArrayList<String> productos = new ArrayList<String>();

        while(c.moveToNext()) {//Para cada producto
            int id = c.getInt(c.getColumnIndex("id"));
            int cantMin = c.getInt(c.getColumnIndex("cantMin"));
            String nom = c.getString(c.getColumnIndex("nombre"));
            //Consulta para saber la cantidad total del producto que tenemos
            Cursor cSum = bdR.rawQuery("SELECT SUM(c.cant) AS suma FROM Cantidades AS c WHERE c.productoID=" + id, null);
            cSum.moveToNext();
            int sum = cSum.getInt(cSum.getColumnIndex("suma"));
            if(sum<=cantMin){ //Si la cantidad almacenada es menor o igual al mínimo, añadir el producto a la lista
                productos.add(nom);//Añadir a la lista
            }
        }
        //Crear el adaptador para que el ListView sea una lista donde podamos seleccionar diferentes elementos
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, productos);
        lista.setAdapter(arrayAdapter);
        lista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {//Cuando se pulse uno d los elementos
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();//Comprobar si está seleccionado
                v.setChecked(currentCheck);//Si está seleccionado quitará el ckeck y si no lo está lo pondrá
            }
        });
    }
}


