package com.example.listadelacompra.minimos;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
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

public class Minimos extends AppCompatActivity implements DialogoAñadirMinimo.ListenerdelDialogo {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.minimos);


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

        ArrayList<String> nombres = new ArrayList<String>();
        ArrayList<Integer> cantidades = new ArrayList<Integer>();

        //Guardamos los datos que necesitaremos para realizar la tarea
        Data datos = new Data.Builder()
                .putString("user", nombreUsuario)
                .putString("funcion", "conseguir")
                .build();

        //Solicitamos la ejecución de la tarea
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(MinimosBBDD.class).setInputData(datos).build();
        WorkManager.getInstance(this).enqueue(otwr);
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) { //Cuando haya terminado
                        //Recogemos el resultado y lo convertimos a formato jsdon
                        String result = status.getOutputData().getString("result");
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(result);
                            //Guardamos los datos en ArrayList
                            for(int i = 0; i < jsonArray.length(); i++) {
                                String nombre = jsonArray.getJSONObject(i).getString("nombre");
                                Log.i("nombre",nombre);
                                nombres.add(nombre);
                                int cant = jsonArray.getJSONObject(i).getInt("cantMin");
                                cantidades.add(cant);
                            }
                            //Rellenamos el listview con los datos obtenidos
                            ListView productos= (ListView) findViewById(R.id.listMinimos);
                            MinimosListView eladap= new MinimosListView(getApplicationContext(),nombres,cantidades);
                            productos.setAdapter(eladap);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    public void añadirMinimo(View view){
        //Abrir diálogo para editar la cantidad mínima de un producto
        DialogFragment dialogoalerta= new DialogoAñadirMinimo();
        dialogoalerta.show(getSupportFragmentManager(), "etiqueta");
    }

    @Override
    public void alpulsarAñadir(String nom, String cant) {

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

        //Guardar los datos necesarios para la tarea
        Data datos = new Data.Builder()
                .putString("user", nombreUsuario)
                .putString("funcion", "añadir")
                .putString("nombre", nom)
                .putString("cant", cant)
                .build();

        //Solicitar la ejecución de la tarea
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(MinimosBBDD.class).setInputData(datos).build();
        WorkManager.getInstance(this).enqueue(otwr);
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) {//Cuando haya terminado
                        //Recoger el resultado
                        String result = status.getOutputData().getString("result");
                        if(result.contains("No existe")){//Si contiene 'No existe'
                            //Notificar que el producto no existe
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
                        }else{ //En caso contrario
                            //Recargar la actividad para que aparezcan las actualizaciones
                            finish();
                            startActivity(getIntent());
                        }

                    }
                });
    }

}
