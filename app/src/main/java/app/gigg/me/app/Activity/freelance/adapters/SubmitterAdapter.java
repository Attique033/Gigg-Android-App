package app.gigg.me.app.Activity.freelance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import app.gigg.me.app.Activity.freelance.model.OpenJob;
import app.gigg.me.app.R;

public class SubmitterAdapter extends RecyclerView.Adapter<SubmitterAdapter.SubmitterAdapterViewHolder>{

    private Context context;
    private List<OpenJob.Submitter> submitterList;
    private onCLickListener listener;

    public SubmitterAdapter(Context context, List<OpenJob.Submitter> submitterList) {
        this.context = context;
        this.submitterList = submitterList;
    }

    public interface onCLickListener{
        void onProofClick(OpenJob.Submitter submitter);
        void onApproveClick(OpenJob.Submitter submitter);
        void onDeclineClick(OpenJob.Submitter submitter);
    }

    public void setOnItemClickListener(onCLickListener listener){
        this.listener = listener;
    }

    @NonNull
    @NotNull
    @Override
    public SubmitterAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.submitter_item, parent, false);
        return new SubmitterAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SubmitterAdapterViewHolder holder, int position) {
        OpenJob.Submitter submitter = submitterList.get(position);
        holder.mName.setText((position + 1) + ") @" + submitter.getFreelance_name());

        holder.mProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null && position != RecyclerView.NO_POSITION){
                    listener.onProofClick(submitter);
                }
            }
        });
        holder.mApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null && position != RecyclerView.NO_POSITION){
                    listener.onApproveClick(submitter);
                }
            }
        });
        holder.mDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null && position != RecyclerView.NO_POSITION){
                    listener.onDeclineClick(submitter);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return submitterList.size();
    }

    public static class SubmitterAdapterViewHolder extends RecyclerView.ViewHolder{

        private TextView mName;
        private TextView mProof;
        private TextView mApprove;
        private TextView mDecline;

        public SubmitterAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.sub_name);
            mProof = itemView.findViewById(R.id.view_proof);
            mApprove = itemView.findViewById(R.id.approve);
            mDecline = itemView.findViewById(R.id.decline);
        }
    }
}
