package app.gigg.me.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import app.gigg.me.app.Activity.MainActivity;
import app.gigg.me.app.Activity.TextActivity;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.Model.AddChatModal;
import app.gigg.me.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddChatAdapter extends RecyclerView.Adapter<AddChatAdapter.Viewholder> {

    private Context context;
    List<AddChatModal> addChatModals;
    SharedPreferences id;
    String uid;

    public AddChatAdapter(Context context, List<AddChatModal> addChatModals) {
        this.context = context;
        this.addChatModals = addChatModals;
    }

    @NonNull
    @Override
    public AddChatAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item_layout, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddChatAdapter.Viewholder holder, int position) {
        String id = addChatModals.get(position).getId();
        String name = addChatModals.get(position).getName();
        String picture = addChatModals.get(position).getPicture();
        holder.setData(id,name,picture);
    }

    @Override
    public int getItemCount() {
        return addChatModals.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        private TextView tv_name, tv_msg, tv_time;
        private ImageView circleImageView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);

            tv_msg = itemView.findViewById(R.id.chat_last_msg);
            tv_name = itemView.findViewById(R.id.chat_name);
            tv_time = itemView.findViewById(R.id.chat_time);
            circleImageView = itemView.findViewById(R.id.chat_image);
        }

        public void setData(final String id, final String name, final String picture) {
            Typeface typeface = ResourcesCompat.getFont(context, R.font.merriweather);
            tv_msg.setTypeface(typeface);
            tv_time.setTypeface(typeface);
            tv_name.setTypeface(typeface);
            tv_msg.setText("Tab to start chat");
            tv_time.setText("");
            tv_name.setText(name);
            Glide.with(context).load(picture).apply(new RequestOptions().placeholder(R.drawable.profile)).into(circleImageView);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (id.equals(MainActivity.userId)) {
                        Toast.makeText(context, "Can't send self message", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(context, TextActivity.class);
                        intent.putExtra("id", Integer.parseInt(id));
                        intent.putExtra("name", name);
                        intent.putExtra("image", picture);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }
                }
            });
        }
    }
}
