package com.example.listadelacompra;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.OutputStreamWriter;


public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
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
        //Cursor cu = bd.query("Usuarios",campos,"'usuario'=? AND 'contraseña'=?",argumentos,null,null,null);
        Cursor cu = bd.rawQuery("SELECT usuario, contraseña FROM Usuarios AS u WHERE u.usuario='"+user+"' AND u.contraseña='"+pass+"'",null);

        if(cu.moveToNext()){
            Intent i = new Intent (this, MainActivity.class);
            startActivity(i);
        }else{
            //ERROR
        }
        bd.close();
        OutputStreamWriter fichero = new OutputStreamWriter(openFileOutput("nombreUsuario.txt",
                Context.MODE_PRIVATE));
        fichero.write(cu.getString(cu.getColumnIndex("usuario")));
        fichero.close();
        cu.close();
    }

    public void registrarse(View view){
        //cambiar a la actividad de config
        Intent i = new Intent (this, Registrarse.class);
        startActivity(i);
    }
}
