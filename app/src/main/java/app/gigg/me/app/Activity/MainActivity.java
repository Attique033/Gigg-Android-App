package app.gigg.me.app.Activity;

import static app.gigg.me.app.Manager.Constants.AD_UNIT;
import static app.gigg.me.app.Manager.Constants.TEST_MODE;
import static app.gigg.me.app.Manager.Constants.UNITY_ID;
import static app.gigg.me.app.R.drawable.ic_menu;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.Purchase;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.login.LoginManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.material.navigation.NavigationView;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import app.gigg.me.app.Activity.escrow.EscrowActivity;
import app.gigg.me.app.Activity.freelance.ui.CreativeProfileActivity;
import app.gigg.me.app.Activity.freelance.ui.JobCategoryActivity;
import app.gigg.me.app.Activity.freelance.ui.JobHistoryActivity;
import app.gigg.me.app.Activity.freelance.ui.LiveTaskActivity;
import app.gigg.me.app.Activity.freelance.ui.ShareProfileActivity;
import app.gigg.me.app.Activity.freelance.ui.VaultActivity;
import app.gigg.me.app.Adapter.BlogAdapter;
import app.gigg.me.app.Adapter.PlayersAdapter;
import app.gigg.me.app.BuildConfig;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.Manager.PreferenceManager;
import app.gigg.me.app.Manager.PurchaseHelper;
import app.gigg.me.app.Model.Blog;
import app.gigg.me.app.Model.Player;
import app.gigg.me.app.Model.Subscription;
import app.gigg.me.app.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static String userId,userImage;
    int points;
    private String bannerBottomType;
    public static String userEmail,userName;
    private CircleImageView mProfileImage;
    private TextView mUserName;
    private TextView mUserEmail;
    private TextView mUserBalance;
    private RecyclerView mTopEarnerRecyclerView, mBlogRecyclerView;
    private PlayersAdapter playersAdapter;
    private BlogAdapter blogAdapter;
    private List<Player> playerList = new ArrayList<>();
    private List<Blog> blogList = new ArrayList<>();
    private SharedPreferences userSituation;
    private SharedPreferences fiftyFiftyOption, startAppID, rewardVideoOption, questionTime, completedOption, admobInterstitial, facebookBanner, bottomBannerType, admobBanner, admobNative;
    private TextView mChatRoomButton;
    private DrawerLayout drawer;
    private ActionBarDrawerToggle toggle;
    public static SharedPreferences userSituationId;
    private TextView currentUserName, currentEmail;
    private CircleImageView currentProfileImage;
    private GoogleSignInClient mGoogleSignInClient;
    private SharedPreferences facebookInterstitial;
    public static String bannerAdID ;
    String currentDate;

    private PurchaseHelper purchaseHelper;
    private PreferenceManager preferenceManager;

    private RelativeLayout banner_layout;
    private BannerView bannerView;
    private RequestQueue queue;

    private UnityBannerListener unityBannerListener = new UnityBannerListener();


    String categoryId, categoryName;
    private LinearLayout adsLinear;
    private AdView bannerAdmobAdView;


    private com.facebook.ads.InterstitialAd facebookInterstitialAd;

    private InterstitialAd mInterstitialAd;

    public void gotoWithdraw(View view) {
        startActivity(new Intent(this, EarningsActivity.class));
        finish();
        if (mInterstitialAd != null) {
            mInterstitialAd.show(MainActivity.this);
        }
    }

    private void gotoPlaySTore() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
    }

    public void openChatRooms(View view) {

    }

    public void oepnEarnings(View view) {
        Intent main = new Intent(this, EarningsActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
        finish();
    }


    private class UnityBannerListener implements BannerView.IListener {
        @Override
        public void onBannerLoaded(BannerView bannerAdView) {
            // Called when the banner is loaded.
        }

        @Override
        public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
            Log.d("SupportTest", "Banner Error" + errorInfo);
            // Note that the BannerErrorInfo object can indicate a no fill (see API documentation).
        }

        @Override
        public void onBannerClick(BannerView bannerAdView) {
            // Called when a banner is clicked.
        }

        @Override
        public void onBannerLeftApplication(BannerView bannerAdView) {
            // Called when the banner links out of the application.
        }
    }

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        startAppID = getSharedPreferences("startAppID", MODE_PRIVATE);
        getWindow().setStatusBarColor(getResources().getColor(R.color.new_blue));
        setContentView(R.layout.activity_main_new);
        //setUpOneSignal();

        mToolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(mToolbar);

        drawer = findViewById(R.id.drawer_test);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        Button logout = (Button) header.findViewById(R.id.logout);
        toggle = new ActionBarDrawerToggle(this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //Unity AD
        banner_layout = findViewById(R.id.bannerView);
        UnityAds.initialize(this, UNITY_ID, null, TEST_MODE, true);
        bannerView = new BannerView(this, AD_UNIT, new UnityBannerSize(320, 50));
        bannerView.setListener(unityBannerListener);
        banner_layout.addView(bannerView);
        bannerView.load();

        queue = Volley.newRequestQueue(this);

        purchaseHelper = PurchaseHelper.getInstance(getApplication());
        getLifecycle().addObserver(purchaseHelper);


        facebookBanner = getSharedPreferences("facebookBanner", Context.MODE_PRIVATE);
        admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
        adsLinear = (LinearLayout) findViewById(R.id.banner_container);
        bannerAdmobAdView = new AdView(MainActivity.this);
        bannerAdmobAdView.setAdUnitId(admobBanner.getString("admobBanner", ""));
        bottomBannerType = getSharedPreferences("bottomBannerType", MODE_PRIVATE);
        bannerBottomType = bottomBannerType.getString("bottomBannerType", "");

        if (bannerBottomType.equals("admob")) {
            MobileAds.initialize(MainActivity.this, initializationStatus -> {

            });
            admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);

            adsLinear = (LinearLayout) findViewById(R.id.banner_container);
            bannerAdmobAdView = new AdView(this);
            bannerAdmobAdView.setAdUnitId(admobBanner.getString("admobBanner", ""));
            bannerAdmobAdView.setAdSize(com.google.android.gms.ads.AdSize.FULL_BANNER);

            adsLinear.addView(bannerAdmobAdView);
            adsLinear.setGravity(Gravity.CENTER_HORIZONTAL);


            AdRequest adRequest2 = new AdRequest.Builder().build();
            bannerAdmobAdView.loadAd(adRequest2);
            bannerAdmobAdView.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    adsLinear.setVisibility(View.VISIBLE);
                }
            });
        } else if (bannerBottomType.equals("facebook")) {
            facebookBanner = getSharedPreferences("facebookBanner", MODE_PRIVATE);
            AudienceNetworkAds.initialize(this);

            com.facebook.ads.AdView facebookAdView = new com.facebook.ads.AdView(this, facebookBanner.getString("facebookBanner", null), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.bannerView10);
            adContainer.addView(facebookAdView);
            facebookAdView.loadAd();
        }

        preferenceManager = new PreferenceManager(this);

        purchaseHelper.purchaseUpdateEvent.observe(this, new Observer<List<Purchase>>() {
            @Override
            public void onChanged(List<Purchase> purchases) {
                if (purchases != null) {
                    if (purchases.size() > 0) {
                        for (Purchase purchase : purchases) {
                            if (purchase.getSku().equals(PurchaseHelper.BASIC_SKU) || purchase.getSku().equals(PurchaseHelper.PREMIUM_SKU)) {
                                Subscription subscription = new Subscription(purchase.getSku(), true);
                                preferenceManager.setPurchase(subscription);
                            } else {
                                Subscription subscription = new Subscription(null, false);
                                preferenceManager.setPurchase(subscription);
                            }
                        }
                    } else {
                        Subscription subscription = new Subscription(null, false);
                        preferenceManager.setPurchase(subscription);
                    }
                }
            }
        });

        fiftyFiftyOption = getSharedPreferences("fiftyFiftyOption", Context.MODE_PRIVATE);
        rewardVideoOption = getSharedPreferences("rewardVideoOption", Context.MODE_PRIVATE);
        completedOption = getSharedPreferences("completedOption", Context.MODE_PRIVATE);
        questionTime = getSharedPreferences("seconds", MODE_PRIVATE);
        userSituationId = getSharedPreferences("userId", MODE_PRIVATE);
        userSituation = getSharedPreferences("userEmail", MODE_PRIVATE);
        admobNative = getSharedPreferences("admobNative", MODE_PRIVATE);

        getQuestionTimeAndCompletedOption();

        currentUserName = (TextView) header.findViewById(R.id.current_user_name);
        currentEmail = (TextView) header.findViewById(R.id.current_user_email);
        currentProfileImage = (CircleImageView) header.findViewById(R.id.profile_image_header);

        SharedPreferences.Editor editor = getSharedPreferences("bannerDate", MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences("bannerDate", MODE_PRIVATE);

        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        drawer.addDrawerListener(new ActionBarDrawerToggle(
                this,
                drawer,
                0,
                0


        ) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                String prevDate = prefs.getString("date", "NoValue");
                if (prevDate.equals("NoValue")) {
                    if (!prevDate.equals(currentDate)) {


                        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.CustomAlertDialog);
                        ViewGroup viewGroup = findViewById(android.R.id.content);
                        View dialogView = LayoutInflater.from(drawerView.getContext()).inflate(R.layout.popup_image, viewGroup, false);

                        builder.setView(dialogView);
                        final AlertDialog alertDialog = builder.create();
                        ImageView imagePop = dialogView.findViewById(R.id.image_pop);
                        ImageButton buttonclose = dialogView.findViewById(R.id.btn_bannerClose);

                        imagePop.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://invest.gigg.me/"));
                                Intent chooseIntent = Intent.createChooser(intent, "Choose from below");
                                startActivity(chooseIntent);
                                editor.putString("date", currentDate);
                                editor.commit();
                                editor.apply();
                            }
                        });
                        buttonclose.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                alertDialog.dismiss();
                            }
                        });
                        alertDialog.show();

                    }
                }

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change Shared Preferences
                userSituation.edit().putString("userEmail", "").apply();
                userSituationId.edit().putString("userId", "").apply();
                // Logout Google
                if (mGoogleSignInClient != null) {
                    mGoogleSignInClient.signOut();
                }
                // Logout Facebook
                if (LoginManager.getInstance() != null) {
                    LoginManager.getInstance().logOut();
                }


                admobInterstitial = getSharedPreferences("admobInterstitial", MODE_PRIVATE);
                facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);

                String admobType = admobInterstitial.getString("admobInterstitial", "");
                String facebookType = facebookInterstitial.getString("facebookInterstitial", "");
//        if (admobType.equals("admobInterstitial")) {
                MobileAds.initialize(MainActivity.this, initializationStatus -> {
                });
                // Admob Interstitial Ads

//            mInterstitialAd = new InterstitialAd(this);
//            mInterstitialAd.setAdUnitId(admobInterstitial.getString("admobInterstitial", ""));
//
//
//            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                prepareInterstitialAdmobAd();
                ScheduledExecutorService schudeler = Executors.newSingleThreadScheduledExecutor();
                schudeler.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                if (mInterstitialAd.isLoaded()) {
//                                    mInterstitialAd.show();
//                                }
                            }
                        });
                    }
                }, 5, 1, TimeUnit.SECONDS);
                //    }
                // Facebook Interstitial
                facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);
                prepareInterstitialAd();


//        ScheduledExecutorService schudeler2 = Executors.newSingleThreadScheduledExecutor();
//
//        schudeler2.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (facebookInterstitialAd.isAdLoaded()) {
//                            facebookInterstitialAd.show();
//                        }
//                    }
//                });
//            }
//        }, 3, 1, TimeUnit.SECONDS);
                // Go To Login Page
                Intent loginPage = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(loginPage);
                finish();

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                }
            }
        });


        userSituation = getSharedPreferences("userEmail", MODE_PRIVATE);
        userEmail = userSituation.getString("userEmail", "");
        userName = userSituation.getString("name", "");

        mProfileImage = findViewById(R.id.imageView_profile);
        mUserName = findViewById(R.id.textView_name);
        mUserEmail = findViewById(R.id.textView_email);
        mUserBalance = findViewById(R.id.textView_balance);
        mTopEarnerRecyclerView = findViewById(R.id.recyclerView_top_earner);
        mBlogRecyclerView = findViewById(R.id.recyclerView_blog);
        mChatRoomButton = findViewById(R.id.btn_ChatRoom);

        mChatRoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                admobInterstitial = getSharedPreferences("admobInterstitial", MODE_PRIVATE);
                facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);

                String admobType = admobInterstitial.getString("admobInterstitial", "");
                String facebookType = facebookInterstitial.getString("facebookInterstitial", "");
//        if (admobType.equals("admobInterstitial")) {
                // MobileAds.initialize(MainActivity.this, getString(R.string.admob_app_id));
                // Admob Interstitial Ads

//            mInterstitialAd = new InterstitialAd(this);
//            mInterstitialAd.setAdUnitId(admobInterstitial.getString("admobInterstitial", ""));
//
//
//            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//                prepareInterstitialAdmobAd();
                ScheduledExecutorService schudeler = Executors.newSingleThreadScheduledExecutor();
                schudeler.scheduleAtFixedRate(new Runnable() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
//                                if (mInterstitialAd.isLoaded()) {
//                                    mInterstitialAd.show();
//                                }
                            }
                        });
                    }
                }, 5, 1, TimeUnit.SECONDS);
                //    }
                // Facebook Interstitial
                facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);
                prepareInterstitialAd();


//        ScheduledExecutorService schudeler2 = Executors.newSingleThreadScheduledExecutor();
//
//        schudeler2.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (facebookInterstitialAd.isAdLoaded()) {
//                            facebookInterstitialAd.show();
//                        }
//                    }
//                });
//            }
//        }, 3, 1, TimeUnit.SECONDS);
//        Intent chat = new Intent(this, ChatActivity.class);
//        startActivity(chat);
//        finish();
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
            }
        });

        playersAdapter = new PlayersAdapter(this, playerList);
        mTopEarnerRecyclerView.setAdapter(playersAdapter);

        blogAdapter = new BlogAdapter(blogList, this);
        mBlogRecyclerView.setAdapter(blogAdapter);

        admobInterstitial = getSharedPreferences("admobInterstitial", MODE_PRIVATE);
        MobileAds.initialize(MainActivity.this, initializationStatus -> {
        });
        // Facebook Interstitial
        facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);
        prepareInterstitialAd();

        // Native Admob
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        bottomBannerType = getSharedPreferences("bottomBannerType", MODE_PRIVATE);
        bannerBottomType = bottomBannerType.getString("bottomBannerType", "");
        if (bannerBottomType.equals("admob")) {
            MobileAds.initialize(MainActivity.this, initializationStatus -> {
            });
            admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
//            adsLinear = (LinearLayout) findViewById(R.id.banner_container_main_activity);
//            bannerAdmobAdView = new AdView(this);
//            bannerAdmobAdView.setAdUnitId(admobBanner.getString("admobBanner", ""));
//            bannerAdmobAdView.setAdSize(AdSize.FULL_BANNER);
//            adsLinear.addView(bannerAdmobAdView);
//            adsLinear.setGravity(Gravity.CENTER_HORIZONTAL);
//            AdRequest adRequest = new AdRequest.Builder().build();
//            bannerAdmobAdView.loadAd(adRequest);
//            bannerAdmobAdView.setAdListener(new AdListener(){
//                @Override
//                public void onAdLoaded() {
//                    adsLinear.setVisibility(View.VISIBLE);
//                }
//            });
        } else if (bannerBottomType.equals("facebook")) {
            facebookBanner = getSharedPreferences("facebookBanner", MODE_PRIVATE);
            AudienceNetworkAds.initialize(this);

//            com.facebook.ads.AdView facebookAdView = new com.facebook.ads.AdView(this, facebookBanner.getString("facebookBanner", null), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
//            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container_main_activity);
//            adContainer.addView(facebookAdView);
//            facebookAdView.loadAd();
        }

        getConnectedUserData();
        getTopPlayers();
        getBlogData();
        loadAdId();
        prepareInterstitialAdmobAd();
    }

    private void getBlogData() {
        blogList.add(new Blog("Best earning article for 2021. Click to read more...", getResources().getDrawable(R.drawable.news_image_2), "https://blog.gigg.me/"));
        blogList.add(new Blog("Best earning article for 2021. Click to read more...", getResources().getDrawable(R.drawable.news_2), "https://blog.gigg.me/"));
        blogAdapter.notifyDataSetChanged();
    }

    private void getConnectedUserData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.domain_name) + "/api/players/getplayerdata", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String _userName = jsonObject.getString("name");
                    String userEmail = jsonObject.getString("email");
                    points = jsonObject.getInt("score");
                    String userImageUrl = jsonObject.getString("image");
                    userId = String.valueOf(jsonObject.getInt("id"));
                    userImage = userImageUrl;
                    userSituation.edit().putString("userEmail", userEmail).apply();
                    userSituationId.edit().putString("userId", userId).apply();
                    userSituationId.edit().putString("name", _userName).apply();
                    mUserName.setText(_userName);
                    userName = _userName;
                    mUserEmail.setText(userEmail);
                    currentUserName.setText(_userName);
                    currentEmail.setText(userEmail);
                    Glide.with(getApplicationContext())
                            .load(userImageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(mProfileImage);
                    Glide.with(getApplicationContext())
                            .load(userImageUrl)
                            .diskCacheStrategy(DiskCacheStrategy.DATA)
                            .into(currentProfileImage);
                    getSettings();
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
                params.put("email", userEmail);
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

    private void getSettings() {
        String url = getResources().getString(R.string.domain_name) + "/api/settings/all";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject settings = jsonArray.getJSONObject(i);
                                String currency = settings.getString("currency");
                                double conversionRate = settings.getInt("conversion_rate");
                                final double balance = (double) (points / conversionRate);
                                mUserBalance.setText(currency + " " + String.format("%.2f", balance));
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
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void getTopPlayers() {
        String url = getResources().getString(R.string.domain_name) + "/api/players/top10";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject player = jsonArray.getJSONObject(i);
                                String name = player.getString("name");
                                String email = player.getString("email");
                                String imageUrl = player.getString("image");
                                String memberSince = player.getString("member_since");
                                int id = player.getInt("id");

                                int points = player.getInt("score");
                                playerList.add(new Player(id,name, email, memberSince, imageUrl, points));
                            }
                            playersAdapter.notifyDataSetChanged();
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
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void setUpOneSignal() {
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.DEBUG, OneSignal.LOG_LEVEL.DEBUG);
//        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
//
//        OneSignal.initWithContext(this);
//        OneSignal.setAppId(ONESIGNAL_APP_ID);
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
        if (id == R.id.drawer_test) {
            Toast.makeText(getApplicationContext(), "dsfksklf", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.statistics:
                Intent stats = new Intent(this, StatisticsActivity.class);
                startActivity(stats);

                finish();
                break;
            case R.id.instructions:
                Intent instr = new Intent(this, InstructionsActivity.class);
                startActivity(instr);
                finish();
                break;
            case R.id.categories:
                Intent cat = new Intent(this, CategoriesActivity.class);
                startActivity(cat);
                finish();
                break;
            case R.id.text_profile:
                Intent prof = new Intent(this, ProfileActivity.class);
                startActivity(prof);
                finish();
                break;
            case R.id.creative_profile:
                Intent cp = new Intent(this, CreativeProfileActivity.class);
                cp.putExtra("email", userEmail);
                startActivity(cp);
                break;
            case R.id.task_history:
                Intent history = new Intent(this, JobHistoryActivity.class);
                startActivity(history);
                break;
            case R.id.tasks:
                Intent tasks = new Intent(this, LiveTaskActivity.class);
                tasks.putExtra("type", true);
                startActivity(tasks);

//                if (preferenceManager.isSubscribed()) {
//                    Intent tasks = new Intent(this, LiveTaskActivity.class);
//                    tasks.putExtra("type", true);
//                    startActivity(tasks);
//                } else {
//                    startActivity(new Intent(this, SubcriptionActivity.class));
//                }
                break;
            case R.id.chat:
                Intent chat = new Intent(this, ChatActivity.class);
                chat.putExtra("id", userId);
                startActivity(chat);
                finish();
                break;
            case R.id.Offerwall:
                startActivity(new Intent(this, JobsActivity.class));

//                if (preferenceManager.isSubscribed()) {
//                    startActivity(new Intent(this, JobsActivity.class));
//                } else {
//                    startActivity(new Intent(this, SubcriptionActivity.class));
//                }
                break;
            case R.id.ranking:
                Intent leader = new Intent(this, LeaderboardsActivity.class);
                startActivity(leader);

                finish();
                break;
            case R.id.vault:
                Intent vault = new Intent(this, VaultActivity.class);
                startActivity(vault);
                break;
            case R.id.freelancerJobs:
                Intent intent = new Intent(this, MicroActivity.class);
                intent.putExtra("id", userId);
                startActivity(intent);
//                if (preferenceManager.isSubscribed()) {
//                    Intent intent = new Intent(this, MicroActivity.class);
//                    intent.putExtra("id", userId);
//                    startActivity(intent);
//                } else {
//                    startActivity(new Intent(this, SubcriptionActivity.class));
//                }
                break;
            case R.id.news:
                startActivity(new Intent(this, NewsActivity.class));

                finish();
                break;
            case R.id.privacy:
                Intent priv = new Intent(this, PrivacyActivity.class);
                startActivity(priv);

                finish();
                break;
            case R.id.terms_of_use:
                Intent terms = new Intent(this, TermsOfUseActivity.class);
                startActivity(terms);
                finish();
                break;
            case R.id.invite_friends:
                Intent inviteIntent = new Intent(this, InviteFriendsActivity.class);
                startActivity(inviteIntent);
                finish();
                break;
            case R.id.report:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Report a Bug");
                builder.setMessage("To report a bug or a problem in this application, please contact us via Email" + "\n\n Thank You!");
                builder.setPositiveButton("Send Email",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
                                emailSelectorIntent.setData(Uri.parse("mailto:"));
                                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
                                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.drawer_menu_report_bug));
                                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                                emailIntent.setSelector(emailSelectorIntent);
                                if (emailIntent.resolveActivity(getPackageManager()) != null)
                                    startActivity(emailIntent);
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
                break;
//            case R.id.spin2WinMenuBtn:
//                startActivity(new Intent(this, Spin2WinActivity.class));
//                finish();
//                break;
            case R.id.share:
                Intent intentP = new Intent(Intent.ACTION_SEND);
                intentP.setType("text/plain");
                intentP.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                intentP.putExtra(Intent.EXTRA_TEXT, "Download this APP From : http://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                startActivity(Intent.createChooser(intentP, "Share Now"));
                break;
            case R.id.contact_us:
                Intent emailSelectorIntent = new Intent(Intent.ACTION_SENDTO);
                emailSelectorIntent.setData(Uri.parse("mailto:"));
                final Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
                emailIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                emailIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                emailIntent.setSelector(emailSelectorIntent);
                if (emailIntent.resolveActivity(getPackageManager()) != null)
                    startActivity(emailIntent);
                break;
            case R.id.rate:
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)));
                break;
            case R.id.exit:
                finishAndRemoveTask();
                break;
            case R.id.video:
                Intent video = new Intent(this, VideoActivity.class);
                video.putExtra("id", userId);
                startActivity(video);
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void openDrawer(View view) {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            drawer.openDrawer(GravityCompat.START);
        }
    }

    public void openChatRoom(View view) {
        Toast.makeText(this, "click", Toast.LENGTH_SHORT).show();
        admobInterstitial = getSharedPreferences("admobInterstitial", MODE_PRIVATE);
        facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);

        String admobType = admobInterstitial.getString("admobInterstitial", "");
        String facebookType = facebookInterstitial.getString("facebookInterstitial", "");
//        if (admobType.equals("admobInterstitial")) {
        // MobileAds.initialize(MainActivity.this, getString(R.string.admob_app_id));
        // Admob Interstitial Ads

//            mInterstitialAd = new InterstitialAd(this);
//            mInterstitialAd.setAdUnitId(admobInterstitial.getString("admobInterstitial", ""));
//
//
//            mInterstitialAd.loadAd(new AdRequest.Builder().build());
//        prepareInterstitialAdmobAd();
        ScheduledExecutorService schudeler = Executors.newSingleThreadScheduledExecutor();
        schudeler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        /*if (mInterstitialAd.isLoaded()) {
                            mInterstitialAd.show();
                        } else {
                            Toast.makeText(MainActivity.this, "ads not loaded", Toast.LENGTH_SHORT).show();
                        }*/
                    }
                });
            }
        }, 5, 1, TimeUnit.SECONDS);
        //    }
        // Facebook Interstitial
        facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);
        prepareInterstitialAd();


//        ScheduledExecutorService schudeler2 = Executors.newSingleThreadScheduledExecutor();
//
//        schudeler2.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (facebookInterstitialAd.isAdLoaded()) {
//                            facebookInterstitialAd.show();
//                        }
//                    }
//                });
//            }
//        }, 3, 1, TimeUnit.SECONDS);
//        Intent chat = new Intent(this, ChatActivity.class);
//        startActivity(chat);
//        finish();
    }

    private void loadAdId() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, DataBaseApi.LOAD_AD, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    // Toast.makeText(MainActivity1.this, response, Toast.LENGTH_LONG).show();
                    JSONObject o = jsonObject.getJSONObject("data");
                    bannerAdID = o.getString("banner");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //             Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void prepareInterstitialAd() {
        AudienceNetworkAds.initialize(this);
        facebookInterstitialAd = new com.facebook.ads.InterstitialAd(this, facebookInterstitial.getString("facebookInterstitial", ""));
        facebookInterstitialAd.loadAd();
    }

    public void openNewsAll(View view) {
        NewsActivity.URL = "https://blog.gigg.me/";
        startActivity(new Intent(this, NewsActivity.class));
        finish();
    }

    public void openMicroJobs(View view) {

        Intent intent = new Intent(this, MicroActivity.class);
        intent.putExtra("id", userId);
        startActivity(intent);
//        if (preferenceManager.isSubscribed()) {
//
//            Intent intent = new Intent(this, MicroActivity.class);
//            intent.putExtra("id", userId);
//            startActivity(intent);
//            return;
//        } else {
//            startActivity(new Intent(this, SubcriptionActivity.class));
//        }
    }

    public void openHireMe(View view) {
        Intent intent = new Intent(this, ShareProfileActivity.class);
        startActivity(intent);
//        if (preferenceManager.isSubscribed()) {
//            Intent intent = new Intent(this, ShareProfileActivity.class);
//            startActivity(intent);
//        } else {
//            startActivity(new Intent(this, SubcriptionActivity.class));
//        }

//        Intent intent = new Intent(this, LiveTaskActivity.class);
//        intent.putExtra("type", true);
//        startActivity(intent);

        admobInterstitial = getSharedPreferences("admobInterstitial", MODE_PRIVATE);
        facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);

        String admobType = admobInterstitial.getString("admobInterstitial", "");
        String facebookType = facebookInterstitial.getString("facebookInterstitial", "");
//        if (admobType.equals("admobInterstitial")) {
        MobileAds.initialize(MainActivity.this, initializationStatus -> {
        });
        // Admob Interstitial Ads
//            mInterstitialAd = new InterstitialAd(this);
//            mInterstitialAd.setAdUnitId(admobInterstitial.getString("admobInterstitial", ""));

        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
        }
        prepareInterstitialAdmobAd();
        ScheduledExecutorService schudeler = Executors.newSingleThreadScheduledExecutor();
        schudeler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        if (mInterstitialAd.isLoaded()) {
//                            mInterstitialAd.show();
//                        }
                    }
                });
            }
        }, 5, 1, TimeUnit.SECONDS);
        //    }
        // Facebook Interstitial
        facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);
        prepareInterstitialAd();


//        ScheduledExecutorService schudeler2 = Executors.newSingleThreadScheduledExecutor();
//
//        schudeler2.scheduleAtFixedRate(new Runnable() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (facebookInterstitialAd.isAdLoaded()) {
//                            facebookInterstitialAd.show();
//                        }
//                    }
//                });
//            }
//        }, 3, 1, TimeUnit.SECONDS);

    }

    private void prepareInterstitialAdmobAd() {
        admobInterstitial = getSharedPreferences("admobInterstitial", MODE_PRIVATE);
        String interstitialId = admobInterstitial.getString("admobInterstitial", "");
        Log.d("interKey", "prepareInterstitialAdmobAd: " + interstitialId);


        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this,
                interstitialId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NotNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }

    public void openLiveTask(View view) {
        startActivity(new Intent(this, LiveTaskActivity.class));

//        if (preferenceManager.isSubscribed()) {
//            startActivity(new Intent(this, LiveTaskActivity.class));
//        } else {
//            startActivity(new Intent(this, SubcriptionActivity.class));
//        }
    }

    public void openEscrow(View view) {
        startActivity(new Intent(this, EscrowActivity.class));
    }

    public void openHire(View view) {
    }

    public void openHireCreative(View view) {
        startActivity(new Intent(this, JobCategoryActivity.class));
    }

    public void openSpin2Win(View view) {
        startActivity(new Intent(this, Spin2WinActivity.class));
    }

    public void openQuiz(View view) {
        Toast.makeText(this, "You have to score atleast 50% for your point to count.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, CategoriesActivity.class));
    }

    public void openInvest(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://invest.gigg.me/"));
        Intent chooseIntent = Intent.createChooser(intent, "Choose from below");
        startActivity(chooseIntent);
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
                                String rewardId = rewardVideoOption.getString("rewardVideoOption", "");
                                Log.d("rewardID", "onResponse: " + rewardId);
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

}