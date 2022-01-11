package app.gigg.me.app.Activity.freelance.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.ads.AudienceNetworkAds;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.DynamicLink;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.ShortDynamicLink;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import app.gigg.me.app.Activity.freelance.adapters.ContactsAdapter;
import app.gigg.me.app.Activity.freelance.model.PhoneContacts;
import app.gigg.me.app.Adapter.PlayersAdapter;
import app.gigg.me.app.Model.Player;
import app.gigg.me.app.R;

public class ShareProfileActivity extends AppCompatActivity {

    public static Uri mInvitationUrl;
    private String email;
    private SharedPreferences sharedPreferences;
    private SharedPreferences bottomBannerType, startAppID, admobInterstitial, facebookInterstitial, facebookBanner, admobBanner, questionTime, userSituationId, completedOption;
    String categoryId, categoryName, userId, bannerBottomType;
    private LinearLayout adsLinear;
    private AdView bannerAdmobAdView;

    private RecyclerView top_player_recyclerView;
    private List<Player> playerList = new ArrayList<>();
    private PlayersAdapter playersAdapter;

    private RecyclerView contacts_recyclerView;
    private ContactsAdapter adapter;
    private List<PhoneContacts> contactsList = new ArrayList<>();
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale(getApplicationContext().getResources().getString(R.string.app_lang));
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, getBaseContext().getResources().getDisplayMetrics());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_profile);


        sharedPreferences = getSharedPreferences("userEmail", MODE_PRIVATE);
        email = sharedPreferences.getString("userEmail", "");
        mInvitationUrl = Uri.parse(sharedPreferences.getString("url", ""));


        top_player_recyclerView = findViewById(R.id.item);
        playersAdapter = new PlayersAdapter(this, playerList, R.layout.gigg_contacts_item);
        top_player_recyclerView.setAdapter(playersAdapter);

        getTopPlayers();

        facebookBanner = getSharedPreferences("facebookBanner", Context.MODE_PRIVATE);
        admobBanner = getSharedPreferences("admobBanner", MODE_PRIVATE);
        adsLinear = (LinearLayout) findViewById(R.id.banner_container);
        bannerAdmobAdView = new AdView(ShareProfileActivity.this);
        bannerAdmobAdView.setAdUnitId(admobBanner.getString("admobBanner", ""));
        bottomBannerType = getSharedPreferences("bottomBannerType", MODE_PRIVATE);
        bannerBottomType = bottomBannerType.getString("bottomBannerType", "");

        if (bannerBottomType.equals("facebook")) {
            facebookBanner = getSharedPreferences("facebookBanner", MODE_PRIVATE);
            AudienceNetworkAds.initialize(this);

            com.facebook.ads.AdView facebookAdView = new com.facebook.ads.AdView(this, facebookBanner.getString("facebookBanner", null), com.facebook.ads.AdSize.BANNER_HEIGHT_50);
            LinearLayout adContainer = (LinearLayout) findViewById(R.id.bannerView10);
            adContainer.addView(facebookAdView);
            facebookAdView.loadAd();
        }

        backButton=(ImageView)findViewById(R.id.menuButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        createLink();

        contacts_recyclerView = findViewById(R.id.c_list);
        adapter = new ContactsAdapter(getApplicationContext(), contactsList);;
        contacts_recyclerView.setAdapter(adapter);

        int hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS);
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            getData();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    1001);
        }
    }

    private void getData() {
        new GetContacts().execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1001 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getData();
        }
    }

    public void createLink() {
        String link = "https://gigg.me/?email=" + email;
        FirebaseDynamicLinks.getInstance().createDynamicLink()
                .setLink(Uri.parse(link))
                .setDomainUriPrefix("https://gigg.page.link")
                .setAndroidParameters(new DynamicLink.AndroidParameters.Builder().build())
                .buildShortDynamicLink(ShortDynamicLink.Suffix.SHORT)
                .addOnSuccessListener(new OnSuccessListener<ShortDynamicLink>() {
                    @Override
                    public void onSuccess(ShortDynamicLink shortDynamicLink) {
                        mInvitationUrl = shortDynamicLink.getShortLink();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("elr", mInvitationUrl.toString());
                        editor.commit();
                    }
                });
    }

    public void sendInvitation() {
        String invitationLink = mInvitationUrl.toString();
        String msg = "Join me on Gigg now and hire me for any job where your payment is secured till I deliver on the project.\n Download the app now!"
                + invitationLink;

        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        startActivity(Intent.createChooser(intent, "Hire Me"));
    }

    public void shareLink(View view) {
        if (mInvitationUrl != null) {
            sendInvitation();
        }
    }

    private void getTopPlayers() {
        String url = getResources().getString(R.string.domain_name) + "/api/players/top10";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject player = jsonArray.getJSONObject(i);
                                String name = player.getString("name");
                                String email = player.getString("email");
                                String imageUrl = player.getString("image");
                                String memberSince = player.getString("member_since");
                                int id = player.getInt("id");

                                int points = player.getInt("score");
                                playerList.add(new Player(id,name, email, memberSince, imageUrl, points));
                            }
                            playersAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void goBack(View view) {
        onBackPressed();
    }

    private class GetContacts extends AsyncTask<Void, Void, List<PhoneContacts>>{

        @Override
        protected List<PhoneContacts> doInBackground(Void... voids) {
            List<PhoneContacts> phoneContactsList = new ArrayList<>();

            ContentResolver cr = getContentResolver();
            Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);

            if ((cur != null ? cur.getCount() : 0) > 0) {
                while (cur != null && cur.moveToNext()) {
                    String id = cur.getString(
                            cur.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cur.getString(cur.getColumnIndex(
                            ContactsContract.Contacts.DISPLAY_NAME));

                    if (cur.getInt(cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                        Cursor pCur = cr.query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id}, null);
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER));

                            Bitmap photo = null;

                            try {
                                InputStream inputStream = ContactsContract.Contacts.openContactPhotoInputStream(getContentResolver(),
                                        ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id)));

                                if (inputStream != null) {
                                    photo = BitmapFactory.decodeStream(inputStream);
                                }

                                if (inputStream != null) inputStream.close();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            PhoneContacts contacts = new PhoneContacts(name, phoneNo, photo);
                            phoneContactsList.add(contacts);
                        }
                        pCur.close();
                    }
                }
            }
            if (cur != null) {
                cur.close();
            }

            return phoneContactsList;
        }

        @Override
        protected void onPostExecute(List<PhoneContacts> phoneContacts) {
            super.onPostExecute(phoneContacts);
            adapter.update(phoneContacts);
        }
    }
}