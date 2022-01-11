package app.gigg.me.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import app.gigg.me.app.Activity.TextActivity;
import app.gigg.me.app.Model.ChatModal;
import app.gigg.me.app.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private Context context;
    List<ChatModal> chatModals;

    public ChatAdapter(Context context, List<ChatModal> chatModals) {
        this.context = context;
        this.chatModals = chatModals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String id = chatModals.get(position).getId();
        String name = chatModals.get(position).getName();
        String last = chatModals.get(position).getLast();
        String time = chatModals.get(position).getTime();
        String picture = chatModals.get(position).getPicture();
        holder.setData(id, name, last, time, picture);
    }

    @Override
    public int getItemCount() {
        return chatModals.size();
    }

    public void updateList(List<ChatModal> chatModals) {
        this.chatModals = chatModals;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView textViewName, textViewLast, textViewTime;
        CircleImageView circleImageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            textViewName = itemView.findViewById(R.id.chat_name);
            textViewLast = itemView.findViewById(R.id.chat_last_msg);
            textViewTime = itemView.findViewById(R.id.chat_time);
            circleImageView = itemView.findViewById(R.id.chat_image);


        }

        private void setData(final String id, final String name, String last, String time, final String picture) {

            Typeface typeface = ResourcesCompat.getFont(context, R.font.merriweather);

            textViewTime.setTypeface(typeface);
            textViewName.setTypeface(typeface);
            textViewLast.setTypeface(typeface);

            textViewTime.setText(time);
            textViewName.setText(name);
            textViewLast.setText(last);

            Glide.with(context).load(picture).apply(new RequestOptions().placeholder(R.drawable.profile)).into(circleImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TextActivity.class);
                    intent.putExtra("id", Integer.parseInt(id));
                    intent.putExtra("name", name);
                    intent.putExtra("pic", picture);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }
}
