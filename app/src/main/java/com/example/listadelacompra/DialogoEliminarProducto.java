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

public class DialogoEliminarProducto extends DialogFragment {
    ListenerdelDialogo miListener;

    public interface ListenerdelDialogo { //Implementada en MisProductos
        void alpulsarEliminar(String nom, String cant);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        miListener =(ListenerdelDialogo) getActivity();

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.eliminarprod));
        //builder.setMessage("Introduzca el producto y la cantidad");

        //Establecer la personalización
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View elaspecto= inflater.inflate(R.layout.dialogo_eliminar_producto,null);

        miListener =(ListenerdelDialogo) getActivity();

        builder.setPositiveButton(getString(R.string.eliminar), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //Cuando se pulse el botón eliminar se recogerán los datos y se llamará al método que gestiona la respuesta
                EditText nombre= (EditText) elaspecto.findViewById(R.id.prodElimTxt);
                EditText cant= (EditText) elaspecto.findViewById(R.id.cantElimTxt);
                miListener.alpulsarEliminar(nombre.getText().toString(),cant.getText().toString());
            }
        });
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.setView(elaspecto);

        return builder.create();
    }
}
