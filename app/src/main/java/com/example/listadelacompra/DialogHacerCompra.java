package com.example.listadelacompra;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogHacerCompra extends DialogFragment {
    ListenerdelDialogo miListener;

    public interface ListenerdelDialogo {
        void alpulsarAbrir(String url);
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        miListener =(ListenerdelDialogo) getActivity();

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle("¿Qué supermercado?");
        //builder.setMessage("Introduzca el producto y la cantidad");

        LayoutInflater inflater=getActivity().getLayoutInflater();
        View elaspecto= inflater.inflate(R.layout.dialogo_hacer_compra,null);

        miListener =(ListenerdelDialogo) getActivity();

        builder.setPositiveButton("Abrir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String url="";
                RadioGroup rg= (RadioGroup) elaspecto.findViewById(R.id.radioGroup);
                int id = rg.getCheckedRadioButtonId();
                RadioButton rb= (RadioButton) elaspecto.findViewById(id);
                String spr = rb.getText().toString();
                if(spr.equals("Mercadona")){
                    url = "";
                } else if(spr.equals("Carrefour")){
                    url = "";
                } else if(spr.equals("BM")){
                    url = "";
                } else if(spr.equals("Eroski")){
                    url = "";
                }
                miListener.alpulsarAbrir(url);
            }
        });
        builder.setNegativeButton("Cancelar", null);
        builder.setView(elaspecto);

        return builder.create();
    }
}
