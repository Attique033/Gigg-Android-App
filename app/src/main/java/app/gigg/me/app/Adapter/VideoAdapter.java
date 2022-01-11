package app.gigg.me.app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.facebook.ads.AdView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import org.jetbrains.annotations.NotNull;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import app.gigg.me.app.Activity.MainActivity;
import app.gigg.me.app.Model.Video;
import app.gigg.me.app.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder>{

    private Context context;
    private List<Video> videoList;
    private OnItemClickListener listener;
    private SharedPreferences admobInterstitial;
    private InterstitialAd mInterstitialAd;

    public VideoAdapter(Context context, List<Video> videoList) {
        this.context = context;
        this.videoList = videoList;
        prepareInterstitialAdmobAd();
    }

    public interface OnItemClickListener {
        void onItemClick(Video video);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_card, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        Video video = videoList.get(position);
        holder.mName.setText(video.getName());
        holder.mImage.setImageDrawable(video.getImage());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (listener != null && position != RecyclerView.NO_POSITION){
//                    listener.onItemClick(video);
//                }

                if (mInterstitialAd != null) {
                    mInterstitialAd.show((Activity) context);
                }
                prepareInterstitialAdmobAd();
            }
        });
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoViewHolder extends RecyclerView.ViewHolder{

        private TextView mName;
        private ImageView mImage;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.video_name);
            mImage = itemView.findViewById(R.id.video_image);
        }
    }

    private void prepareInterstitialAdmobAd() {
        admobInterstitial = context.getSharedPreferences("admobInterstitial", MODE_PRIVATE);
        String interstitialId = admobInterstitial.getString("admobInterstitial", "");

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(context,
                interstitialId, adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NotNull InterstitialAd interstitialAd) {
                        mInterstitialAd = interstitialAd;
                    }

                    @Override
                    public void onAdFailedToLoad(@NotNull LoadAdError loadAdError) {
                        mInterstitialAd = null;
                    }
                });
    }
}
