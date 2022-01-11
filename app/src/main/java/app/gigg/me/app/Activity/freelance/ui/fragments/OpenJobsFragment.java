package app.gigg.me.app.Activity.freelance.ui.fragments;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import app.gigg.me.app.Activity.MainActivity;
import app.gigg.me.app.Activity.TextActivity;
import app.gigg.me.app.Activity.freelance.adapters.OpenJobAdapter;
import app.gigg.me.app.Activity.freelance.model.OpenJob;
import app.gigg.me.app.Activity.freelance.ui.PostReviewActivity;
import app.gigg.me.app.Activity.freelance.util.SharedPrefHelper;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.R;

import static android.content.Context.MODE_PRIVATE;


public class OpenJobsFragment extends Fragment {

    public OpenJobsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_open_jobs, container, false);
    }

    private RecyclerView mOpenJobs;
    private OpenJobAdapter adapter;
    private List<OpenJob> openJobList = new ArrayList<>();
    private String email;
    private SharedPreferences sharedPreferences;
    private long download_id;
    private String fileName;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DownloadManager downloadManager;
    private SharedPrefHelper helper;
    private SharedPreferences userSituationId;
    private String userID;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedPreferences = requireActivity().getSharedPreferences("userEmail", MODE_PRIVATE);
        email = sharedPreferences.getString("userEmail", "");

        userSituationId = requireActivity().getSharedPreferences("userId", MODE_PRIVATE);
        userID = userSituationId.getString("userId", "");

        helper = new SharedPrefHelper(requireContext());
        swipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }
        });
        requireActivity().registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadManager = (DownloadManager) requireActivity().getSystemService(Context.DOWNLOAD_SERVICE);

        mOpenJobs = view.findViewById(R.id.open_jobs_list);
        adapter = new OpenJobAdapter(getContext(), openJobList);
        mOpenJobs.setAdapter(adapter);

        adapter.setOnItemClickListener(new OpenJobAdapter.onCLickListener() {
            @Override
            public void onProofClick(OpenJob.Submitter submitter) {
                download(submitter.getDownload_link());
            }

            @Override
            public void onApproveClick(OpenJob.Submitter submitter, int id, double amount) {
                approve(submitter, id, amount);
            }

            @Override
            public void onDeclineClick(OpenJob.Submitter submitter) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(requireContext());
                dialog.setTitle("Please Wait");
                dialog.setMessage("Kindly chat with the creative before you decline!");
                dialog.setPositiveButton("Decline", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        decline(submitter.getId());
                        dialogInterface.dismiss();
                    }
                });
                dialog.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getConnectedUserData(submitter.getPlayer().getEmail());
                        dialogInterface.dismiss();
                    }
                });

                dialog.show();
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

    private void decline(int id) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.POST, url + "/api/submit_jobs/decline/" + id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getData();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getLocalizedMessage());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private void approve(OpenJob.Submitter submitter, int job_id, double amount) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.PUT, url + "/api/submit_posts/" + submitter.getId(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                getData();
                openReviewScreen(submitter.getFreelancer_id(), job_id, amount);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println(error.getLocalizedMessage());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }

    private void openReviewScreen(int freelancer_id, int job_id, double amount) {
        Intent intent = new Intent(getContext(), PostReviewActivity.class);
        intent.putExtra("freelancer_id", freelancer_id);
        intent.putExtra("job_id", job_id);
        intent.putExtra("reviewer_id", MainActivity.userId);
        intent.putExtra("amount", amount);
        getContext().startActivity(intent);
    }

    private void getData() {
        swipeRefreshLayout.setRefreshing(true);
        openJobList.clear();

        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.GET, url + "/api/open_jobs?email=" + email, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                Type listType = new TypeToken<List<OpenJob>>() {
                }.getType();
                openJobList = gson.fromJson(response, listType);
                adapter.updateData(openJobList);
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

    private void download(String url) {
        Snackbar.make(swipeRefreshLayout, "Downloading File...", Snackbar.LENGTH_SHORT).show();

        fileName = url.substring(url.lastIndexOf("/") + 1) + ".png";

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(requireContext(), Environment.DIRECTORY_DOWNLOADS, fileName)
                .setMimeType("image/*")
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true);

        download_id = downloadManager.enqueue(request);
    }

    private final BroadcastReceiver onComplete = new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), fileName);
            if (download_id == id) {
                if (file.exists()) {
                    Snackbar.make(swipeRefreshLayout, "File Downloaded...", Snackbar.LENGTH_LONG).show();
                }
            }
        }
    };

    private void getConnectedUserData(String emailX) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.domain_name) + "/api/players/getplayerdata", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String userName = jsonObject.getString("name");
                    String userEmail = jsonObject.getString("email");
                    String userImageUrl = jsonObject.getString("image");
                    openChatX(userName, userImageUrl, emailX);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", emailX);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


    private void openChatX(String name, String pic, String x) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DataBaseApi.START_CHAT2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String cid = jsonObject.getString("message");
                    Intent intent = new Intent(requireContext(), TextActivity.class);
                    intent.putExtra("id", cid);
                    intent.putExtra("name", name);
                    intent.putExtra("pic", pic);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("sid", userID);
                stringMap.put("rid", x);
                return stringMap;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(requireContext());
        queue.add(stringRequest);
    }

}