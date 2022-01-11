package app.gigg.me.app.Activity.freelance.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.gigg.me.app.Activity.MainActivity;
import app.gigg.me.app.Activity.TextActivity;
import app.gigg.me.app.Activity.freelance.adapters.LiveTaskAdapter;
import app.gigg.me.app.Activity.freelance.model.LiveTask;
import app.gigg.me.app.Activity.freelance.model.ServerResponse;
import app.gigg.me.app.Activity.freelance.util.SharedPrefHelper;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.R;

public class LiveTaskActivity extends AppCompatActivity implements LiveTaskAdapter.LiveTaskListener {

    private static final int PICK_IMAGE_REQUEST = 1001;
    private RecyclerView recyclerView;
    private List<LiveTask.Task> liveTaskList = new ArrayList<>();
    private LiveTaskAdapter adapter;
    private Uri picUri;
    private int pos;
    private long download_id;
    private String fileName;
    private String email;
    private SharedPreferences sharedPreferences, bottomBannerType, startAppID, admobInterstitial, facebookInterstitial, facebookBanner, admobBanner, questionTime, userSituationId, completedOption;
    private DownloadManager downloadManager;
    private CoordinatorLayout coordinatorLayout;
    private ProgressDialog pDialog;
    private String getDataLink;
    private TextView title;
    private SharedPrefHelper helper;

    String categoryId, categoryName, userId, bannerBottomType;
    private LinearLayout adsLinear;
    private AdView bannerAdmobAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_task);

        sharedPreferences = getSharedPreferences("userEmail", MODE_PRIVATE);
        email = sharedPreferences.getString("userEmail", "");

        helper = new SharedPrefHelper(this);

        title = findViewById(R.id.textView20);

        if (getIntent().getBooleanExtra("type", false)) {
            title.setText("My Tasks");
            getDataLink = getResources().getString(R.string.domain_name) + "/api/freelances?freelancer_email=" + email;
        } else {
            title.setText("Live Tasks");
            getDataLink = getResources().getString(R.string.domain_name) + "/api/freelances";
        }

        recyclerView = findViewById(R.id.live_task_recyclerview);
        adapter = new LiveTaskAdapter(this, liveTaskList, email, this);
        recyclerView.setAdapter(adapter);

        pDialog = new ProgressDialog(this);

        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        facebookBanner = getSharedPreferences("facebookBanner", Context.MODE_PRIVATE);
        admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
        getData();
        adsLinear = (LinearLayout) findViewById(R.id.banner_container);
        bannerAdmobAdView = new AdView(LiveTaskActivity.this);
        bannerAdmobAdView.setAdUnitId(admobBanner.getString("admobBanner", ""));
        //bannerAdmobAdView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
//        bannerAdmobAdView.setAdSize(com.google.android.gms.ads.AdSize.BANNER);
//        adsLinear.addView(bannerAdmobAdView);
//        adsLinear.setGravity(Gravity.CENTER_HORIZONTAL);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        bannerAdmobAdView.loadAd(adRequest);
        bottomBannerType = getSharedPreferences("bottomBannerType", MODE_PRIVATE);
        bannerBottomType = bottomBannerType.getString("bottomBannerType", "");
        if (bannerBottomType.equals("facebook")) {
            facebookBanner = getSharedPreferences("facebookBanner", MODE_PRIVATE);
            AudienceNetworkAds.initialize(this);

            com.facebook.ads.AdView facebookAdView = new com.facebook.ads.AdView(this, facebookBanner.getString("facebookBanner", null), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.bannerView10);
            adContainer.addView(facebookAdView);
            facebookAdView.loadAd();
        }

    }

    private void submit(HashMap<String, String> params, String id) {
        String url = getResources().getString(R.string.domain_name);
        StringRequest request = new StringRequest(Request.Method.POST, url + "/api/submit_posts", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Gson gson = new Gson();
                ServerResponse res = gson.fromJson(response, ServerResponse.class);
                if (res.isStatus()) {
                    helper.setSubmit(id, true);
                    Toast.makeText(getApplicationContext(), "Submitted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LiveTaskActivity.this, "Failed to submit proof!", Toast.LENGTH_SHORT).show();
                }
                getData();
                pDialog.dismiss();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("SUBMIT", "onErrorResponse: " + error);
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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            picUri = data.getData();
            File file = new File(getPath(picUri));
            adapter.update(pos, file.getName());
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


    private void startDownload(String link) {
        Toast.makeText(this, "Downloading File...", Toast.LENGTH_SHORT).show();
        fileName = link.substring(link.lastIndexOf("/") + 1) + ".png";
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link))
                .setTitle(fileName)
                .setDescription("Downloading")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(getBaseContext(), Environment.DIRECTORY_DOWNLOADS, fileName)
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
                    Toast.makeText(ctxt, "File Downloaded...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    private void getData() {
        StringRequest request = new StringRequest(Request.Method.GET, getDataLink, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Gson gson = new Gson();
                LiveTask liveTask = gson.fromJson(response, LiveTask.class);
                if (liveTask.isStatus()) {
                    liveTaskList = liveTask.getList();
                    Collections.reverse(liveTaskList);
                    adapter.updateData(liveTaskList);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("LIVETASK", "onErrorResponse: " + error.getLocalizedMessage());
            }
        });
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
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", emailX);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("sid", MainActivity.userId);
                stringMap.put("rid", x);
                return stringMap;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(stringRequest);
    }

    @Override
    public void onAttachment(String link) {
        if (link == null) {
            Toast.makeText(LiveTaskActivity.this, "Attachment unavailable!", Toast.LENGTH_SHORT).show();
            return;
        }
        startDownload(link);
    }

    @Override
    public void onUploadClick(int position) {
        pos = position;

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
                        Toast.makeText(LiveTaskActivity.this, "Denied", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                    }
                }).check();

    }

    @Override
    public void onSubmitClick(LiveTask.Task liveTask, boolean b) {
        if (picUri == null) {
            Toast.makeText(LiveTaskActivity.this, "Please Upload A Proof!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder dialog = new AlertDialog.Builder(LiveTaskActivity.this);
        dialog.setTitle("Please Wait");
        if (b) {
            dialog.setMessage("You may not submit twice to this task, except agreed by the employer!");
        } else {
            dialog.setMessage("Kindly chat with employer before you submit!");
        }
        dialog.setPositiveButton("Submit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                pDialog.setTitle("Submitting Task...");
                pDialog.setMessage("Please Wait...");
                pDialog.setCancelable(false);
                pDialog.show();

                HashMap<String, String> params = new HashMap<>();
                params.put("freelance_id", String.valueOf(liveTask.getId()));
                params.put("email", email);
                if (picUri != null) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                        params.put("uploaded_file", imageToStr(bitmap));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                submit(params, String.valueOf(liveTask.getId()));
                dialogInterface.dismiss();
            }
        });
        dialog.setNegativeButton("Chat", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                getConnectedUserData(liveTask.getFreelancer_email());
                dialogInterface.dismiss();
            }
        });

        dialog.show();
    }
}