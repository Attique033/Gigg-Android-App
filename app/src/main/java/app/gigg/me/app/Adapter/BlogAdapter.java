package app.gigg.me.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import app.gigg.me.app.Activity.NewsActivity;
import app.gigg.me.app.Model.Blog;
import app.gigg.me.app.R;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder>{

    private List<Blog> blogList;
    private Context context;

    public BlogAdapter(List<Blog> blogList, Context context) {
        this.blogList = blogList;
        this.context = context;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.blog_item, parent, false);
        return new BlogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        final Blog blog = blogList.get(position);
        holder.image.setImageDrawable(blog.getImage());
        holder.blog_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NewsActivity.URL = blog.getUrl();
                context.startActivity(new Intent(context , NewsActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public static class BlogViewHolder extends RecyclerView.ViewHolder{
        private ImageView image;
        private CardView blog_card;

        public BlogViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.imageView_image);
            blog_card = itemView.findViewById(R.id.blog_card);
        }
    }
}
