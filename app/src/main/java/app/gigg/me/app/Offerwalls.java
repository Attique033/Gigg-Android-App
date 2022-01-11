package app.gigg.me.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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
import app.gigg.me.app.R;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.OfferwallListener;
import com.tapjoy.TJActionRequest;
import com.tapjoy.TJConnectListener;
import com.tapjoy.TJEarnedCurrencyListener;
import com.tapjoy.TJError;
import com.tapjoy.TJGetCurrencyBalanceListener;
import com.tapjoy.TJPlacement;
import com.tapjoy.TJPlacementListener;
import com.tapjoy.TJPlacementVideoListener;
import com.tapjoy.Tapjoy;
import com.tapjoy.TapjoyConnectFlag;
import com.tapjoy.TapjoyLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

public class Offerwalls extends AppCompatActivity  implements  TJGetCurrencyBalanceListener, TJPlacementListener, TJPlacementVideoListener{

    public static final String TAG = "TapjoyEasyApp";
    private static final String PREFERENCES = "easyapp";
    private static final String PLACEMENT_NAME_KEY = "placementName";

    private int userPointsInt;


    private String displayText = "";

    private TextView outputTextView;
    private Button Tapjoy_offer_wall;


    // Tapjoy Placements
    private TJPlacement directPlayPlacement;
    private TJPlacement examplePlacement;
    private TJPlacement offerwallPlacement;

    private boolean earnedCurrency = false;


    Button ironsource_offer_wall;

    private String url,spUserEmail, currency;
    private SharedPreferences completedOption,admobBanner,userSituation,facebookBanner, bottomBannerType, facebookInterstitial, admobInterstitial,userSituationId;
    private String userId, categoryId, categoryLevel, categoryName,passedTotalPoints;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offerwalls);


        userSituation = getSharedPreferences("userEmail",MODE_PRIVATE);
        userSituationId = getSharedPreferences("userId",MODE_PRIVATE);

        url = getResources().getString(R.string.domain_name);
        spUserEmail = userSituation.getString("userEmail", "");
        userId = userSituationId.getString("userId", "");
        completedOption = getSharedPreferences("completedOption", Context.MODE_PRIVATE);

        ironsource_offer_wall= findViewById(R.id.ironsource_offer_wall);
        outputTextView = (TextView) findViewById(R.id.textViewOutput);
        Tapjoy_offer_wall =  findViewById(R.id.Tapjoy_offer_wall);


        getConnectedUserData();


        Tapjoy_offer_wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                callShowOffers();
            }
        });

        ironsource_offer_wall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IronSource.showOfferwall("DefaultOfferWall");
            }
        });




        //Init Offerwall
        IronSource.init(this, "ce9db125", IronSource.AD_UNIT.OFFERWALL);

        IronSource.setOfferwallListener(new OfferwallListener() {
            /**
             * Invoked when there is a change in the Offerwall availability status.
             * @param - available - value will change to YES when Offerwall are available.
             * You can then show the offerwall by calling showOfferwall(). Value will *change to NO when Offerwall isn't available.
             */
            @Override
            public void onOfferwallAvailable(boolean isAvailable) {

            }
            /**
             * Invoked when the Offerwall successfully loads for the user, after calling the 'showOfferwall' method
             */
            @Override
            public void onOfferwallOpened() {
            }
            /**
             * Invoked when the method 'showOfferWall' is called and the OfferWall fails to load.
             * @param error - A IronSourceError Object which represents the reason of 'showOfferwall' failure.
             */
            @Override
            public void onOfferwallShowFailed(IronSourceError error) {
            }
            /**
             * Invoked each time the user completes an Offer.
             * Award the user with the credit amount corresponding to the value of the *‘credits’ parameter.
             * @param credits - The number of credits the user has earned.
             * @param totalCredits - The total number of credits ever earned by the user.
             * @param totalCreditsFlag - In some cases, we won’t be able to provide the exact
             * amount of credits since the last event (specifically if the user clears
             * the app’s data). In this case the ‘credits’ will be equal to the ‘totalCredits’, and this flag will be ‘true’.
             * @return boolean - true if you received the callback and rewarded the user, otherwise false.
             */
            @Override
            public boolean onOfferwallAdCredited(int credits, int totalCredits, boolean totalCreditsFlag) {

                updatePlayerPoints(totalCredits);
                return true;
            }
            /**
             * Invoked when the method 'getOfferWallCredits' fails to retrieve
             * the user's credit balance info.
             * @param error - A IronSourceError object which represents the reason of 'getOfferwallCredits' failure.
             * If using client-side callbacks to reward users, it is mandatory to return true on this event
             */
            @Override
            public void onGetOfferwallCreditsFailed(IronSourceError error) {
            }
            /**
             * Invoked when the user is about to return to the application after closing
             * the Offerwall.
             */
            @Override
            public void onOfferwallClosed() {
            }
        });





        connectToTapjoy();



    }

    /**
     * Attempts to connect to Tapjoy
     */
    private void connectToTapjoy() {
        // OPTIONAL: For custom startup flags.
        Hashtable<String, Object> connectFlags = new Hashtable<String, Object>();
        connectFlags.put(TapjoyConnectFlag.ENABLE_LOGGING, "true");

        // If you are not using Tapjoy Managed currency, you would set your own user ID here.
        //	connectFlags.put(TapjoyConnectFlag.USER_ID, "A_UNIQUE_USER_ID");

        // Connect with the Tapjoy server.  Call this when the application first starts.
        // REPLACE THE SDK KEY WITH YOUR TAPJOY SDK Key.
        String tapjoySDKKey = "u6SfEbh_TA-WMiGqgQ3W8QECyiQIURFEeKm0zbOggubusy-o5ZfXp33sTXaD";

	/*
		// To integrate InstallReceiver in your project , uncomment the following lines below
		EasyAppInstallReferrer installReferrer = new EasyAppInstallReferrer();
		installReferrer.init(this);
		*/

	/*	// To integrate Tapjoy InstallReferrer Client, uncomment the following line below
		Tapjoy.activateInstallReferrerClient(this);
	*/

		/*
		// Enable this to send device token to Tapjoy
		String deviceToken = FirebaseInstanceId.getInstance().getToken();
		if (deviceToken != null) {
			Tapjoy.setDeviceToken(deviceToken);
		}
		*/

        // NOTE: This is the only step required if you're an advertiser.
        Tapjoy.connect(this, tapjoySDKKey, connectFlags, new TJConnectListener() {
            @Override
            public void onConnectSuccess() {
                Offerwalls.this.onConnectSuccess();
            }

            @Override
            public void onConnectFailure() {
                Offerwalls.this.onConnectFail();
            }
        });
    }

    /**
     * Handles a successful connect to Tapjoy. Pre-loads direct play placement
     * and sets up Tapjoy listeners
     */
    public void onConnectSuccess() {
        updateTextInUI("Tapjoy SDK connected");

        // NOTE: Current activity can be explicitly set via: Tapjoy.setActivity(this);
        //
        // This is needed before making a TJPlacement requestContent() if supported
        // minSdkVersion<14 and if Tapjoy session tracking is not used via: Tapjoy.onActivityStart(this);
        //
        Tapjoy.setActivity(this);

        // Start preloading direct play event upon successful connect
        directPlayPlacement = Tapjoy.getPlacement("video_unit", (TJPlacementListener) this);

        // Set Video Listener to anonymous callback
        directPlayPlacement.setVideoListener(new TJPlacementVideoListener() {
            @Override
            public void onVideoStart(TJPlacement placement) {
                Log.i(TAG, "Video has started has started for: " + placement.getName());
            }

            @Override
            public void onVideoError(TJPlacement placement, String message) {
                Log.i(TAG, "Video error: " + message + " for " + placement.getName());
            }

            @Override
            public void onVideoComplete(TJPlacement placement) {
                Log.i(TAG, "Video has completed for: " + placement.getName());

                // Best Practice: We recommend calling getCurrencyBalance as often as possible so the user�s balance is always up-to-date.
                Tapjoy.getCurrencyBalance((TJGetCurrencyBalanceListener) Offerwalls.this);
            }

        });

        directPlayPlacement.requestContent();

        // NOTE:  The get/spend/award currency methods will only work if your virtual currency
        // is managed by Tapjoy.
        //
        // For NON-MANAGED virtual currency, Tapjoy.setUserID(...)
        // must be called after requestTapjoyConnect.

        // Setup listener for Tapjoy currency callbacks


        Tapjoy.setEarnedCurrencyListener(new TJEarnedCurrencyListener() {
            @Override
            public void onEarnedCurrency(String currencyName, int amount) {
                earnedCurrency = true;
                updateTextInUI("You've just earned " + amount + " " + currencyName);
                showPopupMessage("You've just earned " + amount + " " + currencyName);

                updatePlayerPoints(amount);


            }
        });
    }

    /**
     * Handles a failed connect to Tapjoy
     */
    public void onConnectFail() {
        Log.e(TAG, "Tapjoy connect call failed");
        updateTextInUI("Tapjoy connect failed!");
    }

    private void updateTextInUI(final String text) {
        displayText = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (outputTextView != null) {
                    outputTextView.setText(text);
                }
            }
        });
    }


    private void showPopupMessage(final String text) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });
    }


    private void callShowOffers() {
        // NOTE: Current activity can be explicitly set via: Tapjoy.setActivity(this);
        //
        // This is needed before making a TJPlacement requestContent() if supported
        // minSdkVersion<14 and if Tapjoy session tracking is not used via: Tapjoy.onActivityStart(this);
        //
        Tapjoy.setActivity(this);

        // Construct TJPlacement to show Offers web view from where users can download the latest offers for virtual currency.
        offerwallPlacement = Tapjoy.getPlacement("offerwall_unit", new TJPlacementListener() {
            @Override
            public void onRequestSuccess(TJPlacement placement) {
                updateTextInUI("onRequestSuccess for placement " + placement.getName());

                if (!placement.isContentAvailable()) {
                    updateTextInUI("No Offerwall content available");
                }

               // setButtonEnabledInUI(currentButton, true);
            }

            @Override
            public void onRequestFailure(TJPlacement placement, TJError error) {

                updateTextInUI("Offerwall error: " + error.message);
            }

            @Override
            public void onContentReady(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentReady for placement " + placement.getName());

                updateTextInUI("Offerwall request success");
                placement.showContent();
            }

            @Override
            public void onContentShow(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentShow for placement " + placement.getName());
            }

            @Override
            public void onContentDismiss(TJPlacement placement) {
                TapjoyLog.i(TAG, "onContentDismiss for placement " + placement.getName());
            }

            @Override
            public void onPurchaseRequest(TJPlacement placement, TJActionRequest request, String productId) {
            }

            @Override
            public void onRewardRequest(TJPlacement placement, TJActionRequest request, String itemId, int quantity) {
            }

            @Override
            public void onClick(TJPlacement placement) {
                TapjoyLog.i(TAG, "onClick for placement " + placement.getName());
            }
        });

        // Add this class as a video listener
        offerwallPlacement.setVideoListener((TJPlacementVideoListener) this);
        offerwallPlacement.requestContent();
    }




    //================================================================================
    // TapjoyListener Methods
    //================================================================================
    @Override
    public void onGetCurrencyBalanceResponse(String currencyName, int balance) {
        Log.i(TAG, "currencyName: " + currencyName);
        Log.i(TAG, "balance: " + balance);

        if (earnedCurrency) {
            updateTextInUI(displayText + "\n" + currencyName + ": " + balance);
            earnedCurrency = false;
        } else {
            updateTextInUI(currencyName + ": " + balance);
        }

    }

    @Override
    public void onGetCurrencyBalanceResponseFailure(String error) {
        updateTextInUI("getCurrencyBalance error: " + error);

    }

    /*
     * TJPlacement callbacks
     */
    @Override
    public void onRequestSuccess(TJPlacement placement) {
        // If content is not available you can note it here and act accordingly as best suited for your app
        Log.i(TAG, "Tapjoy on request success, contentAvailable: " + placement.isContentAvailable());
    }

    @Override
    public void onRequestFailure(TJPlacement placement, TJError error) {
        Log.i(TAG, "Tapjoy send event " + placement.getName() + " failed with error: " + error.message);
    }

    @Override
    public void onContentReady(TJPlacement placement) {
    }

    @Override
    public void onContentShow(TJPlacement placement) {
    }

    @Override
    public void onContentDismiss(TJPlacement placement) {
        Log.i(TAG, "Tapjoy direct play content did disappear");



        // Best Practice: We recommend calling getCurrencyBalance as often as possible so the user's balance is always up-to-date.
        Tapjoy.getCurrencyBalance(Offerwalls.this);

        // NOTE: Current activity can be explicitly set via: Tapjoy.setActivity(this);
        //
        // This is needed before making a TJPlacement requestContent() if supported
        // minSdkVersion<14 and if Tapjoy session tracking is not used via: Tapjoy.onActivityStart(this);
        //
        Tapjoy.setActivity(this);

        // Begin preloading the next placement after the previous one is dismissed
        directPlayPlacement = Tapjoy.getPlacement("video_unit", this);

        // Set Video Listener to anonymous callback
        directPlayPlacement.setVideoListener(new TJPlacementVideoListener() {
            @Override
            public void onVideoStart(TJPlacement placement) {
                Log.i(TAG, "Video has started has started for: " + placement.getName());
            }

            @Override
            public void onVideoError(TJPlacement placement, String errorMessage) {
                Log.i(TAG, "Video error: " + errorMessage +  " for " + placement.getName());
            }

            @Override
            public void onVideoComplete(TJPlacement placement) {
                Log.i(TAG, "Video has completed for: " + placement.getName());

                // Best Practice: We recommend calling getCurrencyBalance as often as possible so the user�s balance is always up-to-date.
                Tapjoy.getCurrencyBalance(Offerwalls.this);
            }
        });

        directPlayPlacement.requestContent();
    }

    @Override
    public void onPurchaseRequest(TJPlacement placement, TJActionRequest request, String productId) {
    }

    @Override
    public void onRewardRequest(TJPlacement placement, TJActionRequest request, String itemId, int quantity) {
    }

    @Override
    public void onClick(TJPlacement placement) {
        TapjoyLog.i(TAG, "onClick for direct play placement " + placement.getName());
    }

    /**
     * Video listener callbacks
     */
    @Override
    public void onVideoStart(TJPlacement placement) {
        Log.i(TAG, "Video has started has started for: " + placement.getName());
    }

    @Override
    public void onVideoError(TJPlacement placement, String errorMessage) {
        Log.i(TAG, "Video error: " + errorMessage +  " for " + placement.getName());
    }

    @Override
    public void onVideoComplete(TJPlacement placement) {
        Log.i(TAG, "Video has completed for: " + placement.getName());

        // Best Practice: We recommend calling getCurrencyBalance as often as possible so the user�s balance is always up-to-date.
        Tapjoy.getCurrencyBalance(Offerwalls.this);
    }



    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }


    private void getConnectedUserData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url+"/api/players/getplayerdata", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    userPointsInt = jsonObject.getInt("score");
                    String userPoints = String.valueOf(userPointsInt);
                    String referral = jsonObject.getString("referral_code");

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
                params.put("email", spUserEmail);
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


    // Change Player Points
    public void updatePlayerPoints(int point) {


        userPointsInt = userPointsInt+point;


        String updateUrl = getResources().getString(R.string.domain_name);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, updateUrl+"/api/players/"+ userId +"/update", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Make quiz Completed
                if(completedOption.getString("completedOption", "").equals("yes")) {

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
                params.put("points", userPointsInt+"");
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