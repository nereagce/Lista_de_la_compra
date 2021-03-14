package com.example.listadelacompra;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class Registrarse extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registrarse);
    }

    public void irLogin(View view){
        //cambiar a la actividad de config
        Intent i = new Intent (this, Login.class);
        startActivity(i);
    }

    public void registrar(View view){
        //registrar la cuenta y llevar a la principal
        BaseDeDatos GestorDB = new BaseDeDatos (this, "miBD", null, 1);
        SQLiteDatabase bd = GestorDB.getWritableDatabase();
        //TENDRIA QUE COMPROBAR SI EXISTE
        EditText nomTxt = (EditText) findViewById(R.id.nombreTxt);
        String nom = nomTxt.getText().toString();
        EditText userTxt = (EditText) findViewById(R.id.usernameTxt);
        String user = userTxt.getText().toString();
        EditText emailTxt = (EditText) findViewById(R.id.emailTxt);
        String email = emailTxt.getText().toString();
        EditText passTxt = (EditText) findViewById(R.id.passwordTxt);
        String pass = passTxt.getText().toString();
        ContentValues nuevo = new ContentValues();
        nuevo.put("usuario", user);
        nuevo.put("nombre", nom);
        nuevo.put("email", email);
        nuevo.put("contraseña", pass);
        bd.insert("Usuarios", null, nuevo);

        bd.close();

        Intent i = new Intent (this, Login.class);
        startActivity(i);
    }
}
