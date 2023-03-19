package com.thenorthside.beekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.thenorthside.beekeeper.Helpers.Internet;
import com.thenorthside.beekeeper.Helpers.SaveUserData;


import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class SplashActivity extends AppCompatActivity {

    private Internet internet;
    private SaveUserData saveUserData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        LottieAnimationView animationView = findViewById(R.id.animation_view);
        animationView.setAnimation("animated_bee.json");
        animationView.playAnimation();


        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        internet = new Internet(SplashActivity.this);

        checkInternetConnection();

        new Handler().postDelayed(() -> {

            if (internet.isConnected()){
                saveUserData = new SaveUserData(SplashActivity.this,"OB");
                if (saveUserData.getState() == 1){

                    startActivity(new Intent(SplashActivity.this, MainActivity.class));

                }else{
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }
            }else {
                startActivity(new Intent(SplashActivity.this, NoInternetActivity.class));
            }
        }, 3000);


    }

    private void checkInternetConnection() {

        if (internet.isConnected()) {
            new isInternetActive().execute();
        }

    }

    class isInternetActive extends AsyncTask<Void, Void, String> {


        @Override
        protected String doInBackground(Void... voids) {

            InputStream inputStream = null;
            String json;

            try {
                String strURL = "https://icons.iconarchive.com/";
                URL url = new URL(strURL);

                URLConnection urlConnection = url.openConnection();
                urlConnection.setDoOutput(true);
                inputStream = urlConnection.getInputStream();
                json = "success";


            } catch (Exception e) {
                e.printStackTrace();
                json = "failed";
            }

            return json;

        }

        @Override
        protected void onPostExecute(String s) {

            if (s != null) {

                if (s.equals("success")) {

                } else {
                    Toast.makeText(SplashActivity.this, "No internet access", Toast.LENGTH_SHORT).show();
                }

            } else {
                Toast.makeText(SplashActivity.this, "No internet", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }

}