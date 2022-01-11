package app.gigg.me.app.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import app.gigg.me.app.Activity.freelance.SharedHelper;
import app.gigg.me.app.Activity.freelance.ui.CreativeProfileSettingsActivity;
import app.gigg.me.app.Activity.freelance.ui.JobCategoryActivity;
import app.gigg.me.app.R;

public class ReferralCodeActivity extends AppCompatActivity {
    private Button send, skip;
    private EditText referralCode;
    private String url;
    private SharedPreferences userSituation;
    private SharedHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral_code);
        userSituation = getSharedPreferences("userEmail", MODE_PRIVATE);
        referralCode = (EditText) findViewById(R.id.referral_code);
        send = (Button) findViewById(R.id.referral_send);
        skip = (Button) findViewById(R.id.skip_referral);
        url = getResources().getString(R.string.domain_name);

        helper = new SharedHelper(this);

        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendReferral();
            }
        });
    }


    private void showDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Congratulations!");
        alertDialog.setMessage("You have successfully signed up on Gigg. Please choose how you want to use the app.");
        alertDialog.setPositiveButton("As Freelancer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.setType(1);
                Intent skip = new Intent(ReferralCodeActivity.this, CreativeProfileSettingsActivity.class);
                startActivity(skip);
                dialog.dismiss();
                finish();
            }
        });

        alertDialog.setNegativeButton("As Employer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                helper.setType(0);
                Intent skip = new Intent(ReferralCodeActivity.this, JobCategoryActivity.class);
                startActivity(skip);
                dialog.dismiss();
                finish();
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    private void sendReferral() {
        final String email = userSituation.getString("userEmail", "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "/api/players/referral/add", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    if (success.equals("1")) {
                        Toast.makeText(ReferralCodeActivity.this, "Referral Code Added Successfully!", Toast.LENGTH_SHORT).show();
                        showDialog();
                    } else {
                        Toast.makeText(ReferralCodeActivity.this, "Referral Code Invalid!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(ReferralCodeActivity.this, "Mail -> " + email, Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                // Check that image is added
                params.put("email", email);
                params.put("referral", referralCode.getText().toString());
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(ReferralCodeActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }
}
