package app.gigg.me.app.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.gigg.me.app.Adapter.RoomAdapter;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.Manager.VolleyMultipartRequest;
import app.gigg.me.app.Model.RoomModal;
import app.gigg.me.app.R;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerErrorInfo;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;
//import com.startapp.sdk.adsbase.StartAppSDK;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static app.gigg.me.app.Manager.Constants.AD_UNIT;
import static app.gigg.me.app.Manager.Constants.TEST_MODE;
import static app.gigg.me.app.Manager.Constants.UNITY_ID;

public class RoomActivity extends AppCompatActivity {

    final Handler handler = new Handler();
    final int delay = 5000;
    Runnable runnable;
    private RecyclerView recyclerView;
    private ImageView imageView, sendMsg, sendImg;
    private RoomAdapter adapter;
    List<RoomModal> roomModals = new ArrayList<>();
    private EditText editText;
    private String msg;
    private AdView mAdView;
    //private static final String ROOT_URL = "http://seoforworld.com/api/v1/file-upload.php";
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST =1 ;
    private Bitmap bitmap;
    private String filePath;
    ImageView imageView2;
    TextView textView;
    long imagename;
    private com.facebook.ads.AdView adView;
    private SharedPreferences facebookBanner,startAppID;

    private RelativeLayout banner_layout;
    private BannerView bannerView;

    private UnityBannerListener unityBannerListener = new UnityBannerListener();

    private class UnityBannerListener implements BannerView.IListener {
        @Override
        public void onBannerLoaded(BannerView bannerAdView) {
            // Called when the banner is loaded.
        }

        @Override
        public void onBannerFailedToLoad(BannerView bannerAdView, BannerErrorInfo errorInfo) {
            Log.d("SupportTest", "Banner Error" + errorInfo);
            // Note that the BannerErrorInfo object can indicate a no fill (see API documentation).
        }

        @Override
        public void onBannerClick(BannerView bannerAdView) {
            // Called when a banner is clicked.
        }

        @Override
        public void onBannerLeftApplication(BannerView bannerAdView) {
            // Called when the banner links out of the application.
        }
    }
    DatabaseReference room;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        room= FirebaseDatabase.getInstance().getReference("roomChat");
//        startAppID = getSharedPreferences("startAppID",MODE_PRIVATE);
//        StartAppSDK.init(this, startAppID.getString("startAppID",""), true);
        setContentView(R.layout.activity_room);
        facebookBanner = getSharedPreferences("facebookBanner",MODE_PRIVATE);
        AudienceNetworkAds.initialize(this);
        Toolbar toolbar = findViewById(R.id.room_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        if(!facebookBanner.getString("facebookBanner","").equals("")){
            adView = new com.facebook.ads.AdView(this, facebookBanner.getString("facebookBanner" , ""), com.facebook.ads.AdSize.BANNER_HEIGHT_50);

            // Find the Ad Container
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

            // Add the ad view to your activity layout
            adContainer.addView(adView);

            // Request an ad
            adView.loadAd();
        }
        recyclerView = findViewById(R.id.room_recycler);
        imageView = findViewById(R.id.room_back_arrow);
        sendMsg = findViewById(R.id.sendBtn2);
        editText = findViewById(R.id.messageBox2);
        sendImg = findViewById(R.id.room_attachment);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        imagename = System.currentTimeMillis();
        adapter = new RoomAdapter(getApplicationContext(), roomModals);
        recyclerView.setAdapter(adapter);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });


        //UNITY
        banner_layout = findViewById(R.id.bannerView2);
        UnityAds.initialize (this, UNITY_ID, null, TEST_MODE, true);
        bannerView = new BannerView(this, AD_UNIT, new UnityBannerSize(320, 50));
        bannerView.setListener(unityBannerListener);
        banner_layout.addView(bannerView);
        bannerView.load();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = new AdView(this);
        FrameLayout frameLayout=findViewById(R.id.room_banner);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getSharedPreferences("admobBanner", MODE_PRIVATE).getString("admobBanner", ""));
        frameLayout.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        try {
            mAdView.loadAd(adRequest);
            frameLayout.setVisibility(View.VISIBLE);
        }catch (Exception e){
            e.printStackTrace();
        }

        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(RoomActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(RoomActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {

                    } else {
                        ActivityCompat.requestPermissions(RoomActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
                    Log.e("Else", "Else");
                    showFileChooser();
                }
            }
        });

//        loadMsg();

    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri picUri = data.getData();
            filePath = getPath(picUri);
            if (filePath != null) {
                try {

                    //textView.setText("File Selected");
                    Log.d("filePath", String.valueOf(filePath));
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);
                    uploadBitmap(bitmap);
                    //imageView.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else
            {
                Toast.makeText(
                        RoomActivity.this,"no image selected",
                        Toast.LENGTH_LONG).show();
            }
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

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void uploadBitmap(final Bitmap bitmap) {

        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, DataBaseApi.UPLOAD,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        sendImage();
                        try {
                            JSONObject obj = new JSONObject(new String(response.data));
                           // Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError",""+error.getMessage());
                    }
                }) {


            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                params.put("image", new DataPart(imagename + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    private void scrollToBottom() {
        adapter.updateList(roomModals);
        if (adapter.getItemCount() > 1)
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
    }

    private void loadMsg2(){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DataBaseApi.LOAD_MSG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                roomModals.clear();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        RoomModal item = new RoomModal(
                                o.getString("id"),
                                o.getString("msg"),
                                o.getString("sid"),
                                o.getString("rid"),
                                o.getString("time"),
                                o.getString("date"),
                                o.getString("name"),
                                o.getString("pic"),
                                o.getString("image")
                        );
                        roomModals.add(item);
                        adapter.notifyDataSetChanged();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Toast.makeText(RoomActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("id", "1");
                return stringMap;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
    ValueEventListener roomListener;
    private void loadMsg(){
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, DataBaseApi.LOAD_MSG, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    JSONArray jsonArray = jsonObject.getJSONArray("data");
//                    for (int i=0; i<jsonArray.length();i++){
//                        JSONObject o = jsonArray.getJSONObject(i);
//                        RoomModal item = new RoomModal(
//                                o.getString("id"),
//                                o.getString("msg"),
//                                o.getString("sid"),
//                                o.getString("rid"),
//                                o.getString("time"),
//                                o.getString("date"),
//                                o.getString("name"),
//                                o.getString("pic"),
//                                o.getString("image")
//                        );
//                        roomModals.add(item);
//                        scrollToBottom();
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //Toast.makeText(RoomActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> stringMap = new HashMap<>();
//                stringMap.put("id", "1");
//                return stringMap;
//            }
//        };
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(stringRequest);
        if (room == null){
            Log.e("Room","Not init");
            return;
        }
        roomListener = room.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                roomModals = new ArrayList<>();
                Log.e("Received room",snapshot.toString());
                for (DataSnapshot snap:snapshot.getChildren()){
                    roomModals.add(snap.getValue(RoomModal.class));
                    Log.e("message#",roomModals.size()+"");
                }
                scrollToBottom();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(){
        msg = editText.getText().toString();
        if (msg.isEmpty()){
            editText.setError("Type a message");
        }else {
            sendMsg.setEnabled(false);
            roomModals.add(new RoomModal("", msg, MainActivity.userId, "",  new SimpleDateFormat("HH:mm").format(new Date()),new SimpleDateFormat("dd MMM").format(new Date()), MainActivity.userName, MainActivity.userImage, ""));
//            scrollToBottom();
            editText.setText("");
            room.child("lastMsg").setValue(roomModals.get(roomModals.size()-1));
            room.child("messages").setValue(roomModals).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    sendMsg.setEnabled(true);
                }else{
                    sendMsg.setEnabled(true);
                    Toast.makeText(RoomActivity.this,"send failed", Toast.LENGTH_SHORT).show();
                }
            });
//            StringRequest stringRequest = new StringRequest(Request.Method.POST, DataBaseApi.SEND_MSG, new Response.Listener<String>() {
//                @Override
//                public void onResponse(String response) {
//                    sendMsg.setEnabled(true);
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    sendMsg.setEnabled(true);
//                    Toast.makeText(RoomActivity.this,"send failed", Toast.LENGTH_SHORT).show();
//                }
//            }) {
//                @Override
//                protected Map<String, String> getParams() throws AuthFailureError {
//                    Map<String, String> stringMap = new HashMap<>();
//                    stringMap.put("msg", msg);
//                    stringMap.put("sid", MainActivity.userId);
//                    stringMap.put("cid", "1");
//                    return stringMap;
//                }
//            };
//            RequestQueue queue = Volley.newRequestQueue(this);
//            queue.add(stringRequest);
        }
    }

    private void sendImage(){
        roomModals.add(new RoomModal("", "", MainActivity.userId, "", "", "", "", "", imagename+".png"));
        scrollToBottom();
        editText.setText("");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DataBaseApi.SEND_IMG, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                sendMsg.setEnabled(true);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                sendMsg.setEnabled(true);
                Toast.makeText(RoomActivity.this,"send failed", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("img", imagename+".png");
                stringMap.put("sid", MainActivity.userId);
                stringMap.put("cid", "1");
                return stringMap;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    @Override
    protected void onResume() {
//        handler.postDelayed( runnable = new Runnable() {
//            public void run() {
//
//                handler.postDelayed(runnable, delay);
//            }
//        }, delay);
        loadMsg();
        super.onResume();
    }

    @Override
    protected void onPause() {
//        handler.removeCallbacks(runnable);
        if (room!=null && roomListener!=null)
            room.removeEventListener(roomListener);
        super.onPause();
    }
}