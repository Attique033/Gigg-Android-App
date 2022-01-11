package app.gigg.me.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import app.gigg.me.app.Model.RoomModal;
import app.gigg.me.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomAdapter extends RecyclerView.Adapter<RoomAdapter.ViewHolder> {

    private Context context;
    List<RoomModal> messageModals;

    final int ITEM_SEND = 1;
    final int ITEM_REC = 2;

    public void updateList(List<RoomModal> messageModals){
        this.messageModals = messageModals;
        notifyDataSetChanged();
    }

    public RoomAdapter(Context context, List<RoomModal> messageModals) {
        this.context = context;
        this.messageModals = messageModals;
    }

    @NonNull
    @Override
    public RoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_SEND){
            view = LayoutInflater.from(context).inflate(R.layout.chat_room_send, parent, false);
        }else{
            view = LayoutInflater.from(context).inflate(R.layout.chat_room_recevied, parent, false);
        }
        return new ViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        RoomModal message = messageModals.get(position);
        String id = message.getSid();
        if (id.equals(MainActivity.userId)){
            return ITEM_SEND;
        }else {
            return ITEM_REC;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RoomAdapter.ViewHolder holder, int position) {
        String id = messageModals.get(position).getId();
        String msg = messageModals.get(position).getMessage();
        String sid = messageModals.get(position).getSid();
        String rid = messageModals.get(position).getRid();
        String time = messageModals.get(position).getTime();
        String date = messageModals.get(position).getDate();
        String name = messageModals.get(position).getName();
        String pic = messageModals.get(position).getPic();
        String img = messageModals.get(position).getImage();
        holder.setData(id,msg,sid,rid,time,date,name,pic,img);
    }

    @Override
    public int getItemCount() {
        return messageModals.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv_name, tv_msg;
        CircleImageView circleImageView;
        LinearLayout linearLayout;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_msg = itemView.findViewById(R.id.room_message);
            tv_name = itemView.findViewById(R.id.room_sender);
            circleImageView = itemView.findViewById(R.id.room_sender_pic);
            linearLayout = itemView.findViewById(R.id.room_user_msg_lay);
            imageView = itemView.findViewById(R.id.room_img);
        }

        public void setData(String id, String msg, final String sid, String rid, String time, String date, final String name, final String pic, final String img) {
            tv_name.setText(name);
            tv_msg.setText(msg);
            Glide.with(context).load(pic).apply(new RequestOptions().placeholder(R.drawable.profile)).into(circleImageView);

            if(!img.equals("")){
                imageView.setVisibility(View.VISIBLE);
                tv_msg.setVisibility(View.GONE);
                Glide.with(context).load(DataBaseApi.IMG_URL+img).apply(new RequestOptions().placeholder(R.drawable.loads)).into(imageView);

            }else {
                imageView.setVisibility(View.GONE);
                tv_msg.setVisibility(View.VISIBLE);
            }

            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    linearLayout.setEnabled(false);
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, DataBaseApi.START_CHAT, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            linearLayout.setEnabled(true);
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                String cid = jsonObject.getString("message");
                                Intent intent = new Intent(context, TextActivity.class);
                                intent.putExtra("id", cid);
                                intent.putExtra("name", name);
                                intent.putExtra("pic", pic);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            linearLayout.setEnabled(true);
                            Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> stringMap = new HashMap<>();
                            stringMap.put("sid", MainActivity.userId);
                            stringMap.put("rid", sid);
                            return stringMap;
                        }
                    };
                    RequestQueue queue = Volley.newRequestQueue(context);
                    queue.add(stringRequest);
                }
            });
        }
    }
}
