package com.thenorthside.beekeeper;

import androidx.annotation.NonNull;
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
import com.thenorthside.beekeeper.Helpers.RandomString;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SignupActivity extends AppCompatActivity {

    private TextView loginTV;
    private Button registerBtn;
    private EditText nameEdit,emailEdit,passwordEdit,confirmPasswordEdit;
    public static final String EMAIL_REGEX = "^(.+)@(.+)$";

    private String userName, email, password, confirm_Pass,randomKey,
            REG_URL = "https://beekeepertn.000webhostapp.com/php/wp_register.php",
            VERIFY_URL = "https://beekeepertn.000webhostapp.com/php/wp_verifyAccount.php";

    private Random randomCode = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        init();
        clickListener();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

    }

    private void init() {
        registerBtn = findViewById(R.id.registerBtn);
        nameEdit = findViewById(R.id.userNameET);
        emailEdit = findViewById(R.id.userEmailET);
        passwordEdit = findViewById(R.id.passwordET);
        confirmPasswordEdit = findViewById(R.id.confirmPasswordET);
        loginTV = findViewById(R.id.login);

    }


    private void clickListener() {

        loginTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                finish();
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                userName = nameEdit.getText().toString().trim();
                email = emailEdit.getText().toString().trim();
                password = passwordEdit.getText().toString().trim();
                confirm_Pass = confirmPasswordEdit.getText().toString().trim();

                int length = userName.length();
                int length_pass = password.length();

                if (userName.isEmpty()) {

                    nameEdit.setError("Required");

                }else if (length < 5) {

                    nameEdit.setError("Username Too Short");

                }else if (email.isEmpty() || !email.matches(EMAIL_REGEX)) {

                    emailEdit.setError("Required");

                }else if (password.isEmpty()) {

                    passwordEdit.setError("Required");

                }else if (length_pass < 6) {

                    passwordEdit.setError("Password  is Too Short");

                }else if (confirm_Pass.isEmpty() || !password.equals(confirm_Pass)) {

                    confirmPasswordEdit.setError("Invalid Password");

                }else {
                    Generate();
                    Register();
                }

            }
        });

    }

    private void Register(){

        final String username = this.nameEdit.getText().toString();
        final String email = this.emailEdit.getText().toString().trim();
        final String password = this.passwordEdit.getText().toString();
        final String confirm_password = this.confirmPasswordEdit.getText().toString();
        final String user_account_key = randomKey;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, REG_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int responseDb = jsonObject.getInt("success");

                    if (responseDb == 0){

                        Toast.makeText(SignupActivity.this, "This User Already Registered", Toast.LENGTH_SHORT).show();

                    }else if (responseDb == 1){
                        SendVerification();
                    }else if(responseDb == 2){
                        Toast.makeText(SignupActivity.this, "Ayyyyy", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignupActivity.this, "Error " +e.toString(), Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(SignupActivity.this, "Error " +error.toString(), Toast.LENGTH_SHORT).show();

            }
        }){

            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String>params = new HashMap<>();
                params.put("user_name", username);
                params.put("user_email", email);
                params.put("user_password", password);
                params.put("confirm_password", confirm_password);
                params.put("email_key", user_account_key);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }

    private void  SendVerification(){
        final String email = this.emailEdit.getText().toString().trim();
        final String user_account_key = randomKey;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, VERIFY_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {

                    JSONObject jsonObject = new JSONObject(response);
                    int responseDb = jsonObject.getInt("success");
                    int responseDbE = jsonObject.getInt("success");

                    if (responseDbE == 0){

                        Toast.makeText(SignupActivity.this, "Something Wrong", Toast.LENGTH_SHORT).show();

                    }else if (responseDb == 1){
                        Toast.makeText(SignupActivity.this, "Please Verify Your Email", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }else{

                        Toast.makeText(SignupActivity.this, "Error", Toast.LENGTH_SHORT).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(SignupActivity.this, "Error " +e.toString(), Toast.LENGTH_SHORT).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(SignupActivity.this, "Error " +error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){

            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String>params = new HashMap<>();
                params.put("user_email", email);
                params.put("email_key", user_account_key);

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