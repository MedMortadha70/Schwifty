package com.thenorthside.beekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thenorthside.beekeeper.Helpers.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private ImageView backToMain;
    private TextView privacyPolicy,reportTv,changeEmailTv,changePassTv,logoutBtn;
    private Button deleteAccBtn;
    private SessionManager sessionManager;

    private String email,
            DELETE_URL = "https://beekeepertn.000webhostapp.com/php/wp_deleteAccount.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        init();
        clickListener();

        sessionManager = new SessionManager(this);

        HashMap<String, String> user = sessionManager.fetchUserDetail();

        email = user.get(sessionManager.EMAIL);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void init(){

        backToMain = findViewById(R.id.backTo);
        privacyPolicy = findViewById(R.id.privacyTv);
        reportTv = findViewById(R.id.reportTv);
        changeEmailTv =findViewById(R.id.changeEmailTv);
        changePassTv = findViewById(R.id.changePassTv);
        deleteAccBtn = findViewById(R.id.deleteAcc);
        logoutBtn = findViewById(R.id.logoutTv);

    }


    private void clickListener() {

        backToMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SettingsActivity.this, MainActivity.class));
                finish();
            }
        });

        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });


        reportTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        changeEmailTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        changePassTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionManager.logOut();
            }
        });

        deleteAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Delete Account");
                builder.setMessage("Are you sure you want to delete your account? This action cannot be undone.");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform delete account action
                        deleteAccount();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.show();


            }
        });

    }

    private void deleteAccount() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DELETE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    String responseDb = jsonObject.getString("success");

                    if (responseDb.equals("0")){

                        Toast.makeText(SettingsActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();

                    }

                    else if (responseDb.equals("1")){

                        Toast.makeText(SettingsActivity.this, "Confirmation Email Sent", Toast.LENGTH_SHORT).show();

                        sessionManager.logOutAccountDel();

                    }else {

                        Toast.makeText(SettingsActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SettingsActivity.this, "Error " +e.toString(), Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(SettingsActivity.this, "Error " +error.toString(), Toast.LENGTH_SHORT).show();

            }
        }){

            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String>params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(SettingsActivity.this, MainActivity.class));
    }

}