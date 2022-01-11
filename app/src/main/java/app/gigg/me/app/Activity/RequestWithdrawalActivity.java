package app.gigg.me.app.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import app.gigg.me.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class RequestWithdrawalActivity extends AppCompatActivity {
    private EditText paymentInfos;
    private Spinner spinnerPaymentMethod;
    private Button sendRequestBtn;
    ArrayList<String> methods;
    private RequestQueue queue;
    SharedPreferences userSituationId, facebookInterstitial, admobInterstitial;
    private String userId, amount, points,selectedSpinnerItem;
    private String bannerBottomType;
    private com.facebook.ads.InterstitialAd facebookInterstitialAd;

    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_withdrawal);
        Toolbar toolbar = findViewById(R.id.toolbar_withdrawals_request);
        setSupportActionBar(toolbar);
        queue = Volley.newRequestQueue(this);
        userSituationId = getSharedPreferences("userId",MODE_PRIVATE);
        userId = userSituationId.getString("userId", "");
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        paymentInfos = (EditText) findViewById(R.id.payment_infos_edittext);
        spinnerPaymentMethod = (Spinner) findViewById(R.id.spinner_payment_methods);
        sendRequestBtn = (Button) findViewById(R.id.send_request_button);
        Intent myIntent = getIntent();
        points = myIntent.getStringExtra("points");
        amount = myIntent.getStringExtra("amount");
        getPaymentMethods();
        builder = new AlertDialog.Builder(this);
        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                builder.setTitle("Warning!");
                builder.setMessage("Your payment will be cancelled if you select a wrong payment method");
                builder.setCancelable(false);
                builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedSpinnerItem = spinnerPaymentMethod.getSelectedItem().toString();
                        sendNewRequest();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });
        admobInterstitial = getSharedPreferences("admobInterstitial", MODE_PRIVATE);
        // Admob Interstitial Ads
        ScheduledExecutorService schudeler = Executors.newSingleThreadScheduledExecutor();
        schudeler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        }, 5, 1, TimeUnit.SECONDS);
        // Facebook Interstitial
        facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);
        prepareInterstitialAd();
        ScheduledExecutorService schudeler2 = Executors.newSingleThreadScheduledExecutor();
        schudeler2.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (facebookInterstitialAd.isAdLoaded()) {
                            facebookInterstitialAd.show();
                        }
                    }
                });
            }
        }, 2, 1, TimeUnit.SECONDS);
    }
    private void prepareInterstitialAd() {
        AudienceNetworkAds.initialize(this);

        facebookInterstitialAd = new com.facebook.ads.InterstitialAd(this, facebookInterstitial.getString("facebookInterstitial", ""));
        facebookInterstitialAd.loadAd();
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(RequestWithdrawalActivity.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
        finish();
        super.onBackPressed();
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Intent main = new Intent(RequestWithdrawalActivity.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
        finish();
        return true;
    }

    private void getPaymentMethods() {
        String url = getResources().getString(R.string.domain_name)+"/api/payments/methods/all";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            methods = new ArrayList<>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject method = jsonArray.getJSONObject(i);
                                String payment_method = method.getString("method");
                                methods.add(payment_method);
                            }
                            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                                    android.R.layout.simple_spinner_item, methods);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerPaymentMethod.setAdapter(adapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        queue.add(request);
    }

    private void sendNewRequest() {
        final String enteredPaymentInfos = this.paymentInfos.getText().toString();
        String url = getResources().getString(R.string.domain_name)+"/api/withdrawals/request/new";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")){
                        Toast.makeText(RequestWithdrawalActivity.this, getString(R.string.java_question_request_withdrawal), Toast.LENGTH_LONG).show();
                        Intent earnings = new Intent(RequestWithdrawalActivity.this, EarningsActivity.class);
                        startActivity(earnings);
                        finish();
                    }
                } catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("player_id", userId);
                params.put("account", enteredPaymentInfos);
                params.put("method", selectedSpinnerItem);
                params.put("points", points);
                params.put("amount", amount);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}

