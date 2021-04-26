package com.example.listadelacompra.generarlista;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.listadelacompra.R;

import org.json.JSONArray;
import org.json.JSONException;

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

        //Guardamos el nombre de usuario para utilizarlo en la tarea que hará la consulta
        Data datos = new Data.Builder()
                .putString("user", nombreUsuario)
                .build();

        //Solicitamos la ejecución de la tarea
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(GenerarListaBBDD.class).setInputData(datos).build();
        WorkManager.getInstance(this).enqueue(otwr);
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) { //Cuando haya terminado
                        //Recogemos el resultado y lo convertimos en json
                        String result = status.getOutputData().getString("result");
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(result);
                            //Utilizamos los datos del json para crear un array con los nombres de los productos
                            ArrayList<String> productos = new ArrayList<String>();
                            for(int i = 0; i < jsonArray.length(); i++) {
                                String nombre = jsonArray.getJSONObject(i).getString("nombre");
                                Log.i("nombre",nombre);
                                productos.add(nombre);
                            }
                            //Utilizamos los ArrayList para rellenar el ListView con los datos obtenidos
                            //Creamos el adaptador para que el ListView sea una lista donde podamos seleccionar diferentes elementos
                            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, productos);
                            lista.setAdapter(arrayAdapter);
                            lista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                            lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {//Cuando se pulse uno de los elementos
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    CheckedTextView v = (CheckedTextView) view;
                                    boolean currentCheck = v.isChecked();//Comprobar si está seleccionado
                                    v.setChecked(currentCheck);//Si está seleccionado quitará el ckeck y si no lo está lo pondrá
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

    }
}


