package com.example.school;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.school.R;
import com.example.school.adapter.StudentByAdapter;
import com.example.school.entities.Student;
import com.google.android.material.navigation.NavigationView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentsByFiliere extends AppCompatActivity {
    private Map<String, Long> filiereMap = new HashMap<>();
    private Spinner filiereSpinner;
    private Button fetchStudentsButton;
    private ListView studentsListView;
    private List<Student> studentList;
    private RequestQueue requestQueue;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_by_filiere);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        filiereSpinner = findViewById(R.id.filiereSpinner);
        fetchStudentsButton = findViewById(R.id.fetchStudentsButton);
        studentsListView = findViewById(R.id.studentsListView);
        studentList = new ArrayList<>();

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_filiere) {
                startActivity(new Intent(StudentsByFiliere.this, FiliereActivity.class));
            } else if (itemId == R.id.nav_role) {
                startActivity(new Intent(StudentsByFiliere.this, RoleActivity.class));
            } else if (itemId == R.id.nav_student) {
                startActivity(new Intent(StudentsByFiliere.this, StudentActivity.class));
            } else if (itemId == R.id.nav_student_by_filiere) {
                startActivity(new Intent(StudentsByFiliere.this, StudentsByFiliere.class));
            }else if (itemId == R.id.affecterrole) {
                startActivity(new Intent(StudentsByFiliere.this, AffecterRole.class));
            }
            drawerLayout.closeDrawers();
            return true;
        });

        fetchStudentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchStudentsByFiliere();
            }
        });

        initializeFiliereSpinner();
        requestQueue = Volley.newRequestQueue(this);
    }

    private void initializeFiliereSpinner() {
        String url = "http://10.0.2.2:8082/api/v1/filieres";
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<String> filiereList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject jsonObject = response.getJSONObject(i);
                                long id = jsonObject.getLong("id");
                                String filiereName = jsonObject.getString("code");
                                filiereList.add(filiereName);
                                filiereMap.put(filiereName, id);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(StudentsByFiliere.this,
                                android.R.layout.simple_spinner_item, filiereList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        filiereSpinner.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private void fetchStudentsByFiliere() {
        String selectedFiliereName = filiereSpinner.getSelectedItem().toString();
        Long selectedFiliereId = filiereMap.get(selectedFiliereName);
        String baseUrl = "http://10.0.2.2:8082/api/student/filiere/";
        String fullUrl = baseUrl + selectedFiliereId;
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, fullUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<Student> students = parseStudentsResponse(response);
                        displayStudents(students);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                });

        requestQueue.add(jsonArrayRequest);
    }

    private List<Student> parseStudentsResponse(JSONArray response) {
        List<Student> students = new ArrayList<>();
        try {
            for (int i = 0; i < response.length(); i++) {
                JSONObject jsonObject = response.getJSONObject(i);
                long id = jsonObject.getLong("id");
                String name = jsonObject.getString("name");
                String email = jsonObject.getString("email");
                int phone = jsonObject.getInt("phone");
                Student student = new Student(name, email, phone);
                students.add(student);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return students;
    }

    private void displayStudents(List<Student> students) {
        if (students != null) {
            StudentByAdapter studentAdapter = new StudentByAdapter(this, students);
            ListView studentsListView = findViewById(R.id.studentsListView);
            studentsListView.setAdapter(studentAdapter);
        }
    }
}
