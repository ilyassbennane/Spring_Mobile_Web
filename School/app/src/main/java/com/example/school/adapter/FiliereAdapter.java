package com.example.school.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.school.R;
import com.example.school.entities.Filiere;
import com.example.school.entities.Role;

import java.util.List;

public class FiliereAdapter extends ArrayAdapter<Filiere> {

    public FiliereAdapter(Context context, List<Filiere> filieres) {
        super(context, 0, filieres);
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        Filiere filiere = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.filiere_item, parent, false);
        }

        TextView filiereCodeTextView = convertView.findViewById(R.id.codeTextView);
        filiereCodeTextView.setText("Code :" +filiere.getCode());

        TextView filierelibelleTextView = convertView.findViewById(R.id.libelleTextView);
        filierelibelleTextView.setText("Libelle :" +filiere.getLibelle());


        return convertView;
    }

}
