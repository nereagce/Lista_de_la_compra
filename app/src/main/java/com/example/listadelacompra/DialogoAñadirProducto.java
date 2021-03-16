package com.example.listadelacompra;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.FileNotFoundException;
import java.io.IOException;

public class DialogoAñadirProducto extends DialogFragment {
    ListenerdelDialogo miListener;

    public interface ListenerdelDialogo {
        void alpulsarAñadir(String nom, String cant, String date) throws IOException;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        miListener =(ListenerdelDialogo) getActivity();

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("Añadir producto");
        //builder.setMessage("Introduzca el producto y la cantidad");

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View elaspecto= inflater.inflate(R.layout.dialogo_anadir_producto,null);

        miListener =(ListenerdelDialogo) getActivity();

        builder.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText nombre= (EditText) elaspecto.findViewById(R.id.prodAñadTxt);
                EditText cant= (EditText) elaspecto.findViewById(R.id.cantAñadTxt);
                EditText date= (EditText) elaspecto.findViewById(R.id.dateTxt);
                try {
                    miListener.alpulsarAñadir(nombre.getText().toString(),cant.getText().toString(),date.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.setView(elaspecto);


        return builder.create();
    }
}
