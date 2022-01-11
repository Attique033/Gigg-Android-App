package app.gigg.me.app.Activity.freelance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import app.gigg.me.app.Activity.freelance.model.Job;
import app.gigg.me.app.R;

public class JobAdapter extends RecyclerView.Adapter<JobAdapter.JobViewHolder> {

    private Context context;
    private List<Job> jobList;
    private OnItemClickListener listener;


    public interface OnItemClickListener {
        void onItemClick(Job job);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public JobAdapter(Context context, List<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
    }

    @NonNull
    @NotNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.job_cat_item, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NotNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.jobTitle.setText(job.getTitle());
        holder.jobImage.setImageDrawable(job.getImage());

        holder.backGround.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(job);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout backGround;
        private TextView jobTitle;
        private ImageView jobImage;

        public JobViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            backGround = itemView.findViewById(R.id.job_layout);
            jobTitle = itemView.findViewById(R.id.job_title);
            jobImage = itemView.findViewById(R.id.job_image);
        }
    }
}
