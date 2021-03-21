package com.example.listadelacompra;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DialogoAñadirProducto extends DialogFragment {
    ListenerdelDialogo miListener;

    public interface ListenerdelDialogo { //Implementada en MisProductos
        void alpulsarAñadir(String nom, String cant, String date) throws IOException;
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        miListener =(ListenerdelDialogo) getActivity();

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.añadirprod));
        //builder.setMessage("Introduzca el producto y la cantidad");

        //Le damos el aspecto personalizado
        LayoutInflater inflater=getActivity().getLayoutInflater();
        View elaspecto= inflater.inflate(R.layout.dialogo_anadir_producto,null);

        miListener =(ListenerdelDialogo) getActivity();
        final String[] fecha = new String[1];

        //Para elegir la fecha de caducidad utilizamos un CalendarView que nos guardará la fecha seleccionada cada vez que se cambie utilizando un listener
        CalendarView date= (CalendarView) elaspecto.findViewById(R.id.calendarView);
        date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {

                fecha[0] = dayOfMonth+"/"+ (month+1) +"/"+year;

            }
        });

        builder.setPositiveButton(getString(R.string.añadir), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) { //Al pulsar en el botón
                //Recogemos los datos introducidos
                EditText nombre= (EditText) elaspecto.findViewById(R.id.prodAñadTxt);
                EditText cant= (EditText) elaspecto.findViewById(R.id.cantAñadTxt);
                try {
                    //Llamamos al método que gestiona la respuesta con los datos correspondientes
                    miListener.alpulsarAñadir(nombre.getText().toString(),cant.getText().toString(), fecha[0]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        builder.setNegativeButton(getString(R.string.cancelar), null);
        builder.setView(elaspecto);


        return builder.create();
    }
}
