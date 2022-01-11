package app.gigg.me.app.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import app.gigg.me.app.Activity.freelance.ui.ShareProfileActivity;
import app.gigg.me.app.R;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import static app.gigg.me.app.Manager.Constants.AD_UNIT;
import static app.gigg.me.app.Manager.Constants.TEST_MODE;
import static app.gigg.me.app.Manager.Constants.UNITY_ID;

public class NewsActivity extends AppCompatActivity {
    private WebView webView;
    private ProgressBar progressBar;
    private SharedPreferences facebookBanner,startAppID;
    public static String URL = "http://blog.earnhq.online/";
    private AdView adView;
    private ImageView backBtn ;

    private RelativeLayout banner_layout;
    private BannerView bannerView;
    private SharedPreferences bottomBannerType, admobInterstitial, facebookInterstitial, admobBanner, questionTime, userSituationId, completedOption;

    String categoryId, categoryName, userId, bannerBottomType;
    private LinearLayout adsLinear;
    private com.google.android.gms.ads.AdView bannerAdmobAdView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startAppID = getSharedPreferences("startAppID", MODE_PRIVATE);
        setContentView(R.layout.activity_news);
        facebookBanner = getSharedPreferences("facebookBanner",MODE_PRIVATE);
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.progressBar);
        AudienceNetworkAds.initialize(this);

        adView = new AdView(this, facebookBanner.getString("facebookBanner" , ""), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        // Add the ad view to your activity layout
        adContainer.addView(adView);

        banner_layout = findViewById(R.id.bannerView5);
        UnityAds.initialize (this, UNITY_ID, null, TEST_MODE, true);
        bannerView = new BannerView(this, AD_UNIT, new UnityBannerSize(320, 50));
        banner_layout.addView(bannerView);
        bannerView.load();

        // Request an ad
        adView.loadAd();

        backBtn = findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(NewsActivity.this , MainActivity.class));
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(URL);
        progressBar.setVisibility(View.VISIBLE);



        facebookBanner = getSharedPreferences("facebookBanner", Context.MODE_PRIVATE);
        admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
        adsLinear = (LinearLayout) findViewById(R.id.banner_container2);
        bannerAdmobAdView = new com.google.android.gms.ads.AdView(NewsActivity.this);
        bannerAdmobAdView.setAdUnitId(admobBanner.getString("admobBanner", ""));
        bottomBannerType = getSharedPreferences("bottomBannerType",MODE_PRIVATE);
        bannerBottomType = bottomBannerType.getString("bottomBannerType", "");

        if(bannerBottomType.equals("facebook")) {
            facebookBanner = getSharedPreferences("facebookBanner",MODE_PRIVATE);
            AudienceNetworkAds.initialize(this);

            com.facebook.ads.AdView facebookAdView = new com.facebook.ads.AdView(this, facebookBanner.getString("facebookBanner", null), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainers = (LinearLayout) findViewById(R.id.bannerView10);
            adContainers.addView(facebookAdView);
            facebookAdView.loadAd();
        }

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.VISIBLE);
                super.onPageStarted(view, url, favicon);
            }
        });


    }
}