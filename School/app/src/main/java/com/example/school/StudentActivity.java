package com.example.school;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.school.adapter.StudentAdapter;
import com.example.school.entities.Filiere;
import com.example.school.entities.Student;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentActivity extends AppCompatActivity {

    private ListView studentListView;
    private StudentAdapter studentAdapter;
    private List<Student> studentList = new ArrayList<>();
    private RequestQueue requestQueue;
    private String backendUrl = "http://10.0.2.2:8082/api/student";
    private Button createStudentButton;
    private Spinner filiereSpinner;
    List<Filiere> FiliereList = new ArrayList<>();
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        studentListView = findViewById(R.id.studentListView);
        studentAdapter = new StudentAdapter(this, R.layout.student_item, studentList);
        studentListView.setAdapter(studentAdapter);
        createStudentButton = findViewById(R.id.createStudentButton);

        requestQueue = Volley.newRequestQueue(this);
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_filiere) {
                startActivity(new Intent(StudentActivity.this, FiliereActivity.class));
            } else if (itemId == R.id.nav_role) {
                startActivity(new Intent(StudentActivity.this, RoleActivity.class));
            } else if (itemId == R.id.nav_student) {
                startActivity(new Intent(StudentActivity.this, StudentActivity.class));
            } else if (itemId == R.id.nav_student_by_filiere) {
                startActivity(new Intent(StudentActivity.this, StudentsByFiliere.class));
            }else if (itemId == R.id.affecterrole) {
                startActivity(new Intent(StudentActivity.this, AffecterRole.class));
            }
            drawerLayout.closeDrawers();
            return true;
        });

        createStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("StudentActivity", "Create Student Button Clicked");
                showCreateStudentDialog();
            }
        });

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET, backendUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            studentList.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject student = response.getJSONObject(i);
                                int id = student.getInt("id");
                                String name = student.getString("name");
                                String email = student.getString("email");
                                int phone = student.getInt("phone");
                                JSONObject filiere = student.optJSONObject("filiere");
                                String codeFiliere = (filiere != null) ? filiere.getString("code") : "Pas du filiere";

                                Student newStudent = new Student(id, name, email, phone, codeFiliere);
                                studentList.add(newStudent);
                            }

                            studentAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);

        studentListView.setOnItemClickListener((parent, view, position, id) -> {
            showActionDialog(studentList.get(position));
        });
    }

    private void fetchFilieres() {
        JsonArrayRequest filieresRequest = new JsonArrayRequest(
                Request.Method.GET, "http://10.0.2.2:8082/api/v1/filieres", null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<String> filieres = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject filiere = response.getJSONObject(i);
                                Filiere filieress = new Filiere();
                                filieress.setId(filiere.getLong("id"));
                                filieress.setCode(filiere.getString("code"));
                                filieress.setLibelle(filiere.getString("libelle"));
                                FiliereList.add(filieress);
                                String code = filiere.getString("code");
                                filieres.add(code);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ArrayAdapter<String> filiereAdapter = new ArrayAdapter<>(StudentActivity.this,
                                android.R.layout.simple_spinner_item, filieres);
                        filiereAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        filiereSpinner.setAdapter(filiereAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(filieresRequest);
    }

    private void showCreateStudentDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Student");
        View dialogLayout = getLayoutInflater().inflate(R.layout.create_student_dialog, null);
        builder.setView(dialogLayout);

        final EditText nameInput = dialogLayout.findViewById(R.id.nameInput);
        final EditText phoneInput = dialogLayout.findViewById(R.id.phoneInput);
        final EditText emailInput = dialogLayout.findViewById(R.id.emailInput);
        final EditText usernameInput = dialogLayout.findViewById(R.id.usernameInput);
        final EditText passwordInput = dialogLayout.findViewById(R.id.passwordInput);

        filiereSpinner = dialogLayout.findViewById(R.id.filiereSpinner);
        fetchFilieres();

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameInput.getText().toString();
                int phone = Integer.parseInt(phoneInput.getText().toString());
                String email = emailInput.getText().toString();
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();

                String selectedFiliereCode = filiereSpinner.getSelectedItem().toString();
                Filiere selectedFiliere = findFiliereByCode(selectedFiliereCode);

                if (selectedFiliere != null) {
                    createStudent(name, phone, email, username, password, selectedFiliere);
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void createStudent(String name, int phone, String email, String username, String password, Filiere selectedFiliere) {
        JSONObject jsonStudent = new JSONObject();
        try {
            jsonStudent.put("name", name);
            jsonStudent.put("phone", phone);
            jsonStudent.put("email", email);
            jsonStudent.put("username", username);
            jsonStudent.put("password", password);

            JSONObject jsonFiliere = new JSONObject();
            jsonFiliere.put("id", selectedFiliere.getId());
            jsonFiliere.put("code", selectedFiliere.getCode());
            jsonFiliere.put("libelle", selectedFiliere.getLibelle());

            jsonStudent.put("filiere", jsonFiliere);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String createUrl = "http://10.0.2.2:8082/api/student";

        JsonObjectRequest createRequest = new JsonObjectRequest(
                Request.Method.POST,
                createUrl,
                jsonStudent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(StudentActivity.this, "Student created successfully", Toast.LENGTH_SHORT).show();
                        Student newStudent = new Student();
                        newStudent.setName(name);
                        newStudent.setPhone(phone);
                        newStudent.setEmail(email);
                        newStudent.setUsername(username);
                        newStudent.setPassword(password);
                        newStudent.setCodeFiliere(selectedFiliere.getCode());
                        studentList.add(newStudent);
                        studentAdapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        requestQueue.add(createRequest);
    }

    private Filiere findFiliereByCode(String code) {
        for (Filiere filiere : FiliereList) {
            if (filiere.getCode().equals(code)) {
                return filiere;
            }
        }
        return null;
    }

    private void showActionDialog(final Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an action")
                .setItems(new CharSequence[]{"Update", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            showUpdateDialog(student);
                        } else {
                            deleteStudent(student);
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUpdateDialog(final Student student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Student Information");
        View dialogLayout = getLayoutInflater().inflate(R.layout.update_student_dialog, null);
        builder.setView(dialogLayout);

        final EditText nameInput = dialogLayout.findViewById(R.id.nameInput);
        final EditText emailInput = dialogLayout.findViewById(R.id.emailInput);
        final EditText phoneInput = dialogLayout.findViewById(R.id.phoneInput);

        nameInput.setText(student.getName());
        emailInput.setText(student.getEmail());
        phoneInput.setText(String.valueOf(student.getPhone()));

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedName = nameInput.getText().toString();
                String updatedEmail = emailInput.getText().toString();
                int updatedPhone = Integer.parseInt(phoneInput.getText().toString());

                student.setName(updatedName);
                student.setEmail(updatedEmail);
                student.setPhone(updatedPhone);

                updateStudent(student);

                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateStudent(Student student) {
        int studentId = student.getId(); // Get the student's ID

        JSONObject jsonStudent = new JSONObject();
        try {
            jsonStudent.put("id", studentId);
            jsonStudent.put("name", student.getName());
            jsonStudent.put("email", student.getEmail());
            jsonStudent.put("phone", student.getPhone());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String updateUrl = backendUrl + "/" + student.getId();

        JsonObjectRequest updateRequest = new JsonObjectRequest(
                Request.Method.PUT,
                updateUrl,
                jsonStudent,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("message") && response.getString("message").equals("Student updated successfully")) {
                                Toast.makeText(StudentActivity.this, "Student updated successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(StudentActivity.this, "Failed to update student", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(StudentActivity.this, "Failed to parse server response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(StudentActivity.this, "Update request failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(updateRequest);
    }

    private void deleteStudent(Student student) {
        int studentId = student.getId();

        String deleteUrl = backendUrl + "/" + studentId;

        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if ("Student deleted successfully".equals(response)) {
                            Toast.makeText(StudentActivity.this, "Student deleted successfully", Toast.LENGTH_SHORT).show();
                            studentList.remove(student);
                            studentAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(StudentActivity.this, "Failed to delete student", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(StudentActivity.this, "Delete request failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(deleteRequest);
    }
}
