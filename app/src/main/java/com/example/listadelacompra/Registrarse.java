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
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

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
        //Registrar la cuenta y llevar a la actividad principal

        //Cargar la base de datos tanto en modo lectura como en escritura
        BaseDeDatos GestorDB = new BaseDeDatos (this, "miBD", null, 1);
        SQLiteDatabase bdW = GestorDB.getWritableDatabase();
        SQLiteDatabase bdR = GestorDB.getReadableDatabase();

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
            //Comprobar si el usuario existe
            Cursor cu = bdR.rawQuery("SELECT usuario FROM Usuarios AS u WHERE u.usuario='" + user + "'", null);
            if (cu.moveToNext()) {
                //Mandar una notificación indicando que el nombre de usuario ya existe
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
                ContentValues nuevo = new ContentValues();
                nuevo.put("usuario", user);
                nuevo.put("nombre", nom);
                nuevo.put("email", email);
                nuevo.put("contraseña", pass);
                bdW.insert("Usuarios", null, nuevo);

                bdW.close();

                Intent i = new Intent(this, Login.class);
                startActivity(i);
            }
        }
    }
}
