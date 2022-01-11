package app.gigg.me.app.Activity;

import android.Manifest;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

import app.gigg.me.app.Adapter.TextAdapter;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.Manager.VolleyMultipartRequest;
import app.gigg.me.app.Model.MessageModal;
import app.gigg.me.app.Model.Player;
import app.gigg.me.app.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class TextActivity extends AppCompatActivity {

    private String picture, name, cid, msg;
    private TextView tv_name;
    private CircleImageView circleImageView;
    private ImageView imageView, sendMsg, sendImg;
    private RecyclerView recyclerView;
    private TextAdapter adapter;
    List<MessageModal> messageModals = new ArrayList<>();
    private EditText editText;
    final Handler handler = new Handler();
    final int delay = 5000;
    Runnable runnable;
    private AdView mAdView;
    private static final int REQUEST_PERMISSIONS = 100;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Bitmap bitmap;
    private String filePath;
    ImageView imageView2;
    TextView textView;
    long imagename;
    Player player;
    DatabaseReference conversation;
    DatabaseReference conversation2;
    String sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text);
        player = new Player(getIntent().getIntExtra("id",1),getIntent().getStringExtra("name"),getIntent().getStringExtra("email"),"",getIntent().getStringExtra("image"),getIntent().getIntExtra("score",0));
        if (player.getImage_url()==null)
            player.setImage_url(getIntent().getStringExtra("pic"));
        Toolbar toolbar = findViewById(R.id.text_toolbar);
        name = getIntent().getStringExtra("name");
        cid = getIntent().getStringExtra("id");
        picture = getIntent().getStringExtra("pic");
        sender = MainActivity.userId;
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        conversation = FirebaseDatabase.getInstance().getReference("conversations").child(MainActivity.userId).child(player.getId()+"");
        conversation2 = FirebaseDatabase.getInstance().getReference("conversations").child(player.getId()+"").child(MainActivity.userId);
        tv_name = findViewById(R.id.text_name);
        circleImageView = findViewById(R.id.text_profile);
        imageView = findViewById(R.id.text_back_arrow);
        sendMsg = findViewById(R.id.sendBtn);
        sendImg = findViewById(R.id.text_attachment);
        editText = findViewById(R.id.messageBox);
        recyclerView = findViewById(R.id.text_recycler);
        tv_name.setText(player.getName());
        Glide.with(this).load(player.getImage_url()).apply(new RequestOptions().placeholder(R.drawable.profile)).into(circleImageView);

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
        adapter = new TextAdapter(TextActivity.this, messageModals);
        recyclerView.setAdapter(adapter);

        sendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = new AdView(this);
        loadAdId();

        sendImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if ((ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    if ((ActivityCompat.shouldShowRequestPermissionRationale(TextActivity.this,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(TextActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE))) {

                    } else {
                        ActivityCompat.requestPermissions(TextActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                                REQUEST_PERMISSIONS);
                    }
                } else {
                    Log.e("Else", "Else");
                    showFileChooser();
                }
            }
        });

        loadMsg();
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
            } else {
                Toast.makeText(
                        TextActivity.this, "no image selected",
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
                            Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("GotError", "" + error.getMessage());
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
    ValueEventListener conversationListener;
    private void loadMsg() {
        conversationListener = conversation.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                messageModals = new ArrayList<>();
                for (DataSnapshot snap:snapshot.getChildren()){
                    messageModals.add(snap.getValue(MessageModal.class));
                }
                adapter.updateList(messageModals);
                scrollToBottom();
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }


    private void sendMessage() {
        msg = editText.getText().toString();
        if (msg.isEmpty()) {
            editText.setError("Type a message");
        } else {
            sendMsg.setEnabled(false);
            messageModals.add(new MessageModal( msg, MainActivity.userId,  new SimpleDateFormat("HH:mm").format(new Date()),new SimpleDateFormat("dd MMM").format(new Date()), "",player.getName()));
            editText.setText("");
            conversation.child("user").setValue(player);
            conversation2.child("user").setValue(new Player(Integer.parseInt(MainActivity.userId),MainActivity.userName,"","",MainActivity.userImage,0));
            conversation.child("lastMsg").setValue(new MessageModal( msg, sender+"",  new SimpleDateFormat("HH:mm").format(new Date()),new SimpleDateFormat("dd MMM").format(new Date()), player.getImage_url(),player.getName()));
            conversation2.child("lastMsg").setValue(new MessageModal( msg, sender+"",  new SimpleDateFormat("HH:mm").format(new Date()),new SimpleDateFormat("dd MMM").format(new Date()), player.getImage_url(),player.getName()));
            conversation2.child("messages").setValue(messageModals);
            conversation.child("messages").setValue(messageModals).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    sendMsg.setEnabled(true);
                }else{
                    Toast.makeText(TextActivity.this, "send failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void sendImage() {
        messageModals.add(new MessageModal("", "", MainActivity.userId, "", "", "", imagename + ".png"));
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
                Toast.makeText(TextActivity.this, "send failed", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("img", imagename + ".png");
                stringMap.put("sid", MainActivity.userId);
                stringMap.put("cid", cid);
                return stringMap;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }

    private void scrollToBottom() {
        adapter.notifyDataSetChanged();
        if (adapter.getItemCount() > 1)
            recyclerView.getLayoutManager().smoothScrollToPosition(recyclerView, null, adapter.getItemCount() - 1);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(runnable);
        super.onPause();
    }

    private void loadAdId() {
        FrameLayout frameLayout = findViewById(R.id.text_banner);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(getSharedPreferences("admobBanner", MODE_PRIVATE).getString("admobBanner", ""));
        frameLayout.addView(mAdView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        frameLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conversation != null && conversationListener != null)
            conversation.removeEventListener(conversationListener);
    }
}