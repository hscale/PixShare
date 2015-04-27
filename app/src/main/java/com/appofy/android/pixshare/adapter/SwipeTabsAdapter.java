package com.appofy.android.pixshare.adapter;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appofy.android.pixshare.fragments.AlbumsFragment;
import com.appofy.android.pixshare.fragments.SharedAlbumsFragment;
import com.appofy.android.pixshare.fragments.ProfileFragment;
/**
 * Created by Mihir on 4/22/2015.
 */
public class SwipeTabsAdapter extends FragmentPagerAdapter{

    private final int TAB_COUNT = 3;

    public SwipeTabsAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {

        switch (index) {
            case 0:
                // Top Rated fragment activity
                return new AlbumsFragment();
            case 1:
                // Games fragment activity
                return new SharedAlbumsFragment();
            case 2:
                // Games fragment activity
                return new ProfileFragment();
        }

        return null;
    }

    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return TAB_COUNT;
    }
}
