package app.gigg.me.app.Activity.freelance.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import app.gigg.me.app.Activity.SubcriptionActivity;
import app.gigg.me.app.Activity.freelance.adapters.SearchRecentAdapter;
import app.gigg.me.app.Activity.freelance.model.Search;
import app.gigg.me.app.Manager.PreferenceManager;
import app.gigg.me.app.R;

public class SearchActivity extends AppCompatActivity {

    private EditText mSearch;
    private RecyclerView mRecentRecycler;
    private List<Search.Datum> searchList = new ArrayList<>();
    private SearchRecentAdapter adapter;
    private ImageView backButton;
    private PreferenceManager preferenceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_new);

        String text = getIntent().getStringExtra("text");
        preferenceManager = new PreferenceManager(this);
        mSearch = findViewById(R.id.search_text);
        mSearch.setText(text);

        mRecentRecycler = findViewById(R.id.recentRecycler);
        adapter = new SearchRecentAdapter(this, searchList);
        mRecentRecycler.setAdapter(adapter);

        backButton = (ImageView) findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getData(text);
    }

    private void getData(String text) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.GET, url + "/api/creative_profile?name=" + text, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                Search search = gson.fromJson(response, Search.class);
                searchList = search.getData();
                adapter.update(searchList);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getLocalizedMessage());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    public void openHireMe(View view) {
        if (preferenceManager.isSubscribed()) {
            Intent intent = new Intent(this, ShareProfileActivity.class);
            startActivity(intent);
        } else {
            startActivity(new Intent(this, SubcriptionActivity.class));
        }
    }

    public void goBack(View view) {
        onBackPressed();
    }
}