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

import app.gigg.me.app.Activity.freelance.model.OpenJob;
import app.gigg.me.app.R;

public class OpenJobAdapter extends RecyclerView.Adapter<OpenJobAdapter.OpenJobAdapterViewHolder> {

    private Context context;
    private List<OpenJob> openJobList;
    private onCLickListener listener;

    public OpenJobAdapter(Context context, List<OpenJob> openJobList) {
        this.context = context;
        this.openJobList = openJobList;
    }

    public interface onCLickListener {
        void onProofClick(OpenJob.Submitter submitter);

        void onApproveClick(OpenJob.Submitter submitter, int id, double amount);

        void onDeclineClick(OpenJob.Submitter submitter);
    }

    public void setOnItemClickListener(onCLickListener listener) {
        this.listener = listener;
    }

    public void updateData(List<OpenJob> openJobList) {
        this.openJobList = openJobList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public OpenJobAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.open_jobs_item, parent, false);
        return new OpenJobAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull OpenJobAdapterViewHolder holder, int position) {
        OpenJob openJob = openJobList.get(position);

        if (openJob.getProject_file() == null) {
            holder.mImageCard.setVisibility(View.GONE);
        } else {
            holder.mImageCard.setVisibility(View.VISIBLE);
            Glide.with(context)
                    .load(openJob.getProject_file())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(holder.mImage);
        }
        holder.mTitle.setText("Project Title: " + openJob.getProject_title());

        if (openJob.getSubmitters() != null) {
            SubmitterAdapter adapter = new SubmitterAdapter(context, openJob.getSubmitters());
            holder.mSubmitter_list.setAdapter(adapter);
            adapter.setOnItemClickListener(new SubmitterAdapter.onCLickListener() {
                @Override
                public void onProofClick(OpenJob.Submitter submitter) {
                    listener.onProofClick(submitter);
                }

                @Override
                public void onApproveClick(OpenJob.Submitter submitter) {
                    listener.onApproveClick(submitter, openJob.getId(), openJob.getAmount());
                }

                @Override
                public void onDeclineClick(OpenJob.Submitter submitter) {
                    listener.onDeclineClick(submitter);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return openJobList.size();
    }

    public static class OpenJobAdapterViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImage;
        private TextView mTitle;
        private RecyclerView mSubmitter_list;
        private CardView mImageCard;

        public OpenJobAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mImage = itemView.findViewById(R.id.imageView16);
            mTitle = itemView.findViewById(R.id.textView19);
            mSubmitter_list = itemView.findViewById(R.id.recyclerView);
            mImageCard = itemView.findViewById(R.id.image_card);
        }
    }
}
