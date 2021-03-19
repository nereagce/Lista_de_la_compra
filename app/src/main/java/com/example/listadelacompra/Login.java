package com.example.listadelacompra;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;

import java.io.IOException;
import java.io.OutputStreamWriter;


public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        Boolean modonoche = sharedPreferences.getBoolean("switch", false);
        if(modonoche){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putBoolean("switch",true);
            editor.apply();
        } else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putBoolean("switch",false);
            editor.apply();
        }
    }

    public void logIn(View view) throws IOException {
        //comprobar que existe la cuenta, si si llevar a main, si no error
        String[] campos = new String[] {"usuario", "contraseña"};
        EditText userTxt = (EditText) findViewById(R.id.userTxt);
        String user = userTxt.getText().toString();
        EditText passTxt = (EditText) findViewById(R.id.passTxt);
        String pass = passTxt.getText().toString();
        String[] argumentos = new String[] {user,pass};

        BaseDeDatos GestorDB = new BaseDeDatos (this, "miBD", null, 1);
        SQLiteDatabase bd = GestorDB.getReadableDatabase();
        Cursor cu = bd.rawQuery("SELECT usuario, contraseña FROM Usuarios AS u WHERE u.usuario='"+user+"' AND u.contraseña='"+pass+"'",null);

        if(cu.moveToNext()){
            Intent i = new Intent (this, MainActivity.class);
            startActivity(i);
            OutputStreamWriter fichero = new OutputStreamWriter(openFileOutput("nombreUsuario.txt",
                    Context.MODE_PRIVATE));
            fichero.write(cu.getString(cu.getColumnIndex("usuario")));
            fichero.close();
        }else{
            NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
            elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setContentTitle("Mensaje de Alerta")
                    .setContentText("Login incorrecto")
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
        }
        bd.close();
        cu.close();
    }

    public void registrarse(View view){
        //cambiar a la actividad de config
        Intent i = new Intent (this, Registrarse.class);
        startActivity(i);
    }
}
