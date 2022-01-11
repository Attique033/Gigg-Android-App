package app.gigg.me.app.Activity.freelance.ui.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import app.gigg.me.app.Activity.freelance.adapters.CompletedJobAdapter;
import app.gigg.me.app.Activity.freelance.model.CompletedJob;
import app.gigg.me.app.R;

import static android.content.Context.MODE_PRIVATE;

public class CompletedJobsFragment extends Fragment {

    public CompletedJobsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_completed_jobs, container, false);
    }

    private String email;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RecyclerView mCompleted;
    private List<CompletedJob> completedJobList = new ArrayList<>();
    private CompletedJobAdapter adapter;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = getActivity().getSharedPreferences("userEmail", MODE_PRIVATE);
        email = sharedPreferences.getString("userEmail", "");

        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        mCompleted = view.findViewById(R.id.completed_jobs_list);
        adapter = new CompletedJobAdapter(requireContext(), completedJobList);
        mCompleted.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
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
        completedJobList.clear();

        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.GET, url + "/api/completed_jobs?email=" + email, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<CompletedJob>>(){}.getType();
                completedJobList = gson.fromJson(response, listType);
                adapter.updateData(completedJobList);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getLocalizedMessage());
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }
}