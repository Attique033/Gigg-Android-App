package app.gigg.me.app.Activity;

import static android.content.ContentValues.TAG;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;
import app.gigg.me.app.R;

import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.unity3d.ads.IUnityAdsListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
//import com.startapp.sdk.adsbase.StartAppAd;
//import com.startapp.sdk.adsbase.StartAppSDK;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import static app.gigg.me.app.Manager.Constants.AD_UNIT;
import static app.gigg.me.app.Manager.Constants.Ad_UNIT_INT;
import static app.gigg.me.app.Manager.Constants.TEST_MODE;
import static app.gigg.me.app.Manager.Constants.UNITY_ID;

public class Spin2WinActivity extends AppCompatActivity {
    private SharedPreferences mailID,identifier;
    private TextView pointsTextView;
    private CardView spinBtn;
    private String mail = "";
    private int userID;
    int apoints;
    String url;
    private SharedPreferences lastDateSpinned;
    private long lastDateSpinnerValue;
    private int currentReward;
    SharedPreferences mpref;
    private boolean canWin = true;
    private int i = 0;
    private InterstitialAd mInterstitialAd;
    private SharedPreferences startAppID , hashMapString;
    private SharedPreferences fiftyFiftyOption,facebookInterstitial, rewardVideoOption, questionTime, completedOption, admobInterstitial, facebookBanner, bottomBannerType, admobBanner, admobNative;

    //private StartAppAd startAppAd;
    private Dialog confirmDialog,insufficientBalanceDialog,congratsDialog,noReward;
    private int currentPoints;
    private TextView congratsText;
    private Button  continueConfirmBtn , cancelBtnConfirm , quizBtn , cancelBtnIn , spinCongratsContinueBtn,TryAgainBtn;
    private ImageView closeBtnConfirm, CloseBtnIn , spinCongratsCloseBtn,closeNoRewardButton;
    private HashMap<String , Integer> values;
    private ArrayList<Integer> canBeRewarded;
    int rewards[] = { 0 , 500 , 0 , 50 , 0 , 1000 , 0 , 200 , 0};

    private RelativeLayout banner_layout;
    private BannerView bannerView;


    private com.facebook.ads.InterstitialAd facebookInterstitialAd;



    private class UnityAdsListener implements IUnityAdsListener {

        public void onUnityAdsReady(String surfacingId) {
            // Implement functionality for an ad being ready to show.
        }

        @Override
        public void onUnityAdsStart(String surfacingId) {
            // Implement functionality for a user starting to watch an ad.
        }

        @Override
        public void onUnityAdsFinish(String surfacingId, UnityAds.FinishState finishState) {
            // Implement conditional logic for each ad completion status:
            if (finishState.equals(UnityAds.FinishState.COMPLETED)) {
                // Reward the user for watching the ad to completion.
            } else if (finishState == UnityAds.FinishState.SKIPPED) {
                // Do not reward the user for skipping the ad.
            } else if (finishState == UnityAds.FinishState.ERROR) {
            }
        }

        @Override
        public void onUnityAdsError(UnityAds.UnityAdsError unityAdsError, String s) {

        }
    }

    private UnityBannerListener unityBannerListener = new UnityBannerListener();

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

    @Override
    public void onBackPressed() {
        SharedPreferences.Editor edit = identifier.edit();
        edit.putInt("identifer", i);
        edit.apply();
        super.onBackPressed();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startAppID = getSharedPreferences("startAppID", MODE_PRIVATE);
        lastDateSpinned = getSharedPreferences("lastDateSpinnerd" , MODE_PRIVATE);
//        startAppAd = new StartAppAd(Spin2WinActivity.this);
//        startAppAd.loadAd();
        values = new HashMap<>();
        canBeRewarded = new ArrayList<>();

        hashMapString = getSharedPreferences("hashMapString",MODE_PRIVATE);

        lastDateSpinnerValue = lastDateSpinned.getLong("lastDateSpinned",0);
        Logic();
        url = getString(R.string.domain_name);
        getConnectedUserData1();



      //  StartAppSDK.init(this, startAppID.getString("startAppID",""), true);
        setContentView(R.layout.activity_spin2_win);
        pointsTextView = findViewById(R.id.pointsTextView);
        //Toolbar toolbar = findViewById(R.id.toolbar_invite);
        spinBtn = findViewById(R.id.spinBtn);
        initialisConfirmDialog();
        mailID = getSharedPreferences("userEmail",MODE_PRIVATE);
        identifier = getSharedPreferences("identifer",MODE_PRIVATE);
        i = identifier.getInt("identifier" , 0);
        mail = mailID.getString("userEmail","");

        //Unity AD
        UnityAdsListener myAdsListener = new UnityAdsListener();
        UnityAds.addListener(myAdsListener);

        banner_layout = findViewById(R.id.bannerView1);
        UnityAds.initialize (this, UNITY_ID, null, TEST_MODE, true);
        bannerView = new BannerView(this, AD_UNIT, new UnityBannerSize(320, 50));
        bannerView.setListener(unityBannerListener);
        banner_layout.addView(bannerView);
        bannerView.load();

        prepareInterstitialAdmobAd();

//        setSupportActionBar(toolbar);
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        final LuckyWheel wheel = findViewById(R.id.wheel);
        ArrayList<WheelItem> wheelItems = new ArrayList<>();
        wheelItems.add(new WheelItem(getColor(R.color.colorPrimary) , BitmapFactory.decodeResource(getResources(), R.drawable.black_circle) , "500 "));
        wheelItems.add(new WheelItem(getColor(R.color.colorAccent) , BitmapFactory.decodeResource(getResources(), R.drawable.black_circle) , "0 "));
        wheelItems.add(new WheelItem(getColor(R.color.colorPrimary) , BitmapFactory.decodeResource(getResources(), R.drawable.black_circle) , "50"));
        wheelItems.add(new WheelItem(getColor(R.color.colorAccent), BitmapFactory.decodeResource(getResources(), R.drawable.black_circle) , "0"));
        wheelItems.add(new WheelItem(getColor(R.color.colorPrimary), BitmapFactory.decodeResource(getResources(), R.drawable.black_circle) , "1000"));
        wheelItems.add(new WheelItem(getColor(R.color.colorAccent), BitmapFactory.decodeResource(getResources(), R.drawable.black_circle) , "0"));
        wheelItems.add(new WheelItem(getColor(R.color.colorPrimary) , BitmapFactory.decodeResource(getResources(), R.drawable.black_circle) , "200"));
        wheelItems.add(new WheelItem(getColor(R.color.colorAccent) , BitmapFactory.decodeResource(getResources(), R.drawable.black_circle) , "0"));
        wheel.addWheelItems(wheelItems);

        spinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.show();
            }
        });
        closeBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });
        cancelBtnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmDialog.dismiss();
            }
        });
        continueConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canBeRewarded.removeAll(canBeRewarded);
                getConnectedUserData1();

                if(currentPoints >= 100) {
                    playAudio();
                    decrementSpinPoints();
                    Logic();
                    int index =  getRandom();
                    currentReward = rewards[index];
                    if(rewards[index] > 0){
                        displayCongratsMessage(rewards[index]);
                    }
                    wheel.rotateWheelTo(index);
                }else {
                    insufficientBalanceDialog.show();
                }
                if (mInterstitialAd != null) {
                    mInterstitialAd.show(Spin2WinActivity.this);
                }
                prepareInterstitialAdmobAd();
                confirmDialog.dismiss();
            }
        });
        getConnectedUserData();
        wheel.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {
            @Override
            public void onReachTarget() {
                if(currentReward > 0){
                    IncreasePoints(currentReward);
                }else {
                    noReward.show();
                }
                lastDateSpinnerValue = lastDateSpinned.getLong("lastDateSpinned",0);
                if(System.currentTimeMillis() - lastDateSpinnerValue < (86400000) ){
                    if((int) Float.parseFloat(String.valueOf(values.get(String.valueOf(currentReward)))) > 0) {
                        int temp = (int) Float.parseFloat(String.valueOf(values.get(String.valueOf(currentReward))));
                        temp--;
                        values.put(String.valueOf(currentReward) , temp);
                        Gson gson = new Gson();
                        String json = gson.toJson(values);
                        hashMapString.edit().putString("hashMapString",json).apply();

                    }

                }else {
                    values.put("1000" , 5);
                    values.put("200" , 5);
                    values.put("500" , 5);
                    values.put("0" , 100);
                    values.put("50",5);
//                        values.put(1 , 5);
                    Gson gson = new Gson();
                    String json = gson.toJson(values);
                    hashMapString.edit().putString("hashMapString",json).commit();
                    long unix = System.currentTimeMillis();
                    lastDateSpinned.edit().putLong("lastDateSpinned",unix).commit();
                }

            }
        });

        admobInterstitial = getSharedPreferences("admobInterstitial", MODE_PRIVATE);
        facebookInterstitial = getSharedPreferences("facebookInterstitial", MODE_PRIVATE);

    }
    private void getConnectedUserData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url+"/api/players/getplayerdata", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    currentPoints = jsonObject.getInt("score");
                    userID = jsonObject.getInt("id");
                    pointsTextView.setText(String.valueOf(currentPoints));




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
                params.put("email", mail);
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
    private void initialisConfirmDialog(){
        noReward = new Dialog(Spin2WinActivity.this);
        noReward.setContentView(R.layout.spin_no_reward);
        noReward.setCancelable(false);
        closeNoRewardButton = noReward.findViewById(R.id.closeNoRewardButton);
        TryAgainBtn = noReward.findViewById(R.id.spinNoRewardTryAgain);
        noReward.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        noReward.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        TryAgainBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noReward.dismiss();
                confirmDialog.show();

                String admobType = admobInterstitial.getString("admobInterstitial", "");
                String facebookType = facebookInterstitial.getString("facebookInterstitial", "");
//        if (admobType.equals("admobInterstitial")) {
                // Admob Interstitial Ads

//            mInterstitialAd = new InterstitialAd(this);
//            mInterstitialAd.setAdUnitId(admobInterstitial.getString("admobInterstitial", ""));
//
//
//            mInterstitialAd.loadAd(new AdRequest.Builder().build());
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
            }
        });
        closeNoRewardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noReward.dismiss();
            }
        });

        confirmDialog = new Dialog(Spin2WinActivity.this);
        confirmDialog.setContentView(R.layout.spin_dialog_confirm);
        confirmDialog.setCancelable(false);
        closeBtnConfirm = confirmDialog.findViewById(R.id.closeBtn);
        continueConfirmBtn = confirmDialog.findViewById(R.id.spinContinueBtn);
        cancelBtnConfirm = confirmDialog.findViewById(R.id.spinCancelConfirmBtn);
        confirmDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        confirmDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        insufficientBalanceDialog = new Dialog(Spin2WinActivity.this);
        insufficientBalanceDialog.setContentView(R.layout.spin_insufficinet_dialog);
        insufficientBalanceDialog.setCancelable(false);
        CloseBtnIn = insufficientBalanceDialog.findViewById(R.id.closeBtn);
        quizBtn = insufficientBalanceDialog.findViewById(R.id.spinQuizBtn);
        cancelBtnIn = insufficientBalanceDialog.findViewById(R.id.spinCancelConfirmBtn);
        insufficientBalanceDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        insufficientBalanceDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        CloseBtnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UnityAds.isReady(Ad_UNIT_INT)) {
                    UnityAds.show(Spin2WinActivity.this, Ad_UNIT_INT);
                }
                insufficientBalanceDialog.dismiss();
            }
        });
        quizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Spin2WinActivity.this , MainActivity.class));
            }
        });
        cancelBtnIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (UnityAds.isReady(Ad_UNIT_INT)) {
                    UnityAds.show(Spin2WinActivity.this, Ad_UNIT_INT);
                }
                insufficientBalanceDialog.dismiss();
            }
        });
        congratsDialog = new Dialog(Spin2WinActivity.this);
        congratsDialog.setContentView(R.layout.spin_congrats_dialog);
        congratsDialog.setCancelable(false);
        spinCongratsCloseBtn = congratsDialog.findViewById(R.id.closeBtn);
        congratsText = congratsDialog.findViewById(R.id.congratsTextView);
        spinCongratsContinueBtn = congratsDialog.findViewById(R.id.spinCongratsContinueBtn);
        congratsDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        congratsDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        spinCongratsCloseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               congratsDialog.dismiss();
            }
        });
        spinCongratsContinueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                congratsDialog.dismiss();
            }
        });


    }
    public void IncreasePoints(final int points) {

        String updateUrl = getResources().getString(R.string.domain_name);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateUrl+"/api/players/"+userID+"/update", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Make quiz Completed
                getConnectedUserData();
                congratsDialog.show();

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
                params.put("points", String.valueOf(points));
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
    public void decrementSpinPoints() {

        String updateUrl = getResources().getString(R.string.domain_name);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateUrl+"/api/players/"+userID+"/update", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Make quiz Completed
                getConnectedUserData();

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
                params.put("points", "-100");
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
    public void displayCongratsMessage( int amount){
        if ( amount == 500) canWin = false;
        congratsText.setText(amount+" point has been Added to your Wallet");
    }
    public int getRandom(){
        if(i % 10 == 0) canWin = true;
        i++;
        Random rand = new Random();
        int range = 1 + rand.nextInt(8);
        if(canWin) {

            while (!canBeRewarded.contains(rewards[range])) {
                range = 1 + rand.nextInt(8);
            }
        }else {
            ArrayList<Integer> lostWin = new ArrayList<>();
            lostWin.add(0);
            lostWin.add(50);
            while (!lostWin.contains(rewards[range])) {
                range = 1 + rand.nextInt(8);
//            Toast.makeText(this, "Value -> " + rewards[range], Toast.LENGTH_SHORT).show();
            }
        }

        return range;
    }
    public void Logic(){
        canBeRewarded.removeAll(canBeRewarded);
        lastDateSpinnerValue = lastDateSpinned.getLong("lastDateSpinned",0);
        if(lastDateSpinnerValue == 0){
            values.put("1000" , 5);
            values.put("0" , 1000);
            values.put("200" , 5);
            values.put("500" , 5);
            values.put("50",5);

//            canBeRewarded.add(10);
            //  canBeRewarded.add(2);
            canBeRewarded.add(1000);
            canBeRewarded.add(0);
            canBeRewarded.add(50);
            canBeRewarded.add(200);
            canBeRewarded.add(500);


//            canBeRewarded.add(0);
        }else {
            String temp = hashMapString.getString("hashMapString","");
            Gson gson = new Gson();
            values = gson.fromJson(temp , HashMap.class);
            if(values != null){
                canBeRewarded.removeAll(canBeRewarded);
                if( (int) Float.parseFloat(String.valueOf(values.get("1000"))) > 0) {
                    canBeRewarded.add(1000);
                }
//                if(values.containsKey("1000") ){
//                    if(values.get("1000") > 0d){
//                        canBeRewarded.add(1000);
//                    }
//                }
//                String temp1 = String.valueOf(values.get("1000"));
//                float value3 = Float.parseFloat(temp1);
//                Toast.makeText(this, "Value is " + values.get("1000") + temp1 + "  Integer Value ->" + (int)value3, Toast.LENGTH_SHORT).show();
                if( (int) Float.parseFloat(String.valueOf(values.get("200"))) > 0) {
                    canBeRewarded.add(200);
                }
                if( (int) Float.parseFloat(String.valueOf(values.get("500"))) > 0) {
                    canBeRewarded.add(500);
                }
                if( (int) Float.parseFloat(String.valueOf(values.get("50"))) > 0) {
                    canBeRewarded.add(50);
                }


                canBeRewarded.add(0);

            }
        }
    }
    public void playAudio(){
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.wheel_effect);
        mp.start();
    }

    private void prepareInterstitialAd() {
        AudienceNetworkAds.initialize(this);
        facebookInterstitialAd = new com.facebook.ads.InterstitialAd(this, facebookInterstitial.getString("facebookInterstitial", ""));
        facebookInterstitialAd.loadAd();
    }

    private void prepareInterstitialAdmobAd() {
        admobInterstitial = getSharedPreferences("admobInterstitial", MODE_PRIVATE);
        String interstitialId = admobInterstitial.getString("admobInterstitial", "");

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
    private void getConnectedUserData1() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url+"/api/players/getplayerdata", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String userName = jsonObject.getString("name");
                    String userEmail = jsonObject.getString("email");
                    int apoints = jsonObject.getInt("score");


                    //add user points to shared preferences
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("MYLABEL", ""+apoints).apply();

                    ProfileActivity.Singleton a = ProfileActivity.Singleton.getInstance();
                    a.setUserPointsTransfer(apoints);

                    userID = jsonObject.getInt("id");
                    String userPoints = String.valueOf(apoints);
                    String userImageUrl = jsonObject.getString("image");
                    String userMemberSince = jsonObject.getString("member_since");
                    // Set Header User Infos
                    //Toast.makeText(getApplicationContext(), ""+apoints, Toast.LENGTH_SHORT).show();

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
                //params.put("email", spUserEmail);
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