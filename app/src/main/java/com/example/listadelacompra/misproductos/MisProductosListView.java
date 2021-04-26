package com.example.listadelacompra.misproductos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.listadelacompra.R;

import java.util.ArrayList;
import java.util.Date;

public class MisProductosListView extends BaseAdapter {
    private Context contexto;
    private LayoutInflater inflater;
    private ArrayList<String> prod;
    private ArrayList<Integer> cant;
    private ArrayList<String> cad;
    public MisProductosListView(Context pcontext, ArrayList<String> pprod, ArrayList<Integer> pcant, ArrayList<String> pcad) {
        contexto = pcontext;
        prod = pprod;
        cant=pcant;
        cad=pcad;
        inflater= (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return prod.size();
    }

    @Override
    public Object getItem(int position) {
        return prod.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view=inflater.inflate(R.layout.mis_productos_lista,null);
        TextView producto= (TextView) view.findViewById(R.id.prodText);
        TextView cantidad=(TextView) view.findViewById(R.id.cantText);
        TextView caducidad=(TextView) view.findViewById(R.id.cadText);
        producto.setText(prod.get(position));
        String k = String.valueOf(cant.get(position));
        cantidad.setText(k);
        String c = String.valueOf(cad.get(position));
        caducidad.setText(c);

        return view;
    }
}
