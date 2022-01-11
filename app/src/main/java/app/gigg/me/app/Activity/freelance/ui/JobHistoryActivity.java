package app.gigg.me.app.Activity.freelance.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;

import app.gigg.me.app.Activity.freelance.adapters.CustomViewPagerAdapter;
import app.gigg.me.app.Activity.freelance.ui.fragments.CompletedJobsFragment;
import app.gigg.me.app.Activity.freelance.ui.fragments.OpenJobsFragment;
import app.gigg.me.app.R;

public class JobHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_history);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        ViewPager viewPager = findViewById(R.id.viewPager);
        tabLayout.setupWithViewPager(viewPager);

        OpenJobsFragment openJobsFragment = new OpenJobsFragment();
        CompletedJobsFragment completedJobsFragment = new CompletedJobsFragment();
        CustomViewPagerAdapter viewPagerAdapter = new CustomViewPagerAdapter(getSupportFragmentManager(), 0);
        viewPagerAdapter.addFragment(openJobsFragment);
        viewPagerAdapter.addFragment(completedJobsFragment);
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.getTabAt(0).setText("Open Jobs");
        tabLayout.getTabAt(1).setText("Completed Jobs");

    }

    public void goBack(View view) {
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}