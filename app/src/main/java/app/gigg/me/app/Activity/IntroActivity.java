package app.gigg.me.app.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;

import org.jetbrains.annotations.NotNull;

import app.gigg.me.app.Manager.PreferenceManager;
import app.gigg.me.app.R;

public class IntroActivity extends AppCompatActivity {

    private TextView next;
    private TextView signIn;
    private ConstraintLayout layout1;
    ConstraintLayout layout2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (new PreferenceManager(this).checkPreference()) {
            loadHome();
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(@NotNull PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getLocalizedMessage());
                    }
                });
        setContentView(R.layout.activity_intro);


        next = findViewById(R.id.btn_next);
        signIn = findViewById(R.id.btn_signinn);
        layout1 = findViewById(R.id.layout_intro_1);
        layout2 = findViewById(R.id.layout_intro_2);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                layout1.setVisibility(View.GONE);
                layout2.setVisibility(View.VISIBLE);
            }
        });

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PreferenceManager(getApplicationContext()).writePreference();
                loadHome();
            }
        });


    }

    private void loadHome() {
        startActivity(new Intent(this, SplashScreenActivity.class));
        finish();
    }


    @Override
    protected void onStart() {
        super.onStart();

    }
}