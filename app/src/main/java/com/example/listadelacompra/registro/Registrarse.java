package com.example.listadelacompra.registro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.example.listadelacompra.R;
import com.example.listadelacompra.login.Login;


public class Registrarse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrarse);
    }

    public void irLogin(View view){
        //Ir a la pantalla de Login
        Intent i = new Intent (this, Login.class);
        startActivity(i);
    }

    public void registrar(View view){
        //Registrar la cuenta y llevar a la actividad de login

        //Los datos que ha introducido el usuario
        EditText nomTxt = (EditText) findViewById(R.id.nombreTxt);
        String nom = nomTxt.getText().toString();
        EditText userTxt = (EditText) findViewById(R.id.usernameTxt);
        String user = userTxt.getText().toString();
        EditText emailTxt = (EditText) findViewById(R.id.emailTxt);
        String email = emailTxt.getText().toString();
        EditText passTxt = (EditText) findViewById(R.id.passwordTxt);
        String pass = passTxt.getText().toString();
        if( nom.trim().equals("") ||user.trim().equals("") || email.trim().equals("") || pass.trim().equals("") ) {
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
            //Guardar los datos necesarios para la tarea
            Data datos = new Data.Builder()
                    .putString("user", user)
                    .putString("nom", nom)
                    .putString("email", email)
                    .putString("pass", pass)
                    .build();

            //Solicitar la ejecución de la tarea
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(RegistroBBDD.class).setInputData(datos).build();
            WorkManager.getInstance(this).enqueue(otwr);
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, status -> {
                        if (status != null && status.getState().isFinished()) {//Cuando haya terminado
                            String result = status.getOutputData().getString("result");
                            if (result.contains("Duplicate entry")) { //Si el resultado contiene 'Duplicate entry'
                                //Notificar que el usuario ya existe
                                NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
                                elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                                        .setContentTitle(getString(R.string.alerta))
                                        .setContentText(getString(R.string.yaexiste))
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
                            } else {
                                //Redirigir a la actividad de login
                                Intent i = new Intent(this, Login.class);
                                startActivity(i);
                            }
                        }
                    });


        }
    }
}
