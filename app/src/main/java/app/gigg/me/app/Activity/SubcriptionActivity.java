package app.gigg.me.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.SkuDetails;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import app.gigg.me.app.Adapter.SubcriptionAdapter;
import app.gigg.me.app.Manager.PreferenceManager;
import app.gigg.me.app.Manager.PurchaseHelper;
import app.gigg.me.app.Model.Subcription;
import app.gigg.me.app.Model.Subscription;
import app.gigg.me.app.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SubcriptionActivity extends AppCompatActivity {

    private RecyclerView recyclerView_subcriptions;
    private SubcriptionAdapter subcriptionAdapter;
    private List<Subcription> subcriptionList = new ArrayList<>();

    private PurchaseHelper purchaseHelper;
    public static Subcription subcription = null;
    private MutableLiveData<Map<String, SkuDetails>> skusWithSkuDetails;

    private PreferenceManager preferenceManager;

    private SharedPreferences sharedPreferences,bottomBannerType,startAppID, admobInterstitial, facebookInterstitial, facebookBanner, admobBanner, questionTime, userSituationId, completedOption;

    String categoryId, categoryName, userId, bannerBottomType;
    private LinearLayout adsLinear;
    private AdView bannerAdmobAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcription);



        facebookBanner = getSharedPreferences("facebookBanner", Context.MODE_PRIVATE);
        admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
        adsLinear = (LinearLayout) findViewById(R.id.banner_container);
        bannerAdmobAdView = new AdView(SubcriptionActivity.this);
        bannerAdmobAdView.setAdUnitId(admobBanner.getString("admobBanner", ""));
        bottomBannerType = getSharedPreferences("bottomBannerType",MODE_PRIVATE);
        bannerBottomType = bottomBannerType.getString("bottomBannerType", "");

        if(bannerBottomType.equals("facebook")) {
            facebookBanner = getSharedPreferences("facebookBanner",MODE_PRIVATE);
            AudienceNetworkAds.initialize(this);

            com.facebook.ads.AdView facebookAdView = new com.facebook.ads.AdView(this, facebookBanner.getString("facebookBanner", null), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.bannerView10);
            adContainer.addView(facebookAdView);
            facebookAdView.loadAd();
        }




        recyclerView_subcriptions = findViewById(R.id.recyclerView_subcriptions);
        subcriptionAdapter = new SubcriptionAdapter(subcriptionList, this);
        recyclerView_subcriptions.setAdapter(subcriptionAdapter);

        purchaseHelper = PurchaseHelper.getInstance(getApplication());
        getLifecycle().addObserver(purchaseHelper);
        skusWithSkuDetails = purchaseHelper.skusWithSkuDetails;

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
                                onBackPressed();
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


        subcriptionList.add(new Subcription(
                1,
                "Economy:",
                "POPULAR",
                "9.99",
                "2.99/weekly",
                "#15CC79",
                "economy_subscription",
                "● Monthly Withdraw"
        ));

        subcriptionList.add(new Subcription(
                2,
                "Premium:",
                "BEST VALUE",
                "15.99",
                "5.99/monthly",
                "#456168",
                "premium_subscription",
                "● Weekly Withdraw"
        ));

        subcriptionAdapter.notifyDataSetChanged();
    }

    public void goBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public static void buyPlan(Subcription sub) {
        subcription = sub;
    }

    public void proceedPay(View view) {
        Subscription subscription = preferenceManager.getPurchase();
        if (subscription.isSubscribed()) {
            if (subscription.getSKU().equals(PurchaseHelper.BASIC_SKU)) {
                //Have Basic
                Toast.makeText(this, "You have already a Basic Subscription", Toast.LENGTH_SHORT).show();
            } else if (subscription.getSKU().equals(PurchaseHelper.PREMIUM_SKU)) {
                //Have Premium
                Toast.makeText(this, "You have already a Premium Subscription", Toast.LENGTH_SHORT).show();
            } else {
                //Have no
            }
        } else {
            SkuDetails skuDetails = skusWithSkuDetails.getValue().get(subcription.getSKU());
            BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(skuDetails)
                    .build();
            purchaseHelper.launchBillingFlow(this, flowParams);
        }
    }

}