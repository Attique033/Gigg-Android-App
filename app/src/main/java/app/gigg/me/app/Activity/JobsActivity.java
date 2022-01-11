package app.gigg.me.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import app.gigg.me.app.Adapter.ViewPagerAdapter;
import app.gigg.me.app.R;
import com.facebook.ads.AdSize;
import com.facebook.ads.AdView;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.material.tabs.TabLayout;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import static app.gigg.me.app.Manager.Constants.AD_UNIT;
import static app.gigg.me.app.Manager.Constants.TEST_MODE;
import static app.gigg.me.app.Manager.Constants.UNITY_ID;
//import com.startapp.sdk.adsbase.StartAppSDK;

public class JobsActivity extends AppCompatActivity {
    private ViewPager viewPager ;
    private SharedPreferences facebookBanner,startAppID;
    private AdView adView;
    private ImageView backBtn ;
    private RelativeLayout banner_layout;
    private BannerView bannerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        startAppID = getSharedPreferences("startAppID",MODE_PRIVATE);
//        StartAppSDK.init(this, startAppID.getString("startAppID",""), true);
        setContentView(R.layout.activity_jobs);

        facebookBanner = getSharedPreferences("facebookBanner",MODE_PRIVATE);
        AudienceNetworkAds.initialize(this);

        adView = new AdView(this, facebookBanner.getString("facebookBanner" , ""), AdSize.BANNER_HEIGHT_50);

        // Find the Ad Container
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

        banner_layout = findViewById(R.id.bannerView6);
        UnityAds.initialize (this, UNITY_ID, null, TEST_MODE, true);
        bannerView = new BannerView(this, AD_UNIT, new UnityBannerSize(320, 50));
        banner_layout.addView(bannerView);
        bannerView.load();

        // Add the ad view to your activity layout
        adContainer.addView(adView);
        backBtn = findViewById(R.id.backbtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JobsActivity.this , MainActivity.class));
            }
        });
        // Request an ad
        adView.loadAd();

        viewPager = findViewById(R.id.container);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));
    }
}