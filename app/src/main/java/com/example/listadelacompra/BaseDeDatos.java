package com.example.listadelacompra;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class BaseDeDatos extends SQLiteOpenHelper {
    public BaseDeDatos(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Usuarios ('usuario' VARCHAR(50) PRIMARY KEY NOT NULL,'nombre' VARCHAR(200), 'email' VARCHAR(100), 'contraseña' VARCHAR(100))");
        db.execSQL("CREATE TABLE Productos ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,'nombre' VARCHAR(100), 'cantMin' INTEGER, 'userID' VARCHAR(50) REFERENCES Usuarios(usuario) ON UPDATE CASCADE)");
        db.execSQL("CREATE TABLE Cantidades ('id' INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,'cant' INTEGER, 'caducidad' DATE, 'productoID' INTEGER REFERENCES Productos(id) ON UPDATE CASCADE)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}