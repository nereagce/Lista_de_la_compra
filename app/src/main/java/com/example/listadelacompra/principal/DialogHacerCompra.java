package com.example.listadelacompra.principal;

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

import com.example.listadelacompra.R;

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
        builder.setTitle(getString(R.string.quesuper));
        //builder.setMessage("Introduzca el producto y la cantidad");

        //Personalización del dialogo
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View elaspecto= inflater.inflate(R.layout.dialogo_hacer_compra,null);

        miListener =(ListenerdelDialogo) getActivity();

        builder.setPositiveButton(getString(R.string.abrir), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {//Cuando el usuario pulse el botón 'Abrir'
                String url="";
                //Recoger cuál ha sido la elección del usuario
                RadioGroup rg= (RadioGroup) elaspecto.findViewById(R.id.radioGroup);
                int id = rg.getCheckedRadioButtonId();
                RadioButton rb= (RadioButton) elaspecto.findViewById(id);
                String spr = rb.getText().toString();
                //Establecer la url en base a la elección
                if(spr.equals("Mercadona")){
                    url = "https://tienda.mercadona.es/";
                } else if(spr.equals("Carrefour")){
                    url = "https://www.carrefour.es/";
                } else if(spr.equals("BM")){
                    url = "https://www.online.bmsupermercados.es/bm/#!Home";
                } else if(spr.equals("Eroski")){
                    url = "https://supermercado.eroski.es/";
                }
                //Llamar al método que gestiona la respuesta con la url correspondiente
                miListener.alpulsarAbrir(url);
            }
        });
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.setView(elaspecto);

        return builder.create();
    }
}
