package com.thenorthside.beekeeper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thenorthside.beekeeper.Helpers.Internet;
import com.thenorthside.beekeeper.Helpers.RandomString;
import com.thenorthside.beekeeper.Helpers.SaveUserData;
import com.thenorthside.beekeeper.Helpers.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private TextView signupTv,forgotTV;
    private Button loginBtn;
    private EditText userEmail,userPassword;
    private SessionManager sessionManager;
    private Internet internet;
    private  String email, password,randomKey,getEmail,
            LOG_URL = "https://beekeepertn.000webhostapp.com/php/wp_login.php",
            PASS_RESET_URL = "https://beekeepertn.000webhostapp.com/php/wp_resetPass.php",
            RESET_URL = "https://beekeepertn.000webhostapp.com/php/wp_resetUrlPass.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        internet = new Internet(LoginActivity.this);

        sessionManager = new SessionManager(this);

        if (sessionManager.isLogin()){

            SaveUserData.setState(1);
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        }

        init();
        clickListener();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void init() {
        userEmail = findViewById(R.id.userEmailET);
        userPassword = findViewById(R.id.userPasswordET);
        loginBtn = findViewById(R.id.loginBtn);
        signupTv = findViewById(R.id.createAccount);
        forgotTV = findViewById(R.id.resetPass);

    }

    private void clickListener() {

        //Go To Register

        signupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
                finish();
            }
        });

        //Login

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = userEmail.getText().toString().trim();
                password = userPassword.getText().toString().trim();

                if (email.isEmpty()){

                    userEmail.setError("Required");

                }else if (password.isEmpty()){

                    userPassword.setError("Required");

                }else{

                    Login(email, password);

                }

            }
        });


        //Reset Password


        forgotTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                View dialogView = getLayoutInflater().inflate(R.layout.forgot_pass_popup, null);
                EditText emailBox = dialogView.findViewById(R.id.emailBox);
                Button btnCancel = dialogView.findViewById(R.id.btnCancel);
                Button btnSend = dialogView.findViewById(R.id.btnSend);


                builder.setView(dialogView);
                AlertDialog dialog = builder.create();
                dialog.show();

                Generate();

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btnSend.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String email = emailBox.getText().toString().trim();
                        final String user_account_key = randomKey;

                        getEmail = email;

                        if (email.isEmpty()){

                            emailBox.setError("Required");

                        }else{
                            StringRequest stringRequest = new StringRequest(Request.Method.POST, PASS_RESET_URL, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    try {

                                        JSONObject jsonObject = new JSONObject(response);
                                        int responseDb = jsonObject.getInt("success");

                                        if (responseDb == 1){

                                            dialog.dismiss();
                                            SendResetLink();

                                        }else{

                                            Toast.makeText(LoginActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();

                                        }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(LoginActivity.this, "Error " +e.toString(), Toast.LENGTH_SHORT).show();

                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {

                                    Toast.makeText(LoginActivity.this, "Error " +error.toString(), Toast.LENGTH_SHORT).show();

                                }
                            }){

                                @NonNull
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {

                                    Map<String, String>params = new HashMap<>();
                                    params.put("email", email);
                                    params.put("emailKey", user_account_key);

                                    return params;
                                }
                            };

                            RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                            requestQueue.add(stringRequest);

                        }


                    }
                });

            }
        });
    }

    private void Login(String email, String password) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOG_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("login");

                    switch (success) {
                        case "0":
                            Toast.makeText(LoginActivity.this, "Please Verify Your Email", Toast.LENGTH_SHORT).show();
                            break;
                        case "3":
                            userPassword.setError("Wrong Password");
                            break;
                        case "1":

                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject object = jsonArray.getJSONObject(i);

                                String email = object.getString("user_email").trim();

                                sessionManager.createSession(email, null);

                                startActivity(new Intent(LoginActivity.this, MainActivity.class));

                            }

                            break;
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error " +e.toString(), Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(LoginActivity.this, "Error " +error.toString(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("email",email);
                params.put("password", password);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void SendResetLink(){
        final String email = getEmail;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, RESET_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int responseDb = jsonObject.getInt("success");
                    if (responseDb == 1){
                        Toast.makeText(LoginActivity.this, "Check Your Email", Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Error " +e.toString(), Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(LoginActivity.this, "Error " +error.toString(), Toast.LENGTH_SHORT).show();

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

    private void Generate(){

        RandomString randomString = new RandomString();

        randomKey = randomString.generateAlphaNumeric(200);

    }


    @Override
    public void onBackPressed() {
    }

}