package com.example.school.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.school.R;
import com.example.school.entities.Student;

import java.util.List;

public class StudentAdapter extends BaseAdapter {
    private List<Student> students;
    private LayoutInflater inflater;
    private Context context;
    private List<Student> studentList;

    public StudentAdapter(Context context, int student_item, List<Student> students) {
        this.students = students;
        inflater = LayoutInflater.from(context);
    }
    public StudentAdapter(Context context, List<Student> studentList) {
        this.context = context;
        this.studentList = studentList;
    }

    @Override
    public int getCount() {
        return students.size();
    }

    @Override
    public Object getItem(int position) {
        return students.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.student_item, null);
            holder = new ViewHolder();
            holder.nameTextView = convertView.findViewById(R.id.nameTextView);
            holder.emailTextView = convertView.findViewById(R.id.emailTextView);
            holder.phoneTextView = convertView.findViewById(R.id.phoneTextView);
            holder.codeFiliereTextView = convertView.findViewById(R.id.codeFiliereTextView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Student student = students.get(position);
        holder.nameTextView.setText("Name: " + student.getName());
        holder.emailTextView.setText("Email: " + student.getEmail());
        holder.phoneTextView.setText("Phone: " + student.getPhone());
        String codeFiliere = student.getCodeFiliere();
        if (codeFiliere == null || codeFiliere.isEmpty()) {
            codeFiliere = "Pas du filiere";
        }
        holder.codeFiliereTextView.setText("Code de Filiere: " + codeFiliere);

        return convertView;
    }

    static class ViewHolder {
        TextView nameTextView, emailTextView, phoneTextView, codeFiliereTextView;
    }
}
