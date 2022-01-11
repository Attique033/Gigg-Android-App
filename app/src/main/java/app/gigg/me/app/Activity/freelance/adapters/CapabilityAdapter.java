package app.gigg.me.app.Activity.freelance.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import app.gigg.me.app.Activity.freelance.model.Capability;
import app.gigg.me.app.R;

public class CapabilityAdapter extends RecyclerView.Adapter<CapabilityAdapter.CapabilityViewHolder> {

    private Context context;
    private List<Capability> capabilityList;

    public CapabilityAdapter(Context context, List<Capability> capabilityList) {
        this.context = context;
        this.capabilityList = capabilityList;
    }

    @NonNull
    @NotNull
    @Override
    public CapabilityViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.capability_item, parent, false);
        return new CapabilityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CapabilityViewHolder holder, int position) {
        Capability capability = capabilityList.get(position);
        holder.textView.setText(capability.getName());

//        switch (position) {
//            case 4:
//                holder.linearLayout.setBackground(context.getDrawable(R.drawable.capability_bg_1));
//                holder.textView.setTextColor(Color.parseColor("#FF8A00"));
//                break;
//            case 1:
//                holder.linearLayout.setBackground(context.getDrawable(R.drawable.capability_bg_2));
//                holder.textView.setTextColor(Color.parseColor("#0089FF"));
//                break;
//            case 2:
//                holder.linearLayout.setBackground(context.getDrawable(R.drawable.capability_bg_3));
//                holder.textView.setTextColor(Color.parseColor("#FF0000"));
//                break;
//            case 3:
//                holder.linearLayout.setBackground(context.getDrawable(R.drawable.capability_bg_4));
//                holder.textView.setTextColor(Color.parseColor("#A000EC"));
//                break;
//        }
    }

    @Override
    public int getItemCount() {
        return capabilityList.size();
    }

    public class CapabilityViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout linearLayout;
        private TextView textView;

        public CapabilityViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            linearLayout = itemView.findViewById(R.id.capability_layout);
            textView = itemView.findViewById(R.id.capability_text);
        }
    }
}
