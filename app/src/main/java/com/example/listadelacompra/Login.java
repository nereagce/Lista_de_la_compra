package com.example.listadelacompra;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
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
import java.util.Locale;


public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        //CARGAR PREFERENCIAS
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor= sharedPreferences.edit();

        //Si las preferencias indican que usemos el modo noche, activarlo.
        Boolean modonoche = sharedPreferences.getBoolean("switch", false);
        if(modonoche){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            editor.putBoolean("switch",true);
            editor.apply();
        } else{ // Si no está activado
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            editor.putBoolean("switch",false);
            editor.apply();
        }
        String idioma = sharedPreferences.getString("list_preference_1", "ES");
        Locale nuevaloc = new Locale(idioma);
        Locale.setDefault(nuevaloc);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nuevaloc);
        configuration.setLayoutDirection(nuevaloc);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

    }

    public void logIn(View view) throws IOException {//Comprobar que existe la cuenta. Si existe llevar a main, si no existe notificar que el login es incorrecto.
        //Coger los datos que ha introducido el usuario
        EditText userTxt = (EditText) findViewById(R.id.userTxt);
        String user = userTxt.getText().toString();
        EditText passTxt = (EditText) findViewById(R.id.passTxt);
        String pass = passTxt.getText().toString();

        //Cargar la base de datos para poder leerla
        BaseDeDatos GestorDB = new BaseDeDatos (this, "miBD", null, 1);
        SQLiteDatabase bd = GestorDB.getReadableDatabase();
        Cursor cu = bd.rawQuery("SELECT usuario, contraseña FROM Usuarios AS u WHERE u.usuario='"+user+"' AND u.contraseña='"+pass+"'",null);

        if(cu.moveToNext()){ //Si hay una línea en el cursor significa que el login es correcto
            //Ir la página principal
            Intent i = new Intent (this, MainActivity.class);
            startActivity(i);
            //Guardar el nombre de usuario en un fichero para utilizarlo en las consultas más adelante
            OutputStreamWriter fichero = new OutputStreamWriter(openFileOutput("nombreUsuario.txt",
                    Context.MODE_PRIVATE));
            fichero.write(cu.getString(cu.getColumnIndex("usuario")));
            fichero.close();
        }else{
            //Mandar una notificación indicando que el login es incorrecto
            NotificationManager elManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
            elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setContentTitle(getString(R.string.alerta))
                    .setContentText(getString(R.string.loginmal))
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
        bd.close();
        cu.close();
    }

    public void registrarse(View view){
        //Ir a la actividad de registro
        Intent i = new Intent (this, Registrarse.class);
        startActivity(i);
    }
}
