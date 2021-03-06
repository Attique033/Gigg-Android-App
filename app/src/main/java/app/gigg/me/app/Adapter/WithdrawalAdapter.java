package app.gigg.me.app.Adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import app.gigg.me.app.Model.Withdrawal;
import app.gigg.me.app.R;

import java.util.ArrayList;

public class WithdrawalAdapter extends RecyclerView.Adapter<WithdrawalAdapter.WithdrawalsHolder> {
    private Context context;
    private ArrayList<Withdrawal> withdrawals;

    public class WithdrawalsHolder extends RecyclerView.ViewHolder {
        private TextView name, status, account, method, amount, points, date;

        public WithdrawalsHolder(@NonNull View itemView) {
            super(itemView);
            points = (TextView) itemView.findViewById(R.id.payment_points);
            status = (TextView) itemView.findViewById(R.id.payment_status);
            account = (TextView) itemView.findViewById(R.id.payment_account);
            method = (TextView) itemView.findViewById(R.id.payment_method);
            amount = (TextView) itemView.findViewById(R.id.payment_amount);
            date = (TextView) itemView.findViewById(R.id.payment_date);
        }
        public void setDetails(Withdrawal withdrawal) {
            Typeface typeface = ResourcesCompat.getFont(context, R.font.merriweather);
            points.setTypeface(typeface);
            status.setTypeface(typeface);
            account.setTypeface(typeface);
            method.setTypeface(typeface);
            amount.setTypeface(typeface);
            date.setTypeface(typeface);
            points.setText(String.valueOf(withdrawal.getPoints()));
            status.setText(withdrawal.getStatus());
            account.setText(withdrawal.getPayment_account());
            method.setText(withdrawal.getPayment_method());
            amount.setText(String.valueOf(withdrawal.getAmount()));
            date.setText(withdrawal.getDate());
        }
    }

    public WithdrawalAdapter(Context context, ArrayList<Withdrawal> withdrawals) {
        this.context = context;
        this.withdrawals = withdrawals;
    }

    @NonNull
    @Override
    public WithdrawalsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_withdrawal, parent, false);
        return new WithdrawalsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WithdrawalsHolder holder, int position) {
        Withdrawal withdrawal = withdrawals.get(position);
        holder.setDetails(withdrawal);
    }

    @Override
    public int getItemCount() {
        return withdrawals.size();
    }
}



