package com.thenorthside.beekeeper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bumptech.glide.Glide;
import com.thenorthside.beekeeper.Helpers.Internet;
import com.thenorthside.beekeeper.Helpers.SessionManager;
import com.thenorthside.beekeeper.Helpers.VolleySingleton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private int isLogin;
    private Dialog dialog;
    private Button goToBuyBt;
    private CardView warning,settings;
    private LinearLayout userData;
    private Internet internet;
    private Runnable runnable;
    private String getUserEmail;
    private SessionManager sessionManager;
    private CircleImageView userProfileImage;
    private final Handler handler = new Handler();
    private TextView username,userEmail, exp_date, dangers, liquid;
    private final String API_URL = "https://beekeepertn.000webhostapp.com/php/wp_main_fetch_data.php",
            States_URL = "https://beekeepertn.000webhostapp.com/php/wp_main_fetch_stats.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        internet = new Internet(MainActivity.this);
        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();


        if (sessionManager.isLogin()){
            isLogin = 1;
            realTimeFetch();

        }

        init();

        clickListener();

        HashMap<String, String> user = sessionManager.fetchUserDetail();

        getUserEmail = user.get(SessionManager.EMAIL);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void init(){

        username = findViewById(R.id.nameTv);
        userEmail = findViewById(R.id.emailTv);
        userProfileImage = findViewById(R.id.profileImage);
        warning = findViewById(R.id.warningPopUp);
        goToBuyBt = findViewById(R.id.goToBuy);
        userData = findViewById(R.id.userData);
        settings = findViewById(R.id.settingsBtn);
        exp_date = findViewById(R.id.exp_dateTv);
        dangers = findViewById(R.id.dangersTv);
        liquid = findViewById(R.id.liquidTv);

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.loading_dialog);
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

    }

    private void clickListener() {

        goToBuyBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PlansActivity.class);
                startActivity(intent);
                finish();
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    public void getUserData(){

        StringRequest stringRequestData = new StringRequest(Request.Method.POST, API_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String success = jsonObject.getString("success");
                    String successEmail = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("dataFetched");
                    if (successEmail.equals("1")){
                        if (success.equals("1")){
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                username.setText(object.getString("user_name"));
                                userEmail.setText(object.getString("user_email"));
                                String plan_name = object.getString("plan_name");
                                String userImg = object.getString("user_image");
                                if (!isFinishing()) {
                                    Glide.with(MainActivity.this).load(userImg).into(userProfileImage);
                                }

                                if (!isFinishing()) {
                                    if (Objects.equals(plan_name, "premium") || Objects.equals(plan_name, "basic")){
                                        warning.setVisibility(View.GONE);
                                        userData.setVisibility(View.VISIBLE);
                                    }else{
                                        userData.setVisibility(View.GONE);
                                        warning.setVisibility(View.VISIBLE);
                                    }
                                }


                            }
                        }else if (success.equals("0")){
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error " +e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("email",getUserEmail);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequestData);
    }

    public void getUserStats(){

        StringRequest stringRequestData = new StringRequest(Request.Method.POST, States_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String success = jsonObject.getString("success");
                    String successEmail = jsonObject.getString("success");
                    JSONArray jsonArray = jsonObject.getJSONArray("dataFetched");
                    if (successEmail.equals("1")){
                        if (success.equals("1")){
                            for (int i = 0; i < jsonArray.length(); i++){
                                JSONObject object = jsonArray.getJSONObject(i);
                                dangers.setText(object.getString("user_dangers"));
                                liquid.setText(object.getString("liquid_level") + "%");
                                exp_date.setText(object.getString("end_date"));
                            }
                        }else if (success.equals("0")){
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(MainActivity.this, "Error " +e.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();
                params.put("email",getUserEmail);
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequestData);
    }

    private void realTimeFetch(){

        handler.postDelayed(new Runnable() {
            public void run() {
                if (isLogin == 1){
                    dialog.show();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            final Handler handler = new Handler();
                            final int delay = 1000;
                            handler.postDelayed(new Runnable(){
                                public void run(){
                                    getUserData();
                                    getUserStats();
                                    dialog.dismiss();
                                    handler.postDelayed(this, delay);
                                }
                            }, delay);
                        }
                    }, 1000);
                }

            }
        }, 50);
    }

    @Override
    public void onBackPressed() {

    }

}