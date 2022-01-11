package app.gigg.me.app.Activity.freelance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.text.DecimalFormat;
import java.util.List;

import app.gigg.me.app.Activity.freelance.model.ReviewData;
import app.gigg.me.app.R;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>{

    private Context context;
    private List<ReviewData.Review> reviewDataList;
    private static DecimalFormat df = new DecimalFormat("0.00");
    public ReviewAdapter(Context context, List<ReviewData.Review> reviewDataList) {
        this.context = context;
        this.reviewDataList = reviewDataList;
    }

    @NonNull
    @NotNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull ReviewViewHolder holder, int position) {
        ReviewData.Review review = reviewDataList.get(position);
        ReviewData.Player profile = review.getPlayer();

        Glide.with(context)
                .load(profile.getImage_url())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mProfileImage);

        holder.mUsername.setText(profile.getName());
        holder.mDescription.setText(review.getDescription());
        holder.mDayCount.setText(String.valueOf(review.getDay_count()));
        holder.mScore.setText(df.format(review.getRating_score()));
        holder.mRating.setRating((float) review.getRating_score());
    }

    @Override
    public int getItemCount() {
        return reviewDataList.size();
    }

    public void updateData(List<ReviewData.Review> reviews) {
        this.reviewDataList = reviews;
        notifyDataSetChanged();
    }


    public class ReviewViewHolder extends RecyclerView.ViewHolder{
        private ImageView mProfileImage;
        private TextView mUsername;
        private TextView mDescription;
        private TextView mDayCount;
        private TextView mScore;
        private RatingBar mRating;

        public ReviewViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mProfileImage = itemView.findViewById(R.id.review_item_profile);
            mUsername = itemView.findViewById(R.id.review_item_username);
            mDescription = itemView.findViewById(R.id.review_item_desc);
            mDayCount = itemView.findViewById(R.id.review_item_day);
            mScore = itemView.findViewById(R.id.review_item_score);
            mRating = itemView.findViewById(R.id.review_item_rating);
        }
    }
}
