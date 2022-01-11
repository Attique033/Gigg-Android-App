package app.gigg.me.app.Activity.freelance.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.gigg.me.app.Activity.MainActivity;
import app.gigg.me.app.Activity.TextActivity;
import app.gigg.me.app.Activity.freelance.adapters.CapabilityAdapter;
import app.gigg.me.app.Activity.freelance.model.CalculateAge;
import app.gigg.me.app.Activity.freelance.model.Capability;
import app.gigg.me.app.Activity.freelance.model.ProfileSetting;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.Model.Player;
import app.gigg.me.app.R;

public class CreativeProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Capability> capabilityList = new ArrayList<>();
    private CapabilityAdapter adapter;
    private TextView mUsernameAge, mSchool, mCountry, mAboutMe, mIGFollowers, mIGPosts, mTWFollowers, mTWTweets, mFBFollowers, mFBPosts, mYTSubscribers, mYTViews;
    private String email;
    private ImageView imageView;
    private RelativeLayout mHireMe;
    private RelativeLayout mReviews;
    private String userId;
    private ImageView mMenuButton;

    private SharedPreferences sharedPreferences, bottomBannerType, startAppID, admobInterstitial, facebookInterstitial, facebookBanner, admobBanner, questionTime, userSituationId, completedOption;

    String categoryId, categoryName, bannerBottomType;
    private LinearLayout adsLinear;
    private AdView bannerAdmobAdView;

    private ImageView backButton;
    private CountryCodePicker countryCodePicker;
    private ImageView mFlag;

    private String myEmail;
    private Player player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creative_profile);
        email = getIntent().getStringExtra("email");
        userId = getIntent().getStringExtra("id");
        player = new Player(getIntent().getIntExtra("id",1),getIntent().getStringExtra("name"),getIntent().getStringExtra("email"),"",getIntent().getStringExtra("image"),getIntent().getIntExtra("score",0));
        recyclerView = findViewById(R.id.capability_recyclerview_creative);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(CreativeProfileActivity.this, LinearLayoutManager.HORIZONTAL, false);

        sharedPreferences = getSharedPreferences("userEmail", MODE_PRIVATE);
        myEmail = sharedPreferences.getString("userEmail", "");

        adapter = new CapabilityAdapter(this, capabilityList);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        countryCodePicker = new CountryCodePicker(this);
        mFlag = findViewById(R.id.flag_image);
        imageView = findViewById(R.id.imageView12);
        mUsernameAge = findViewById(R.id.text_name_age);
        mSchool = findViewById(R.id.text_school);
        findViewById(R.id.message).setOnClickListener(this::openChat);
        mCountry = findViewById(R.id.text_country);
        mAboutMe = findViewById(R.id.text_about_me);
        //  mIGFollowers = findViewById(R.id.ig_followers);
        // mIGPosts = findViewById(R.id.ig_photos);
        mTWFollowers = findViewById(R.id.tw_followers);
        // mTWTweets = findViewById(R.id.tw_tweets);
        mFBFollowers = findViewById(R.id.fb_followers);
        // mFBPosts = findViewById(R.id.fb_posts);
        mYTSubscribers = findViewById(R.id.yt_subcribers);
        //  mYTViews = findViewById(R.id.yt_views);
        mReviews = findViewById(R.id.reviews_button);

        mMenuButton = findViewById(R.id.menuButton);

        backButton = (ImageView) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), ShowReviewsActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });

        mHireMe = findViewById(R.id.hireMe_button);
        mHireMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HireMeActivity.class);
                intent.putExtra("email", email);
                startActivity(intent);
            }
        });
        facebookBanner = getSharedPreferences("facebookBanner", Context.MODE_PRIVATE);
        admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
        adsLinear = (LinearLayout) findViewById(R.id.banner_container);
        bannerAdmobAdView = new AdView(CreativeProfileActivity.this);
        bannerAdmobAdView.setAdUnitId(admobBanner.getString("admobBanner", ""));
        bottomBannerType = getSharedPreferences("bottomBannerType", MODE_PRIVATE);
        bannerBottomType = bottomBannerType.getString("bottomBannerType", "");

        getData(email);
    }

    private void getData(String email) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "/api/creative_profile/" + email, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                System.out.println(response);
                Gson gson = new Gson();
                ProfileSetting profileSetting = gson.fromJson(response, ProfileSetting.class);
                if (profileSetting.isStatus()) {
                    ProfileSetting.Data data = profileSetting.getData();
                    CalculateAge calculateAge = new CalculateAge();
                    long age = calculateAge.calculateAge(Long.parseLong(data.getDate_of_birth()));
                    Glide.with(getApplicationContext())
                            .load(data.getProfile_url())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageView);
                    mUsernameAge.setText(data.getUsername() + ", " + age);
                    mSchool.setText(data.getSchool());
                    mAboutMe.setText(data.getAbout());
                    for (ProfileSetting.Capalibity capalibity : data.getCapalibity()) {
                        capabilityList.add(new Capability(capalibity.getName()));
                        adapter.notifyDataSetChanged();
                    }

                    ProfileSetting.Twitter twitter = profileSetting.getTwitter();
                    ProfileSetting.Youtube youtube = profileSetting.getYoutube();

                    mTWFollowers.setText(String.valueOf(twitter.getFollowers()));
                    //mTWTweets.setText(String.valueOf(twitter.getTweets()));

                    mYTSubscribers.setText(youtube.getSubscriber());
                    //mYTViews.setText(youtube.getViews());
                    mCountry.setText(data.getCountry());
                    try {
                        countryCodePicker.setCountryName(data.getCountry());
                        Glide.with(getApplicationContext())
                                .load(countryCodePicker.getSelectedCountryFlagResourceId())
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .into(mFlag);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

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

    public void openChat(View view) {
        Intent intent = new Intent(getApplicationContext(), TextActivity.class);
        intent.putExtra("email", player.getEmail());
        intent.putExtra("id", player.getId());
        intent.putExtra("image", player.getImage_url());
        intent.putExtra("score", player.getPoints());
        intent.putExtra("name", player.getName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void getConnectedUserData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.domain_name) + "/api/players/getplayerdata", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String userName = jsonObject.getString("name");
                    String userEmail = jsonObject.getString("email");
                    String userImageUrl = jsonObject.getString("image");
                    openChatX(userName, userImageUrl);
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


    private void openChatX(String name, String pic) {
        if (userId == null) {
            userId = MainActivity.userId;
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DataBaseApi.START_CHAT2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String cid = jsonObject.getString("message");
                    Intent intent = new Intent(getApplicationContext(), TextActivity.class);
                    intent.putExtra("id", userId);
                    intent.putExtra("name", name);
                    intent.putExtra("pic", pic);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("sid", userId);
                stringMap.put("rid", email);
                return stringMap;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }

    public void goBack(View view) {
        onBackPressed();
    }

    public void openEditMenu(View view) {
        if (email.equals(myEmail)) {
            PopupMenu popup = new PopupMenu(this, mMenuButton);
            popup.getMenuInflater()
                    .inflate(R.menu.option_menu, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                public boolean onMenuItemClick(MenuItem item) {
                    int id = item.getItemId();
                    if (id == R.id.edit_profile) {
                        startActivity(new Intent(CreativeProfileActivity.this, CreativeProfileSettingsActivity.class));
                    }
                    return true;
                }
            });

            popup.show();
        }
    }

}