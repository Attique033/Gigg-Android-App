package app.gigg.me.app.Activity.freelance.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import app.gigg.me.app.Activity.freelance.util.SharedPrefHelper;
import app.gigg.me.app.R;

public class VaultActivity extends AppCompatActivity {

    private TextView avalaible_balance;
    private SharedPrefHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vault);

        helper = new SharedPrefHelper(this);
        avalaible_balance = findViewById(R.id.avalaible_balance);

        avalaible_balance.setText(helper.getVault() + "$");
    }

    public void goBack(View view) {
        onBackPressed();
    }
}