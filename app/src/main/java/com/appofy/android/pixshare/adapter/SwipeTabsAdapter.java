package com.appofy.android.pixshare.adapter;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.appofy.android.pixshare.fragments.AlbumsFragment;
import com.appofy.android.pixshare.fragments.FriendsFragment;
import com.appofy.android.pixshare.fragments.GroupsFragment;
import com.appofy.android.pixshare.fragments.SharedAlbumsFragment;
/**
 * Created by Mihir on 4/22/2015.
 */
public class SwipeTabsAdapter extends FragmentPagerAdapter{

    private final int TAB_COUNT = 4;
    private String tabtitles[] = new String[] { "My Albums", "Shared Albums","Friends","Groups"};
    public SwipeTabsAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public int getCount() {
        return TAB_COUNT;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AlbumsFragment();
            case 1:
                return new SharedAlbumsFragment();
            case 2:
                return new FriendsFragment();
            case 3:
                return new GroupsFragment();
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabtitles[position];
    }
}
