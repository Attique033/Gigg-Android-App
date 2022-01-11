package app.gigg.me.app.Activity.test;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import app.gigg.me.app.R;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(getResources().getColor(R.color.new_blue));
        setContentView(R.layout.activity_main);
    }
}