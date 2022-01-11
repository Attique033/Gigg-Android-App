package app.gigg.me.app.Activity.freelance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import app.gigg.me.app.Activity.freelance.model.CompletedJob;
import app.gigg.me.app.R;

public class CompletedJobAdapter extends RecyclerView.Adapter<CompletedJobAdapter.CompletedJobViewHolder> {
    private Context context;
    private List<CompletedJob> completedJobList;

    public CompletedJobAdapter(Context context, List<CompletedJob> openJobList) {
        this.context = context;
        this.completedJobList = openJobList;
    }

    public void updateData(List<CompletedJob> openJobList) {
        this.completedJobList = openJobList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public CompletedJobViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.completed_jobs, parent, false);
        return new CompletedJobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CompletedJobViewHolder holder, int position) {
        CompletedJob job = completedJobList.get(position);

        if (job.getProject_file() == null) {
            holder.mImageCard.setVisibility(View.GONE);
        } else {
            Glide.with(context)
                    .load(job.getProject_file())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mImage);
        }
        holder.mTitle.setText("Project Title: " + job.getTitle());
    }

    @Override
    public int getItemCount() {
        return completedJobList.size();
    }

    public static class CompletedJobViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mTitle;
        private CardView mImageCard;

        public CompletedJobViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.imageView16);
            mTitle = itemView.findViewById(R.id.textView19);
            mImageCard = itemView.findViewById(R.id.image_card);
        }
    }
}
