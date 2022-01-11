package app.gigg.me.app.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import app.gigg.me.app.Activity.SubcriptionActivity;
import app.gigg.me.app.Model.Subcription;
import app.gigg.me.app.R;

import java.util.List;

public class SubcriptionAdapter extends RecyclerView.Adapter<SubcriptionAdapter.SubcriptionViewHolder> {

    private List<Subcription> subcriptionList;
    private Context context;
    int selectedPosition = 1;

    public SubcriptionAdapter(List<Subcription> subcriptionList, Context context) {
        this.subcriptionList = subcriptionList;
        this.context = context;
    }

    @NonNull
    @Override
    public SubcriptionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.subcription_item, parent, false);
        return new SubcriptionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubcriptionViewHolder holder, final int position) {
        final Subcription subcription = subcriptionList.get(position);
        holder.mTag.setText(subcription.getTag());
        holder.mTitle.setText(subcription.getTitle());
        holder.mDiscount.setText("$" + subcription.getDiscount());
        holder.mPrice.setText("$" + subcription.getPrice());
        holder.withdraw.setText(subcription.getWithdrawal());

        if (selectedPosition == position) {
            if (subcription.getId() == 2) {
                holder.mBg.setBackground(context.getResources().getDrawable(R.drawable.selected_card));
                holder.mDot.setBackground(context.getResources().getDrawable(R.drawable.card_selected_dot));
            } else {
                holder.mBg.setBackground(context.getResources().getDrawable(R.drawable.selected_card_green));
                holder.mDot.setBackground(context.getResources().getDrawable(R.drawable.card_selected_dot_green));
            }

            SubcriptionActivity.buyPlan(subcription);
        } else {
            holder.mBg.setBackground(context.getResources().getDrawable(R.drawable.unselected_card));
            holder.mDot.setBackground(context.getResources().getDrawable(R.drawable.card_unselected_dot));
        }

        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedPosition = position;
                notifyDataSetChanged();
            }
        });

        if (subcription.getId() == 2) {
            holder.mTag.setBackground(context.getDrawable(R.drawable.subcription_text));
        } else {
            holder.mTag.setBackground(context.getDrawable(R.drawable.subcription_text_green));
        }

        holder.mDiscount.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        holder.mPrice.setTextColor(Color.parseColor(subcription.getColor()));
    }

    @Override
    public int getItemCount() {
        return subcriptionList.size();
    }

    public static class SubcriptionViewHolder extends RecyclerView.ViewHolder {

        private TextView mTag;
        private TextView mTitle;
        private ConstraintLayout mBg;
        private TextView mDiscount;
        private TextView mPrice;
        private View mDot;
        private LinearLayout linearLayout;
        private TextView withdraw;

        public SubcriptionViewHolder(@NonNull View itemView) {
            super(itemView);

            mTag = itemView.findViewById(R.id.textView_Tag);
            mTitle = itemView.findViewById(R.id.textView_sub_title);
            mBg = itemView.findViewById(R.id.subcription_Bg);
            mDiscount = itemView.findViewById(R.id.textView_sub_Discount);
            mPrice = itemView.findViewById(R.id.textView_sub_price);
            mDot = itemView.findViewById(R.id.view_sub_dot);
            linearLayout = itemView.findViewById(R.id.linearLayout);
            withdraw = itemView.findViewById(R.id.withdrawal_day);
        }
    }
}
