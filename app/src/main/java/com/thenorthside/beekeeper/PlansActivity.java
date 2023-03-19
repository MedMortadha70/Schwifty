package com.thenorthside.beekeeper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.thenorthside.beekeeper.Helpers.SessionManager;
import com.thenorthside.beekeeper.Helpers.VolleySingleton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PlansActivity extends AppCompatActivity {

    private Button buyPlan;
    private String getUserEmail;
    private RadioGroup planOptions;
    private ImageView backToMain;
    private SessionManager sessionManager;
    private int validDate;
    private EditText cardHolder,cardNumber,expDate,codeCvv;
    private  String cardHolderName, userCardNumber, creditCardNumber, userCodeCvv, expirationDate, userPlan
            ,Update_Data_URL = "https://beekeepertn.000webhostapp.com/php/wp_updatePlan.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plans);

        sessionManager = new SessionManager(this);
        sessionManager.checkLogin();

        init();

        clickListener();

        HashMap<String, String> user = sessionManager.fetchUserDetail();

        getUserEmail = user.get(SessionManager.EMAIL);


    }

    private void init(){

        cardHolder = findViewById(R.id.cardHolderEt);
        cardNumber = findViewById(R.id.cardNumberEt);
        expDate = findViewById(R.id.cardDateEt);
        codeCvv = findViewById(R.id.cardCvvEt);
        backToMain = findViewById(R.id.backTo);
        planOptions = findViewById(R.id.planOptions);
        buyPlan = findViewById(R.id.buyBtn);

    }

    private void clickListener() {

        expDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(PlansActivity.this, mDateSetListener, year, month, day);
                dialog.show();
            }
        });

        buyPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cardHolderName = cardHolder.getText().toString();
                userCardNumber = cardNumber.getText().toString();
                userCodeCvv = codeCvv.getText().toString();
                expirationDate = expDate.getText().toString();
                creditCardNumber = userCardNumber.replaceAll("[\\s-]+", "");

                if (cardHolderName.isEmpty()){
                    cardHolder.setError("Required");
                }
                else if (userCardNumber.isEmpty()){
                    cardNumber.setError("Required");
                }
                else if (creditCardNumber.length() != 16){
                    cardNumber.setError("Please enter a valid card number");
                }else if(userCodeCvv.length() < 3 || userCodeCvv.length() > 4){
                    codeCvv.setError("Please enter a valid cvv");
                }else if (!expirationDate.isEmpty()){
                    SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
                    sdf.setLenient(false);
                    try {
                        Date expiry = sdf.parse(expirationDate);
                        Date today = new Date();
                        if (expiry.before(today)) {
                            expDate.setError("The expiration date has already passed");
                        }else{
                            validDate = 1;
                        }
                    } catch (ParseException e) {
                        expDate.setError("Invalid expiration date");
                    }
                }
                if (validDate == 1){
                    if(planOptions.getCheckedRadioButtonId() == -1) {
                        Toast.makeText(PlansActivity.this, "Choose a plan", Toast.LENGTH_SHORT).show();
                    }else{
                        int checkedId = planOptions.getCheckedRadioButtonId();
                        if (checkedId == R.id.basicBtn) {
                            userPlan = "basic";
                            update_plan(userPlan);
                        } else if (checkedId == R.id.premiumBtn) {
                            userPlan = "premium";
                            update_plan(userPlan);
                        }
                    }
                }

            }
        });

    }

    private void update_plan(String userPlan) {

        Date today = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);
        Date newDate = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/yy");
        String expirationDate = sdf.format(newDate);


        StringRequest stringRequestData = new StringRequest(Request.Method.POST, Update_Data_URL, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String responseData) {
                try {
                    JSONObject jsonObject = new JSONObject(responseData);
                    String success = jsonObject.getString("success");

                    if (success.equals("1")){

                        Intent intent = new Intent(PlansActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }else if (success.equals("0")){
                        Toast.makeText(PlansActivity.this, "Error", Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(PlansActivity.this, "Error " +e.toString(), Toast.LENGTH_SHORT).show();
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
                params.put("plan_name", userPlan);
                params.put("start_date", String.valueOf(today));
                params.put("end_date", expirationDate);


                return params;
            }
        };
        VolleySingleton.getInstance(PlansActivity.this).addToRequestQueue(stringRequestData);

    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String selectedDate = String.format(Locale.getDefault(), "%02d/%04d", monthOfYear + 1, year);
            expDate.setText(selectedDate);
        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PlansActivity.this, MainActivity.class));
    }



}