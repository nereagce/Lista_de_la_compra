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
        final String[] fecha = new String[1];

        CalendarView date= (CalendarView) elaspecto.findViewById(R.id.calendarView);
        date.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                                            int dayOfMonth) {
                // TODO Auto-generated method stub

                fecha[0] = dayOfMonth+"/"+ (month+1) +"/"+year;

            }
        });

        builder.setPositiveButton("Añadir", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText nombre= (EditText) elaspecto.findViewById(R.id.prodAñadTxt);
                EditText cant= (EditText) elaspecto.findViewById(R.id.cantAñadTxt);
                try {
                    miListener.alpulsarAñadir(nombre.getText().toString(),cant.getText().toString(), fecha[0]);
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
