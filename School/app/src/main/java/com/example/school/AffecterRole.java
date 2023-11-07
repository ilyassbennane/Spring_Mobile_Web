package com.example.school;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class AffecterRole extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Spinner studentSpinner;
    private Spinner roleSpinner;
    private Button affecterButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affecter_role);

        studentSpinner = findViewById(R.id.studentSpinner);
        roleSpinner = findViewById(R.id.roleSpinner);
        affecterButton = findViewById(R.id.affecteButton);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_filiere) {
                startActivity(new Intent(AffecterRole.this, FiliereActivity.class));
            } else if (itemId == R.id.nav_role) {
                startActivity(new Intent(AffecterRole.this, RoleActivity.class));
            } else if (itemId == R.id.nav_student) {
                startActivity(new Intent(AffecterRole.this, StudentActivity.class));
            }else if (itemId == R.id.nav_student_by_filiere) {
                startActivity(new Intent(AffecterRole.this, StudentsByFiliere.class));
            }else if (itemId == R.id.affecterrole) {
                startActivity(new Intent(AffecterRole.this, AffecterRole.class));
            }

            drawerLayout.closeDrawers();
            return true;
        });




        affecterButton.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View view) {
                                                  affectRoleToStudent();
                                              }
                                          });

                fetchStudentsFromDatabase();
        fetchRolesFromDatabase();
    }

    private void fetchStudentsFromDatabase() {
        String studentsUrl = "http://10.0.2.2:8082/api/student"; // Replace with your API endpoint
        JsonArrayRequest studentsRequest = new JsonArrayRequest(Request.Method.GET, studentsUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<String> studentList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject student = response.getJSONObject(i);
                                String studentName = student.getString("name");
                                studentList.add(studentName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ArrayAdapter<String> studentAdapter = new ArrayAdapter<>(AffecterRole.this, android.R.layout.simple_spinner_item, studentList);
                        studentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        studentSpinner.setAdapter(studentAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("AffecterRole", "Error fetching students: " + error.getMessage());
                    }
                }
        );

        Volley.newRequestQueue(this).add(studentsRequest);
    }

    private void fetchRolesFromDatabase() {
        String rolesUrl = "http://10.0.2.2:8082/api/roles";
        JsonArrayRequest rolesRequest = new JsonArrayRequest(Request.Method.GET, rolesUrl, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        ArrayList<String> roleList = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject role = response.getJSONObject(i);
                                String roleName = role.getString("name");
                                roleList.add(roleName);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        ArrayAdapter<String> roleAdapter = new ArrayAdapter<>(AffecterRole.this, android.R.layout.simple_spinner_item, roleList);
                        roleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        roleSpinner.setAdapter(roleAdapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("AffecterRole", "Error fetching roles: " + error.getMessage());
                    }
                }
        );

        Volley.newRequestQueue(this).add(rolesRequest);
    }

    private void affectRoleToStudent() {
        String selectedStudent = studentSpinner.getSelectedItem().toString();
        String selectedRole = roleSpinner.getSelectedItem().toString();

        String message = "Role '" + selectedRole + "' has been assigned to '" + selectedStudent + "'.";
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
