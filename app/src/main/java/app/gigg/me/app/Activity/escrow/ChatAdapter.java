package app.gigg.me.app.Activity.escrow;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import app.gigg.me.app.R;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by kishon on 16,September,2021
 */
public class ChatAdapter extends ListAdapter<ChatModel, ChatAdapter.ChatViewHolder> {

    protected ChatAdapter() {
        super(ChatModel.itemCallback);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.escrow_chat_item, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatModel chatModel = getItem(position);
        holder.mName.setText(chatModel.getName());
        holder.mMessage.setText(chatModel.getMessage());
        holder.mTime.setText(chatModel.getTime());
        holder.mUnread.setText(chatModel.getUnread());
    }

    public static class ChatViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView mImage;
        private TextView mName;
        private TextView mMessage;
        private TextView mTime;
        private TextView mUnread;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);

            mImage = itemView.findViewById(R.id.imageView10);
            mName = itemView.findViewById(R.id.name);
            mMessage = itemView.findViewById(R.id.message);
            mTime = itemView.findViewById(R.id.textView4);
            mUnread = itemView.findViewById(R.id.textView11);
        }
    }
}
