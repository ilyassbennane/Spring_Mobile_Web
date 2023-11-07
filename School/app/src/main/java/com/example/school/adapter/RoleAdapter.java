package com.example.school.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.school.R;
import com.example.school.entities.Role;

import java.util.List;

public class RoleAdapter extends ArrayAdapter<Role> {

    public RoleAdapter(Context context, List<Role> roles) {
        super(context, 0, roles);
    }

    @Override

    public View getView(int position, View convertView, ViewGroup parent) {
        Role role = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.role_item, parent, false);
        }

        TextView roleNameTextView = convertView.findViewById(R.id.roleNameTextView);
        roleNameTextView.setText("Nom :"+role.getName());

        return convertView;
    }

}
