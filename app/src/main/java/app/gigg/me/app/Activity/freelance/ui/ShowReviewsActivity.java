package app.gigg.me.app.Activity.freelance.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import app.gigg.me.app.Activity.freelance.adapters.ReviewAdapter;
import app.gigg.me.app.Activity.freelance.model.CalculateAge;
import app.gigg.me.app.Activity.freelance.model.OpenJob;
import app.gigg.me.app.Activity.freelance.model.ProfileSetting;
import app.gigg.me.app.Activity.freelance.model.ReviewData;
import app.gigg.me.app.R;

public class ShowReviewsActivity extends AppCompatActivity {
    private TextView mName, mSchool, mCountry, mCapabilities, mScore, mDescription;
    private ImageView mProfile;
    private RatingBar mRating;
    private ProgressBar mAttitude, mProf, mAv;
    private RecyclerView mReviewList;

    private SwipeRefreshLayout swipeRefreshLayout;
    private List<ReviewData.Review> reviewList = new ArrayList<>();
    private ReviewAdapter adapter;

    private String email;

    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_reviews);

        email = getIntent().getStringExtra("email");

        swipeRefreshLayout = findViewById(R.id.swipresfresh_layout);
        mProfile = findViewById(R.id.review_profile_image);
        mName = findViewById(R.id.review_name_text);
        mSchool = findViewById(R.id.review_school_text);
        mCountry = findViewById(R.id.review_country_text);
        mCapabilities = findViewById(R.id.review_cap_text);
        mScore = findViewById(R.id.review_score);
        mDescription = findViewById(R.id.review_desc);
        mRating = findViewById(R.id.rating_bar);
        mAttitude = findViewById(R.id.review_attitude_progress);
        mProf = findViewById(R.id.review_prof_progress);
        mAv = findViewById(R.id.review_av_progress);
        mReviewList = findViewById(R.id.review_list);

        adapter = new ReviewAdapter(this, reviewList);
        mReviewList.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });

        getProfileData(email);

        backButton=(ImageView)findViewById(R.id.imageView15);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getData();
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        swipeRefreshLayout.setRefreshing(true);
        reviewList.clear();

        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.GET, url + "/api/reviews?freelancer_email=" + email, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                ReviewData reviewData = gson.fromJson(response, ReviewData.class);

                if (reviewData.isStatus()){
                    ReviewData.Data data = reviewData.getData();
                    mScore.setText(String.format("%1.1f", data.getTotal()));
                    mRating.setRating((float) data.getTotal());
                    mDescription.setText("Showing " + data.getNum_of_reviews() + " reviews total");
                    setProgress(Integer.parseInt(data.getTotal_attitude()), mAttitude);
                    setProgress(Integer.parseInt(data.getTotal_professionalism()), mProf);
                    setProgress(Integer.parseInt(data.getTotal_availability()), mAv);
                    adapter.updateData(data.getReviews());
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getLocalizedMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private void setProgress(int value, ProgressBar progressBar){
        if (value <= 5){
            progressBar.setProgressDrawable(getDrawable(R.drawable.progress_red));
        }else if (value <= 7){
            progressBar.setProgressDrawable(getDrawable(R.drawable.progress_blue));
        }else{
            progressBar.setProgressDrawable(getDrawable(R.drawable.progress_green));
        }
        progressBar.setProgress(value);
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
                    CalculateAge calculateAge = new CalculateAge();
                    long age = calculateAge.calculateAge(Long.parseLong(data.getDate_of_birth()));
                    mName.setText(data.getUsername() +", " + age);
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
}