package app.gigg.me.app.Activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.unity3d.ads.UnityAds;
import com.unity3d.services.banners.BannerView;
import com.unity3d.services.banners.UnityBannerSize;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import app.gigg.me.app.Activity.freelance.ui.CreativeProfileActivity;
import app.gigg.me.app.Adapter.ChatAdapter;
import app.gigg.me.app.Adapter.PlayersAdapter;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.Model.ChatModal;
import app.gigg.me.app.Model.Conversation;
import app.gigg.me.app.Model.MessageModal;
import app.gigg.me.app.Model.Player;
import app.gigg.me.app.Model.RoomModal;
import app.gigg.me.app.R;

import static app.gigg.me.app.Activity.MainActivity.bannerAdID;
import static app.gigg.me.app.Manager.Constants.AD_UNIT;
import static app.gigg.me.app.Manager.Constants.TEST_MODE;
import static app.gigg.me.app.Manager.Constants.UNITY_ID;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private FloatingActionButton floatingActionButton;
    private LinearLayout linearLayout;
    private Dialog loadingDialog;
    private TextView lastMsg, lasTime;
    private ConstraintLayout chatLayout;
    public static ChatActivity chatActivity;
    List<ChatModal> chatModals = new ArrayList<>();
    SharedPreferences facebookBanner, startAppID;
    private String email;
    private SharedPreferences sharedPreferences;
    private AdView mAdView;
    private com.facebook.ads.AdView adView;
    private String userId;
    private RelativeLayout banner_layout;
    private BannerView bannerView;

    private RecyclerView top_player_recyclerView;
    private List<Player> playerList = new ArrayList<>();
    private PlayersAdapter playersAdapter;
    DatabaseReference lastMessage;
    private DatabaseReference conversations;
    private ValueEventListener conversationsListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        lastMessage = FirebaseDatabase.getInstance().getReference("roomChat").child("lastMsg");
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
//        startAppID = getSharedPreferences("startAppID",MODE_PRIVATE);
//        StartAppSDK.init(this, startAppID.getString("startAppID",""), false);
        setContentView(R.layout.activity_chat);

        sharedPreferences = getSharedPreferences("userEmail", MODE_PRIVATE);
        email = sharedPreferences.getString("userEmail", "");
        userId = getIntent().getStringExtra("id");

        facebookBanner = getSharedPreferences("facebookBanner", MODE_PRIVATE);
        if (!facebookBanner.getString("facebookBanner", "").equals("")) {
            AudienceNetworkAds.initialize(this);
            adView = new com.facebook.ads.AdView(this, facebookBanner.getString("facebookBanner", ""), com.facebook.ads.AdSize.BANNER_HEIGHT_50);

            // Find the Ad Container
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.banner_container);

            // Add the ad view to your activity layout
            adContainer.addView(adView);

            // Request an ad
            adView.loadAd();
        }
        //Toolbar toolbar = findViewById(R.id.chat_toolbar);
        //toolbar.setTitle("Chat Room");
        // setSupportActionBar(toolbar);
        // Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);


        banner_layout = findViewById(R.id.bannerView7);
        UnityAds.initialize(this, UNITY_ID, null, TEST_MODE, true);
        bannerView = new BannerView(this, AD_UNIT, new UnityBannerSize(320, 50));
        banner_layout.addView(bannerView);
        bannerView.load();

        recyclerView = findViewById(R.id.all_chat_recycler);
//        recyclerView.setHasFixedSize(true);
        floatingActionButton = findViewById(R.id.add_chat_btn);
        lastMsg = findViewById(R.id.chat_last_msg2);
        lasTime = findViewById(R.id.chat_time2);
        chatLayout = findViewById(R.id.chat_room_layout);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadingDialog = new Dialog(ChatActivity.this);
        loadingDialog.setContentView(R.layout.loading_progress_dialog);
        loadingDialog.setCancelable(false);
        loadingDialog.getWindow().setBackgroundDrawable(getDrawable(R.drawable.slider_background));
        loadingDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getRoomData();
        top_player_recyclerView = findViewById(R.id.recycler_top);
        playersAdapter = new PlayersAdapter(this, playerList, R.layout.chat_item_layout_top);
        top_player_recyclerView.setAdapter(playersAdapter);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ChatActivity.this, AddChatActivity.class));
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setTitle("Warning!");
                builder.setMessage("Your account will be disabled for outrageous complaints and abuse in the chatroom");
                builder.setCancelable(false);
                builder.setPositiveButton("Agree", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ChatActivity.this, RoomActivity.class));
                        dialog.dismiss();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.show();
            }
        });

        adapter = new ChatAdapter(getApplicationContext(), chatModals);
        recyclerView.setAdapter(adapter);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = new AdView(this);

        loadAdId();


        //loadData();

        getTopPlayers();
    }
    ValueEventListener lastListener;
    private void getRoomData() {
        lastListener = lastMessage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    RoomModal data = snapshot.getValue(RoomModal.class);
                    lasTime.setText(data.getTime());
                    lastMsg.setText(data.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    private void getTopPlayers() {
        String url = getResources().getString(R.string.domain_name) + "/api/players/top10";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject player = jsonArray.getJSONObject(i);
                                String name = player.getString("name");
                                String email = player.getString("email");
                                String imageUrl = player.getString("image");
                                String memberSince = player.getString("member_since");
                                int id = player.getInt("id");

                                int points = player.getInt("score");
                                playerList.add(new Player(id,name, email, memberSince, imageUrl, points));
                            }
                            playersAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    private void loadAdId() {
        FrameLayout frameLayout = findViewById(R.id.chat_banner);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getSharedPreferences("admobBanner", MODE_PRIVATE).getString("admobBanner", ""));
        frameLayout.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        frameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        Intent main = new Intent(ChatActivity.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
        finish();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent main = new Intent(ChatActivity.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(main);
        finish();
        super.onBackPressed();
    }

    private void loadData() {
        conversations = FirebaseDatabase.getInstance().getReference("conversations").child(MainActivity.userId);
        conversations.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                chatModals = new ArrayList<>();
                for (DataSnapshot snap:snapshot.getChildren()){
                    Conversation conversation=snap.getValue(Conversation.class);
                    MessageModal msg=conversation.getLastMsg();
                    Log.e("xxassa",snap.child("user").getValue().toString()+"");
                    int id = Integer.parseInt(snap.child("user").child("id").getValue().toString());
                    String img= snap.child("user").child("image_url").getValue().toString();
                    chatModals.add(new ChatModal(id+"",img,snap.child("user").child("name").getValue(String.class),msg.getMessage(),msg.getTime()));
                }
                adapter.updateList(chatModals);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    @Override
    protected void onStart() {
        loadData();
        loadRoom();
        super.onStart();
    }



    private void loadRoom() {

        //
//        StringRequest stringRequest = new StringRequest(Request.Method.POST, DataBaseApi.LOAD_ROOM, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String response) {
//                try {
//                    JSONObject jsonObject = new JSONObject(response);
//                    JSONObject o = jsonObject.getJSONObject("user");
//                    lastMsg.setText(o.getString("msg"));
//                    lasTime.setText(o.getString("time"));
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                //Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        RequestQueue queue = Volley.newRequestQueue(this);
//        queue.add(stringRequest);
    }

    public void goToDashBoard(View view) {
        onBackPressed();
    }

    public void chatWithSupport(View view) {
        getConnectedUserData();
//        try {
//            Intent intent = new Intent(Intent.ACTION_MAIN);
//            intent.addCategory(Intent.CATEGORY_APP_EMAIL);
//            intent.putExtra(Intent.EXTRA_EMAIL  , new String[]{"mailto:support@gigg.me"});
//            this.startActivity(intent);
//        } catch (android.content.ActivityNotFoundException e) {
//            Toast.makeText(this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
//        }
//        Intent i = new Intent(Intent.ACTION_SEND);
//        i.setType("message/rfc822");
//        i.putExtra(Intent.EXTRA_EMAIL  , new String[]{"mailto:support@gigg.me"});
//        i.putExtra(Intent.EXTRA_SUBJECT, "Support Email");
//        try {
//            startActivity(Intent.createChooser(i, "Send mail..."));
//        } catch (android.content.ActivityNotFoundException ex) {
//            Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
//        }

    }

    public void openProfile(View view) {
        Intent profile = new Intent(this, CreativeProfileActivity.class);
        profile.putExtra("email", email);
        startActivity(profile);
    }


    private void getConnectedUserData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.domain_name) + "/api/players/getplayerdata", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String userName = jsonObject.getString("name");
                    String userEmail = jsonObject.getString("email");
                    String id = jsonObject.getString("id");
                    String userImageUrl = jsonObject.getString("image");
                    openChatX(userName, userImageUrl,id);
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
                params.put("email", "support@gigg.me");
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


    private void openChatX(String name, String pic,String userId) {

        Intent intent = new Intent(this, TextActivity.class);
        intent.putExtra("id", Integer.parseInt(userId));
        intent.putExtra("name", name);
        intent.putExtra("pic", pic);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (lastMessage != null && lastListener!=null)
            lastMessage.removeEventListener(lastListener);
    }
}