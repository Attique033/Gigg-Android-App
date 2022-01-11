package app.gigg.me.app.Manager;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.onesignal.OneSignal;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.config.CheckoutConfig;
import com.paypal.checkout.config.Environment;
import com.paypal.checkout.config.SettingsConfig;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.UserAction;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

import app.gigg.me.app.BuildConfig;
import co.paystack.android.PaystackSdk;

public class MyApplication extends Application {
    private static final String ONESIGNAL_APP_ID = "925f49ec-0e6d-4fda-a284-a4a425b16f51";
//    public static final String CLIENT_ID = "AfQZl3UMaA5N6G8zjIrkZD0BVzBPw0Y-FMHGMTOgowsODR9G1zEo-HltekrOPS48E1cvO7sqridk05dL";
    public static final String CLIENT_ID = "AYMs4UKd5hHY9jWpQ7Ga5lMjkkBGHPmqoFnz8f2Soi6o6MrofqFhc1UALp7S8btEpwGS0CyVsxzUmH6q";

    @Override
    public void onCreate() {
        super.onCreate();
        PaystackSdk.initialize(getApplicationContext());

        // Enable verbose OneSignal logging to debug issues if needed.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.NONE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(ONESIGNAL_APP_ID);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NotNull InitializationStatus initializationStatus) {
                Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
                for (String adapterClass : statusMap.keySet()) {
                    AdapterStatus status = statusMap.get(adapterClass);
                    Log.d("MyApp", String.format(
                            "Adapter name: %s, Description: %s, Latency: %d",
                            adapterClass, status.getDescription(), status.getLatency()));
                }

                // Start loading ads here...
            }
        });

        CheckoutConfig config = new CheckoutConfig(
                this,
                CLIENT_ID,
                Environment.LIVE,
                String.format("%s://paypalpay", BuildConfig.APPLICATION_ID),
                CurrencyCode.USD,
                UserAction.PAY_NOW,
                new SettingsConfig(false, false)
        );
        PayPalCheckout.setConfig(config);
    }

    private HttpProxyCacheServer proxy;

    public static HttpProxyCacheServer getProxy(Context context) {
        MyApplication app = (MyApplication) context.getApplicationContext();
        return app.proxy == null ? (app.proxy = app.newProxy()) : app.proxy;
    }

    private HttpProxyCacheServer newProxy() {
        return new HttpProxyCacheServer(this);
    }
}

