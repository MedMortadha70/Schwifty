package com.thenorthside.beekeeper;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.thenorthside.beekeeper.Helpers.Internet;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class NoInternetActivity extends AppCompatActivity {

    Internet internet;
    Button exitBtn, refreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_en_no_internet);

        exitBtn = findViewById(R.id.exitTv);
        refreshBtn = findViewById(R.id.refreshTv);

        internet = new Internet(NoInternetActivity.this);
        checkInternetConnection();

        exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitBtn();
            }
        });

        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (internet.isConnected()){

                    startActivity(new Intent(NoInternetActivity.this, MainActivity.class));

                }else {

                    Toast.makeText(NoInternetActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }

    public void exitBtn(){

        moveTaskToBack(true);
        android.os.Process.killProcess(android.os.Process.myPid());

        System.exit(1);

    }

    private void checkInternetConnection() {

        if (internet.isConnected()) {
            new isInternetActive().execute();
        }

    }

    @SuppressLint("StaticFieldLeak")
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

                }

            } else {
                Toast.makeText(NoInternetActivity.this, "No internet", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

    }

}