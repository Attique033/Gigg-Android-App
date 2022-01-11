package app.gigg.me.app.Activity;

import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

import app.gigg.me.app.Adapter.VideoAdapter;
import app.gigg.me.app.Manager.MyApplication;
import app.gigg.me.app.Model.Video;
import app.gigg.me.app.R;

public class VideoActivity extends AppCompatActivity {

    private final List<Video> videoList = new ArrayList<>();
    private String user_id;
    private AdView mAdView;
    private SharedPreferences admobBanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        user_id = getIntent().getStringExtra("id");

        VideoView videoView = findViewById(R.id.videoView);
        HttpProxyCacheServer proxy = MyApplication.getProxy(this);
        String proxyUrl = proxy.getProxyUrl("http://earnhq.online/Videos/Earn%20$50%20EVERY%2010%20Minutes%20IN%20FREE%20PAYPAL%20MONEY%20(Make%20Money%20Online).mp4");
        videoView.setVideoPath(proxyUrl);
        videoView.start();

        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.setLooping(true);
            }
        });


        RecyclerView videoRecyclerView = findViewById(R.id.videoRecyclerView);
        VideoAdapter adapter = new VideoAdapter(this, videoList);
        videoRecyclerView.setAdapter(adapter);

        videoList.add(new Video(getResources().getDrawable(R.drawable.ic_finance), "Finance"));
        videoList.add(new Video(getResources().getDrawable(R.drawable.ic_travel), "Travel"));
        videoList.add(new Video(getResources().getDrawable(R.drawable.ic_ecom), "Ecommerce"));
        videoList.add(new Video(getResources().getDrawable(R.drawable.ic_game), "Game"));
        videoList.add(new Video(getResources().getDrawable(R.drawable.ic_auto), "Automobile"));
        videoList.add(new Video(getResources().getDrawable(R.drawable.ic_food), "Food"));
        videoList.add(new Video(getResources().getDrawable(R.drawable.ic_fasion), "Fasion"));
        videoList.add(new Video(getResources().getDrawable(R.drawable.ic_property), "Property"));
        videoList.add(new Video(getResources().getDrawable(R.drawable.ic_sports), "Sport"));

        adapter.notifyDataSetChanged();

        admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
        String admobBannerId = admobBanner.getString("admobBanner", "");

        mAdView = new AdView(this);
        FrameLayout adLayout = findViewById(R.id.adView);
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(admobBannerId);
        AdRequest adRequest = new AdRequest.Builder().build();
        adLayout.addView(mAdView);
        mAdView.loadAd(adRequest);


    }

    public void goBack(View view) {
        onBackPressed();
    }
}