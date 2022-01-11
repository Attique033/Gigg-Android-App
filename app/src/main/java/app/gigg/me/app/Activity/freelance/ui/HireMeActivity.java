package app.gigg.me.app.Activity.freelance.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.hbb20.CountryCodePicker;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.paypal.checkout.PayPalCheckout;
import com.paypal.checkout.approve.Approval;
import com.paypal.checkout.approve.OnApprove;
import com.paypal.checkout.cancel.OnCancel;
import com.paypal.checkout.createorder.CreateOrder;
import com.paypal.checkout.createorder.CreateOrderActions;
import com.paypal.checkout.createorder.CurrencyCode;
import com.paypal.checkout.createorder.OrderIntent;
import com.paypal.checkout.createorder.UserAction;
import com.paypal.checkout.error.ErrorInfo;
import com.paypal.checkout.error.OnError;
import com.paypal.checkout.order.Amount;
import com.paypal.checkout.order.AppContext;
import com.paypal.checkout.order.CaptureOrderResult;
import com.paypal.checkout.order.OnCaptureComplete;
import com.paypal.checkout.order.Order;
import com.paypal.checkout.order.PurchaseUnit;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import app.gigg.me.app.Activity.MainActivity;
import app.gigg.me.app.Activity.TextActivity;
import app.gigg.me.app.Activity.freelance.model.CalculateAge;
import app.gigg.me.app.Activity.freelance.model.ProfileSetting;
import app.gigg.me.app.Activity.freelance.model.ServerResponse;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.R;

public class HireMeActivity extends AppCompatActivity implements CardFragment.CardFragmentInterface {

    private static final int PICK_IMAGE_REQUEST = 1002;
    private LinearLayout mPayPal, mPaymentLayout;
    private EditText mAmount;
    private TextView mPaid;
    private LinearLayout mVisa, mMaster;
    private String email;
    private RelativeLayout mSubmit;
    private TextView mTitle, mDescription;
    private boolean isPaid;
    private RelativeLayout mUpload;
    private Uri picUri;
    private ProgressDialog dialog;
    private String emplyoeeEmail;
    private SharedPreferences sharedPreferences;
    private CountryCodePicker countryCodePicker;
    TextView upload_text;
    private SharedPreferences bottomBannerType, startAppID, admobInterstitial, facebookInterstitial, facebookBanner, admobBanner, questionTime, userSituationId, completedOption;

    String categoryId, categoryName, bannerBottomType;
    private LinearLayout adsLinear;
    private AdView bannerAdmobAdView;

    private ImageView backButton;

    private ImageView mProfile;
    private TextView mName;
    private TextView mSchool;
    private TextView mCountry;
    private ImageView mFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire_me);

        email = getIntent().getStringExtra("email");
        sharedPreferences = getSharedPreferences("userEmail", MODE_PRIVATE);
        emplyoeeEmail = sharedPreferences.getString("userEmail", "");
        mAmount = findViewById(R.id.amount_text);
        mPaymentLayout = findViewById(R.id.layout_payment);
        mPaid = findViewById(R.id.text_paid);
        mVisa = findViewById(R.id.button_visa);
        mPayPal = findViewById(R.id.pay_pal_button);
        mMaster = findViewById(R.id.button_master);

        mSubmit = findViewById(R.id.submit_button);
        mTitle = findViewById(R.id.editText_project_title);
        mDescription = findViewById(R.id.editText_project_description);
        mUpload = findViewById(R.id.btn_upload);
        upload_text = findViewById(R.id.uploadText);

        dialog = new ProgressDialog(this);

        facebookBanner = getSharedPreferences("facebookBanner", Context.MODE_PRIVATE);
        admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
        adsLinear = (LinearLayout) findViewById(R.id.banner_container);
        bannerAdmobAdView = new AdView(HireMeActivity.this);
        bannerAdmobAdView.setAdUnitId(admobBanner.getString("admobBanner", ""));
        bottomBannerType = getSharedPreferences("bottomBannerType", MODE_PRIVATE);
        bannerBottomType = bottomBannerType.getString("bottomBannerType", "");

        mProfile = findViewById(R.id.imageView12);
        mName = findViewById(R.id.text_name_age);
        mSchool = findViewById(R.id.text_school);
        mCountry = findViewById(R.id.text_country);
        mFlag = findViewById(R.id.flag_image);


        countryCodePicker = new CountryCodePicker(this);
        getData(email);

        backButton = (ImageView) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                            }
                        }).check();
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitPost();
            }
        });

        mPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(mAmount.getText().toString())) {
                    mAmount.setError("Please enter amount");
                } else {
                    double amount = Double.parseDouble(mAmount.getText().toString());
                    double total = (amount + (amount / 100) * 10);
                    payWithPayPal(String.valueOf(total));
                }

            }
        });

        mVisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mAmount.getText().toString())) {
                    mAmount.setError("Please enter amount");
                } else {
                    double amount = Double.parseDouble(mAmount.getText().toString());
                    double total = (amount + (amount / 100) * 10);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    CardFragment cardFragment = new CardFragment(String.format("%.2f", total), email, HireMeActivity.this);
                    cardFragment.show(fragmentManager, "CardFragment");
                }
            }
        });

        mMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(mAmount.getText().toString())) {
                    mAmount.setError("Please enter amount");
                } else {
                    double amount = Double.parseDouble(mAmount.getText().toString());
                    double total = (amount + (amount / 100) * 10);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    CardFragment cardFragment = new CardFragment(String.format("%.2f", total), email, HireMeActivity.this);
                    cardFragment.show(fragmentManager, "CardFragment");
                }
            }
        });
    }

    private void submitPost() {
        String title = mTitle.getText().toString();
        String description = mDescription.getText().toString();
        String amount = mAmount.getText().toString();

        if (TextUtils.isEmpty(title)) {
            mTitle.setError("Please enter title");
        } else if (TextUtils.isEmpty(description)) {
            mDescription.setError("Please enter description");
        } else if (TextUtils.isEmpty(amount)) {
            mAmount.setError("Please enter amount");
        } else if (!isPaid) {
            Toast.makeText(this, "Please make the payment", Toast.LENGTH_SHORT).show();
        } else {
            dialog.setTitle("Posting Job...");
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);
            dialog.show();

            HashMap<String, String> params = new HashMap<>();
            params.put("email", emplyoeeEmail);
            params.put("freelancer_email", email);
            params.put("type", "direct");
            params.put("title", title);
            params.put("description", description);
            params.put("target_country", "Default");
            params.put("volume", "0");
            params.put("amount", amount);
            params.put("project_category", "Default");
            if (picUri != null) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                    params.put("project_file", imageToStr(bitmap));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            submit(params);
        }
    }

    private String imageToStr(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        String encodedImg = Base64.encodeToString(imageByte, Base64.DEFAULT);
        return encodedImg;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            picUri = data.getData();
            File file = new File(getPath(picUri));
            upload_text.setText(file.getName());
        }
    }

    private void submit(HashMap<String, String> params) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.POST, url + "/api/freelances", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                ServerResponse res = gson.fromJson(response, ServerResponse.class);
                if (res.isStatus()) {
                    Toast.makeText(getApplicationContext(), "Job posted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), JobHistoryActivity.class));
                }

                dialog.dismiss();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("TAG", "onErrorResponse: " + error);
            }
        }) {
            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private void payWithPayPal(String amount) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setMessage("Payment Processing...");
        progressDialog.setCancelable(false);

        PayPalCheckout.start(new CreateOrder() {
            @Override
            public void create(@NotNull CreateOrderActions createOrderActions) {
                ArrayList<PurchaseUnit> purchaseUnits = new ArrayList<>();
                purchaseUnits.add(
                        new PurchaseUnit.Builder()
                                .amount(new Amount.Builder()
                                        .currencyCode(CurrencyCode.USD)
                                        .value(amount)
                                        .build())
                                .build()
                );
                Order order = new Order(OrderIntent.CAPTURE,
                        new AppContext.Builder()
                                .userAction(UserAction.CONTINUE)
                                .build(), purchaseUnits);
                createOrderActions.create(order, (CreateOrderActions.OnOrderCreated) null);
            }
        }, new OnApprove() {
            @Override
            public void onApprove(@NotNull Approval approval) {
                progressDialog.show();

                approval.getOrderActions().capture(new OnCaptureComplete() {
                    @Override
                    public void onCaptureComplete(@NotNull CaptureOrderResult captureOrderResult) {
                        if (captureOrderResult.toString().contains("COMPLETED")) {
                            progressDialog.dismiss();
                            paymentSuccess();
                        }
                    }
                });
            }
        }, new OnCancel() {
            @Override
            public void onCancel() {
                Toast.makeText(HireMeActivity.this, "Transaction Cancelled!", Toast.LENGTH_SHORT).show();
            }
        }, new OnError() {
            @Override
            public void onError(@NotNull ErrorInfo errorInfo) {
                Log.d("PAYPAL", "onError: " + errorInfo);
            }
        });
    }

    @Override
    public void paymentSuccess() {
        setPaidText("Card", mAmount.getText().toString());
    }

    private void setPaidText(String name, String value) {
        isPaid = true;
        mPaid.setVisibility(View.VISIBLE);
        mPaymentLayout.setVisibility(View.GONE);
        mPaid.setText("Paid by: " + name + ", Amount: " + value);

        updateVault(value);
    }

    private void updateVault(String value) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.POST, url + "/api/players/vault", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getLocalizedMessage());
            }
        }) {
            @Nullable
            @org.jetbrains.annotations.Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("email", emplyoeeEmail);
                params.put("vault", value);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }

    @Override
    public void paymentFailed() {

    }

    private void getData(String email) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "/api/creative_profile/" + email, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                ProfileSetting profileSetting = gson.fromJson(response, ProfileSetting.class);
                if (profileSetting.isStatus()) {
                    ProfileSetting.Data data = profileSetting.getData();
                    CalculateAge calculateAge = new CalculateAge();
                    long age = calculateAge.calculateAge(Long.parseLong(data.getDate_of_birth()));
                    Glide.with(getApplicationContext())
                            .load(data.getProfile_url())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mProfile);
                    mName.setText(data.getUsername() + ", " + age);
                    mSchool.setText(data.getSchool());
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
        }, new com.android.volley.Response.ErrorListener() {
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
        getConnectedUserData();
    }

    private void getConnectedUserData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.domain_name) + "/api/players/getplayerdata", new com.android.volley.Response.Listener<String>() {
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
        }, new com.android.volley.Response.ErrorListener() {
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

        StringRequest stringRequest = new StringRequest(Request.Method.POST, DataBaseApi.START_CHAT2, new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String cid = jsonObject.getString("message");
                    Intent intent = new Intent(getApplicationContext(), TextActivity.class);
                    intent.putExtra("id", cid);
                    intent.putExtra("name", name);
                    intent.putExtra("pic", pic);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("sid", MainActivity.userId);
                stringMap.put("rid", email);
                return stringMap;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }

}