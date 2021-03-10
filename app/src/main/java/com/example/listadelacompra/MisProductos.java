package com.example.listadelacompra;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

public class MisProductos extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_productos);

        int[] cantidades={1,4,6,7,2};
        String[] nombres={"Bart Simpson","EdnaKrabappel","HomerSimpson","LisaSimpson","SeymourSkinner"};

        ListView productos= (ListView) findViewById(R.id.listProductos);
        AdaptadorListView eladap= new AdaptadorListView(getApplicationContext(),nombres,cantidades);
        productos.setAdapter(eladap);
    }

    public void añadirProducto(View view){
        //añadir al listview
        //sacar un dialogo para meter el producto
        DialogFragment dialogoalerta= new DialogoAñadir();
        dialogoalerta.show(getSupportFragmentManager(), "etiqueta");
    }
}
