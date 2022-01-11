package app.gigg.me.app.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import app.gigg.me.app.Activity.freelance.ui.JobCategoryActivity;
import app.gigg.me.app.R;

public class LoginActivity extends AppCompatActivity {
    private EditText loginEmail, loginPassword;
    private Button login;
    private ProgressBar progressBar;
    private String url;
    private TextView apiLoginError;
    AwesomeValidation validator;
    SharedPreferences userSituation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_2);

        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login_button);

        progressBar = (ProgressBar) findViewById(R.id.login_progress_bar);
        url = getResources().getString(R.string.domain_name);
        apiLoginError = (TextView) findViewById(R.id.api_login_error);
        userSituation = getSharedPreferences("userEmail", MODE_PRIVATE);
        validator = new AwesomeValidation(ValidationStyle.BASIC);
        TextView newMember = (TextView) findViewById(R.id.new_member);
        newMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerPage = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerPage);
                finish();
            }
        });
        setupRules();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validator.validate()) {
                    tryLogin();
                    validator.clear();
                }
            }
        });

        TextView forgotPassword = (TextView) findViewById(R.id.forgot_password);
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent reset = new Intent(LoginActivity.this, SendResetLinkActivity.class);
                startActivity(reset);
                finish();

            }
        });
    }

    public void setupRules() {
        validator.addValidation(this, R.id.login_email, Patterns.EMAIL_ADDRESS, R.string.email_login_error);
    }


    private void tryLogin() {
        final String enteredEmail = this.loginEmail.getText().toString().trim();
        final String enteredPassword = this.loginPassword.getText().toString().trim();
        apiLoginError.setVisibility(View.GONE);
        apiLoginError.setText(null);
        progressBar.setVisibility(View.VISIBLE);
        login.setVisibility(View.GONE);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "/api/players/login", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    // No Errors
                    if (success.equals("loggedSuccess")) {
                        // Change Shared Preferences
                        userSituation.edit().putString("userEmail", enteredEmail).apply();
                        // Message Success
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(LoginActivity.this, "Logged In Successfully!", Toast.LENGTH_LONG).show();
                        // Go To Home Page
                        Intent homePage = new Intent(LoginActivity.this, JobCategoryActivity.class);
                        startActivity(homePage);
                        finish();
                    }
                    // Email Problem
                    if (success.equals("emailError")) {
                        progressBar.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        String message = jsonObject.getString("email_error");
                        apiLoginError.setVisibility(View.VISIBLE);
                        apiLoginError.setText("* " + message);
                    }
                    // Password Problem
                    if (success.equals("passwordError")) {
                        progressBar.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        String message = jsonObject.getString("password_error");
                        apiLoginError.setVisibility(View.VISIBLE);
                        apiLoginError.setText("* " + message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(LoginActivity.this, "Catch : " + e.getMessage(), Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                    login.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(LoginActivity.this, "ERROR : " + error.getMessage(), Toast.LENGTH_LONG).show();
                progressBar.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", enteredEmail);
                params.put("password", enteredPassword);
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
