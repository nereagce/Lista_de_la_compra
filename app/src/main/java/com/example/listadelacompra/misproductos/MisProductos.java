package com.example.listadelacompra.misproductos;

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

public class MisProductos extends AppCompatActivity implements DialogoAñadirProducto.ListenerdelDialogo, DialogoEliminarProducto.ListenerdelDialogo{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_productos);

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


        ArrayList<Integer> cantidades = new ArrayList<Integer>();
        ArrayList<String> nombres = new ArrayList<String>();
        ArrayList<String> caducidades = new ArrayList<String>();

        //Guardar los datos necesarios para la tarea
        Data datos = new Data.Builder()
                .putString("user", nombreUsuario)
                .putString("conseguir", "true")
                .putString("añadir", "false")
                .putString("eliminar", "false")
                .build();

        //Solicitar la ejecución de la tarea
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(MisProductosBBDD.class).setInputData(datos).build();
        WorkManager.getInstance(this).enqueue(otwr);
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) {//Cuando haya terminado
                        String result = status.getOutputData().getString("result");
                        JSONArray jsonArray = null;
                        try {
                            jsonArray = new JSONArray(result); //Convertir la respuesta a formato json

                            for(int i = 0; i < jsonArray.length(); i++) { //Guardar los datos obtenidos en ArrayList
                                String nombre = jsonArray.getJSONObject(i).getString("nombre");
                                Log.i("nombre",nombre);
                                nombres.add(nombre);
                                String caducidad = jsonArray.getJSONObject(i).getString("caducidad");
                                caducidades.add(caducidad);
                                int cant = jsonArray.getJSONObject(i).getInt("cant");
                                cantidades.add(cant);
                            }
                            //Utilizar los ArrayList para rellenar el ListView con los datos obtenidos
                            ListView productos= (ListView) findViewById(R.id.listProductos);
                            MisProductosListView eladap= new MisProductosListView(getApplicationContext(),nombres,cantidades,caducidades);
                            productos.setAdapter(eladap);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

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

        //Leer del fichero de texto el nombre del usuario conectado
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
            //Guardar los datos necesarios para la ejecución de la tarea
            Data datos = new Data.Builder()
                    .putString("user", nombreUsuario)
                    .putString("nombre", nombre)
                    .putString("cant", cant)
                    .putString("caducidad", date)
                    .putString("conseguir", "false")
                    .putString("añadir", "true")
                    .putString("eliminar", "false")
                    .build();

            //Solicitar la ejecución de la tarea
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(MisProductosBBDD.class).setInputData(datos).build();
            WorkManager.getInstance(this).enqueue(otwr);
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, status -> {
                        if (status != null && status.getState().isFinished()) { //Cuando haya terminado
                            String result = status.getOutputData().getString("result");
                            //Recargar la actividad para que aparezcan las actualizaciones
                            finish();
                            startActivity(getIntent());
                        }
                    });


        }
    }

    @Override
    public void alpulsarEliminar(String nom, String cant) {

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

        //Guardar los datos necesarios para la tarea
        Data datos = new Data.Builder()
                .putString("user", nombreUsuario)
                .putString("nombre", nom)
                .putString("cant", cant)
                .putString("conseguir", "false")
                .putString("añadir", "false")
                .putString("eliminar", "true")
                .build();

        //Solicitar la ejecución de la tarea
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(MisProductosBBDD.class).setInputData(datos).build();
        WorkManager.getInstance(this).enqueue(otwr);
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) {//Cuando haya terminado
                        String result = status.getOutputData().getString("result");//Recoger el resultado
                        if(result.contains("No existe")){//Si contiene "No existe"
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
                        }else if(result.contains("Cantidad mayor")){//Si contiene "Cantidad mayor"
                            //Notificar que se está intentando eliminar un nuero mayor al existente
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
                        }else if(result.contains("Minimo")) { //Si el resultado contiene "Minimo"
                            //Notificar que algun producto esta por debajo del minimo
                            NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
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
                });
    }
}
