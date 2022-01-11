package app.gigg.me.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.android.billingclient.api.Purchase;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdView;
import com.pollfish.Pollfish;
import com.pollfish.builder.Params;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJError;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import app.gigg.me.app.Adapter.TaskAdapter;
import app.gigg.me.app.Manager.PreferenceManager;
import app.gigg.me.app.Manager.PurchaseHelper;
import app.gigg.me.app.Model.Subscription;
import app.gigg.me.app.Model.Task;
import app.gigg.me.app.R;

public class MicroActivity extends AppCompatActivity implements TJPlacementListener {

    private final List<Task> taskList = new ArrayList<>();
    private String user_id;
    private TaskAdapter taskAdapter;
    private PurchaseHelper purchaseHelper;
    private PreferenceManager preferenceManager;
    private TJPlacement tjPlacement;
    private Params params;

    private SharedPreferences sharedPreferences, bottomBannerType, startAppID, admobInterstitial, facebookInterstitial, facebookBanner, admobBanner, questionTime, userSituationId, completedOption;
    String categoryId, categoryName, bannerBottomType;
    private LinearLayout adsLinear;
    private AdView bannerAdmobAdView;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_micro);


        user_id = getIntent().getStringExtra("id");

        facebookBanner = getSharedPreferences("facebookBanner", Context.MODE_PRIVATE);
        admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
        adsLinear = (LinearLayout) findViewById(R.id.banner_container);
        bannerAdmobAdView = new AdView(MicroActivity.this);
        bannerAdmobAdView.setAdUnitId(admobBanner.getString("admobBanner", ""));
        bottomBannerType = getSharedPreferences("bottomBannerType", MODE_PRIVATE);
        bannerBottomType = bottomBannerType.getString("bottomBannerType", "");

        userSituationId = getSharedPreferences("userId", MODE_PRIVATE);
        user_id = userSituationId.getString("userId", "");

        if (bannerBottomType.equals("facebook")) {
            facebookBanner = getSharedPreferences("facebookBanner", MODE_PRIVATE);
            AudienceNetworkAds.initialize(this);

            com.facebook.ads.AdView facebookAdView = new com.facebook.ads.AdView(this, facebookBanner.getString("facebookBanner", null), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.bannerView10);
            adContainer.addView(facebookAdView);
            facebookAdView.loadAd();
        }

        RecyclerView recyclerView = findViewById(R.id.recyclerView_task);
        taskAdapter = new TaskAdapter(taskList, this);
        recyclerView.setAdapter(taskAdapter);


        setData();

        purchaseHelper = PurchaseHelper.getInstance(getApplication());
        getLifecycle().addObserver(purchaseHelper);

        preferenceManager = new PreferenceManager(this);

        Tapjoy.setActivity(this);

        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "false"); // Disable this in production builds
        Tapjoy.connect(getApplicationContext(), "Nx1DEFLOS6qiSrHMSifoiQECPItJYahwCnTxEMfmSS8jG9APpSWMe7vkWrO3", connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                initTapJoy();
            }

            @Override
            public void onConnectFailure() {
            }
        });

        params = new Params.Builder("24bf41c4-6a85-45a2-8cc9-9ace22f097f3")
                .offerwallMode(true)
                .build();

        Pollfish.initWith(this, params);

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

        taskAdapter.setOnItemClickListener(task -> {
            String task_name = task.getTitle();
            if (task_name.equalsIgnoreCase("App Tasks")) {
                startIntent("http://quiz.earnhq.online/offerwalls/offertoro/");
            } else if (task_name.equalsIgnoreCase("Game Tasks")) {
                startIntent("http://quiz.earnhq.online/offerwalls/cpalead/");
            } else if (task_name.equalsIgnoreCase("Video Tasks")) {
//                if (preferenceManager.isSubscribed()) {
                    Intent intent = new Intent(getApplicationContext(), VideoActivity.class);
//                    intent.putExtra("id", user_id);
//                    startActivity(intent);`
//                } else {
//                startActivity(new Intent(getApplicationContext(), SubcriptionActivity.class));
//                }
            } else if (task_name.equalsIgnoreCase("Form Tasks")) {
                startIntent("http://quiz.earnhq.online/offerwalls/kiwiwall/");
            } else if (task_name.equalsIgnoreCase("Digital Tasks")) {
                startIntent("http://quiz.earnhq.online/offerwalls/personaly/");
            } else if (task_name.equalsIgnoreCase("Install Tasks")) {
                startIntent("http://quiz.earnhq.online/offerwalls/ayetstudio/");
            } else if (task_name.equalsIgnoreCase("Freelance Tasks")) {
                startIntent("http://quiz.earnhq.online/offerwalls/adgem/");
            } else if (task_name.equalsIgnoreCase("Remote Tasks")) {
                startIntent("http://quiz.earnhq.online/offerwalls/clixwall/");
            } else if (task_name.equalsIgnoreCase("Micro Tasks")) {
                startIntent("http://quiz.earnhq.online/offerwalls/mediumpath/");
            } else if (task_name.equalsIgnoreCase("Gaming Tasks")) {
                if (tjPlacement != null && tjPlacement.isContentReady()) {
                    tjPlacement.showContent();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Pollfish.initWith(this, params);
    }

    private void initTapJoy() {
        Tapjoy.setUserID(user_id);
        tjPlacement = Tapjoy.getPlacement("OfferWall", this);
        if (Tapjoy.isConnected()) {
            tjPlacement.requestContent();
        } else {
            Log.d("MyApp", "Tapjoy SDK must finish connecting before requesting content.");
        }
    }

    private void startIntent(String url) {
        Intent intent = new Intent(getApplicationContext(), OfferwallWebActivity.class);
        intent.putExtra("link", makeUrl(url));
        startActivity(intent);
    }

    private String makeUrl(String url) {
        return url + user_id + "/show";
    }

    private void setData() {
        taskList.add(new Task(getResources().getDrawable(R.drawable.app_task), "App Tasks"));
        taskList.add(new Task(getResources().getDrawable(R.drawable.game_task), "Game Tasks"));
        taskList.add(new Task(getResources().getDrawable(R.drawable.video_task), "Video Tasks"));
        taskList.add(new Task(getResources().getDrawable(R.drawable.form_task), "Form Tasks"));
        taskList.add(new Task(getResources().getDrawable(R.drawable.game_task), "Gaming Tasks"));
        taskList.add(new Task(getResources().getDrawable(R.drawable.instant_task), "Install Tasks"));
        taskList.add(new Task(getResources().getDrawable(R.drawable.freelance_task), "Freelance Tasks"));
        taskList.add(new Task(getResources().getDrawable(R.drawable.remote_task), "Remote Tasks"));
        taskList.add(new Task(getResources().getDrawable(R.drawable.micro_ic_task), "Micro Tasks"));
        taskList.add(new Task(getResources().getDrawable(R.drawable.digital_task), "Digital Tasks"));
        taskAdapter.notifyDataSetChanged();
    }

    public void goBack(View view) {
        onBackPressed();
    }

    @Override
    public void onRequestSuccess(TJPlacement tjPlacement) {

    }

    @Override
    public void onRequestFailure(TJPlacement tjPlacement, TJError tjError) {

    }

    @Override
    public void onContentReady(TJPlacement tjPlacement) {

    }

    @Override
    public void onContentShow(TJPlacement tjPlacement) {

    }

    @Override
    public void onContentDismiss(TJPlacement tjPlacement) {

    }

    @Override
    public void onPurchaseRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s) {

    }

    @Override
    public void onRewardRequest(TJPlacement tjPlacement, TJActionRequest tjActionRequest, String s, int i) {

    }

    @Override
    public void onClick(TJPlacement tjPlacement) {

    }
}