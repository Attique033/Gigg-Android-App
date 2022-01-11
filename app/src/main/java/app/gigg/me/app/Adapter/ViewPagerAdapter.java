package app.gigg.me.app.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import app.gigg.me.app.Fragments.GuruJobFragment;
import app.gigg.me.app.Fragments.UpworkFragment;
import app.gigg.me.app.Fragments.freelancerJobsFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0 :
                return new freelancerJobsFragment();

            case 1 :
                return new UpworkFragment();

            case 2 :
                return new GuruJobFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
