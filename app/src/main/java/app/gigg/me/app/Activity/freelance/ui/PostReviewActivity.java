package app.gigg.me.app.Activity.freelance.ui;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import android.widget.RatingBar;
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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.gigg.me.app.Activity.freelance.model.ProfileSetting;
import app.gigg.me.app.Activity.freelance.model.ServerResponse;
import app.gigg.me.app.R;

public class PostReviewActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1002;
    private ImageView mProfile;
    private TextView mName, mSchool, mCountry, mCapabilities;
    private RatingBar mAttitude, mProfessionalism, mAvailability;
    private EditText mDescription;
    private TextView mFileName;
    private String email;
    private SharedPreferences sharedPreferences;
    private double vAttitude = 0.0, vProfessionalism = 0.0, vAvailability = 0.0;
    private Uri picUri;
    private ProgressDialog progressDialog;
    private int freelancer_id, job_id;
    private SharedPreferences user_id_pref;
    private String reviewer_id;
    private double amount;
    private TextView mAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_review);

        sharedPreferences = getSharedPreferences("userEmail", MODE_PRIVATE);
        email = sharedPreferences.getString("userEmail", "");

        user_id_pref = getSharedPreferences("userId", MODE_PRIVATE);;
        reviewer_id = user_id_pref.getString("userId", "");

        freelancer_id = getIntent().getIntExtra("freelancer_id", 0);
        job_id = getIntent().getIntExtra("job_id", 0);

        amount = getIntent().getDoubleExtra("amount", 0.00);

        mProfile = findViewById(R.id.review_profile_image);
        mName = findViewById(R.id.review_name_text);
        mSchool = findViewById(R.id.review_school_text);
        mCountry = findViewById(R.id.review_country_text);
        mCapabilities = findViewById(R.id.review_cap_text);
        mDescription = findViewById(R.id.review_desc_text);
        mFileName = findViewById(R.id.review_file_name);
        mAmount = findViewById(R.id.textView28);

        mAmount.setText("$"+ amount);
        mAttitude = findViewById(R.id.review_attitude_ratingBar);
        mProfessionalism = findViewById(R.id.review_prof_ratingBar);
        mAvailability = findViewById(R.id.review_av_ratingBar);

        progressDialog = new ProgressDialog(this);

        mAttitude.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                vAttitude = v;
            }
        });
        mProfessionalism.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                vProfessionalism = v;
            }
        });
        mAvailability.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                vAvailability = v;
            }
        });

        getProfileData(email);
    }

    private void getProfileData(String email) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "/api/creative_profile/" + email, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                ProfileSetting profileSetting = gson.fromJson(response, ProfileSetting.class);
                if (profileSetting.isStatus()) {
                    ProfileSetting.Data data = profileSetting.getData();
                    Glide.with(getApplicationContext())
                            .load(data.getProfile_url())
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(mProfile);
                    mName.setText(data.getUsername() +", " + data.getAge());
                    mSchool.setText(data.getSchool());
                    mCountry.setText(data.getCountry());

                    StringBuffer cap = new StringBuffer();
                    List<ProfileSetting.Capalibity> capalibityList = data.getCapalibity();

                    for (ProfileSetting.Capalibity capalibity : capalibityList){
                        cap.append(capalibity.getName() + ", ");
                    }
                    String str = cap.toString();
                    str = str.substring(0, str.length() -1);
                    mCapabilities.setText(str);
                } else {
                    Log.d("onResponse: ", "No Data");
                }
            }
        }, new Response.ErrorListener() {
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

    public void loadFile(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            picUri = data.getData();
            File file = new File(getPath(picUri));
            mFileName.setText(file.getName());
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

    public void submitReview(View view) {
        String description = mDescription.getText().toString();

        if (TextUtils.isEmpty(description)){
            mDescription.setError("Please enter your experience...");
            return;
        }

        HashMap<String, String> params = new HashMap<>();
        params.put("freelancer_id", String.valueOf(freelancer_id));
        params.put("reviewer_id", String.valueOf(reviewer_id));
        params.put("attitude", String.valueOf(vAttitude));
        params.put("professionalism", String.valueOf(vProfessionalism));
        params.put("availability", String.valueOf(vAvailability));
        params.put("description", description);
        params.put("job_id", String.valueOf(job_id));
        if (picUri != null) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                params.put("uploaded_file", imageToStr(bitmap));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        submit(params);
    }

    private void submit(HashMap<String, String> params) {
        progressDialog.setTitle("Posting Review");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();

        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.POST, url + "/api/reviews", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
                Gson gson = new Gson();
                ServerResponse serverResponse = gson.fromJson(response, ServerResponse.class);
                if (serverResponse.isStatus()){
                    Toast.makeText(PostReviewActivity.this, "Review Given!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), JobCategoryActivity.class));
                    finish();
                }

                progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getLocalizedMessage());
                progressDialog.dismiss();
            }
        }){
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

    public void goBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}