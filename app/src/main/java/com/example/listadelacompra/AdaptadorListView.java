package com.example.listadelacompra;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

public class AdaptadorListView extends BaseAdapter {
    private Context contexto;
    private LayoutInflater inflater;
    private String[] prod;
    private int[] cant;
    public AdaptadorListView(Context pcontext, String[] pprod, int[] pcant) {
        contexto = pcontext;
        prod = pprod;
        cant=pcant;
        inflater= (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return prod.length;
    }

    @Override
    public Object getItem(int position) {
        return prod[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view=inflater.inflate(R.layout.lista,null);
        TextView producto= (TextView) view.findViewById(R.id.prodText);
        TextView cantidad=(TextView) view.findViewById(R.id.cantText);
        producto.setText(prod[position]);
        String k = String.valueOf(cant[position]);
        cantidad.setText(k);

        return view;
    }
}
