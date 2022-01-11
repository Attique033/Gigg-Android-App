package app.gigg.me.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import app.gigg.me.app.Adapter.AddChatAdapter;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.Model.AddChatModal;
import app.gigg.me.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class AddChatActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    List<AddChatModal> addChatModals = new ArrayList<>();
    private AddChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_chat);

        recyclerView = findViewById(R.id.all_player_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        loadData();
    }

    private void loadData() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET,getResources().getString(R.string.domain_name) + "/api/players/all/desc", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    Log.e("allplayers",jsonObject.toString());
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject o = jsonArray.getJSONObject(i);
                        AddChatModal item = new AddChatModal(
                                o.getString("id"),
                                o.getString("name"),
                                o.getString("image")
                        );
                        addChatModals.add(item);
                        adapter = new AddChatAdapter(getApplicationContext(), addChatModals);
                        recyclerView.setAdapter(adapter);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
     //           Toast.makeText(AddChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(stringRequest);
    }
}