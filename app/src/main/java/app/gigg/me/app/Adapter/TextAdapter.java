package app.gigg.me.app.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import app.gigg.me.app.Activity.MainActivity;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.Model.MessageModal;
import app.gigg.me.app.R;

import java.util.List;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.ViewHolder> {

    private Context context;
    List<MessageModal> messageModals;

    final int ITEM_SEND = 1;
    final int ITEM_REC = 2;

    public TextAdapter(Context context, List<MessageModal> messageModals) {
        this.context = context;
        this.messageModals = messageModals;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_SEND){
            view = LayoutInflater.from(context).inflate(R.layout.item_send, parent, false);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.item_recevied, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        MessageModal message = messageModals.get(position);
        String id = message.getSid();
        if (id.equals(MainActivity.userId)){
            return ITEM_SEND;
        }else {
            return ITEM_REC;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String msg = messageModals.get(position).getMessage();
        String img = messageModals.get(position).getImage();
        holder.setData(msg,img);
    }

    @Override
    public int getItemCount() {
        return messageModals.size();
    }

    public void updateList(List<MessageModal> messageModals) {
        this.messageModals = messageModals;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.chat_message);
            imageView = itemView.findViewById(R.id.text_img);
        }

        public void setData(String msg, String img) {
            textView.setText(msg);

            if(!img.equals("")){
                imageView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                Glide.with(context).load(DataBaseApi.IMG_URL+img).apply(new RequestOptions().placeholder(R.drawable.loads)).into(imageView);

            }else {
                imageView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        }
    }
}
