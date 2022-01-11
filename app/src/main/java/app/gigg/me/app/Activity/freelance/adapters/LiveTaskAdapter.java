package app.gigg.me.app.Activity.freelance.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.devs.readmoreoption.ReadMoreOption;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import app.gigg.me.app.Activity.MainActivity;
import app.gigg.me.app.Activity.TextActivity;
import app.gigg.me.app.Activity.freelance.model.LiveTask;
import app.gigg.me.app.Activity.freelance.util.SharedPrefHelper;
import app.gigg.me.app.Manager.DataBaseApi;
import app.gigg.me.app.R;

public class LiveTaskAdapter extends RecyclerView.Adapter<LiveTaskAdapter.LiveTaskViewHolder> {
    private final Context context;
    private List<LiveTask.Task> liveTaskList;
    private final SharedPrefHelper helper;
    private String email;
    private LiveTaskListener liveTaskListener;

    public interface LiveTaskListener {
        void onAttachment(String link);

        void onUploadClick(int pos);

        void onSubmitClick(LiveTask.Task liveTask, boolean b);
    }


    public LiveTaskAdapter(Context context, List<LiveTask.Task> liveTaskList, String email, LiveTaskListener liveTaskListener) {
        this.context = context;
        this.liveTaskList = liveTaskList;
        this.email = email;
        this.liveTaskListener = liveTaskListener;
        helper = new SharedPrefHelper(context);
    }

    public void update(int pos, String filename) {
        LiveTask.Task task = liveTaskList.get(pos);
        task.setUpdate(filename);
        notifyItemChanged(pos);
    }

    public void updateData(List<LiveTask.Task> liveTaskList) {
        this.liveTaskList = liveTaskList;
        notifyDataSetChanged();
    }

    @NonNull
    @NotNull
    @Override
    public LiveTaskViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.live_task_item, parent, false);
        return new LiveTaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LiveTaskViewHolder holder, int position) {
        LiveTask.Task liveTask = liveTaskList.get(position);

        holder.mUsername.setText("@" + liveTask.getEmployee_name());
        holder.mTitle.setText(liveTask.getProject_title());
        holder.mCountry.setText(liveTask.getTarget_country());
        holder.mVolume.setText(String.valueOf(liveTask.getVolume()));
        holder.mAmount.setText("$" + liveTask.getAmount());

        holder.mUpdate.setText(liveTask.getUpdate() == null ? "Upload" : liveTask.getUpdate());

        setDescription(holder.mDescription, liveTask.getProject_description());


        boolean submit = helper.getSubmit(String.valueOf(liveTask.getId()));
        if (submit) {
            holder.mSend.setImageResource(R.drawable.ic_send);
        } else {
            holder.mSend.setImageResource(R.drawable.ic_send_e);
        }

        holder.mUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (liveTaskListener != null && position != RecyclerView.NO_POSITION) {
                    liveTaskListener.onUploadClick(position);
                }
            }
        });

        holder.mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (submit) {
                    if (liveTaskListener != null && position != RecyclerView.NO_POSITION) {
                        liveTaskListener.onSubmitClick(liveTask, true);
                    }
                } else {
                    if (liveTaskListener != null && position != RecyclerView.NO_POSITION) {
                        liveTaskListener.onSubmitClick(liveTask, false);
                    }
                }
            }
        });

        holder.view_attachment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (liveTaskListener != null && position != RecyclerView.NO_POSITION) {
                    liveTaskListener.onAttachment(liveTask.getDownload_link());
                }
            }
        });

        holder.chat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                getConnectedUserData(liveTask.getFreelancer_email());
            }
        });
    }

    private void setDescription(TextView mDescription, String project_description) {
        ReadMoreOption readMoreOption = new ReadMoreOption.Builder(context)
                .textLength(2, ReadMoreOption.TYPE_LINE)
                .moreLabel("Show More")
                .lessLabel("Show Less")
                .moreLabelColor(Color.parseColor("#B5B5B5"))
                .lessLabelColor(Color.parseColor("#B5B5B5"))
                .labelUnderLine(true)
                .build();

        String LINK_REGEX = "((http:\\/\\/|https:\\/\\/)?(www.)?(([a-zA-Z0-9-]){2,2083}\\.){1,4}([a-zA-Z]){2,6}(\\/(([a-zA-Z-_\\/\\.0-9#:?=&;,]){0,2083})?){0,2083}?[^ \\n]*)";
        Pattern p = Pattern.compile(LINK_REGEX, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(project_description);

        SpannableString ss = new SpannableString(project_description);


        while (m.find()) {
            String linkText = m.group();
            int firstIndex = project_description.indexOf(linkText);
            int lastIndex = project_description.lastIndexOf(linkText) + linkText.length();

            ClickableSpan clickableSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(linkText)));
                }

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(Color.BLUE);
                }
            };

            ss.setSpan(clickableSpan, firstIndex, lastIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            readMoreOption.addReadMoreTo(mDescription, ss);
            mDescription.setMovementMethod(LinkMovementMethod.getInstance());
        }

    }

    @Override
    public int getItemCount() {
        return liveTaskList.size();
    }

    public static class LiveTaskViewHolder extends RecyclerView.ViewHolder {
        private final TextView mUsername;
        private final TextView mTitle;
        private final TextView mDescription;
        private final TextView mCountry;
        private final TextView mVolume;
        private final TextView mAmount;
        private final ConstraintLayout mUpload;
        private final ImageView mSend;
        private final TextView mUpdate;
        private final TextView view_attachment;
        private LinearLayout chat;

        public LiveTaskViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            mUsername = itemView.findViewById(R.id.text_username);
            mTitle = itemView.findViewById(R.id.text_title);
            mDescription = itemView.findViewById(R.id.text_description);
            mCountry = itemView.findViewById(R.id.text_country);
            mVolume = itemView.findViewById(R.id.text_volume);
            mAmount = itemView.findViewById(R.id.text_amount);
            mUpload = itemView.findViewById(R.id.btn_upload);
            mSend = itemView.findViewById(R.id.btn_send);
            mUpdate = itemView.findViewById(R.id.text_file);
            view_attachment = itemView.findViewById(R.id.view_attachment);
            chat = itemView.findViewById(R.id.chat);
        }
    }

    private void getConnectedUserData(String emailX) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, context.getString(R.string.domain_name) + "/api/players/getplayerdata", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String userName = jsonObject.getString("name");
                    String userEmail = jsonObject.getString("email");
                    String userImageUrl = jsonObject.getString("image");
                    openChatX(userName, userImageUrl, emailX);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("email", emailX);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


    private void openChatX(String name, String pic, String x) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, DataBaseApi.START_CHAT2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
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
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> stringMap = new HashMap<>();
                stringMap.put("sid", MainActivity.userId);
                stringMap.put("rid", x);
                return stringMap;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(context);
        queue.add(stringRequest);
    }
}
