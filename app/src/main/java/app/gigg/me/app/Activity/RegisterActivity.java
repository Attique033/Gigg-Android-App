package app.gigg.me.app.Activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.facebook.CallbackManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import app.gigg.me.app.Manager.PermissionManager;
import app.gigg.me.app.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int REQUEST_WRITE_PERMISSION = 786;
    private Button btnAllowPermissions;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_WRITE_PERMISSION: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    new PermissionManager(RegisterActivity.this).writePreference();
                   /* Intent goToMain  =  new Intent(getApplicationContext(), SplashScreenActivity.class);
                    goToMain.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(goToMain);
                    finish();*/
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }


    private EditText name, email, password;
    private Button register;
    private ProgressBar mProgressBar;
    private String url, country = "";
    private TextView apiEmailError;
    private TextView apiNameError;
    AwesomeValidation validator;
    SharedPreferences userSituation;
    CallbackManager callbackManager;

    private CircleImageView imageProfile;
    private ImageView changeImage;
    private ImageView back_to_login_btn;
    private static final int PICK_IMAGE = 1;
    Uri imageurl;
    String imageData = "";
    private Bitmap bitmap;

    AlertDialog.Builder builder;
    AlertDialog alert;
    Spinner spinner;
    private String[] countryName;

    private Spinner countrySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_2);

        TextView textView = findViewById(R.id.termTxt);
        SpannableString content = new SpannableString("Terms & Condition");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);
        textView.setText(content);

        countryName = getResources().getStringArray(R.array.countries_array);
//        spinner = findViewById(R.id.spinner);
//        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,countryName);
//        spinner.setAdapter(aa);
//        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        userSituation = getSharedPreferences("userEmail", MODE_PRIVATE);
        name = (EditText) findViewById(R.id.register_name);
        email = (EditText) findViewById(R.id.register_email);
        password = (EditText) findViewById(R.id.register_password);
        register = (Button) findViewById(R.id.register_button);
        mProgressBar = (ProgressBar) findViewById(R.id.progress_bar);
        apiEmailError = (TextView) findViewById(R.id.api_email_error);
        apiNameError = (TextView) findViewById(R.id.api_name_error);

        builder = new AlertDialog.Builder(this);
        countrySpinner = findViewById(R.id.country_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, getResources().getStringArray(R.array.countries_array));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        countrySpinner.setAdapter(adapter);

        countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                country = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        back_to_login_btn = (ImageView) findViewById(R.id.back_to_login_btn);
        changeImage = (ImageView) findViewById(R.id.change_image_profile_re);
        imageProfile = (CircleImageView) findViewById(R.id.edit_profile_picture_re);

        TextView alreadyMember = (TextView) findViewById(R.id.already_member);

        if (imageData.length() < 1) {
            //Setting message manually and performing action on button click
            builder.setMessage("Upload Profile image ")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {

                            alert.dismiss();

                            if (new PermissionManager(RegisterActivity.this).checkPreference()) {

                            } else {
                                requestPermission();
                            }

                            Intent gallery = new Intent();
                            gallery.setType("image/*");
                            gallery.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
                        }
                    });
            //Creating dialog box
            alert = builder.create();
            alert.show();
        } else {

            alert.dismiss();


        }


        changeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
            }
        });


        alreadyMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginPage();
            }
        });

        back_to_login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLoginPage();
            }
        });

        validator = new AwesomeValidation(ValidationStyle.BASIC);
        url = getResources().getString(R.string.domain_name);
        setupRules();


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (country.equalsIgnoreCase("Select Country")) {
                    Toast.makeText(RegisterActivity.this, "Please select country", Toast.LENGTH_SHORT).show();
                } else {
                    if (!imageData.equals("")) {

                        if (validator.validate() && imageData.length() > 1) {
                            registerUser();

                            validator.clear();
                        }
                    } else {

                        Toast.makeText(v.getContext(), "upload profile image ", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


        callbackManager = CallbackManager.Factory.create();

    }


    public void setupRules() {
        validator.addValidation(this, R.id.register_name, RegexTemplate.NOT_EMPTY, R.string.name_register_error);
        validator.addValidation(this, R.id.register_email, Patterns.EMAIL_ADDRESS, R.string.email_register_error);
        String regexPassword = ".{6,}";
        validator.addValidation(this, R.id.register_password, regexPassword, R.string.password_register_error);
    }

    public void goToLoginPage() {
        // Go To Login Page
        Intent loginPage = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(loginPage);
        finish();
    }


    private void registerUser() {
        final String enteredName = this.name.getText().toString().trim();
        final String enteredEmail = this.email.getText().toString().trim();
        final String enteredPassword = this.password.getText().toString().trim();
        apiEmailError.setVisibility(View.GONE);
        apiEmailError.setText(null);
        apiNameError.setVisibility(View.GONE);
        apiNameError.setText(null);
        mProgressBar.setVisibility(View.VISIBLE);
        register.setVisibility(View.GONE);


        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "/api/players/new", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("success");
                    // No Errors

                    if (success.equals("1")) {
                        uploadImage(imageData, enteredEmail);
                        mProgressBar.setVisibility(View.GONE);
                        // Change Shared Preferences
                        userSituation.edit().putString("userEmail", enteredEmail).apply();
                        Intent homePage = new Intent(RegisterActivity.this, ReferralCodeActivity.class);
                        homePage.putExtra("username", enteredName);
                        homePage.putExtra("checkUpdate", "1");
                        startActivity(homePage);
                        finish();
                    }
                    // Name Exists
                    if (success.equals("nameError")) {
                        mProgressBar.setVisibility(View.GONE);
                        register.setVisibility(View.VISIBLE);
                        String message = jsonObject.getString("message_name");
                        apiNameError.setVisibility(View.VISIBLE);
                        apiNameError.setText("* " + message);
                    }
                    // Device Exist
                    if (success.equals("deviceError")) {
                        Toast.makeText(RegisterActivity.this, "Only 1 account per device is allowed!", Toast.LENGTH_LONG).show();
                        mProgressBar.setVisibility(View.GONE);
                        register.setVisibility(View.VISIBLE);
                    }
                    // Email Exists
                    if (success.equals("emailError")) {
                        mProgressBar.setVisibility(View.GONE);
                        register.setVisibility(View.VISIBLE);
                        String message = jsonObject.getString("message_email");
                        apiEmailError.setVisibility(View.VISIBLE);
                        apiEmailError.setText("* " + message);
                    }
                    // Email & Name Exists
                    if (success.equals("twoError")) {
                        mProgressBar.setVisibility(View.GONE);
                        register.setVisibility(View.VISIBLE);
                        String messageName = jsonObject.getString("message_name");
                        String messageEmail = jsonObject.getString("message_email");
                        apiEmailError.setVisibility(View.VISIBLE);
                        apiEmailError.setText("* " + messageEmail);
                        apiNameError.setVisibility(View.VISIBLE);
                        apiNameError.setText("* " + messageName);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    mProgressBar.setVisibility(View.GONE);
                    register.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mProgressBar.setVisibility(View.GONE);
                register.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("name", enteredName);
                params.put("email", enteredEmail);
                params.put("country", country);
                params.put("password", enteredPassword);
//                    params.put("country", country);
                @SuppressLint("HardwareIds") final String device = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                params.put("device_id", device);
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {
            assert data != null;
            imageurl = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(imageurl);
                bitmap = BitmapFactory.decodeStream(inputStream);
                imageProfile.setImageBitmap(bitmap);
                imageData = imageToStr(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String imageToStr(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        String encodedImg = Base64.encodeToString(imageByte, Base64.DEFAULT);
        return encodedImg;
    }

    private void uploadImage(final String imageStr, final String email) {
        final String userEmail = email;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "/api/players/image/upload", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Toast.makeText(RegisterActivity.this, "Image changed successfully", Toast.LENGTH_SHORT).show();

                if (getIntent().getStringExtra("checkUpdate") != null) {
                    Intent main = new Intent(RegisterActivity.this, MainActivity.class);
                    main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(main);
                    finish();

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
                params.put("image", imageStr);
                params.put("email", userEmail);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_PERMISSION);
        }
    }


}
