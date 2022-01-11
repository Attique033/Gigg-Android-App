package app.gigg.me.app.Activity.freelance.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import app.gigg.me.app.Activity.freelance.model.Search;
import app.gigg.me.app.R;

public class SearchRecentAdapter extends RecyclerView.Adapter<SearchRecentAdapter.SearchViewHolder> {

    private Context context;
    private List<Search.Datum> searchList;

    public SearchRecentAdapter(Context context, List<Search.Datum> searchList) {
        this.context = context;
        this.searchList = searchList;
    }

    public void update(List<Search.Datum> searchList){
        this.searchList = searchList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_creative_item_new, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull SearchViewHolder holder, int position) {
        Search.Datum search = searchList.get(position);

        Glide.with(context)
                .load(search.getProfile_image())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.mProfile);

        holder.mName.setText(search.getUsername() + ", " + search.getAge());
        holder.mSchool.setText(search.getSchool());
        holder.mCountry.setText(search.getCountry());
        holder.mRating.setText(String.valueOf(search.getRating_score()));
    }

    @Override
    public int getItemCount() {
        System.out.println(searchList.size());
        return searchList.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {

        private ImageView mProfile;
        private TextView mName;
        private TextView mSchool;
        private TextView mCountry;
        private TextView mRating;

        public SearchViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            mProfile = itemView.findViewById(R.id.item_profile);
            mName = itemView.findViewById(R.id.item_name);
            mSchool = itemView.findViewById(R.id.item_school);
            mCountry = itemView.findViewById(R.id.item_country);
            mRating = itemView.findViewById(R.id.level);
        }
    }
}
