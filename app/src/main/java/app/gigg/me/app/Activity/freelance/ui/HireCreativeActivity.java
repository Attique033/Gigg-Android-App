package app.gigg.me.app.Activity.freelance.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
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
import com.travijuu.numberpicker.library.Enums.ActionEnum;
import com.travijuu.numberpicker.library.Interface.ValueChangedListener;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.gigg.me.app.Activity.freelance.adapters.CountryAdapter;
import app.gigg.me.app.Activity.freelance.model.ServerResponse;
import app.gigg.me.app.Activity.freelance.util.SharedPrefHelper;
import app.gigg.me.app.R;

public class HireCreativeActivity extends AppCompatActivity implements CardFragment.CardFragmentInterface, CountryAdapter.CountryInterface {
    private static final int PICK_IMAGE_REQUEST = 1002;
    private String email, category, tag, country, title, description, amount, volume;
    private SharedPreferences sharedPreferences;
    private ConstraintLayout mDetailsLayout, mBudgetLayout;
    private View mDetailsView, mBudgetView;
    private int VIEW_TYPE = 0;
    private EditText mCategory, mProjectTitle, mDescription, mBudget;
    private Spinner mCountrySpinner;
    private com.travijuu.numberpicker.library.NumberPicker mVolume;
    private RelativeLayout mContinue, mSubmit;
    private Button mUpload;
    private LinearLayout mPayPal, mVisa, mMasterCard, mPaymentLayout;
    private TextView mPaid;
    private int count = 1;
    private Uri picUri;
    private SharedPrefHelper helper;
    private ProgressDialog dialog;

    private ImageView backButton;
    private double budget;
    private boolean canPay;
    private TextView mPaymentAmount;

    private List<String> countryList = new ArrayList<>();
    private RecyclerView mCountryRecyclerview;
    private CountryAdapter countryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire_creative_new);

        dialog = new ProgressDialog(this);

        sharedPreferences = getSharedPreferences("userEmail", MODE_PRIVATE);
        email = sharedPreferences.getString("userEmail", "");
        category = getIntent().getStringExtra("cat");
        tag = getIntent().getStringExtra("tag");
        helper = new SharedPrefHelper(this);
        mDetailsLayout = findViewById(R.id.layout_details);
        mBudgetLayout = findViewById(R.id.layout_budget);
        mDetailsLayout.setVisibility(View.VISIBLE);
        mBudgetLayout.setVisibility(View.GONE);
        mDetailsView = findViewById(R.id.view_details);
        mBudgetView = findViewById(R.id.view_budget);

        mCategory = findViewById(R.id.editText_project_category);
        mProjectTitle = findViewById(R.id.editText_project_title);
        mDescription = findViewById(R.id.editText_project_description);
        mBudget = findViewById(R.id.editText_budget_input);

        mCountrySpinner = findViewById(R.id.country_spinner);
        mContinue = findViewById(R.id.hireMe_button);
        mSubmit = findViewById(R.id.sibmit_button);
        mUpload = findViewById(R.id.upload_btn);
        mPaid = findViewById(R.id.text_paid);
        mPayPal = findViewById(R.id.pay_pal_button);
        mPaymentLayout = findViewById(R.id.layout_payment);
        mVolume = findViewById(R.id.volume_picker);
        mVisa = findViewById(R.id.button_visa);
        mMasterCard = findViewById(R.id.button_master);
        backButton = (ImageView) findViewById(R.id.backButton);

        mPaymentAmount = findViewById(R.id.project_payment);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mVolume.setValueChangedListener(new ValueChangedListener() {
            @Override
            public void valueChanged(int value, ActionEnum action) {
                count = value;
                volume = String.valueOf(value);
                setAmount();
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


        mCategory.setText(category);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.simple_list_item, getResources().getStringArray(R.array.countries_array_job));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCountrySpinner.setAdapter(adapter);

        mCountrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String c = adapterView.getItemAtPosition(i).toString();
                if (c != null){
                    countryList.add(c);
                }
                countryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mDetailsView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (VIEW_TYPE == 1) {
                    VIEW_TYPE = 0;
                    mDetailsLayout.setVisibility(View.VISIBLE);
                    mBudgetLayout.setVisibility(View.GONE);
                    mDetailsView.setBackground(getDrawable(R.drawable.job_post_indicator_view1));
                    mBudgetView.setBackground(null);
                }
            }
        });

        mBudgetView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    if (VIEW_TYPE == 0) {
                        VIEW_TYPE = 1;
                        mDetailsLayout.setVisibility(View.GONE);
                        mBudgetLayout.setVisibility(View.VISIBLE);
                        mDetailsView.setBackground(null);
                        mBudgetView.setBackground(getDrawable(R.drawable.job_post_indicator_view1));
                    }
                }
            }
        });

        mContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    if (VIEW_TYPE == 0) {
                        VIEW_TYPE = 1;
                        mDetailsLayout.setVisibility(View.GONE);
                        mBudgetLayout.setVisibility(View.VISIBLE);
                        mDetailsView.setBackground(null);
                        mBudgetView.setBackground(getDrawable(R.drawable.job_post_indicator_view1));
                    }
                }
            }
        });

        mPayPal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (canPay) {
                    double total = (budget * count);
                    double subTotal = total + ((total / 100) * 10);
                    if (subTotal >= 5) {
                        payWithPayPal(String.valueOf(subTotal));
                    } else {
                        setAmountError();
                    }
                } else {
                    mBudget.setError("Please enter budget");
                    mBudget.requestFocus();
                }

            }
        });

        mVisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceedCardPayment();
            }
        });

        mMasterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                proceedCardPayment();
            }
        });

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validate()) {
                    if (TextUtils.isEmpty(amount)) {
                        Toast.makeText(HireCreativeActivity.this, "Please make the payment", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog.setTitle("Posting Job...");
                        dialog.setMessage("Please Wait...");
                        dialog.setCancelable(false);
                        dialog.show();

                        HashMap<String, String> params = new HashMap<>();
                        params.put("email", email);
                        params.put("title", title);
                        params.put("description", description);
                        params.put("target_country", country);
                        params.put("volume", String.valueOf(count));
                        params.put("amount", amount);
                        params.put("project_category", tag);
                        System.out.println(params);
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
            }
        });

        mBudget.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String value = s.toString();
                if (!TextUtils.isEmpty(s)) {
                    if (s.toString().startsWith(".")) {
                        value = "0.";
                        mBudget.setText(value);
                        mBudget.setSelection(value.length());
                    }
                    budget = Double.parseDouble(value);
                    checkAmount();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mCountryRecyclerview = findViewById(R.id.country_re);
        countryAdapter = new CountryAdapter(countryList, this);
        mCountryRecyclerview.setAdapter(countryAdapter);
    }


    private void checkAmount() {
        if (TextUtils.isEmpty(mBudget.getText()) || tag.equals("#social_media_promotions") && budget < 0.2) {
            mBudget.setError("Minimum Amount $0.1");
            mBudget.requestFocus();
            canPay = false;
            return;
        } else if (TextUtils.isEmpty(mBudget.getText()) || tag.equals("#app_download") && budget < 0.2) {
            mBudget.setError("Minimum Amount $0.2");
            mBudget.requestFocus();
            canPay = false;
            return;
        } else if (TextUtils.isEmpty(mBudget.getText()) || tag.equals("#app/product_review") && budget < 1) {
            mBudget.setError("Minimum Amount $0.1");
            mBudget.requestFocus();
            canPay = false;
            return;
        } else if (TextUtils.isEmpty(mBudget.getText()) || tag.equals("#arricle_writing") && budget < 5) {
            mBudget.setError("Minimum Amount $2");
            mBudget.requestFocus();
            canPay = false;
            return;
        } else if (TextUtils.isEmpty(mBudget.getText()) || tag.equals("#polls_and_survey") && budget < 0.2) {
            mBudget.setError("Minimum Amount $0.3");
            mBudget.requestFocus();
            canPay = false;
            return;
        } else if (TextUtils.isEmpty(mBudget.getText()) || tag.equals("#CPA_surveys/Offers/Leads") && budget < 7) {
            mBudget.setError("Minimum Amount $0.1");
            mBudget.requestFocus();
            canPay = false;
            return;
        } else if (TextUtils.isEmpty(mBudget.getText()) || tag.equals("#multimedia") && budget < 7) {
            mBudget.setError("Minimum Amount $7");
            mBudget.requestFocus();
            canPay = false;
            return;
        } else if (TextUtils.isEmpty(mBudget.getText()) || tag.equals("#web_visit/click_ads") && budget < 5) {
            mBudget.setError("Minimum Amount $0.05");
            mBudget.requestFocus();
            canPay = false;
            return;
        } else if (TextUtils.isEmpty(mBudget.getText()) || tag.equals("#programming_and_tech") && budget < 10) {
            mBudget.setError("Minimum Amount $10");
            mBudget.requestFocus();
            canPay = false;
            return;
        } else if (TextUtils.isEmpty(mBudget.getText()) || tag.equals("#anything_else") && budget < 10) {
            mBudget.setError("Minimum Amount $0.1");
            mBudget.requestFocus();
            canPay = false;
            return;
        } else {
            canPay = true;
            setAmount();
        }
    }

    private void setAmount() {
        double total = (budget * count);
        double subTotal = total + ((total / 100) * 10);
        mPaymentAmount.setText("Amount: " + String.format("%.2f", subTotal) + "$");
    }

    private void proceedCardPayment() {
        if (canPay) {
            double total = (budget * count);
            double subTotal = total + ((total / 100) * 10);
            if (subTotal >= 5) {
                showValueDialog(Math.ceil(subTotal));
            } else {
                setAmountError();
            }
        } else {
            mBudget.setError("Please enter budget");
            mBudget.requestFocus();
        }
    }

    private void showValueDialog(double subTotal) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Note");
        alert.setMessage("Decimal figures will be rounded up.");
        alert.setCancelable(false);
        alert.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                CardFragment cardFragment = new CardFragment(String.format("%.2f", subTotal), email, HireCreativeActivity.this);
                cardFragment.show(fragmentManager, "CardFragment");
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }


    private void setAmountError() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("Sorry, minimum deposit is $5. Kindly increase your bid amount!");
        builder.setCancelable(true);
        builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mBudget.requestFocus();
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private boolean validate() {
        title = mProjectTitle.getText().toString();
        description = mDescription.getText().toString();
        country = "";

        for (String c : countryList){
            country += c + " ,";
        }

        if (TextUtils.isEmpty(title)) {
            mProjectTitle.setError("Enter Title");
            mProjectTitle.requestFocus();
            return false;
        } else if (TextUtils.isEmpty(description)) {
            mDescription.setError("Enter Description");
            mDescription.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    private void payWithPayPal(String amount) {

//        AlertDialog.Builder alert = new AlertDialog.Builder(this);
//        alert.setTitle("Sorry");
//        alert.setMessage("Only card payments accepted for now!");
//        alert.setCancelable(false);
//        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//        alert.show();

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
                Toast.makeText(HireCreativeActivity.this, "Transaction Cancelled!", Toast.LENGTH_SHORT).show();
            }
        }, new OnError() {
            @Override
            public void onError(@NotNull ErrorInfo errorInfo) {
                Log.d("PAYPAL", "onError: " + errorInfo);
            }
        });
    }

    private void setPaidText(String name, String value) {
        amount = value;
        mPaid.setVisibility(View.VISIBLE);
        mPaymentLayout.setVisibility(View.GONE);
        mPaid.setText("Paid: successfully" + ", Amount: " + value);
        helper.setVault(Float.parseFloat(value));
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
                params.put("email", email);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            picUri = data.getData();
            File file = new File(getPath(picUri));
            mUpload.setText(file.getName());
        }
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

    private String imageToStr(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageByte = byteArrayOutputStream.toByteArray();
        String encodedImg = Base64.encodeToString(imageByte, Base64.DEFAULT);
        return encodedImg;
    }

    private void submit(HashMap<String, String> params) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.POST, url + "/api/freelances", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                ServerResponse res = gson.fromJson(response, ServerResponse.class);
                if (res.isStatus()) {
                    Toast.makeText(HireCreativeActivity.this, "Job posted", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), JobCategoryActivity.class));
                    finish();
                }

                dialog.dismiss();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

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

    @Override
    public void paymentSuccess() {
        setPaidText("Card", mBudget.getText().toString());

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Payment successful");
        alert.setMessage("Now click the submit button to submit your request.");
        alert.setCancelable(false);
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public void paymentFailed() {

    }

    @Override
    public void onCloseClick(String country) {
        countryList.remove(country);
        countryAdapter.notifyDataSetChanged();
    }
}