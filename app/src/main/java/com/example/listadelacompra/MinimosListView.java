package com.example.listadelacompra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;

public class MinimosListView extends BaseAdapter {
    private Context contexto;
    private LayoutInflater inflater;
    private ArrayList<String> prod;
    private ArrayList<Integer> min;
    public MinimosListView(Context pcontext, ArrayList<String> pprod, ArrayList<Integer> pmin) {
        contexto = pcontext;
        prod = pprod;
        min=pmin;
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
        view = inflater.inflate(R.layout.minimos_lista,null);
        TextView producto = (TextView) view.findViewById(R.id.nomprodText);
        TextView cantidad =(TextView) view.findViewById(R.id.cantminText);
        producto.setText(prod.get(position));
        String k = String.valueOf(min.get(position));
        cantidad.setText(k);

        return view;
    }
}
