package com.example.school;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.school.adapter.FiliereAdapter;
import com.example.school.entities.Filiere;
import com.example.school.entities.Role;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class FiliereActivity extends AppCompatActivity {

    private ListView filiereListView;
    private FiliereAdapter filiereAdapter;
    private List<Filiere> filiereList;
    private RequestQueue requestQueue;
    private Button createFiliereButton;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filiere);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        filiereListView = findViewById(R.id.filiereListView);
        filiereAdapter = new FiliereAdapter(this, new ArrayList<>());
        filiereListView.setAdapter(filiereAdapter);
        createFiliereButton = findViewById(R.id.createFiliereButton);


        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.nav_filiere) {
                startActivity(new Intent(FiliereActivity.this, FiliereActivity.class));
            } else if (itemId == R.id.nav_role) {
                startActivity(new Intent(FiliereActivity.this, RoleActivity.class));
            } else if (itemId == R.id.nav_student) {
                startActivity(new Intent(FiliereActivity.this, StudentActivity.class));
            }else if (itemId == R.id.nav_student_by_filiere) {
                startActivity(new Intent(FiliereActivity.this, StudentsByFiliere.class));
            }else if (itemId == R.id.affecterrole) {
                startActivity(new Intent(FiliereActivity.this, AffecterRole.class));
            }
            drawerLayout.closeDrawers();
            return true;
        });
        createFiliereButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateFiliereDialog();
            }
        });

        // Initialize the request queue
        requestQueue = Volley.newRequestQueue(this);


        fetchDataFromBackend();
    }
    private void showCreateFiliereDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Create New Filiere");
        View dialogLayout = getLayoutInflater().inflate(R.layout.create_filiere_dialog, null);
        builder.setView(dialogLayout);

        final EditText codeInput = dialogLayout.findViewById(R.id.CodeInput);
        final EditText libelleInput = dialogLayout.findViewById(R.id.LibelleInput);


        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String filiereCode = codeInput.getText().toString();
                String filiereLibelle=libelleInput.getText().toString();
                createFiliere(filiereCode,filiereLibelle);
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

    private void createFiliere(String filiereCode,String filiereLibelle) {
        // Create a JSON object with the role name
        JSONObject jsonRole = new JSONObject();
        try {
            jsonRole.put("code", filiereCode);
            jsonRole.put("libelle", filiereLibelle);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        String createUrl = "http://10.0.2.2:8082" + "/api/v1/filieres"; // Complete URL

        JsonObjectRequest createRequest = new JsonObjectRequest(
                Request.Method.POST,
                createUrl,
                jsonRole,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Check the response for success
                        try {
                            if (response.has("message") && response.getString("message").equals("Filiere created successfully")) {
                                // Handle a successful role creation here
                                Toast.makeText(FiliereActivity.this, "Filiere created successfully", Toast.LENGTH_SHORT).show();
                                // You can refresh the role list if needed
                                fetchDataFromBackend();
                            } else {
                                // Handle a successful role creation here
                                Toast.makeText(FiliereActivity.this, "Filiere created successfully", Toast.LENGTH_SHORT).show();
                                // You can refresh the role list if needed
                                fetchDataFromBackend();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                            Toast.makeText(FiliereActivity.this, "Failed to parse server response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle creation errors here
                        error.printStackTrace();
                        Toast.makeText(FiliereActivity.this, "Create request failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(createRequest);
    }


    private void fetchDataFromBackend() {
        String baseUrl = "http://10.0.2.2:8082"; // Base URL
        String url = baseUrl + "/api/v1/filieres"; // Complete URL

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        filiereList = parseFilieresFromJson(response);
                        filiereAdapter.clear();
                        filiereAdapter.addAll(filiereList);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle error, e.g., show an error message
                        error.printStackTrace();
                        Toast.makeText(FiliereActivity.this, "Failed to fetch filieres", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(jsonArrayRequest);

        filiereListView.setOnItemClickListener((parent, view, position, id) -> {
            showActionDialog(filiereList.get(position));
        });
    }

    private void showActionDialog(final Filiere filiere) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an action")
                .setItems(new CharSequence[]{"Update", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            showUpdateDialog(filiere);
                        } else {
                            deleteFiliere(filiere);
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showUpdateDialog(final Filiere filiere) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Filiere Information");
        View dialogLayout = getLayoutInflater().inflate(R.layout.update_filier_dialog, null);
        builder.setView(dialogLayout);

        final EditText codeInput = dialogLayout.findViewById(R.id.codeInput);

        codeInput.setText(filiere.getCode());

        final EditText libelleInput = dialogLayout.findViewById(R.id.libelleInput);
        libelleInput.setText(filiere.getLibelle());


        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Get updated data from EditText fields
                String updateCode = codeInput.getText().toString();
                String updateLibelle=libelleInput.getText().toString();

                // Update the role object
                filiere.setCode(updateCode);
                filiere.setLibelle(updateLibelle);

                // Call the updateRole method to send updates to the server
                updateFiliere(filiere);

                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Dismiss the dialog
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void updateFiliere(Filiere filiere) {
        // Create a JSON object with the updated role data
        JSONObject jsonRole = new JSONObject();
        try {
            jsonRole.put("id", filiere.getId());
            jsonRole.put("code", filiere.getCode());
            jsonRole.put("libelle", filiere.getLibelle());
// Add other properties as needed
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String updateUrl = "http://10.0.2.2:8082" + "/api/v1/filieres/" + filiere.getId(); // Complete URL
        JsonObjectRequest updateRequest = new JsonObjectRequest(
                Request.Method.PUT,
                updateUrl,
                jsonRole,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Check the response for success
                        try {
                            if (response.has("message") && response.getString("message").equals("Filiere updated successfully")) {
                                // Handle a successful update here
                                Toast.makeText(FiliereActivity.this, "Filiere updated successfully", Toast.LENGTH_SHORT).show();
                                fetchDataFromBackend();
                                // You can refresh the role list if needed
                            } else {
                                // Handle other responses or errors
                                // Display an error message to the user
                                Toast.makeText(FiliereActivity.this, "Filiere updated successfully", Toast.LENGTH_SHORT).show();
                                fetchDataFromBackend();                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Handle JSON parsing error
                            Toast.makeText(FiliereActivity.this, "Failed to parse server response", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle update errors here
                        error.printStackTrace();
                        Toast.makeText(FiliereActivity.this, "Update request failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(updateRequest);
    }

    private void deleteFiliere(Filiere filiere) {
        long filiereId = filiere.getId();

        String deleteUrl = "http://10.0.2.2:8082" + "/api/v1/filieres/" + filiereId; // Complete URL

        StringRequest deleteRequest = new StringRequest(
                Request.Method.DELETE,
                deleteUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if ("Filiere deleted successfully".equals(response)) {
                            Toast.makeText(FiliereActivity.this, "Filiere deleted successfully", Toast.LENGTH_SHORT).show();
                            fetchDataFromBackend();
                        } else {
                            Toast.makeText(FiliereActivity.this, "Filiere deleted successfully", Toast.LENGTH_SHORT).show();
                            fetchDataFromBackend();                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle delete errors here
                        error.printStackTrace();
                        Toast.makeText(FiliereActivity.this, "Delete request failed", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(deleteRequest);
    }

    private List<Filiere> parseFilieresFromJson(JSONArray jsonArray) {
        List<Filiere> filieres = new ArrayList<>();

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject filiereObject = jsonArray.getJSONObject(i);
                long id = filiereObject.getLong("id");
                String code = filiereObject.getString("code");
                String libelle = filiereObject.getString("libelle");
                Filiere filiere = new Filiere(id, code,libelle);
                filieres.add(filiere);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return filieres;
    }
}
