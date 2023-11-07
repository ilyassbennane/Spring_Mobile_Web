package com.example.school;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
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
import com.example.school.adapter.RoleAdapter;
import com.example.school.entities.Role;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RoleActivity extends AppCompatActivity {

    private ListView roleListView;
    private RoleAdapter roleAdapter;
    private List<Role> roleList;
    private RequestQueue requestQueue;
    private Button createRoleButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        roleListView = findViewById(R.id.roleListView);
        roleAdapter = new RoleAdapter(this, new ArrayList<>());
        roleListView.setAdapter(roleAdapter);
        createRoleButton = findViewById(R.id.createRoleButton);

        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_filiere) {
                startActivity(new Intent(RoleActivity.this, FiliereActivity.class));
            } else if (itemId == R.id.nav_role) {
                startActivity(new Intent(RoleActivity.this, RoleActivity.class));
            } else if (itemId == R.id.nav_student) {
                startActivity(new Intent(RoleActivity.this, StudentActivity.class));
            } else if (itemId == R.id.nav_student_by_filiere) {
                startActivity(new Intent(RoleActivity.this, StudentsByFiliere.class));
            }else if (itemId == R.id.affecterrole) {
                startActivity(new Intent(RoleActivity.this, AffecterRole.class));
            }
            drawerLayout.closeDrawers();
            return true;
        });
        createRoleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateRoleDialog();
            }
        });

        requestQueue = Volley.newRequestQueue(this);

        fetchDataFromBackend();
    }

    private void showCreateRoleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Role");
        View dialogLayout = getLayoutInflater().inflate(R.layout.create_role_dialog, null);
        builder.setView(dialogLayout);

        final EditText nameInput = dialogLayout.findViewById(R.id.nameInput);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String roleName = nameInput.getText().toString();
                createRole(roleName);
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

    private void createRole(String roleName) {
        JSONObject jsonRole = new JSONObject();
        try {
            jsonRole.put("name", roleName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String createUrl = "http://10.0.2.2:8082" + "/api/roles";

        JsonObjectRequest createRequest = new JsonObjectRequest(
                Request.Method.POST,
                createUrl,
                jsonRole,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("message") && response.getString("message").equals("Role created successfully")) {
                                Toast.makeText(RoleActivity.this, "Role created successfully", Toast.LENGTH_SHORT).show();
                                fetchDataFromBackend();
                            } else {
                                Toast.makeText(RoleActivity.this, "Role created successfully", Toast.LENGTH_SHORT).show();
                                fetchDataFromBackend();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RoleActivity.this, "Failed to parse server response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(RoleActivity.this, "Create request failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(createRequest);
    }

    private void fetchDataFromBackend() {
        String baseUrl = "http://10.0.2.2:8082";
        String url = baseUrl + "/api/roles";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        roleList = parseRolesFromJson(response);
                        roleAdapter.clear();
                        roleAdapter.addAll(roleList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(RoleActivity.this, "Failed to fetch roles", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);

        roleListView.setOnItemClickListener((parent, view, position, id) -> {
            showActionDialog(roleList.get(position));
        });
    }

    private void showActionDialog(final Role role) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an action")
                .setItems(new CharSequence[]{"Update", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            showUpdateDialog(role);
                        } else {
                            deleteRole(role);
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUpdateDialog(final Role role) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Role Information");
        View dialogLayout = getLayoutInflater().inflate(R.layout.update_role_dialog, null);
        builder.setView(dialogLayout);

        final EditText nameInput = dialogLayout.findViewById(R.id.nameInput);

        nameInput.setText(role.getName());

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String updatedName = nameInput.getText().toString();
                role.setName(updatedName);
                updateRole(role);
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

    private void updateRole(Role role) {
        JSONObject jsonRole = new JSONObject();
        try {
            jsonRole.put("id", role.getId());
            jsonRole.put("name", role.getName());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String updateUrl = "http://10.0.2.2:8082" + "/api/roles/" + role.getId();
        JsonObjectRequest updateRequest = new JsonObjectRequest(
                Request.Method.PUT,
                updateUrl,
                jsonRole,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if (response.has("message") && response.getString("message").equals("Role updated successfully")) {
                                Toast.makeText(RoleActivity.this, "Role updated successfully", Toast.LENGTH_SHORT).show();
                                fetchDataFromBackend();
                            } else {
                                Toast.makeText(RoleActivity.this, "Failed to update Role", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RoleActivity.this, "Failed to parse server response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(RoleActivity.this, "Update request failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(updateRequest);
    }

    private void deleteRole(Role role) {
        long roleId = role.getId();

        String deleteUrl = "http://10.0.2.2:8082" + "/api/roles/" + roleId;

        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if ("Role deleted successfully".equals(response)) {
                            Toast.makeText(RoleActivity.this, "Role deleted successfully", Toast.LENGTH_SHORT).show();
                            fetchDataFromBackend();
                        } else {
                            Toast.makeText(RoleActivity.this, "Failed to delete role", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                        Toast.makeText(RoleActivity.this, "Delete request failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(deleteRequest);
    }

    private List<Role> parseRolesFromJson(JSONArray jsonArray) {
        List<Role> roles = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject roleObject = jsonArray.getJSONObject(i);
                long id = roleObject.getLong("id");
                String name = roleObject.getString("name");
                Role role = new Role(id, name);
                roles.add(role);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return roles;
    }
}
