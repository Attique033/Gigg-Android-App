package app.gigg.me.app.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import app.gigg.me.app.Activity.freelance.SharedHelper;
import app.gigg.me.app.Activity.freelance.ui.CreativeProfileActivity;
import app.gigg.me.app.Activity.freelance.ui.JobCategoryActivity;
import app.gigg.me.app.Activity.freelance.ui.LiveTaskActivity;
import app.gigg.me.app.R;

public class SplashScreenActivity extends AppCompatActivity {
    private ProgressBar mProgressBar;
    private String url;
    private SharedPreferences admobBanner, admobNative, admobInterstitial, startAppID, admobVideo, facebookBanner, facebookNative, facebookInterstitial, bottomBannerType, userSituation;
    private RequestQueue queue;
    GoogleSignInClient mGoogleSignInClient;
    private String email;

    private SharedPreferences userSituationId;
    private static final String ONESIGNAL_APP_ID = "c4645c23-2326-4cf9-a9f5-55dd136ba580";
    private String userEmailStr;

    private boolean openLive, openMicro;

    private SharedHelper sharedHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        url = getString(R.string.domain_name);
        queue = Volley.newRequestQueue(this);
        userSituation = getSharedPreferences("userEmail", MODE_PRIVATE);
        userSituationId = getSharedPreferences("userId", MODE_PRIVATE);
        admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
        startAppID = getSharedPreferences("startAppID", MODE_PRIVATE);
        bottomBannerType = getSharedPreferences("bottomBannerType", MODE_PRIVATE);
        admobNative = getSharedPreferences("admobNative", MODE_PRIVATE);
        admobVideo = getSharedPreferences("admobVideo", MODE_PRIVATE);
        admobInterstitial = getSharedPreferences("admobInterstitial", MODE_PRIVATE);
        facebookBanner = getSharedPreferences("facebookBanner", MODE_PRIVATE);
        facebookNative = getSharedPreferences("facebookNative", MODE_PRIVATE);
        facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);

        userEmailStr = userSituation.getString("userEmail", "");
        mProgressBar = (ProgressBar) findViewById(R.id.splash_progressbar);
        Sprite foldingCube = new ThreeBounce();
        foldingCube.setColor(Color.parseColor("#456268"));
        mProgressBar.setIndeterminateDrawable(foldingCube);

        sharedHelper = new SharedHelper(this);

        int SPLASH_DISPLAY_LENGTH = 4000;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isNetworkConnected()) {
                    getAds();
                    getUserSituation();
                    getConnectedUserData();

                } else {
                    showAlertDialogConnectionProblem();
                }
            }
        }, SPLASH_DISPLAY_LENGTH);
    }

    private void getUserSituation() {

        if (!userEmailStr.equals("")) {
            final String userToCheck = userEmailStr;
            StringRequest stringRequest = new StringRequest(Request.Method.POST, url + "/api/players/situation", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("success");
                        // No Errors
                        if (success.equals("loggedSuccess")) {
                            if (email == null) {
                                if (openLive) {
                                    Intent homePage = new Intent(SplashScreenActivity.this, LiveTaskActivity.class);
                                    startActivity(homePage);
                                    finish();
                                } else if (openMicro) {
                                    Intent homePage = new Intent(SplashScreenActivity.this, MicroActivity.class);
                                    startActivity(homePage);
                                    finish();
                                } else {
                                    if (sharedHelper.getType() == 0) {
                                        Intent homePage = new Intent(SplashScreenActivity.this, JobCategoryActivity.class);
                                        startActivity(homePage);
                                        finish();
                                    } else {
                                        Intent homePage = new Intent(SplashScreenActivity.this, MainActivity.class);
                                        startActivity(homePage);
                                        finish();
                                    }
                                }
                            } else {
                                Intent profile = new Intent(SplashScreenActivity.this, CreativeProfileActivity.class);
                                profile.putExtra("email", email);
                                startActivity(profile);
                                finish();
                            }
                        }
                        if (success.equals("loggedError")) {
                            userSituation.edit().putString("userEmail", "").apply();
                            if (LoginManager.getInstance() != null) {
                                LoginManager.getInstance().logOut();
                            }

                            Intent registerPage = new Intent(SplashScreenActivity.this, LoginActivity.class);
                            startActivity(registerPage);
                            finish();
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
                    params.put("email", userToCheck);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(stringRequest);
        } else {
            Intent loginPage = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(loginPage);
            finish();
        }
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    public void showAlertDialogConnectionProblem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.splash_connection_problem_title));
        builder.setIcon(R.drawable.ic_wifi);
        builder.setMessage(getString(R.string.splash_connection_problem_message));
        builder.setPositiveButton(getString(R.string.splash_connection_problem_retry), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SplashScreenActivity.this.recreate();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    static public String rewardAD = "",initAD="";
    private void getAds() {
        String adsUrl = url + "/api/ads/all";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, adsUrl, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject ads = jsonArray.getJSONObject(i);
                                final String NativeAdId = ads.getString("admob_native");
                                final String AdmobVideoAdId = ads.getString("admob_video");
                                Log.e("AdmobRewardId",AdmobVideoAdId);
                                rewardAD = AdmobVideoAdId;
                                admobVideo.edit().putString("admobVideo", AdmobVideoAdId).apply();
                                final String BannerAdId = ads.getString("admob_banner");
                                final String InterstitialAdId = ads.getString("admob_interstitial");
                                initAD= InterstitialAdId;
                                final String facebookNativeAdId = ads.getString("fb_native");
                                final String facebookBannerAdId = ads.getString("fb_banner");
                                final String facebookInterstitialAdId = ads.getString("fb_interstitial");
                                final String BottomBannerType = ads.getString("bottom_banner_type");
                                admobBanner.edit().putString("admobBanner", BannerAdId).apply();
                                Log.e("BannerAdId",BannerAdId+"");
                                bottomBannerType.edit().putString("bottomBannerType", BottomBannerType).apply();
                                admobNative.edit().putString("admobNative", NativeAdId).apply();
                                // Insert Data from Server of StartAdApp ID
                                // For time Being I am hardCoding It
                                startAppID.edit().putString("startAppID", "203387611").apply();
                                admobInterstitial.edit().putString("admobInterstitial", InterstitialAdId).apply();
                                facebookBanner.edit().putString("facebookBanner", facebookBannerAdId).apply();
                                facebookNative.edit().putString("facebookNative", facebookNativeAdId).apply();
                                facebookInterstitial.edit().putString("facebookInterstitial", facebookInterstitialAdId).apply();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.e("ads id","failed "+e.getLocalizedMessage());
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
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(@NotNull PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                            if (deepLink != null && deepLink.getBooleanQueryParameter("email", false)) {
                                email = deepLink.getQueryParameter("email");
                            } else if (deepLink != null && String.valueOf(deepLink).contains("live")) {
                                openLive = true;
                            } else if (deepLink != null && String.valueOf(deepLink).contains("micro")) {
                                openMicro = true;
                            }
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                });
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
                params.put("email", userEmailStr);
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
