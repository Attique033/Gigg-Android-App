package app.gigg.me.app.Activity.freelance.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.gigg.me.app.Activity.MainActivity;
import app.gigg.me.app.Activity.freelance.adapters.JobAdapter;
import app.gigg.me.app.Activity.freelance.model.Job;
import app.gigg.me.app.Activity.freelance.util.SharedPrefHelper;
import app.gigg.me.app.BuildConfig;
import app.gigg.me.app.R;

public class JobCategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JobAdapter adapter;
    private List<Job> jobList = new ArrayList<>();
    private SharedPreferences userSituation;
    private SharedPreferences fiftyFiftyOption, startAppID, rewardVideoOption, questionTime, completedOption, admobInterstitial, facebookBanner, bottomBannerType, admobBanner, admobNative;
    private RequestQueue queue;
    private String bannerBottomType;

    
    private String email;
    private SharedPreferences sharedPreferences;
    private SharedPreferences userSituationId;
    private SharedPrefHelper helper;
    private EditText search_text;


    private LinearLayout adsLinear;
    private AdView bannerAdmobAdView;

    private ImageView backButton;
    private Toolbar mToolbar;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_category);

        mToolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        sharedPreferences = getSharedPreferences("userEmail", MODE_PRIVATE);
        email = sharedPreferences.getString("userEmail", "");

        helper = new SharedPrefHelper(this);
        queue = Volley.newRequestQueue(this);
        recyclerView = findViewById(R.id.job_recyeclerView);

        backButton = (ImageView) findViewById(R.id.backButton);

//        backButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                onBackPressed();
//            }
//        });


//        facebookBanner = getSharedPreferences("facebookBanner", Context.MODE_PRIVATE);
//        admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
//        adsLinear = (LinearLayout) findViewById(R.id.banner_container);
//        bannerAdmobAdView = new AdView(JobCategoryActivity.this);
//        bannerAdmobAdView.setAdUnitId(admobBanner.getString("admobBanner", ""));
//        bottomBannerType = getSharedPreferences("bottomBannerType",MODE_PRIVATE);
//        bannerBottomType = bottomBannerType.getString("bottomBannerType", "");
//
//        if (bannerBottomType.equals("admob")) {
//            MobileAds.initialize(JobCategoryActivity.this, getString(R.string.admob_app_id));
//            admobBanner = getSharedPreferences("admobBanner",MODE_PRIVATE);
//
//            adsLinear = (LinearLayout) findViewById(R.id.banner_container);
//            bannerAdmobAdView = new AdView(this);
//            bannerAdmobAdView.setAdUnitId(admobBanner.getString("admobBanner", ""));
//            bannerAdmobAdView.setAdSize(com.google.android.gms.ads.AdSize.FULL_BANNER);
//
//            adsLinear.addView(bannerAdmobAdView);
//            adsLinear.setGravity(Gravity.CENTER_HORIZONTAL);
//
//
//            AdRequest adRequest2 = new AdRequest.Builder().build();
//            bannerAdmobAdView.loadAd(adRequest2);
//            bannerAdmobAdView.setAdListener(new AdListener(){
//                @Override
//                public void onAdLoaded() {
//                    adsLinear.setVisibility(View.VISIBLE);
//                }
//            });
//        } else if(bannerBottomType.equals("facebook")) {
//            facebookBanner = getSharedPreferences("facebookBanner",MODE_PRIVATE);
//            AudienceNetworkAds.initialize(this);
//
//            com.facebook.ads.AdView facebookAdView = new com.facebook.ads.AdView(this, facebookBanner.getString("facebookBanner", null), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
//            LinearLayout adContainer = (LinearLayout) findViewById(R.id.bannerView10);
//            adContainer.addView(facebookAdView);
//            facebookAdView.loadAd();
//        }


        search_text = findViewById(R.id.search_text);
        search_text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String text = search_text.getText().toString();
                    if (!TextUtils.isEmpty(text)) {
                        Intent intent = new Intent(getApplicationContext(), SearchActivity.class);
                        intent.putExtra("text", text);
                        startActivity(intent);
                    }
                    handled = true;
                }
                return handled;
            }
        });

        adapter = new JobAdapter(this, jobList);
        recyclerView.setAdapter(adapter);

        jobList.add(new Job("Social media promotions", "#2DC6C9", getApplicationContext().getDrawable(R.drawable.social_media), "#social_media_promotions"));
        jobList.add(new Job("App download", "#58B4FF", getApplicationContext().getDrawable(R.drawable.website), "#website/app_review"));
        jobList.add(new Job("App/Product review", "#5271FF", getApplicationContext().getDrawable(R.drawable.image__1__1), "#product_testing"));
        jobList.add(new Job("Article writing", "#B74FFF", getApplicationContext().getDrawable(R.drawable.graphics), "#graphics_design"));
        jobList.add(new Job("Polls and Survey", "#DC44E9", getApplicationContext().getDrawable(R.drawable.checklist), "#polls_and_survey"));
        jobList.add(new Job("CPA surveys/Offers/Leads", "#FF96D9", getApplicationContext().getDrawable(R.drawable.video), "#video_and_animation"));
        jobList.add(new Job("Multimedia", "#FC739F", getApplicationContext().getDrawable(R.drawable.music), "#music_and_audio"));
        jobList.add(new Job("Web visit/Click ads", "#EEB6BF", getApplicationContext().getDrawable(R.drawable.assistant), "#virtual_assistant"));
        jobList.add(new Job("Tech", "#A69790", getApplicationContext().getDrawable(R.drawable.programming), "#programming_and_tech"));
        jobList.add(new Job("Anything else", "#A9B1BB", getApplicationContext().getDrawable(R.drawable.local), "#local_jobs"));

        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new JobAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Job job) {
                Intent intent = new Intent(getApplicationContext(), HireCreativeActivity.class);
                intent.putExtra("cat", job.getTitle());
                intent.putExtra("tag", job.getTag());
                startActivity(intent);
            }
        });


        fiftyFiftyOption = getSharedPreferences("fiftyFiftyOption", Context.MODE_PRIVATE);
        rewardVideoOption = getSharedPreferences("rewardVideoOption", Context.MODE_PRIVATE);
        completedOption = getSharedPreferences("completedOption", Context.MODE_PRIVATE);
        questionTime = getSharedPreferences("seconds", MODE_PRIVATE);
        userSituationId = getSharedPreferences("userId", MODE_PRIVATE);
        userSituation = getSharedPreferences("userEmail", MODE_PRIVATE);
        admobNative = getSharedPreferences("admobNative", MODE_PRIVATE);
        bottomBannerType = getSharedPreferences("bottomBannerType", MODE_PRIVATE);
        bannerBottomType = bottomBannerType.getString("bottomBannerType", "");
        getQuestionTimeAndCompletedOption();
        getConnectedUserData();
    }

    private void getQuestionTimeAndCompletedOption() {
        String url = getResources().getString(R.string.domain_name) + "/api/settings/all";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject settings = jsonArray.getJSONObject(i);
                                final int question_time = settings.getInt("question_time");
                                final String completedOptionStr = settings.getString("completed_option");
                                final String fiftyFiftyOptionStr = settings.getString("fifty_fifty");
                                final String rewardVideoOptionStr = settings.getString("video_reward");
                                questionTime.edit().putInt("seconds", question_time).apply();
                                completedOption.edit().putString("completedOption", completedOptionStr).apply();
                                fiftyFiftyOption.edit().putString("fiftyFiftyOption", fiftyFiftyOptionStr).apply();
                                rewardVideoOption.edit().putString("rewardVideoOption", rewardVideoOptionStr).apply();
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
        });
        queue.add(request);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        String token = task.getResult();
                        helper.setToken(token);

                        HashMap<String, String> params = new HashMap<>();
                        params.put("email", email);
                        params.put("token", token);
                        updatToken(params);

                        System.out.println(token);
                    }
                });
    }

    private void updatToken(HashMap<String, String> params) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.POST, url + "/api/update-token", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("FCM", "onResponse: " + response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("FCM", "onResponse: " + error.getLocalizedMessage());
            }
        }) {
            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    public void openDash(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void openJob(View view) {
        Intent intent = new Intent(this, JobHistoryActivity.class);
        startActivity(intent);
    }

    private void getConnectedUserData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.domain_name) + "/api/players/getplayerdata", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String userEmail = jsonObject.getString("email");
                    String userId = String.valueOf(jsonObject.getInt("id"));

                    userSituation.edit().putString("userEmail", userEmail).apply();
                    userSituationId.edit().putString("userId", userId).apply();

                    System.out.println(response);
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
                params.put("email", email);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.lang_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.options_rate) {
            gotoPlaySTore();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void gotoPlaySTore() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}