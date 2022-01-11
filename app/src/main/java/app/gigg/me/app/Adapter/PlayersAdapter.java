package app.gigg.me.app.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import app.gigg.me.app.Activity.MainActivity;
import app.gigg.me.app.Activity.TextActivity;
import app.gigg.me.app.Activity.freelance.ui.CreativeProfileActivity;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.Model.Player;
import app.gigg.me.app.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayersHolder> {

    private final Context context;
    private final List<Player> players;
    private int layout = R.layout.single_player;


    public PlayersAdapter(Context context, List<Player> players) {
        this.context = context;
        this.players = players;
    }

    public PlayersAdapter(Context context, List<Player> players, int layout) {
        this.context = context;
        this.players = players;
        this.layout = layout;
    }

    @NonNull
    @Override
    public PlayersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(layout, parent, false);
        return new PlayersHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayersHolder holder, int position) {
        Player player = players.get(position);
        String email = players.get(position).getEmail();
        String name = players.get(position).getName();
        String pic = players.get(position).getImage_url();
        holder.setDetails(player, email, name, pic);
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    public class PlayersHolder extends RecyclerView.ViewHolder {
        private final TextView playerName;
        private final CircleImageView playerImage;
        private final LinearLayout playerItem;

        public PlayersHolder(@NonNull View itemView) {
            super(itemView);
            playerName = itemView.findViewById(R.id.singlePlayer_name);
            playerImage =  itemView.findViewById(R.id.singlePlayer_image);
            playerItem = itemView.findViewById(R.id.singlePlayerItem);
        }

        public void setDetails(Player player, final String email, final String name, final String pic) {
            playerName.setText(player.getName());
            Glide.with(context)
                    .load(pic)
                    .diskCacheStrategy(DiskCacheStrategy.DATA)
                    .into(playerImage);
 //           Picasso.get().load(player.getImage_url()).fit().centerInside().into(playerImage);

            playerItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (email.equals(MainActivity.userEmail)) {
//                        Toast.makeText(context, "Can't send self message", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(context, CreativeProfileActivity.class);
                        intent.putExtra("email", player.getEmail());
                        intent.putExtra("id", player.getId());
                        intent.putExtra("image", player.getImage_url());
                        intent.putExtra("score", player.getPoints());
                        intent.putExtra("name", player.getName());
                        context.startActivity(intent);
//                        StringRequest stringRequest = new StringRequest(Request.Method.POST, DataBaseApi.START_CHAT2, new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                try {
//                                    JSONObject jsonObject = new JSONObject(response);
//                                    String cid = jsonObject.getString("message");
//                                    Intent intent = new Intent(context, TextActivity.class);
//                                    intent.putExtra("id", cid);
//                                    intent.putExtra("name", name);
//                                    intent.putExtra("pic", pic);
//                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                                    context.startActivity(intent);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
//                            }
//                        }) {
//                            @Override
//                            protected Map<String, String> getParams() throws AuthFailureError {
//                                Map<String, String> stringMap = new HashMap<>();
//                                stringMap.put("sid", MainActivity.userId);
//                                stringMap.put("rid", email);
//                                return stringMap;
//                            }
//                        };
//                        RequestQueue queue = Volley.newRequestQueue(context);
//                        queue.add(stringRequest);
                    }
                }
            });
        }
    }

}
