package com.example.school.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.school.R;
import com.example.school.entities.Student;

import java.util.List;

public class StudentByAdapter extends ArrayAdapter<Student> {
    private Context context;
    private List<Student> studentList;

    public StudentByAdapter(Context context, List<Student> studentList) {
        super(context, 0, studentList);
        this.context = context;
        this.studentList = studentList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;

        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.student_list_item, parent, false);
        }

        Student student = studentList.get(position);

        TextView nameTextView = listItemView.findViewById(R.id.nameTextView);
        TextView emailTextView = listItemView.findViewById(R.id.emailTextView);
        TextView phoneTextView = listItemView.findViewById(R.id.phoneTextView);

        nameTextView.setText("Name: " + student.getName());
        emailTextView.setText("Email: " + student.getEmail());
        phoneTextView.setText("Phone: " + String.valueOf(student.getPhone()));

        return listItemView;
    }
}
