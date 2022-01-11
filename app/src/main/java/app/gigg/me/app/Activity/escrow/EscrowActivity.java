package app.gigg.me.app.Activity.escrow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.rakshakhegde.stepperindicator.StepperIndicator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import app.gigg.me.app.Activity.freelance.adapters.CustomViewPagerAdapter;
import app.gigg.me.app.R;

public class EscrowActivity extends AppCompatActivity {

    private ViewPager mViewPager;
    private StepperIndicator mIndicator;
    private EscrowPagerAdapter mAdapter;
    private Spinner spinner;
    private RecyclerView mChatRecyclerView;
    private ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escrow);

        mViewPager = findViewById(R.id.view_pager);
        mIndicator = findViewById(R.id.sptepper_indicator);
        mAdapter = new EscrowPagerAdapter(this);
        mViewPager.setAdapter(mAdapter);
        mIndicator.setViewPager(mViewPager);

        mIndicator.addOnStepClickListener(new StepperIndicator.OnStepClickListener() {
            @Override
            public void onStepClicked(int step) {
                mViewPager.setCurrentItem(step, true);
            }
        });

        spinner = findViewById(R.id.spinner);
        spinner.setAdapter(new ArrayAdapter<String>(this, R.layout.spinner_text, getResources().getStringArray(R.array.list_of_chat)));



        mChatRecyclerView = findViewById(R.id.chat_recyclerView);
        chatAdapter = new ChatAdapter();
        mChatRecyclerView.setAdapter(chatAdapter);

        populateChat();
    }

    private void populateChat() {
        List<ChatModel> chatModelList = new ArrayList<>();
        for (int i = 0; i <= 10; i++){
            chatModelList.add(new ChatModel(i, "Dilukachamod", "Hello", "Local time jul 24, 9:48 Am", "1"));
        }

        chatAdapter.submitList(chatModelList);
        chatAdapter.notifyDataSetChanged();
    }

    public void next(View view){
        if (mViewPager.getCurrentItem() < mAdapter.getCount() - 1) {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1, true);
        }
    }
    private static class EscrowPagerAdapter extends PagerAdapter {
        private Context context;
        private int[] layouts = {R.layout.escrow_1, R.layout.escrow_2, R.layout.escrow_3};
        private LayoutInflater layoutInflater;

        public EscrowPagerAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @NotNull
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }
}