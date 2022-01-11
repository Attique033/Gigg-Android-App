package app.gigg.me.app.Activity.freelance.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import app.gigg.me.app.R;

/**
 * Created by kishon on 09,August,2021
 */
public class CountryAdapter extends RecyclerView.Adapter<CountryAdapter.CountryViewHolder>{
    private List<String> countryList;
    private CountryInterface countryInterface;

    public CountryAdapter(List<String> countryList, CountryInterface countryInterface) {
        this.countryList = countryList;
        this.countryInterface = countryInterface;
    }

    public interface CountryInterface{
        void onCloseClick(String country);
    }

    @NonNull
    @Override
    public CountryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.country_spinner, parent, false);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CountryViewHolder holder, int position) {
        String country = countryList.get(position);
        holder.mName.setText(country);
        holder.mClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countryInterface != null){
                    countryInterface.onCloseClick(country);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return countryList.size();
    }

    public static class CountryViewHolder extends RecyclerView.ViewHolder{

        private TextView mName;
        private ImageView mClose;

        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.text_country_name);
            mClose = itemView.findViewById(R.id.btn_cross);
        }
    }
}
