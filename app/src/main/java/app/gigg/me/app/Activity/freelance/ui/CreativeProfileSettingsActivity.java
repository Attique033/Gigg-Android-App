package app.gigg.me.app.Activity.freelance.ui;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.gigg.me.app.Activity.freelance.adapters.SettingsCapabilityAdapter;
import app.gigg.me.app.Activity.freelance.model.ProfileSetting;
import app.gigg.me.app.Activity.freelance.model.SettingsCapability;
import app.gigg.me.app.Activity.freelance.model.University;
import app.gigg.me.app.R;

public class CreativeProfileSettingsActivity extends AppCompatActivity {

    private EditText dob_button;
    private RecyclerView recyclerView;
    private List<SettingsCapability> capabilityList = new ArrayList<>();
    private SettingsCapabilityAdapter re_adapter;
    private Spinner capability_spinner;
    private int selected_items = 0;
    private TextView mSelectLimit;
    private ArrayList<String> capabilities = new ArrayList<>();

    private EditText mUsername, mOccupation, mAboutMe, mInstagram, mTwitter, mYoutube;

    private String email;
    private SharedPreferences sharedPreferences;
    private Calendar calendar;
    private AutoCompleteTextView mUniversity;
    private ArrayList<String> universites = new ArrayList<>();
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creative_profile_settings_2);

        sharedPreferences = getSharedPreferences("userEmail", MODE_PRIVATE);
        email = sharedPreferences.getString("userEmail", "");

        calendar = Calendar.getInstance();
        dob_button = findViewById(R.id.dob_button);
        recyclerView = findViewById(R.id.capability_recyclerview);
        mSelectLimit = findViewById(R.id.selection_text);
        re_adapter = new SettingsCapabilityAdapter(this, capabilityList);
        recyclerView.setAdapter(re_adapter);
        capability_spinner = findViewById(R.id.capability_spinner);

        mUsername = findViewById(R.id.editText_username);
        mOccupation = findViewById(R.id.editText_occupation);
        mAboutMe = findViewById(R.id.editText_aboutme);
        mInstagram = findViewById(R.id.editText_ig_username);
        mTwitter = findViewById(R.id.editText_twitter_username);
        mYoutube = findViewById(R.id.editText_yt_username);
        mUniversity = findViewById(R.id.university);


        getData(email);
        capabilities.add("Select Capability");
        capabilities.add("#social_media_promotions");
        capabilities.add("#app_download");
        capabilities.add("#app/product_review");
        capabilities.add("#arricle_writing");
        capabilities.add("#polls_and_survey");
        capabilities.add("#CPA_surveys/Offers/Leads");
        capabilities.add("#multimedia");
        capabilities.add("#web_visit/click_ads");
        capabilities.add("#programming_and_tech");
        capabilities.add("#anything_else");


        mUniversity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchUniversity(editable.toString());
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, capabilities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        capability_spinner.setAdapter(adapter);

        capability_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!adapterView.getItemAtPosition(i).toString().equalsIgnoreCase("Select Capability")) {
                    if (selected_items >= 5) {
                        Toast.makeText(CreativeProfileSettingsActivity.this, "Maximum amount selected", Toast.LENGTH_SHORT).show();
                    } else {
                        selected_items += 1;
                        capabilityList.add(new SettingsCapability(adapterView.getItemAtPosition(i).toString()));
                        re_adapter.notifyDataSetChanged();
                        capabilities.remove(i);
                    }
                    mSelectLimit.setText("Capability : Select " + (5 - selected_items));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        re_adapter.setOnClickListener(new SettingsCapabilityAdapter.OnItemClickListener() {
            @Override
            public void onClick(SettingsCapability settingsCapability) {
                selected_items -= 1;
                capabilityList.remove(settingsCapability);
                re_adapter.notifyDataSetChanged();
                mSelectLimit.setText("Capability : Select " + (5 - selected_items));
                capabilities.add(settingsCapability.getName());
            }
        });

    }

    private void searchUniversity(String toString) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.GET, url + "/api/unisearch?search=" + toString, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                universites.clear();
                Gson gson = new Gson();
                try {
                    List<University> universities = gson.fromJson(response, new TypeToken<List<University>>() {
                    }.getType());
                    for (University university : universities) {
                        universites.add(university.getName());
                    }
                    adapter = new ArrayAdapter<String>(getApplicationContext(),
                            R.layout.simple_list_item, universites);
                    mUniversity.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getLocalizedMessage());
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    public void opnDatePicker(View view) {
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MMM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                dob_button.setText(sdf.format(calendar.getTime()));
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public void skip(View view) {
        //Skip
        Intent intent = new Intent(this, JobCategoryActivity.class);
        startActivity(intent);
        finish();
    }

    public void goBack(View view) {
        //Back
        onBackPressed();
    }

    public void submit(View view) {
        String username = mUsername.getText().toString();
        String occupation = mOccupation.getText().toString();
        String school = mUniversity.getText().toString();
        String about_me = mAboutMe.getText().toString();
        String instagram = mInstagram.getText().toString();
        String twitter = mTwitter.getText().toString();
        String youtube = mYoutube.getText().toString();
        ArrayList<String> capalibities = new ArrayList<>();
        for (SettingsCapability capability : capabilityList) {
            capalibities.add("\"" + capability.getName() + "\"");
        }

        long date_of_birth = calendar.getTimeInMillis();

        if (TextUtils.isEmpty(username)) {
            mUsername.setError("Please enter username");
            return;
        } else if (TextUtils.isEmpty(occupation)) {
            mOccupation.setError("Please enter occupation");
            return;
        } else if (TextUtils.isEmpty(about_me)) {
            mOccupation.setError("Please enter about yourself");
            return;
        } else if (capalibities.size() <= 0) {
            mSelectLimit.setText("Please select at least 1 capability");
            return;
        } else if (date_of_birth <= 0) {
            Toast.makeText(this, "Please enter date of birth..", Toast.LENGTH_SHORT).show();
            return;
        } else {
            Map<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("username", username);
            params.put("occupation", occupation);
            params.put("about", about_me);
            params.put("capalibities", capalibities.toString());
            params.put("school", "school");
            params.put("date_of_birth", String.valueOf(date_of_birth));

            if (!TextUtils.isEmpty(twitter)) {
                params.put("instagram_token", instagram);
            }
            if (!TextUtils.isEmpty(instagram)) {
                params.put("twitter_token", twitter);
            }
            if (!TextUtils.isEmpty(youtube)) {
                params.put("youtube_channel", youtube);
            }
            saveProfile(params);
        }
    }

    private void saveProfile(Map<String, String> params) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "/api/creative_profile", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                Intent intent = new Intent(getApplicationContext(), JobCategoryActivity.class);
                startActivity(intent);
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

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

    private void getData(String email) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "/api/creative_profile/" + email, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                ProfileSetting profileSetting = gson.fromJson(response, ProfileSetting.class);
                if (profileSetting.isStatus()) {
                    ProfileSetting.Data data = profileSetting.getData();
                    mUsername.setText(data.getUsername());
                    mOccupation.setText(data.getOccupation());
                    mUniversity.setText(data.getSchool());
                    mAboutMe.setText(data.getAbout());
                    for (ProfileSetting.Capalibity capalibity : data.getCapalibity()) {
                        String cap = capalibity.getName();
                        capabilities.remove(cap);
                        capabilityList.add(new SettingsCapability(cap));
                        re_adapter.notifyDataSetChanged();
                    }
                    mInstagram.setText(data.getInstagram_token());
                    mTwitter.setText(data.getTwitter_token());
                    mYoutube.setText(data.getYoutube_channel());

                    calendar.setTimeInMillis(Long.parseLong(data.getDate_of_birth()));
                    String myFormat = "yyyy-MMM-dd";
                    SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                    dob_button.setText(sdf.format(calendar.getTime()));

                } else {
                    Log.d("onResponse: ", "No Data");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}